/**
 * Demo purpose In-SideFX (Un)decorator for JavaFX scene License: You can use
 * this code for any kind of purpose, commercial or not.
 */
package demoapp;

import insidefx.undecorator.Undecorator;
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
        
        Parent root = FXMLLoader.load(getClass().getResource("ClientArea.fxml"));

        // The Undecorator as a Scene
        final UndecoratorScene undecoratorScene = new UndecoratorScene(stage, root);
        // UndecoratorScene undecoratorScene = new UndecoratorScene(stage, StageStyle.UTILITY,root,null);

        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecoratorScene.setAsStageDraggable(stage, node);

        // Set minimum size
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        
        /*
         * Close transition
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
        
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
