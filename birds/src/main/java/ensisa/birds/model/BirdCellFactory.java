package ensisa.birds.model;

import java.io.IOException;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

public class BirdCellFactory implements Callback<ListView<Bird>, ListCell<Bird>> {
    @Override
    public ListCell<Bird> call(ListView<Bird> param) {
        return new ListCell<>() {
            @FXML
            private VBox vBox;
            @FXML
            private Label commonNameLabel;
            @FXML
            private Label latinNameLabel;
            private FXMLLoader loader;

            public void updateItem(Bird bird, boolean empty) {
                super.updateItem(bird, empty);
                if (empty || bird == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (loader == null) {
                        loader = new FXMLLoader(getClass().getResource("/ensisa/birds/bird-cell.fxml"));
                        loader.setController(this);
                        try {
                            loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    commonNameLabel.textProperty().bind(bird.commonNameProperty());
                    latinNameLabel.textProperty().bind(bird.latinNameProperty());

                    setText(null);
                    setGraphic(vBox);
                }
            };
        };
    }

}
