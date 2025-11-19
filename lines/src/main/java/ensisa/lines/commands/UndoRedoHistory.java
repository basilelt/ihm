package ensisa.lines.commands;

import java.util.*;

import javafx.beans.property.*;

public class UndoRedoHistory {
    private Stack<UndoableCommand> undoStack;
    private Stack<UndoableCommand> redoStack;
    private boolean inUndoRedo;
    private final BooleanProperty canUndo;
    private final BooleanProperty canRedo;

    public UndoRedoHistory() {
        undoStack = new Stack<UndoableCommand>();
        redoStack = new Stack<UndoableCommand>();
        canUndo = new SimpleBooleanProperty(false);
        canRedo = new SimpleBooleanProperty(false);
    }

    public void undo() {
        inUndoRedo = true;
        var top = undoStack.pop();
        top.undo();
        redoStack.push(top);
        inUndoRedo = false;
        canUndo.set(!undoStack.isEmpty());
        canRedo.set(true);
    }

    public void redo() {
        inUndoRedo = true;
        var top = redoStack.pop();
        top.redo();
        undoStack.push(top);
        inUndoRedo = false;
        canUndo.set(true);
        canRedo.set(!redoStack.isEmpty());
    }

    public void execute(UndoableCommand command) {
        if (inUndoRedo)
            throw new RuntimeException("Invoking execute within an undo/redo action.");
        // On ne peut réaliser une nouvelle
        // opération pendant l'annulation ou le
        // rétablissement d'une autre
        redoStack.clear();
        // La pile de rétablissement est vidée
        // lorsqu’une nouvelle opération est
        // réalisée
        undoStack.push(command);
        canUndo.set(true);
        canRedo.set(false);
        command.execute();
    }

    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    public BooleanProperty canRedoProperty() {
        return canRedo;
    }

}
