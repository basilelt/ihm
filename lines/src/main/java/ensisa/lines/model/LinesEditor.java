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
}