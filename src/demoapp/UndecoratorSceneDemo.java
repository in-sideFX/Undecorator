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
import javafx.scene.Parent;
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
        Parent root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));
        final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
 
        // Stage Utility usage
        //Parent root = FXMLLoader.load(getClass().getResource("ClientAreaUtility.fxml"));
        //final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, StageStyle.UTILITY,root,null);

        // Enable fade transition
        undecoratorScene.setFadeTransitionEnabled();
        
      
        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecoratorScene.setAsStageDraggable(stage, node);

        // Set minimum size
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        
        /*
         * Fade transition on window closing request
         */
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                t.consume();
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), undecoratorScene.getUndecorator());
                fadeTransition.setToValue(0);
                fadeTransition.play();
                fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        stage.hide();
                    }
                });
            }
        });
        
        stage.setScene(undecoratorScene);
        stage.toFront();
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
