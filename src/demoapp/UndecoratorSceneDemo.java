/**
 * Demo purpose In-SideFX (Un)decorator for JavaFX scene License: You can use this code for any kind of purpose,
 * commercial or not.
 */
package demoapp;

import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author in-sideFX
 */
public class UndecoratorSceneDemo extends Application {

    Stage primaryStage;
    @FXML
    Accordion accordion;
    @FXML
    HBox clientAreaHbox;
    @FXML
    Slider sliderOpacity;
    @FXML
    Hyperlink hyperlink;

    @Override
    public void start(final Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Undecorator Scene Demo");

        // The UI (Client Area) to display
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientArea.fxml"));
        fxmlLoader.setController(this);
        Region root = (Region) fxmlLoader.load();

        // The Undecorator as a Scene
        final UndecoratorScene undecoratorScene = new UndecoratorScene(primaryStage, root);
        // Overrides defaults
        undecoratorScene.addStylesheet("demoapp/demoapp.css");
        // Enable fade transition
        undecoratorScene.setFadeInTransition();

        // Optional: Enable this node to drag the stage
        // By default the root argument of Undecorator is set as draggable
//        Node node = root.lookup("#draggableNode");
//        undecoratorScene.setAsStageDraggable(stage, node);

        /*
         * Fade out transition on window closing request
         */
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                we.consume();   // Do not hide yet
                undecoratorScene.setFadeOutTransition();
            }
        });

        // Application icons
        Image image = new Image("/demoapp/in-sidefx.png");
        primaryStage.getIcons().addAll(image);
        initUI();

        primaryStage.setScene(undecoratorScene);
        primaryStage.sizeToScene();
        primaryStage.toFront();

        // Set minimum size based on client area's minimum sizes
        Undecorator undecorator = undecoratorScene.getUndecorator();
        primaryStage.setMinWidth(undecorator.getMinWidth());
        primaryStage.setMinHeight(undecorator.getMinHeight());

        primaryStage.show();
    }

    void initUI() {
        accordion.setExpandedPane(accordion.getPanes().get(1));
        clientAreaHbox.opacityProperty().bind(sliderOpacity.valueProperty());
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI("https://arnaudnouard.wordpress.com/category/javafx/undecorator/"));
                } catch (Exception ex) {
                }
            }
        });
    }

    /**
     * The button's handler in the ClientArea.fxml Manage the UTILITY mode stage
     *
     * @param event
     */
    @FXML
    private void handleShowUtilityStage(ActionEvent event) throws IOException {
        // Stage Utility usage
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientAreaUtility.fxml"));
        fxmlLoader.setController(this);
        Region root = (Region) fxmlLoader.load();
        Stage utilityStage = new Stage();
        utilityStage.setTitle("Stage Utility type demo");
        UndecoratorScene scene = new UndecoratorScene(utilityStage, StageStyle.UTILITY, root, null);
        // Overrides defaults
        scene.addStylesheet("demoapp/demoapp.css");

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
     * Show a non resizable Stage
     *
     * @param event
     */
    @FXML
    private void handleShowNonResizableStage(ActionEvent event) throws Exception {
        UndecoratorSceneDemo undecoratorSceneDemo = new UndecoratorSceneDemo();
        Stage stage = new Stage();
        stage.setTitle("Not resizable stage");
        stage.setResizable(false);
        stage.setWidth(600);
        stage.setMinHeight(400);
        undecoratorSceneDemo.start(stage);
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
