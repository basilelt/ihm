// ... existing code ...
package ensisa.curves.commands;

import java.util.Stack;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UndoRedoHistory {
    private final Stack<UndoableCommand> undoStack;
    private final Stack<UndoableCommand> redoStack;
    private boolean inUndoRedo = false;
    private final BooleanProperty canUndo;
    private final BooleanProperty canRedo;

    public UndoRedoHistory() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        canUndo = new SimpleBooleanProperty(false);
        canRedo = new SimpleBooleanProperty(false);
    }

    public void execute(UndoableCommand command) {
        if (inUndoRedo) {
            throw new RuntimeException("Invoking execute within an undo/redo action.");
        }
        redoStack.clear();
        undoStack.push(command);
        canUndo.set(true);
        canRedo.set(false);
        command.execute();
    }

    public void undo() {
        if (undoStack.isEmpty())
            return;
        inUndoRedo = true;
        var top = undoStack.pop();
        top.undo();
        redoStack.push(top);
        inUndoRedo = false;
        canUndo.set(!undoStack.isEmpty());
        canRedo.set(true);
    }

    public void redo() {
        if (redoStack.isEmpty())
            return;
        inUndoRedo = true;
        var top = redoStack.pop();
        top.redo();
        undoStack.push(top);
        inUndoRedo = false;
        canUndo.set(true);
        canRedo.set(!redoStack.isEmpty());
    }

    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    public BooleanProperty canRedoProperty() {
        return canRedo;
    }
}
// ... existing code ...