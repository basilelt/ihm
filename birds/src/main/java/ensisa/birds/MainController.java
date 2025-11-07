package ensisa.birds;

import ensisa.birds.model.Bird;
import ensisa.birds.model.BirdRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    private BirdRepository repository;

    @FXML
    private Label commonNameLabel;

    @FXML
    private Label latinNameLabel;

    public Bird currentBird;

    public MainController() {
        repository = new BirdRepository();
        repository.load();

        currentBird = repository.birds.get(0);
    }

    public void initialize() {
        commonNameLabel.textProperty().bind(currentBird.commonNameProperty());
        latinNameLabel.textProperty().bind(currentBird.latinNameProperty());
    }
}
