package ensisa.birds.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.*;

public class BirdRepository {
    public List<Bird> birds;

    public BirdRepository() {
        birds = new ArrayList<Bird>();
    }

    public void load() {
        try (InputStream inputStream = getClass().
                getResourceAsStream("/ensisa/birds/assets/Birds.json")) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var birdArray = mapper.readValue(inputStream, Bird[].class);
            birds = new ArrayList<Bird>(List.of(birdArray));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
