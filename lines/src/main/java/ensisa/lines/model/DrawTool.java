package ensisa.lines.model;
import ensisa.lines.tools.Tool;
import javafx.scene.input.MouseEvent;
import ensisa.lines.MainController;
import ensisa.lines.model.StraightLine;


public class DrawTool implements Tool {
    enum State {
        initial, drawing
    }

    private State state;
    private StraightLine currentLine;
    public MainController mainController;

    public DrawTool(MainController controller) {
        state = State.initial;
        this.mainController = controller;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            currentLine = new StraightLine();
            currentLine.setStartX(event.getX());
            currentLine.setStartY(event.getY());
            currentLine.setEndX(event.getX());
            currentLine.setEndY(event.getY());
            mainController.getDocument().getLines().add(currentLine);
            state = State.drawing;
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (state == State.drawing && event.isPrimaryButtonDown()) {
            currentLine.setEndX(event.getX());
            currentLine.setEndY(event.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        state = State.initial;
    }
}