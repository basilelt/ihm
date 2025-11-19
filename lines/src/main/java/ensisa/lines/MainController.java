package ensisa.lines;

import javafx.scene.input.MouseEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ensisa.lines.tools.SelectTool;
import ensisa.lines.tools.Tool;
import ensisa.lines.model.DrawTool;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import ensisa.lines.model.Document;
import ensisa.lines.model.LinesEditor;
import ensisa.lines.model.StraightLine;
import javafx.collections.ListChangeListener;
import javafx.scene.shape.Rectangle;

public class MainController {
    @FXML
    public Pane editorPane;
    @FXML
    private RadioButton selectToolButton;
    @FXML
    private RadioButton drawToolButton;

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

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

    @FXML
    private void mouseEntered(MouseEvent event) {
        getCurrentTool().mouseEntered(event);
    }

    @FXML
    void mouseExited(MouseEvent event) {
        getCurrentTool().mouseExited(event);
    }

    @FXML
    private void selectToolAction() {
        setCurrentTool(selectTool);
    }

    @FXML
    private void drawToolAction() {
        setCurrentTool(drawTool);
    }

    private final Document document;
    private LinesEditor linesEditor;
    private final ObjectProperty<Tool> currentTool;
    private final DrawTool drawTool;
    private final SelectTool selectTool;

    public MainController() {
        document = new Document();
        selectTool = new SelectTool(this);
        drawTool = new DrawTool(this);
        currentTool = new SimpleObjectProperty<>(selectTool);
    }

    public LinesEditor getLinesEditor() {
        return linesEditor;
    }

    public Document getDocument() {
        return document;
    }

    public Tool getCurrentTool() {
        return currentTool.get();
    }

    public void setCurrentTool(Tool currentTool) {
        this.currentTool.set(currentTool);
    }

    public ObjectProperty<Tool> currentToolProperty() {
        return currentTool;
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

    private void setClipping() {
        final Rectangle clip = new Rectangle();
        editorPane.setClip(clip);
        editorPane.layoutBoundsProperty().addListener((v, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }

    private void initializeToolPalette() {
        // Change style class to not paint the round button
        selectToolButton.getStyleClass().remove("radio-button");
        selectToolButton.getStyleClass().add("toggle-button");
        drawToolButton.getStyleClass().remove("radio-button");
        drawToolButton.getStyleClass().add("toggle-button");
    }

    public void initialize() {
        linesEditor = new LinesEditor(editorPane);
        setClipping();
        initializeToolPalette();
        observeDocument();

        StraightLine l = new StraightLine();
        l.setStartX(10);
        l.setStartY(20);
        l.setEndX(300);
        l.setEndY(60);
        document.getLines().add(l);
    }
}
