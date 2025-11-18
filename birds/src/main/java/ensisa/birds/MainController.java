package ensisa.birds;

import ensisa.birds.model.*;

import javafx.beans.property.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;
import java.util.function.*;

public class MainController {
    private BirdRepository repository;
    private final ObjectProperty<Bird> currentBird;
    private FilteredList<Bird> filteredBirdList;

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
    @FXML
    private VBox birdView;
    @FXML
    private Button editButton;

    @FXML
    private void editButtonAction(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        BirdEditDialog dialog = new BirdEditDialog(stage, getCurrentBird());
        dialog.showAndWait().ifPresent(bird -> {
            getCurrentBird().copyFrom(bird);
        });
    }

    @FXML
    private Button deleteButton;

    @FXML
    private void deleteButtonAction(ActionEvent event) {
        repository.birds.remove(getCurrentBird());
    }

    public MainController() {
        repository = new BirdRepository();
        repository.load();

        currentBird = new SimpleObjectProperty<>(repository.birds.get(0));
    }

    @FXML
    private TextField filterTextField;

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
        birdListView.setCellFactory(new BirdCellFactory());
        filteredBirdList = new FilteredList<>(repository.birds);
        birdListView.setItems(filteredBirdList);
        currentBirdProperty().bind(birdListView.getSelectionModel().selectedItemProperty());
        currentBirdProperty().addListener((observable, oldBird, newBird) -> {
            // Pour une liaison unidirectionnelle, il n'est pas nécessaire de supprimer
            // l'ancienne liaison avant d'en créer une nouvelle
            if (newBird != null)
                bind(newBird);
        });
        birdView.visibleProperty().bind(currentBirdProperty().isNotNull());
        // ou
        // birdView.visibleProperty().bind(Bindings.createBooleanBinding(
        // () -> getCurrentBird() != null, currentBirdProperty()) );
        editButton.disableProperty().bind(currentBirdProperty().isNull());
        deleteButton.disableProperty().bind(currentBirdProperty().isNull());
        filterTextField.textProperty().addListener((observable, oldText, newText) -> {
            Predicate<Bird> filter = bird -> {
                String f = newText.trim().toLowerCase();
                return f.isEmpty() || bird.getCommonName().toLowerCase().contains(f);
            };
            filteredBirdList.setPredicate(filter);
        });
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
