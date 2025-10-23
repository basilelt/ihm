package ensisa.lines;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.application.Platform;

public class MainController {
    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }
}
