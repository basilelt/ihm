package ensisa.curves;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MainController {
    @FXML
    private Canvas canvas;

    private static final int NUM_POINTS = 4;
    private static final double CANVAS_WIDTH = 340.0;
    private static final double CANVAS_HEIGHT = 340.0;
    private static final double MAX_Y = 255.0;
    private static final double POINT_RADIUS = 5.0;

    private double[] xPoints = new double[NUM_POINTS];
    private double[] yPoints = new double[NUM_POINTS];
    private int draggingIndex = -1;

    @FXML
    public void initialize() {
        // Initialize control points
        for (int i = 0; i < NUM_POINTS; i++) {
            xPoints[i] = i * (CANVAS_WIDTH / (NUM_POINTS - 1));
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
            double dx = mouseX - xPoints[i];
            double dy = mouseY - valueToCanvasY(yPoints[i]);
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
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw curve
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2.0);
        for (double x = 0; x < CANVAS_WIDTH; x += 1.0) {
            double y = lagrangeInterpolate(x);
            double canvasY = valueToCanvasY(y);
            if (x == 0) {
                gc.beginPath();
                gc.moveTo(x, canvasY);
            } else {
                gc.lineTo(x, canvasY);
            }
        }
        gc.stroke();

        // Draw control points
        gc.setFill(Color.GRAY);
        for (int i = 0; i < NUM_POINTS; i++) {
            double canvasY = valueToCanvasY(yPoints[i]);
            gc.fillOval(xPoints[i] - POINT_RADIUS, canvasY - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
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

    private double valueToCanvasY(double value) {
        // Map 0 -> CANVAS_HEIGHT, MAX_Y -> 0
        return CANVAS_HEIGHT - (value / MAX_Y) * CANVAS_HEIGHT;
    }

    private double canvasYToValue(double canvasY) {
        // Inverse mapping
        return MAX_Y - (canvasY / CANVAS_HEIGHT) * MAX_Y;
    }

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }
}
