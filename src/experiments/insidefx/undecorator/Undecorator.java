package experiments.insideFX.undecorator;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventDispatchChain;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 */
public class Undecorator extends StackPane {

    Node clientArea;
    Parent decoration = null;
    Rectangle shadowRectangle;
    BorderPane borderPane;
    static public int SHADOW_WIDTH = 15;
    static public int RESIZE_PADDING = 15;
    DropShadow dsFocused;
    DropShadow dsNotFocused;
    public static final Logger LOGGER = Logger.getLogger("Undecorator");

    public Undecorator(Stage stage, final Node root) {

        loadConfig();
        // The controller
        UndecoratorController.setAsDraggable(stage, root);
        // radius, spread, offsets
        dsFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, SHADOW_WIDTH, 0.1, 0, 0);
        dsNotFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.DARKGREY, SHADOW_WIDTH, 0, 0, 0);

        getStylesheets().add("/css/undecorator.css");

        shadowRectangle = new Rectangle();
        UndecoratorController.setAsResizable(stage, shadowRectangle, RESIZE_PADDING, SHADOW_WIDTH);

        try {
            decoration = FXMLLoader.load(getClass().getResource("decoration.fxml"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Decorations not found", ex);
        }
        borderPane = new BorderPane() {
            @Override
            public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
                return root.buildEventDispatchChain(tail);
//                return super.buildEventDispatchChain(tail);
            }
//            @Override
//            public boolean contains(double d, double d1) {
//                ObservableList<Node> onechild = getChildren();
//                Pane root = (Pane)onechild.get(0);
//                ObservableList<Node> children = root.getChildren();
//                for(Node node: children){
//                    if(node.contains(d, d1)){
//                        return true;
//                    }
//                }
//                return false;
//            }
        };
     //   borderPane.setCenter(decoration);
        //    decoration = borderPane;

        // Take shadow into account        
        //shadowRectangle.setPickOnBounds(true);

        // TODO: how to programmatically get css values? wait for JavaFX custom CSS
        shadowRectangle.getStyleClass().add("undecorator-background");

        super.getChildren().addAll(shadowRectangle, root);//, borderPane);

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
    }

    void maximize(Stage stage) {
        ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen screen = screensForRectangle.get(0);
        Rectangle2D visualBounds = screen.getVisualBounds();
        // Save stage bounds

        stage.setX(0);
        stage.setY(0);
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());
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
            } else if (node == borderPane) {
                node.resize(w - SHADOW_WIDTH * 2, h - SHADOW_WIDTH * 2);
                node.setLayoutX(SHADOW_WIDTH);
                node.setLayoutY(SHADOW_WIDTH);
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
