package insidefx.undecorator;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author In-SideFX
 */
public class UndecoratorScene extends Scene {

    static public final String DEFAULT_STYLESHEET = "skin/undecorator.css";
    Undecorator undecorator;

    public UndecoratorScene(Stage stage, Parent root) {
        super(root);

        undecorator = new Undecorator(stage, root);
        super.setRoot(undecorator);

        // Customize it by CSS if needed:
        undecorator.getStylesheets().add(DEFAULT_STYLESHEET);

        // Transparent scene and stage
        stage.initStyle(StageStyle.TRANSPARENT);
        super.setFill(Color.TRANSPARENT);
    }

    public void removeDefaultStylesheet() {
        undecorator.getStylesheets().remove(DEFAULT_STYLESHEET);
    }

    public void addStylesheet(String css) {
        undecorator.getStylesheets().add(css);
    }

    public void setAsStageDraggable(Stage stage, Node node) {
        undecorator.setAsStageDraggable(stage, node);
    }
}
