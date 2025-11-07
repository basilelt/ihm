package ensisa.birds;

import ensisa.birds.model.*;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class MainController {
    private BirdRepository repository;
    private final ObjectProperty<Bird> currentBird;

    @FXML
    private Label commonNameLabel;
    @FXML
    private Label latinNameLabel;
    @FXML
    private Label familyLabel;
    @FXML
    private Label genusLabel;
    @FXML
    private Label specieLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView birdImageView;
    @FXML
    private ListView<Bird> birdListView;

    public MainController() {
        repository = new BirdRepository();
        repository.load();

        currentBird = new SimpleObjectProperty<>(repository.birds.get(0));
    }

    public void bind(Bird bird) {
        commonNameLabel.textProperty().bind(bird.commonNameProperty());
        latinNameLabel.textProperty().bind(bird.latinNameProperty());
        familyLabel.textProperty().bind(bird.familyProperty());
        genusLabel.textProperty().bind(bird.genusProperty());
        specieLabel.textProperty().bind(bird.specieProperty());
        descriptionLabel.textProperty().bind(bird.descriptionProperty());
        birdImageView.imageProperty().bind(bird.imageProperty());
    }

    public void initialize() {
        bind(getCurrentBird());
        birdListView.setCellFactory(new BirdCellFactory());
        birdListView.setItems(repository.birds);
    }

    public ObjectProperty<Bird> currentBirdProperty() {
        return currentBird;
    }

    public Bird getCurrentBird() {
        return currentBird.get();
    }

    public void setCurrentBird(Bird bird) {
        currentBird.set(bird);
    }
}
