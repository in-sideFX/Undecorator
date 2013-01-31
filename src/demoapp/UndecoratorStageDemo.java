/**
 * Demo purpose
 */
package demoapp;

import insidefx.undecorator.Undecorator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorStageDemo extends Application {

    @Override
    public void start(final Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
        Undecorator undecorator = new Undecorator(stage, root);
        undecorator.getStylesheets().add("skin/undecorator.css");
        
        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecorator.setAsStageDraggable(stage, node);

        
        Scene scene = new Scene(undecorator);

        // Transparent scene and stage
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        // Test Minimum size
        stage.setMinWidth(500);
        stage.setMinHeight(400);

        stage.setTitle("No title bar");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
