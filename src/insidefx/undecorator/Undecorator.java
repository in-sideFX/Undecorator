/*
 * Copyright (c) 2013, Arnaud Nouard
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the In-SideFX nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package insidefx.undecorator;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

/**
 * The Stage Decorator TODO: API, utility style, win7 window behavior on
 * edge,Themes, title bar
 */
public class Undecorator extends StackPane {

    @FXML
    private Button menu;
    @FXML
    private Button close;
    @FXML
    private Button maximize;
    @FXML
    private Button minimize;
    @FXML
    private Button resize;
    MenuItem maximizeMenuItem;
    public static final Logger LOGGER = Logger.getLogger("Undecorator");
    Node clientArea;
    Pane stageDecoration = null;
    Rectangle shadowRectangle;
    static public int SHADOW_WIDTH = 15;
    static public int SAVED_SHADOW_WIDTH = 15;
    static public int RESIZE_PADDING = 7;
    DropShadow dsFocused;
    DropShadow dsNotFocused;
    UndecoratorController undecoratorController;
    Stage stage;
    Rectangle resizeRect;
    SimpleBooleanProperty maximizeProperty;
    SimpleBooleanProperty minimizeProperty;
    SimpleBooleanProperty closeProperty;

    public Undecorator(Stage stage, final Node root) {
        this(stage, root, "stagedecoration.fxml");
    }

    public Undecorator(Stage stage, final Node root, String stageDecorationFxml) {
        this.stage = stage;

        loadConfig();

        // Properties 
        maximizeProperty = new SimpleBooleanProperty(false);
        maximizeProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                getController().maximizeOrRestore();
            }
        });
        minimizeProperty = new SimpleBooleanProperty(false);
        minimizeProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                getController().minimize();
            }
        });

        closeProperty = new SimpleBooleanProperty(false);
        closeProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                getController().close();
            }
        });

        // The controller
        undecoratorController = new UndecoratorController(this);

        undecoratorController.setAsStageDraggable(stage, root);

        // radius, spread, offsets
        dsFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, SHADOW_WIDTH, 0.1, 0, 0);
        dsNotFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.DARKGREY, SHADOW_WIDTH, 0, 0, 0);

        shadowRectangle = new Rectangle();

        // UI part of the decoration
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(stageDecorationFxml));
//            fxmlLoader.setController(new StageDecorationController(this));
            fxmlLoader.setController(this);
            stageDecoration = (Pane) fxmlLoader.load();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Decorations not found", ex);
        }
        initDecoration();

        /*
         * Resize rectangle
         */
        resizeRect = new Rectangle();
        resizeRect.setFill(null);
        resizeRect.setStrokeWidth(RESIZE_PADDING);
        resizeRect.setStrokeType(StrokeType.INSIDE);
        resizeRect.setStroke(Color.TRANSPARENT);
        undecoratorController.setStageResizableWith(stage, resizeRect, RESIZE_PADDING, SHADOW_WIDTH);

        // TODO: how to programmatically get css values? wait for JavaFX custom CSS
        shadowRectangle.getStyleClass().add("undecorator-background");
        super.getChildren().addAll(shadowRectangle, root, stageDecoration, resizeRect);

        /*
         * Focused stage
         */
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                setShadowFocused(t1.booleanValue());
            }
        });
        /*
         * Fullscreen
         */
        stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                setShadow(t1.booleanValue());
            }
        });
    }

    public Rectangle getBackground() {
        return shadowRectangle;
    }

    public void initDecoration() {
        MenuItem minimizeMenuItem=null;
        // Menu
        final ContextMenu contextMenu = new ContextMenu();
        if (minimize != null) { // Utility Stage
            minimizeMenuItem = new MenuItem("Minimize");
            minimizeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    minimizeProperty().set(!minimizeProperty().get());
                }
            });
            contextMenu.getItems().add(minimizeMenuItem);
        }
        if (maximize != null) { // Utility Stage
            maximizeMenuItem = new MenuItem("Maximize");
            maximizeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    maximizeProperty().set(!maximizeProperty().get());
                    contextMenu.hide(); // Stay stuck on screen
                }
            });
            contextMenu.getItems().addAll(maximizeMenuItem,new SeparatorMenuItem());
        }
        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeProperty().set(!closeProperty().get());
            }
        });
        contextMenu.getItems().add(closeMenuItem);
        
        // menu.setContextMenu(contextMenu);
        menu.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                contextMenu.show(menu, Side.BOTTOM, 0, 0);
            }
        });

        // Close button
        close.setTooltip(new Tooltip("Close"));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                closeProperty().set(!closeProperty().get());
            }
        });

        // Maximize button
        // If changed via contextual menu
        maximizeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                Tooltip tooltip = maximize.getTooltip();
                if (tooltip.getText().equals("Maximize")) {
                    tooltip.setText("Restore");
                    maximizeMenuItem.setText("Restore");
                    maximize.getStyleClass().add("decoration-button-restore");
                } else {
                    tooltip.setText("Maximize");
                    maximizeMenuItem.setText("Maximize");
                    maximize.getStyleClass().remove("decoration-button-restore");
                }
            }
        });

        if (maximize != null) { // Utility Stage
            maximize.setTooltip(new Tooltip("Maximize"));
            maximize.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    maximizeProperty().set(!maximizeProperty().get());
                }
            });
        }

        // Minimize button
        if (minimize != null) { // Utility Stage
            minimize.setTooltip(new Tooltip("Minimize"));
            minimize.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    minimizeProperty().set(!minimizeProperty().get());
                }
            });
        }
    }

    public SimpleBooleanProperty maximizeProperty() {
        return maximizeProperty;
    }

    public SimpleBooleanProperty minimizeProperty() {
        return minimizeProperty;
    }

    public SimpleBooleanProperty closeProperty() {
        return closeProperty;
    }

    /**
     * Bridge to controller to enable this node to drag the stage
     *
     * @param stage
     * @param node
     */
    public void setAsStageDraggable(Stage stage, Node node) {
        undecoratorController.setAsStageDraggable(stage, node);
    }

    protected void setShadow(boolean b) {
        if (!b) {
            shadowRectangle.setEffect(null);
            SAVED_SHADOW_WIDTH = SHADOW_WIDTH;
            SHADOW_WIDTH = 0;
        } else {
            shadowRectangle.setEffect(dsFocused);
            SHADOW_WIDTH = SAVED_SHADOW_WIDTH;
        }
    }

    protected void setShadowFocused(boolean b) {
        if (b) {
            shadowRectangle.setEffect(dsFocused);
        } else {
            shadowRectangle.setEffect(dsNotFocused);
        }
    }

    @Override
    public void layoutChildren() {
        Bounds b = super.getLayoutBounds();
        double w = b.getWidth();
        double h = b.getHeight();
        ObservableList<Node> list = super.getChildren();
        for (Node node : list) {
            if (node == shadowRectangle) {
                shadowRectangle.setWidth(w - SHADOW_WIDTH * 2);
                shadowRectangle.setHeight(h - SHADOW_WIDTH * 2);
                shadowRectangle.setX(SHADOW_WIDTH);
                shadowRectangle.setY(SHADOW_WIDTH);
            } else if (node == stageDecoration) {
                stageDecoration.resize(w - SHADOW_WIDTH * 2, h - SHADOW_WIDTH * 2);
                stageDecoration.setLayoutX(SHADOW_WIDTH);
                stageDecoration.setLayoutY(SHADOW_WIDTH);
            } else if (node == resizeRect) {
                resizeRect.setWidth(w - SHADOW_WIDTH * 2);
                resizeRect.setHeight(h - SHADOW_WIDTH * 2);
                resizeRect.setLayoutX(SHADOW_WIDTH);
                resizeRect.setLayoutY(SHADOW_WIDTH);
            } else {
                node.resize(w - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2, h - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2);
                node.setLayoutX(SHADOW_WIDTH + RESIZE_PADDING);
                node.setLayoutY(SHADOW_WIDTH + RESIZE_PADDING);
            }
        }
    }

    public UndecoratorController getController() {
        return undecoratorController;
    }

    public Stage getStage() {
        return stage;
    }

    static void loadConfig() {
        Properties prop = new Properties();

        try {
            prop.load(Undecorator.class.getClassLoader().getResourceAsStream("skin/undecorator.properties"));
            SHADOW_WIDTH = Integer.parseInt(prop.getProperty("window-shadow-width"));
            RESIZE_PADDING = Integer.parseInt(prop.getProperty("window-resize-padding"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error while loading confguration flie", ex);
        }
    }
}
