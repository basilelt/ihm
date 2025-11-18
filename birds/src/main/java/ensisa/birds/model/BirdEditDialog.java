package ensisa.birds.model;

import java.io.IOException;
import java.util.Objects;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class BirdEditDialog extends Dialog<Bird> {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    private Bird editedBird;

    public BirdEditDialog(Window owner, Bird bird) {
        try {
            editedBird = new Bird();
            editedBird.copyFrom(bird);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ensisa/birds/editor-pane.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(true);
            setTitle("Edition de " + editedBird.getCommonName());
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> {
                if (!Objects.equals(ButtonBar.ButtonData.APPLY, buttonType.getButtonData())) {
                    return null;
                }

                return editedBird;
            });

            setOnShowing(dialogEvent -> Platform.runLater(() -> nameTextField.requestFocus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        nameTextField.textProperty().bindBidirectional(editedBird.commonNameProperty());
        descriptionTextArea.textProperty().bindBidirectional(editedBird.descriptionProperty());
    }
}
