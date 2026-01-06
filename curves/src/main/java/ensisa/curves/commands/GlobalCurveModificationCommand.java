// ... existing code ...
package ensisa.curves.commands;

import ensisa.curves.MainController;

public class GlobalCurveModificationCommand implements UndoableCommand {
    private final MainController controller;
    private final double[] oldRed;
    private final double[] oldGreen;
    private final double[] oldBlue;
    private final double[] newRed;
    private final double[] newGreen;
    private final double[] newBlue;

    public GlobalCurveModificationCommand(MainController controller, double[] oldRed, double[] oldGreen,
            double[] oldBlue, double[] newRed, double[] newGreen, double[] newBlue) {
        this.controller = controller;
        this.oldRed = oldRed.clone();
        this.oldGreen = oldGreen.clone();
        this.oldBlue = oldBlue.clone();
        this.newRed = newRed.clone();
        this.newGreen = newGreen.clone();
        this.newBlue = newBlue.clone();
    }

    @Override
    public void execute() {
        controller.setCurves(newRed, newGreen, newBlue);
    }

    @Override
    public void undo() {
        controller.setCurves(oldRed, oldGreen, oldBlue);
    }
}
// ... existing code ...