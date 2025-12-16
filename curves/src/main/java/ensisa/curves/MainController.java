package ensisa.curves;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MainController {
    @FXML
    private Canvas canvas;
    
    @FXML
    private MenuItem yEqualsXMenu;

    private static final int NUM_POINTS = 8;
    private static final double CURVE_WIDTH = 340.0;
    private static final double CURVE_HEIGHT = 340.0;
    private static final double MAX_Y = 255.0;
    private static final double POINT_RADIUS = 5.0;
    private static final double MARGIN = POINT_RADIUS + 2.0; // Margin to ensure full points are visible

    private double[] xPoints = new double[NUM_POINTS];
    private double[] yPoints = new double[NUM_POINTS];
    private int draggingIndex = -1;

    @FXML
    public void initialize() {
        // Initialize control points (in curve coordinates, 0 to CURVE_WIDTH)
        for (int i = 0; i < NUM_POINTS; i++) {
            xPoints[i] = i * (CURVE_WIDTH / (NUM_POINTS - 1));
            yPoints[i] = 128.0; // Initial y value
        }

        // Set up mouse event handlers
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);

        draw();
    }

    private void handleMousePressed(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        for (int i = 0; i < NUM_POINTS; i++) {
            double canvasX = curveXToCanvasX(xPoints[i]);
            double canvasY = valueToCanvasY(yPoints[i]);
            double dx = mouseX - canvasX;
            double dy = mouseY - canvasY;
            if (dx * dx + dy * dy <= POINT_RADIUS * POINT_RADIUS) {
                draggingIndex = i;
                break;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (draggingIndex != -1) {
            double newY = canvasYToValue(event.getY());
            yPoints[draggingIndex] = Math.max(0.0, Math.min(MAX_Y, newY));
            draw();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        draggingIndex = -1;
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double canvasWidth = CURVE_WIDTH + 2 * MARGIN;
        double canvasHeight = CURVE_HEIGHT + 2 * MARGIN;
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Draw curve with clamped values (flat against boundaries when out of range)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.beginPath();
        for (double x = 0; x <= CURVE_WIDTH; x += 1.0) {
            double y = lagrangeInterpolate(x);
            // Clamp y to [0, MAX_Y] range - this makes curve flat against boundaries
            y = Math.max(0.0, Math.min(MAX_Y, y));
            double canvasX = curveXToCanvasX(x);
            double canvasY = valueToCanvasY(y);
            if (x == 0) {
                gc.moveTo(canvasX, canvasY);
            } else {
                gc.lineTo(canvasX, canvasY);
            }
        }
        gc.stroke();

        // Draw control points
        gc.setFill(Color.GRAY);
        for (int i = 0; i < NUM_POINTS; i++) {
            double canvasX = curveXToCanvasX(xPoints[i]);
            double canvasY = valueToCanvasY(yPoints[i]);
            gc.fillOval(canvasX - POINT_RADIUS, canvasY - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
        }
    }

    private double lagrangeInterpolate(double x) {
        double result = 0.0;
        for (int i = 0; i < NUM_POINTS; i++) {
            double li = 1.0;
            for (int j = 0; j < NUM_POINTS; j++) {
                if (i != j) {
                    li *= (x - xPoints[j]) / (xPoints[i] - xPoints[j]);
                }
            }
            result += yPoints[i] * li;
        }
        return result;
    }

    // Convert curve X coordinate to canvas X coordinate (add left margin)
    private double curveXToCanvasX(double curveX) {
        return curveX + MARGIN;
    }

    // Convert canvas X coordinate to curve X coordinate (remove left margin)
    private double canvasXToCurveX(double canvasX) {
        return canvasX - MARGIN;
    }

    // Convert value (0-255) to canvas Y coordinate (with top margin)
    private double valueToCanvasY(double value) {
        // Map MAX_Y -> MARGIN (top), 0 -> CURVE_HEIGHT + MARGIN (bottom)
        return MARGIN + CURVE_HEIGHT - (value / MAX_Y) * CURVE_HEIGHT;
    }

    // Convert canvas Y coordinate to value (0-255)
    private double canvasYToValue(double canvasY) {
        // Inverse mapping
        return MAX_Y - ((canvasY - MARGIN) / CURVE_HEIGHT) * MAX_Y;
    }

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }
    
    @FXML
    private void yEqualsXAction() {
        // Set control points to create y = x curve
        // Map X coordinates (0 to CURVE_WIDTH) to Y coordinates (0 to MAX_Y)
        for (int i = 0; i < NUM_POINTS; i++) {
            // y = (x / CURVE_WIDTH) * MAX_Y
            yPoints[i] = (xPoints[i] / CURVE_WIDTH) * MAX_Y;
        }
        draw();
    }
}
