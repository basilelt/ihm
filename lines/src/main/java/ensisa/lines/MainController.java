package ensisa.lines;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ensisa.lines.tools.Tool;
import ensisa.lines.model.DrawTool;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import ensisa.lines.model.Document;
import ensisa.lines.model.LinesEditor;
import ensisa.lines.model.StraightLine;
import javafx.collections.ListChangeListener;

public class MainController {
    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    @FXML
    public Pane editorPane;

    @FXML
    private void mousePressedInEditor(MouseEvent event) {
        getCurrentTool().mousePressed(event);
    }

    @FXML
    private void mouseDraggedInEditor(MouseEvent event) {
        getCurrentTool().mouseDragged(event);
    }

    @FXML
    private void mouseReleasedInEditor(MouseEvent event) {
        getCurrentTool().mouseReleased(event);
    }

    private final Document document;
    private LinesEditor linesEditor;
    private final ObjectProperty<Tool> currentTool;

    public MainController() {
        document = new Document();
        currentTool = new SimpleObjectProperty<>(new DrawTool(this));
    }

    public ObjectProperty<Tool> currentToolProperty() {
        return currentTool;
    }

    public Tool getCurrentTool() {
        return currentTool.get();
    }

    public void setCurrentTool(Tool currentTool) {
        this.currentTool.set(currentTool);
    }

    public void initialize() {
        linesEditor = new LinesEditor(editorPane);
        observeDocument();

        StraightLine l = new StraightLine();
        l.setStartX(10);
        l.setStartY(20);
        l.setEndX(300);
        l.setEndY(60);
        document.getLines().add(l);
    }

    private void observeDocument() {
        document.getLines().addListener(new ListChangeListener<StraightLine>() {
            public void onChanged(ListChangeListener.Change<? extends StraightLine> c) {
                while (c.next()) {
                    // Des lignes ont été supprimées du modèle
                    for (StraightLine line : c.getRemoved()) {
                        linesEditor.removeLine(line);
                    }
                    // Des lignes ont été ajoutées au modèle
                    for (StraightLine line : c.getAddedSubList()) {
                        linesEditor.createLine(line);
                    }
                }
            }
        });
    }

    public LinesEditor getLinesEditor() {
        return linesEditor;
    }

    public Document getDocument() {
        return document;
    }
}
