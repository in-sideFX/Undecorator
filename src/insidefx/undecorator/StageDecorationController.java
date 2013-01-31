package insidefx.undecorator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;

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
                e = (ActionEvent) e.copyFor(minimize, minimize);
                minimize.getOnAction().handle(e);
            }
        });
        final MenuItem item2 = new MenuItem("Maximize");
        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                e = (ActionEvent) e.copyFor(maximize, maximize);
                maximize.getOnAction().handle(e);
                contextMenu.hide(); // Stay stuck on screen
            }
        });
        MenuItem item3 = new MenuItem("Close");
        item3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                e = (ActionEvent) e.copyFor(close, close);
                close.getOnAction().handle(e);
            }
        });
        contextMenu.getItems().addAll(item1, item2, new SeparatorMenuItem(), item3);
        menu.setContextMenu(contextMenu);

        // Close button
        close.setTooltip(new Tooltip("Close"));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.getController().close();
            }
        });

        // Maximize button
        maximize.setTooltip(new Tooltip("Maximize"));
        maximize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.getController().maximize();

                Button source = (Button) t.getSource();
                Tooltip tooltip = source.getTooltip();
                if (tooltip.getText().equals("Maximize")) {
                    tooltip.setText("Restore");
                    source.getStyleClass().add("decoration-button-restore");
                } else {
                    tooltip.setText("Maximize");
                    source.getStyleClass().remove("decoration-button-restore");
                }
            }
        });


        // Minimize button
        minimize.setTooltip(new Tooltip("Minimize"));
        minimize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                undecorator.getController().minimize();
            }
        });
    }
}
