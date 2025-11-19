package ensisa.lines;

import ensisa.lines.tools.*;
import ensisa.lines.model.*;

import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.application.*;
import javafx.collections.*;

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
    private final ObservableSet<StraightLine> selectedLines;

    public MainController() {
        document = new Document();
        selectTool = new SelectTool(this);
        drawTool = new DrawTool(this);
        currentTool = new SimpleObjectProperty<>(selectTool);
        selectedLines = FXCollections.observableSet();
    }

    public ObjectProperty<Tool> currentToolProperty() {
        return currentTool;
    }

    public ObservableSet<StraightLine> getSelectedLines() {
        return selectedLines;
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

    private void observeDocument() {
        document.getLines().addListener(new ListChangeListener<StraightLine>() {
            public void onChanged(ListChangeListener.Change<? extends StraightLine> c) {
                while (c.next()) {
                    // Des lignes ont été supprimées du modèle
                    for (StraightLine line : c.getRemoved()) {
                        deselectLine(line);
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

    public void selectLine(StraightLine line, boolean keepSelection) {
        if (!keepSelection)
            getSelectedLines().clear();
        getSelectedLines().add(line);
    }

    public void deselectLine(StraightLine line) {
        getSelectedLines().remove(line);
    }

    public void deselectAll() {
        getSelectedLines().clear();
    }

    public StraightLine findLineForPoint(double x, double y) {
        for (var straightLine : getDocument().getLines()) {
            if (linesEditor.isPointInStartSelectionSquare(x, y, straightLine)
                    || linesEditor.isPointInEndSelectionSquare(x, y, straightLine)
                    || linesEditor.isPointInLine(x, y, straightLine))
                return straightLine;
        }
        return null;
    }

    private void observeSelection() {
        selectedLines.addListener(new SetChangeListener<StraightLine>() {
            @Override
            public void onChanged(Change<? extends StraightLine> change) {
                if (change.wasRemoved()) {
                    linesEditor.deselectLine(change.getElementRemoved());
                }
                if (change.wasAdded()) {
                    linesEditor.selectLine(change.getElementAdded());
                }
            }
        });
    }

    public void initialize() {
        linesEditor = new LinesEditor(editorPane);
        setClipping();
        initializeToolPalette();
        observeDocument();
        observeSelection();

        StraightLine l = new StraightLine();
        l.setStartX(10);
        l.setStartY(20);
        l.setEndX(300);
        l.setEndY(60);
        document.getLines().add(l);
    }
}
