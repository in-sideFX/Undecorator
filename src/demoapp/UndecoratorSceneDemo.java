/**
 * Demo purpose In-SideFX (Un)decorator for JavaFX scene License: You can use
 * this code for any kind of purpose, commercial or not.
 */
package demoapp;

import insidefx.undecorator.UndecoratorScene;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorSceneDemo extends Application {
    
    @Override
    public void start(final Stage stage) throws Exception {
        
        // The Undecorator as a Scene
        Region root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
        final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
 
        // Stage Utility usage
        //Parent root = FXMLLoader.load(getClass().getResource("ClientAreaUtility.fxml"));
        //final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, StageStyle.UTILITY,root,null);

        // Enable fade transition
        undecoratorScene.setFadeInTransition();
      
        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecoratorScene.setAsStageDraggable(stage, node);

        /*
         * Fade transition on window closing request
         */
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                we.consume();   // Do not hide
                undecoratorScene.setFadeOutTransition();
            }
        });
        
        stage.setScene(undecoratorScene);
        stage.sizeToScene();
        stage.toFront();
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
