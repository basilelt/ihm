package ensisa.curves;

import java.io.File;

import ensisa.curves.commands.CurveModificationCommand;
import ensisa.curves.commands.GlobalCurveModificationCommand;
import ensisa.curves.commands.LoadImageCommand;
import ensisa.curves.commands.UndoRedoHistory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private MenuItem undoMenuItem;

    @FXML
    private MenuItem redoMenuItem;

    @FXML
    private ImageView imageView;

    private final UndoRedoHistory history = new UndoRedoHistory();
    private Image originalImage;
    private Image filteredImage;

    private static final int NUM_POINTS = 8;
    private static final double MAX_Y = 255.0;
    private static final double POINT_RADIUS = 5.0;
    private static final double MARGIN = POINT_RADIUS + 2.0; // Margin to ensure full points are visible

    // Dynamic curve dimensions (calculated from canvas size)
    private double curveWidth = 340.0;
    private double curveHeight = 340.0;

    // Control points for each color channel
    private double[] xPoints = new double[NUM_POINTS];
    private double[] redYPoints = new double[NUM_POINTS];
    private double[] greenYPoints = new double[NUM_POINTS];
    private double[] blueYPoints = new double[NUM_POINTS];

    // Track which point is being dragged and on which canvas
    private int draggingIndex = -1;
    private Canvas activeCanvas = null;
    private double[] pendingOldValues = null;
    private double[] pendingTargetArray = null;

    @FXML
    public void initialize() {
        // Initialize X control points (same for all curves)
        for (int i = 0; i < NUM_POINTS; i++) {
            xPoints[i] = i * (curveWidth / (NUM_POINTS - 1));
            redYPoints[i] = 128.0; // Initial y value
            greenYPoints[i] = 128.0; // Initial y value
            blueYPoints[i] = 128.0; // Initial y value
        }

        // Set up mouse event handlers for each canvas
        setupCanvasHandlers(redCanvas, redYPoints);
        setupCanvasHandlers(greenCanvas, greenYPoints);
        setupCanvasHandlers(blueCanvas, blueYPoints);

        undoMenuItem.disableProperty().bind(history.canUndoProperty().not());
        redoMenuItem.disableProperty().bind(history.canRedoProperty().not());

        // Set keyboard accelerators
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        // Add resize listeners to make canvases dynamic
        setupDynamicResizing();

        // Set initial canvas sizes
        Platform.runLater(() -> {
            initializeCanvasSizes();
            drawAll();
        });

        // Draw initial state
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
                pendingOldValues = yPoints.clone();
                pendingTargetArray = yPoints;
                break;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event, Canvas canvas, double[] yPoints) {
        if (draggingIndex != -1 && activeCanvas == canvas) {
            double newY = canvasYToValue(event.getY());
            yPoints[draggingIndex] = Math.max(0.0, Math.min(MAX_Y, newY));
            drawCanvas(canvas, yPoints, getCanvasColor(canvas));
            refreshFilteredImage();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (draggingIndex != -1 && pendingOldValues != null && pendingTargetArray != null) {
            double[] newValues = pendingTargetArray.clone();
            history.execute(new CurveModificationCommand(this, pendingTargetArray, pendingOldValues, newValues));
        }
        draggingIndex = -1;
        activeCanvas = null;
        pendingOldValues = null;
        pendingTargetArray = null;
    }

    private Color getCanvasColor(Canvas canvas) {
        if (canvas == redCanvas)
            return Color.RED;
        if (canvas == greenCanvas)
            return Color.GREEN;
        if (canvas == blueCanvas)
            return Color.BLUE;
        return Color.BLACK;
    }

    private void drawAll() {
        drawCanvas(redCanvas, redYPoints, Color.RED);
        drawCanvas(greenCanvas, greenYPoints, Color.GREEN);
        drawCanvas(blueCanvas, blueYPoints, Color.BLUE);
        refreshFilteredImage();
    }

    private void drawCanvas(Canvas canvas, double[] yPoints, Color curveColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Draw axes
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        // Y-axis (vertical)
        gc.strokeLine(MARGIN, MARGIN, MARGIN, MARGIN + curveHeight);
        // X-axis (horizontal)
        gc.strokeLine(MARGIN, MARGIN + curveHeight, MARGIN + curveWidth, MARGIN + curveHeight);

        // Draw curve with clamped values (flat against boundaries when out of range)
        gc.setStroke(curveColor);
        gc.setLineWidth(2.0);
        gc.beginPath();
        for (double x = 0; x <= curveWidth; x += 1.0) {
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
        return MARGIN + curveHeight - (value / MAX_Y) * curveHeight;
    }

    // Convert canvas Y coordinate to value (0-255)
    private double canvasYToValue(double canvasY) {
        // Inverse mapping
        return MAX_Y - ((canvasY - MARGIN) / curveHeight) * MAX_Y;
    }

    @FXML
    private void quitMenuAction() {
        Platform.exit();
    }

    @FXML
    private void initialStateAction() {
        double[] oldRed = redYPoints.clone();
        double[] oldGreen = greenYPoints.clone();
        double[] oldBlue = blueYPoints.clone();
        double[] newRed = new double[NUM_POINTS];
        double[] newGreen = new double[NUM_POINTS];
        double[] newBlue = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            newRed[i] = 128.0;
            newGreen[i] = 128.0;
            newBlue[i] = 128.0;
        }
        history.execute(new GlobalCurveModificationCommand(this, oldRed, oldGreen, oldBlue, newRed, newGreen, newBlue));
    }

    @FXML
    private void yEqualsXAction() {
        double[] oldRed = redYPoints.clone();
        double[] oldGreen = greenYPoints.clone();
        double[] oldBlue = blueYPoints.clone();
        double[] newRed = new double[NUM_POINTS];
        double[] newGreen = new double[NUM_POINTS];
        double[] newBlue = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            double yValue = (xPoints[i] / curveWidth) * MAX_Y;
            newRed[i] = yValue;
            newGreen[i] = yValue;
            newBlue[i] = yValue;
        }
        history.execute(new GlobalCurveModificationCommand(this, oldRed, oldGreen, oldBlue, newRed, newGreen, newBlue));
    }

    @FXML
    private void openImageAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir une image JPEG");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Images JPEG", "*.jpg", "*.jpeg", "*.JPG", "*.JPEG"));
        File selectedFile = fileChooser.showOpenDialog(redCanvas.getScene().getWindow());
        if (selectedFile != null) {
            Image newImage = new Image(selectedFile.toURI().toString());
            history.execute(new LoadImageCommand(this, originalImage, newImage));
        }
    }

    @FXML
    private void undoAction() {
        history.undo();
    }

    @FXML
    private void redoAction() {
        history.redo();
    }

    public void setSingleCurve(double[] targetArray, double[] values) {
        System.arraycopy(values, 0, targetArray, 0, NUM_POINTS);
        drawAll();
    }

    public void setCurves(double[] newRed, double[] newGreen, double[] newBlue) {
        System.arraycopy(newRed, 0, redYPoints, 0, NUM_POINTS);
        System.arraycopy(newGreen, 0, greenYPoints, 0, NUM_POINTS);
        System.arraycopy(newBlue, 0, blueYPoints, 0, NUM_POINTS);
        drawAll();
    }

    public void setOriginalImage(Image image) {
        this.originalImage = image;
        refreshFilteredImage();
    }

    private void refreshFilteredImage() {
        if (originalImage == null || imageView == null) {
            return;
        }
        filteredImage = applyCurves(originalImage);
        imageView.setImage(filteredImage);
    }

    private WritableImage applyCurves(Image srcImage) {
        int width = (int) srcImage.getWidth();
        int height = (int) srcImage.getHeight();
        WritableImage dstImage = new WritableImage(width, height);
        PixelReader reader = srcImage.getPixelReader();
        PixelWriter writer = dstImage.getPixelWriter();

        double[] redLut = buildLut(redYPoints);
        double[] greenLut = buildLut(greenYPoints);
        double[] blueLut = buildLut(blueYPoints);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                int nr = clampToByte(redLut[r]);
                int ng = clampToByte(greenLut[g]);
                int nb = clampToByte(blueLut[b]);

                int newArgb = (a << 24) | (nr << 16) | (ng << 8) | nb;
                writer.setArgb(x, y, newArgb);
            }
        }
        return dstImage;
    }

    private double[] buildLut(double[] yPoints) {
        double[] lut = new double[256];
        for (int i = 0; i < 256; i++) {
            double x = (i / 255.0) * curveWidth;
            double y = Math.max(0.0, Math.min(MAX_Y, lagrangeInterpolate(x, yPoints)));
            lut[i] = y;
        }
        return lut;
    }

    private int clampToByte(double value) {
        return (int) Math.max(0, Math.min(255, Math.round(value)));
    }

    private void setupDynamicResizing() {
        // Get the parent panes for each canvas
        Pane redPane = (Pane) redCanvas.getParent().getParent();
        Pane greenPane = (Pane) greenCanvas.getParent().getParent();
        Pane bluePane = (Pane) blueCanvas.getParent().getParent();

        // Add resize listeners
        redPane.widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(redCanvas, redPane));
        redPane.heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(redCanvas, redPane));

        greenPane.widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(greenCanvas, greenPane));
        greenPane.heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(greenCanvas, greenPane));

        bluePane.widthProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(blueCanvas, bluePane));
        bluePane.heightProperty().addListener((obs, oldVal, newVal) -> updateCanvasSize(blueCanvas, bluePane));

        // Add resize listener for image panel
        Pane imagePane = (Pane) imageView.getParent();
        imagePane.widthProperty().addListener((obs, oldVal, newVal) -> updateImageSize(imagePane));
        imagePane.heightProperty().addListener((obs, oldVal, newVal) -> updateImageSize(imagePane));
    }

    private void updateImageSize(Pane imagePane) {
        double paneWidth = imagePane.getWidth();
        double paneHeight = imagePane.getHeight();

        if (paneWidth > 0 && paneHeight > 0) {
            // Account for padding (10px on each side)
            double availableWidth = paneWidth - 20;
            double availableHeight = paneHeight - 20;

            imageView.setFitWidth(availableWidth);
            imageView.setFitHeight(availableHeight);
        }
    }

    private void updateCanvasSize(Canvas canvas, Pane parentPane) {
        double paneWidth = parentPane.getWidth();
        double paneHeight = parentPane.getHeight();

        if (paneWidth > 0 && paneHeight > 0) {
            // Update curve dimensions (leave margin for axes)
            curveWidth = paneWidth - 40; // 20px margin on each side
            curveHeight = paneHeight - 40; // 20px margin on each side

            // Update canvas size
            double canvasWidth = curveWidth + 2 * MARGIN;
            double canvasHeight = curveHeight + 2 * MARGIN;

            canvas.setWidth(canvasWidth);
            canvas.setHeight(canvasHeight);

            // Position canvas pane to align with axes
            Pane canvasPane = (Pane) canvas.getParent();
            canvasPane.setLayoutX(20 - MARGIN); // 20px margin - MARGIN offset
            canvasPane.setLayoutY(20 - MARGIN);

            // Update X control points
            for (int i = 0; i < NUM_POINTS; i++) {
                xPoints[i] = i * (curveWidth / (NUM_POINTS - 1));
            }

            // Redraw the canvas
            double[] yPoints = getYPointsForCanvas(canvas);
            drawCanvas(canvas, yPoints, getCanvasColor(canvas));
        }
    }

    private double[] getYPointsForCanvas(Canvas canvas) {
        if (canvas == redCanvas)
            return redYPoints;
        if (canvas == greenCanvas)
            return greenYPoints;
        if (canvas == blueCanvas)
            return blueYPoints;
        return new double[0];
    }

    private void initializeCanvasSizes() {
        Pane redPane = (Pane) redCanvas.getParent().getParent();
        Pane greenPane = (Pane) greenCanvas.getParent().getParent();
        Pane bluePane = (Pane) blueCanvas.getParent().getParent();

        updateCanvasSize(redCanvas, redPane);
        updateCanvasSize(greenCanvas, greenPane);
        updateCanvasSize(blueCanvas, bluePane);

        // Initialize image size
        Pane imagePane = (Pane) imageView.getParent();
        updateImageSize(imagePane);
    }
}
