package ensisa.lines.commands;

import java.util.*;

import ensisa.lines.*;
import ensisa.lines.model.*;

public class DeleteCommand implements UndoableCommand {
    private MainController mainController;
    private List<StraightLine> savedLines;
    private Set<StraightLine> savedSelectedLines;

    public DeleteCommand(MainController mainController) {
        this.mainController = mainController;
        savedLines = new ArrayList<>(mainController.getDocument().getLines());
        savedSelectedLines = new HashSet<>(mainController.getSelectedLines());
    }

    @Override
    public void execute() {
        mainController.deselectAll();
        mainController.getDocument().getLines().removeAll(savedSelectedLines);
    }

    @Override
    public void undo() {
        mainController.deselectAll();
        mainController.getDocument().getLines().clear();
        mainController.getDocument().getLines().addAll(savedLines);
        mainController.getSelectedLines().addAll(savedSelectedLines);
    }

}
