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
    private Canvas redCanvas;
    
    @FXML
    private Canvas greenCanvas;
    
    @FXML
    private Canvas blueCanvas;
    
    @FXML
    private MenuItem initialStateMenu;
    
    @FXML
    private MenuItem yEqualsXMenu;

    private static final int NUM_POINTS = 8;
    private static final double CURVE_WIDTH = 340.0;
    private static final double CURVE_HEIGHT = 340.0;
    private static final double MAX_Y = 255.0;
    private static final double POINT_RADIUS = 5.0;
    private static final double MARGIN = POINT_RADIUS + 2.0; // Margin to ensure full points are visible

    // Control points for each color channel
    private double[] xPoints = new double[NUM_POINTS];
    private double[] redYPoints = new double[NUM_POINTS];
    private double[] greenYPoints = new double[NUM_POINTS];
    private double[] blueYPoints = new double[NUM_POINTS];
    
    // Track which point is being dragged and on which canvas
    private int draggingIndex = -1;
    private Canvas activeCanvas = null;

    @FXML
    public void initialize() {
        // Initialize X control points (same for all curves)
        for (int i = 0; i < NUM_POINTS; i++) {
            xPoints[i] = i * (CURVE_WIDTH / (NUM_POINTS - 1));
            redYPoints[i] = 128.0;   // Initial y value
            greenYPoints[i] = 128.0; // Initial y value
            blueYPoints[i] = 128.0;  // Initial y value
        }

        // Set up mouse event handlers for each canvas
        setupCanvasHandlers(redCanvas, redYPoints);
        setupCanvasHandlers(greenCanvas, greenYPoints);
        setupCanvasHandlers(blueCanvas, blueYPoints);

        drawAll();
    }

    private void setupCanvasHandlers(Canvas canvas, double[] yPoints) {
        canvas.setOnMousePressed(event -> handleMousePressed(event, canvas, yPoints));
        canvas.setOnMouseDragged(event -> handleMouseDragged(event, canvas, yPoints));
        canvas.setOnMouseReleased(event -> handleMouseReleased(event));
    }

    private void handleMousePressed(MouseEvent event, Canvas canvas, double[] yPoints) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        for (int i = 0; i < NUM_POINTS; i++) {
            double canvasX = curveXToCanvasX(xPoints[i]);
            double canvasY = valueToCanvasY(yPoints[i]);
            double dx = mouseX - canvasX;
            double dy = mouseY - canvasY;
            if (dx * dx + dy * dy <= POINT_RADIUS * POINT_RADIUS) {
                draggingIndex = i;
                activeCanvas = canvas;
                break;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event, Canvas canvas, double[] yPoints) {
        if (draggingIndex != -1 && activeCanvas == canvas) {
            double newY = canvasYToValue(event.getY());
            yPoints[draggingIndex] = Math.max(0.0, Math.min(MAX_Y, newY));
            drawCanvas(canvas, yPoints, getCanvasColor(canvas));
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        draggingIndex = -1;
        activeCanvas = null;
    }

    private Color getCanvasColor(Canvas canvas) {
        if (canvas == redCanvas) return Color.RED;
        if (canvas == greenCanvas) return Color.GREEN;
        if (canvas == blueCanvas) return Color.BLUE;
        return Color.BLACK;
    }

    private void drawAll() {
        drawCanvas(redCanvas, redYPoints, Color.RED);
        drawCanvas(greenCanvas, greenYPoints, Color.GREEN);
        drawCanvas(blueCanvas, blueYPoints, Color.BLUE);
    }

    private void drawCanvas(Canvas canvas, double[] yPoints, Color curveColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double canvasWidth = CURVE_WIDTH + 2 * MARGIN;
        double canvasHeight = CURVE_HEIGHT + 2 * MARGIN;
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Draw curve with clamped values (flat against boundaries when out of range)
        gc.setStroke(curveColor);
        gc.setLineWidth(2.0);
        gc.beginPath();
        for (double x = 0; x <= CURVE_WIDTH; x += 1.0) {
            double y = lagrangeInterpolate(x, yPoints);
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

        gc.stroke();

        // Draw control points
        gc.setFill(Color.GRAY);
        for (int i = 0; i < NUM_POINTS; i++) {
            double canvasX = curveXToCanvasX(xPoints[i]);
            double canvasY = valueToCanvasY(yPoints[i]);
            gc.fillOval(canvasX - POINT_RADIUS, canvasY - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
        }
    }

    private double lagrangeInterpolate(double x, double[] yPoints) {
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
    private void initialStateAction() {
        // Reset all curves to initial state (flat line at y = 128)
        for (int i = 0; i < NUM_POINTS; i++) {
            redYPoints[i] = 128.0;
            greenYPoints[i] = 128.0;
            blueYPoints[i] = 128.0;
        }
        drawAll();
    }
    
    @FXML
    private void yEqualsXAction() {
        // Set control points to create y = x curve for all color channels
        // Map X coordinates (0 to CURVE_WIDTH) to Y coordinates (0 to MAX_Y)
        for (int i = 0; i < NUM_POINTS; i++) {
            // y = (x / CURVE_WIDTH) * MAX_Y
            double yValue = (xPoints[i] / CURVE_WIDTH) * MAX_Y;
            redYPoints[i] = yValue;
            greenYPoints[i] = yValue;
            blueYPoints[i] = yValue;
        }
        drawAll();
    }
}
