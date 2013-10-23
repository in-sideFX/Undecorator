/**
 * Demo purpose In-SideFX (Un)decorator for JavaFX stage License: You can use
 * this code for any kind of purpose, commercial or not.
 */
package demoapp;

import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorStageDemo extends Application {

    Stage primaryStage;

    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void start(final Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Undecorator Stage Demo");
        // The UI (Client Area) to display
        Region root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientArea.fxml"));
            fxmlLoader.setController(this);
            root = (Region) fxmlLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Undecorator undecorator = new Undecorator(stage, root);
        // Customize it by CSS if needed:
        undecorator.getStylesheets().add("skin/undecorator.css");

        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
        Node node = root.lookup("#draggableNode");
        undecorator.setAsStageDraggable(stage, node);

        Scene scene = new Scene(undecorator);

        // Transparent scene and stage
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);

        stage.show();

        // Set minimum size
        stage.setMinWidth(undecorator.getMinWidth());
        stage.setMinHeight(undecorator.getMinHeight());

    }

    /**
     * The button's handler in the ClientArea.fxml Manage the UTILITY mode
     * stages
     *
     * @param event
     */
    @FXML
    @SuppressWarnings("CallToThreadDumpStack")
    private void handleShowUtilityStage(ActionEvent event) {
        // Stage Utility usage
        Region root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientAreaUtility.fxml"));
            fxmlLoader.setController(this);
            root = (Region) fxmlLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Stage utilityStage = new Stage();
        utilityStage.setTitle("Stage Utility type demo");
        UndecoratorScene scene = new UndecoratorScene(utilityStage, StageStyle.UTILITY, root, null);
        utilityStage.setScene(scene);
        utilityStage.initModality(Modality.WINDOW_MODAL);
        utilityStage.initOwner(primaryStage);

        // Set sizes based on client area's sizes
        Undecorator undecorator = scene.getUndecorator();
        utilityStage.setMinWidth(undecorator.getMinWidth());
        utilityStage.setMinHeight(undecorator.getMinHeight());
        utilityStage.setWidth(undecorator.getPrefWidth());
        utilityStage.setHeight(undecorator.getPrefHeight());
        if (undecorator.getMaxWidth() > 0) {
            utilityStage.setMaxWidth(undecorator.getMaxWidth());
        }
        if (undecorator.getMaxHeight() > 0) {
            utilityStage.setMaxHeight(undecorator.getMaxHeight());
        }
        utilityStage.sizeToScene();
        utilityStage.show();
    }

    /**
     * Handles Utility stage buttons
     *
     * @param event
     */
    public void handleUtilityAction(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
