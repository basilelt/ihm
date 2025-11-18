package ensisa.birds.model;

import java.io.*;

import com.fasterxml.jackson.databind.*;
import javafx.collections.*;

public class BirdRepository {
    public ObservableList<Bird> birds;

    public BirdRepository() {
        birds = FXCollections.observableArrayList();
    }

    public void load() {
        try (InputStream inputStream = getClass().
                getResourceAsStream("/ensisa/birds/assets/Birds.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Birds.json not found");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var birdArray = mapper.readValue(inputStream, Bird[].class);
            birds.clear();
            birds.addAll(birdArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
