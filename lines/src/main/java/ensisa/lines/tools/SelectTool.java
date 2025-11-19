package ensisa.lines.tools;

import ensisa.lines.*;
import ensisa.lines.model.*;

import javafx.scene.input.*;

public class SelectTool implements Tool {
    enum State {
        initial, selection
    }

    private State state;
    private MainController mainController;
    private StraightLine straightLine;

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            state = State.selection;
            straightLine = mainController.findLineForPoint(event.getX(), event.getY());
            if (straightLine != null) {
                var isSelected = mainController.getSelectedLines().contains(straightLine);
                if (!event.isShiftDown()) {
                    if (!isSelected)
                        mainController.selectLine(straightLine, false);
                } else if (isSelected)
                    mainController.deselectLine(straightLine);
                else
                    mainController.selectLine(straightLine, true);
            } else {
                if (!event.isShiftDown())
                    mainController.deselectAll();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        state = State.initial;
    }

    public SelectTool(MainController controller) {
        this.mainController = controller;
        state = State.initial;
    }
}