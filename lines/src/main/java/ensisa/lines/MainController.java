package ensisa.lines;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.application.Platform;
import ensisa.lines.model.Document;

public class MainController {
    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    private final Document document;

    public MainController() {
        document = new Document();
    }

    public Document getDocument() {
        return document;
    }
}
