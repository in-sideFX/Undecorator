package insidefx.undecorator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 */
public class StageDecorationController implements Initializable {

    @FXML
    private Button menu;
    @FXML
    private Button close;
    @FXML
    private Button maximize;
    @FXML
    private Button minimize;
    @FXML
    private Button resize;
    Undecorator undecorator;
    MenuItem maximizeMenuItem;

    public StageDecorationController(Undecorator ud) {
        undecorator = ud;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Menu
        final ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Minimize");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                undecorator.minimizeProperty().set(!undecorator.minimizeProperty().get());
            }
        });
        maximizeMenuItem = new MenuItem("Maximize");
        maximizeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                undecorator.maximizeProperty().set(!undecorator.maximizeProperty().get());
                contextMenu.hide(); // Stay stuck on screen
            }
        });
        MenuItem item3 = new MenuItem("Close");
        item3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                undecorator.closeProperty().set(!undecorator.closeProperty().get());
            }
        });
        contextMenu.getItems().addAll(item1, maximizeMenuItem, new SeparatorMenuItem(), item3);
        // menu.setContextMenu(contextMenu);
        menu.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                contextMenu.show(menu, Side.BOTTOM, 0, 0);
            }
        });

        // Close button
        close.setTooltip(new Tooltip("Close"));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.closeProperty().set(!undecorator.closeProperty().get());
            }
        });

        // Maximize button
        // If changed via contextual menu
        undecorator.maximizeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                Tooltip tooltip = maximize.getTooltip();
                if (tooltip.getText().equals("Maximize")) {
                    tooltip.setText("Restore");
                    maximizeMenuItem.setText("Restore");
                    maximize.getStyleClass().add("decoration-button-restore");
                } else {
                    tooltip.setText("Maximize");
                    maximizeMenuItem.setText("Maximize");
                    maximize.getStyleClass().remove("decoration-button-restore");
                }
            }
        });

        maximize.setTooltip(new Tooltip("Maximize"));
        maximize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.maximizeProperty().set(!undecorator.maximizeProperty().get());
            }
        });


        // Minimize button
        minimize.setTooltip(new Tooltip("Minimize"));
        minimize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.minimizeProperty().set(!undecorator.minimizeProperty().get());
            }
        });
    }
}
