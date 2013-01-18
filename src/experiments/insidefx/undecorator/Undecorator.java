package experiments.insideFX.undecorator;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 */
public class Undecorator extends StackPane {

    Node clientArea;
    static public int SHADOW_WIDTH = 15;
    static public int RESIZE_PADDING = 15;
    DropShadow dsFocused;
    DropShadow dsNotFocused;
    Rectangle shadowRectangle;
    public static final Logger LOGGER = Logger.getLogger("Undecorator");

    public Undecorator(Stage stage, Node root) {

        loadConfig();
        // The controller
        UndecoratorController.setAsDraggable(stage, root);
        // radius, spread, offsets
        dsFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, SHADOW_WIDTH, 0.1, 0, 0);
        dsNotFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.DARKGREY, SHADOW_WIDTH, 0, 0, 0);

        getStylesheets().add("/css/undecorator.css");


        shadowRectangle = new Rectangle();
        UndecoratorController.setAsResizable(stage, shadowRectangle, RESIZE_PADDING, SHADOW_WIDTH);
        
        // Take shadow into account        
        //shadowRectangle.setPickOnBounds(true);

        // TODO: how to programmatically get css values?
        // CSS
        //super.getStyleClass().add("windowenhancer-padding");
        shadowRectangle.getStyleClass().add("undecorator-background");

        super.getChildren().add(shadowRectangle);
       // UndecoratorController.setAsDraggable(stage, shadowRectangle);

        /*
         * Focused stage
         */
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (t1.booleanValue()) {
                    shadowRectangle.setEffect(dsFocused);
                } else {
                    shadowRectangle.setEffect(dsNotFocused);
                }
            }
        });
        /*
         * Fullscreen
         */
        stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (t1.booleanValue()) {
                    shadowRectangle.setEffect(null);
                } else {
                    shadowRectangle.setEffect(dsFocused);
                }
            }
        });
        /*
         * TODO: Maximized
         */

//        UndecoratorController.setAsResizable(shadowRectangle);

    }

    @Override
    public void layoutChildren() {
        Bounds b = super.getLayoutBounds();
        double w = b.getWidth();
        double h = b.getHeight();
        ObservableList<Node> list = super.getChildren();
        for (Node n : list) {
            if (n == shadowRectangle) {
                shadowRectangle.setWidth(w - SHADOW_WIDTH * 2);
                shadowRectangle.setHeight(h - SHADOW_WIDTH * 2);
                shadowRectangle.setX(SHADOW_WIDTH);
                shadowRectangle.setY(SHADOW_WIDTH);
            } else {
                n.resize(w - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2, h - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2);
                n.setLayoutX(SHADOW_WIDTH + RESIZE_PADDING);
                n.setLayoutY(SHADOW_WIDTH + RESIZE_PADDING);
            }
        }

    }

    void loadConfig() {
        Properties prop = new Properties();

        try {
            prop.load(Undecorator.class.getClassLoader().getResourceAsStream("undecorator.properties"));
            RESIZE_PADDING = Integer.parseInt(prop.getProperty("window-resize-padding"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error while loading confguration flie", ex);
        }
    }
}
