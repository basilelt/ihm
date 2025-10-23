package ensisa.lines;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import ensisa.lines.model.Document;

public class MainController {
    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    @FXML
    public Pane editorPane;

    private final Document document;

    public MainController() {
        document = new Document();
    }

    public Document getDocument() {
        return document;
    }
}
