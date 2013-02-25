/**
 * Demo purpose
 * In-SideFX (Un)decorator for JavaFX scene
 * License: You can use this code for any kind of purpose, commercial or not.
 */
package demoapp;

import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorSceneDemo extends Application {

    @Override
    public void start(final Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
        
        // The Undecorator as a Scene
        UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
       // UndecoratorScene undecoratorScene = new UndecoratorScene(stage, StageStyle.UTILITY,root,null);

        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecoratorScene.setAsStageDraggable(stage, node);
        
        // Set minimum size
        stage.setMinWidth(500);
        stage.setMinHeight(400);

        stage.setScene(undecoratorScene);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
