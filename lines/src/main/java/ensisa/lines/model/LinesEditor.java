package ensisa.lines.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.HashMap;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class LinesEditor {
    private final Pane editorPane;
    private final Map<StraightLine, Line> lines;
    private final Map<StraightLine, Rectangle> startSelectionSquares;
    private final Map<StraightLine, Rectangle> endSelectionSquares;
    private final static int selectionSquareWidth = 6;

    public LinesEditor(Pane editorPane) {
        this.editorPane = editorPane;
        lines = new HashMap<>();
        startSelectionSquares = new HashMap<>();
        endSelectionSquares = new HashMap<>();
    }

    private void bind(Line line, StraightLine straightLine) {
        line.startXProperty().bind(straightLine.startXProperty());
        line.startYProperty().bind(straightLine.startYProperty());
        line.endXProperty().bind(straightLine.endXProperty());
        line.endYProperty().bind(straightLine.endYProperty());
        line.strokeWidthProperty().bind(straightLine.strokeWidthProperty());
        line.strokeProperty().bind(straightLine.colorProperty());
    }

    public void createLine(StraightLine straightLine) {
        Line line = new Line();
        lines.put(straightLine, line);
        bind(line, straightLine);
        editorPane.getChildren().add(line);
    }

    public void removeLine(StraightLine straightLine) {
        Line line = lines.remove(straightLine);
        editorPane.getChildren().remove(line);
    }

    // Crée un carré de sélection
    private Rectangle createSelectionSquare() {
        var square = new Rectangle();
        square.setWidth(selectionSquareWidth);
        square.setHeight(selectionSquareWidth);
        square.setFill(Color.WHITE);
        square.setStroke(Color.BLACK);
        return square;
    }

    // Lie le centre des carrés de sélection aux extrémités de la ligne
    private void bind(Rectangle startSelectionSquare, Rectangle endSelectionSquare, StraightLine straightLine) {
        startSelectionSquare.xProperty().bind(straightLine.startXProperty().subtract(selectionSquareWidth / 2));
        startSelectionSquare.yProperty().bind(straightLine.startYProperty().subtract(selectionSquareWidth / 2));
        endSelectionSquare.xProperty().bind(straightLine.endXProperty().subtract(selectionSquareWidth / 2));
        endSelectionSquare.yProperty().bind(straightLine.endYProperty().subtract(selectionSquareWidth / 2));
    }

    public void selectLine(StraightLine straightLine) {
        var startSelectionSquare = createSelectionSquare();
        var endSelectionSquare = createSelectionSquare();
        startSelectionSquares.put(straightLine, startSelectionSquare);
        endSelectionSquares.put(straightLine, endSelectionSquare);
        bind(startSelectionSquare, endSelectionSquare, straightLine);
        editorPane.getChildren().add(startSelectionSquare);
        editorPane.getChildren().add(endSelectionSquare);
    }

    public void deselectLine(StraightLine straightLine) {
        var selectionSquare = startSelectionSquares.get(straightLine);
        editorPane.getChildren().remove(selectionSquare);
        selectionSquare = endSelectionSquares.get(straightLine);
        editorPane.getChildren().remove(selectionSquare);
    }

    public boolean isPointInLine(double x, double y, StraightLine straightLine) {
        return squaredDistanceToSegment(x, y, straightLine.getStartX(), straightLine.getStartY(),
                straightLine.getEndX(), straightLine.getEndY()) < 16;
    }

    /**
     * return the squared distance between a point and a segment p point for which
     * the squared distance is evaluated ps start of segment pe end of segment
     */
    public static double squaredDistanceToSegment(double x, double y, double startX, double startY, double endX,
            double endY) {
        if (startX == endX && startY == endY)
            return squaredDistance(startX, startY, x, y);
        double sx = endX - startX;
        double sy = endY - startY;
        double ux = x - startX;
        double uy = y - startY;
        double dp = sx * ux + sy * uy;
        if (dp < 0)
            return squaredDistance(startX, startY, x, y);
        double sn2 = sx * sx + sy * sy;
        if (sn2 <= dp)
            return squaredDistance(endX, endY, x, y);
        double b = dp / sn2;
        return squaredDistance(startX + b * sx, startY + b * sy, x, y);
    }

    /**
     * return the squared distance between two points
     */
    public static double squaredDistance(double p1X, double p1Y, double p2X, double p2Y) {
        return (p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y);
    }

    public boolean isPointInStartSelectionSquare(double x, double y, StraightLine straightLine) {
        var selectionSquare = startSelectionSquares.get(straightLine);
        if (selectionSquare != null) {
            return selectionSquare.contains(x, y);
        }
        return false;
    }

    public boolean isPointInEndSelectionSquare(double x, double y, StraightLine straightLine) {
        var selectionSquare = endSelectionSquares.get(straightLine);
        if (selectionSquare != null) {
            return selectionSquare.contains(x, y);
        }
        return false;
    }
}