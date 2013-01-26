package experiments.insidefx.undecorator;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 */
public class Undecorator extends StackPane {

    Node clientArea;
    Pane stageDecoration = null;
    Rectangle shadowRectangle;
    BorderPane decorationWrapper;
    static public int SHADOW_WIDTH = 15;
    static public int SAVED_SHADOW_WIDTH = 15;
    static public int RESIZE_PADDING = 15;
    DropShadow dsFocused;
    DropShadow dsNotFocused;
    public static final Logger LOGGER = Logger.getLogger("Undecorator");

    public Undecorator(Stage stage, final Node root) {

        loadConfig();
        // The controller
        new UndecoratorController(this);
        
        UndecoratorController.setAsDraggable(stage, root);
        // radius, spread, offsets
        dsFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, SHADOW_WIDTH, 0.1, 0, 0);
        dsNotFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.DARKGREY, SHADOW_WIDTH, 0, 0, 0);

        getStylesheets().add("/css/undecorator.css");

        shadowRectangle = new Rectangle();
        UndecoratorController.setAsResizable(stage, shadowRectangle, RESIZE_PADDING, SHADOW_WIDTH);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("stagedecoration.fxml"));
            fxmlLoader.setController(new StageDecorationController(stage));
            stageDecoration = (Pane) fxmlLoader.load();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Decorations not found", ex);
        }
        /*
         * Create this wrapper to make stageDecoration pane transparent for mouse event
         */
        decorationWrapper = new BorderPane() {
            /**
             * Ideally must not override deprecated method, but at this jfx
             * stage, there is no good API to pick component under mouse.
             */
            @Override
            protected boolean containsBounds(double d, double d1) {
                ObservableList<Node> children = stageDecoration.getChildren();
                for (Node node : children) {
                    if (node.isMouseTransparent()) {
                        continue;
                    }
                    Bounds boundsInParent = node.getBoundsInParent();
                    if (boundsInParent.contains(d, d1)) {
                        return true;
                    }
                }
                return false;
            }
        };
        decorationWrapper.setCenter(stageDecoration);

        // TODO: how to programmatically get css values? wait for JavaFX custom CSS
        shadowRectangle.getStyleClass().add("undecorator-background");

        super.getChildren().addAll(shadowRectangle, root, decorationWrapper);

        // UndecoratorController.setAsDraggable(stage, shadowRectangle);

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

    protected void setShadow(boolean b) {
        if (!b) {
            shadowRectangle.setEffect(null);
            SAVED_SHADOW_WIDTH=SHADOW_WIDTH;
            SHADOW_WIDTH=0;
        } else {
            shadowRectangle.setEffect(dsFocused);
            SHADOW_WIDTH=SAVED_SHADOW_WIDTH;
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
            } else if (node == decorationWrapper) {
                decorationWrapper.resize(w - SHADOW_WIDTH * 2, h - SHADOW_WIDTH * 2);
                decorationWrapper.setLayoutX(SHADOW_WIDTH);
                decorationWrapper.setLayoutY(SHADOW_WIDTH);
            } else {
                node.resize(w - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2, h - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2);
                node.setLayoutX(SHADOW_WIDTH + RESIZE_PADDING);
                node.setLayoutY(SHADOW_WIDTH + RESIZE_PADDING);
            }
        }
    }

    static void loadConfig() {
        Properties prop = new Properties();

        try {
            prop.load(Undecorator.class.getClassLoader().getResourceAsStream("undecorator.properties"));
            RESIZE_PADDING = Integer.parseInt(prop.getProperty("window-resize-padding"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error while loading confguration flie", ex);
        }
    }
}
