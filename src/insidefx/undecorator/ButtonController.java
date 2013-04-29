/*
 * BSD
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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Handles interactions with window buttons.
 */
public class ButtonController {
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
    private MenuItem maximizeMenuItem;
    private UndecoratorController undecoratorController;
    private final SimpleBooleanProperty maximizeProperty;
    private final SimpleBooleanProperty minimizeProperty;
    private final SimpleBooleanProperty closeProperty;
    private CheckMenuItem fullScreenMenuItem;
    private StageStyle stageStyle;
    private Stage stage;

    public ButtonController(UndecoratorController undecoratorController, Stage stage){
	this.undecoratorController = undecoratorController;
	this.stage = stage;
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
    }

    public void setFullScreen(boolean isVisible){
	maximize.setVisible(isVisible);
	minimize.setVisible(isVisible);
	resize.setVisible(isVisible);
    }

    public void initDecoration() {
        MenuItem minimizeMenuItem = null;
        // Menu
        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        if (minimize != null) { // Utility Stage
            minimizeMenuItem = new MenuItem(Undecorator.LOC.getString("Minimize"));
            minimizeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    minimizeProperty().set(!minimizeProperty().get());
                }
            });
            contextMenu.getItems().add(minimizeMenuItem);
        }
        if (maximize != null) { // Utility Stage
            maximizeMenuItem = new MenuItem(Undecorator.LOC.getString("Maximize"));
            maximizeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    maximizeProperty().set(!maximizeProperty().get());
                    contextMenu.hide(); // Stay stuck on screen
                }
            });
            contextMenu.getItems().addAll(maximizeMenuItem, new SeparatorMenuItem());
        }
        MenuItem closeMenuItem = new MenuItem(Undecorator.LOC.getString("Close"));
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeProperty().set(!closeProperty().get());
            }
        });

        contextMenu.getItems().add(closeMenuItem);
        if (stageStyle != StageStyle.UTILITY) {
            fullScreenMenuItem = new CheckMenuItem(Undecorator.LOC.getString("FullScreen"));
            fullScreenMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // fake
                    undecoratorController.setFullScreen(!stage.isFullScreen());
                }
            });

            contextMenu.getItems().addAll(new SeparatorMenuItem(), fullScreenMenuItem);
        }
        // menu.setContextMenu(contextMenu);
        menu.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(contextMenu.isShowing())
                    contextMenu.hide();
                else
                   contextMenu.show(menu, Side.BOTTOM, 0, 0);
            }
        });

        // Close button
        close.setTooltip(new Tooltip(Undecorator.LOC.getString("Close")));
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
                if (tooltip.getText().equals(Undecorator.LOC.getString("Maximize"))) {
                    tooltip.setText(Undecorator.LOC.getString("Restore"));
                    maximizeMenuItem.setText(Undecorator.LOC.getString("Restore"));
                    maximize.getStyleClass().add("decoration-button-restore");
                    resize.setVisible(false);
                } else {
                    tooltip.setText(Undecorator.LOC.getString("Maximize"));
                    maximizeMenuItem.setText(Undecorator.LOC.getString("Maximize"));
                    maximize.getStyleClass().remove("decoration-button-restore");
                    resize.setVisible(true);
                }
            }
        });

        if (maximize != null) { // Utility Stage
            maximize.setTooltip(new Tooltip(Undecorator.LOC.getString("Maximize")));
            maximize.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    maximizeProperty().set(!maximizeProperty().get());
                }
            });
        }

        // Minimize button
        if (minimize != null) { // Utility Stage
            minimize.setTooltip(new Tooltip(Undecorator.LOC.getString("Minimize")));
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

    public UndecoratorController getController() {
        return undecoratorController;
    }

    public void setStageStyle(StageStyle st) {
        stageStyle = st;
    }

    public StageStyle getStageStyle() {
        return stageStyle;
    }
    
}
