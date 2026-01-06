// ... existing code ...
package ensisa.curves.commands;

public interface UndoableCommand extends Command {
    void undo();

    default void redo() {
        execute();
    }
}
// ... existing code ...