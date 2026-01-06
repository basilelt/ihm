// ... existing code ...
package ensisa.curves.commands;

import ensisa.curves.MainController;

public class CurveModificationCommand implements UndoableCommand {
    private final MainController controller;
    private final double[] targetArray;
    private final double[] oldValues;
    private final double[] newValues;

    public CurveModificationCommand(MainController controller, double[] targetArray, double[] oldValues,
            double[] newValues) {
        this.controller = controller;
        this.targetArray = targetArray;
        this.oldValues = oldValues.clone();
        this.newValues = newValues.clone();
    }

    @Override
    public void execute() {
        controller.setSingleCurve(targetArray, newValues);
    }

    @Override
    public void undo() {
        controller.setSingleCurve(targetArray, oldValues);
    }
}
// ... existing code ...