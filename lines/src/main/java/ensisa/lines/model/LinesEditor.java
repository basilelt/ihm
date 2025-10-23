package ensisa.lines.model;

import javafx.scene.layout.Pane;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.shape.Line;

public class LinesEditor {
    private final Pane editorPane;
    private final Map<StraightLine, Line> lines;

    public LinesEditor(Pane editorPane) {
        this.editorPane = editorPane;
        lines = new HashMap<>();
    }

    private void bind(Line line, StraightLine straightLine) {
        line.startXProperty().bind(straightLine.startXProperty());
        line.startYProperty().bind(straightLine.startYProperty());
        line.endXProperty().bind(straightLine.endXProperty());
        line.endYProperty().bind(straightLine.endYProperty());
        line.strokeWidthProperty().bind(straightLine.strokeWidthProperty());
        line.strokeProperty().bind(straightLine.colorProperty());
    }

    public void createLine(StraightLine straightLine) {
        Line line = new Line();
        lines.put(straightLine, line);
        bind(line, straightLine);
        editorPane.getChildren().add(line);
    }

    public void removeLine(StraightLine straightLine) {
        Line line = lines.remove(straightLine);
        editorPane.getChildren().remove(line);
    }
}