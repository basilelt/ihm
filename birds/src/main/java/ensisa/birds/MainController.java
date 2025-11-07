package ensisa.birds;

import ensisa.birds.model.Bird;
import ensisa.birds.model.BirdRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class MainController {
    private BirdRepository repository;
    public Bird currentBird;

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

    public MainController() {
        repository = new BirdRepository();
        repository.load();

        currentBird = repository.birds.get(0);
    }

    public void initialize() {
        commonNameLabel.textProperty().bind(currentBird.commonNameProperty());
        latinNameLabel.textProperty().bind(currentBird.latinNameProperty());
        familyLabel.textProperty().bind(currentBird.familyProperty());
        genusLabel.textProperty().bind(currentBird.genusProperty());
        specieLabel.textProperty().bind(currentBird.specieProperty());
        descriptionLabel.textProperty().bind(currentBird.descriptionProperty());
    }
}
