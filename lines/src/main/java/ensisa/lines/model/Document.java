package ensisa.lines.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Document {
    private final ObservableList<StraightLine> lines;

    public Document() {
        lines = FXCollections.observableArrayList();
    }

    public ObservableList<StraightLine> getLines() {
        return lines;
    }
}