package experiments.insidefx.undecorator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 */
public class StageDecorationController implements Initializable {

    @FXML
    private Button close;
    @FXML
    private Button maximize;
    @FXML
    private Button minimize;
    @FXML
    private Button resize;
    Stage stage;
//    UndecoratorController   undecoratorController;
//    public StageDecorationController(UndecoratorController uc){
//       undecoratorController  = uc;
//    }

    public StageDecorationController(Stage s) {
        stage = s;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Close button
        close.setTooltip(new Tooltip("Close"));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                UndecoratorController.close(stage);
            }
        });

        // Maximize button
        maximize.setTooltip(new Tooltip("Maximize"));
        maximize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                UndecoratorController.maximize(stage);
                
                Button source = (Button) t.getSource();
                Tooltip tooltip = source.getTooltip();
                if (tooltip.getText().equals("Maximize")){
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
                UndecoratorController.minimize(stage);
            }
        });
    }
}
