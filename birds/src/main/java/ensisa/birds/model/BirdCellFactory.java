package ensisa.birds.model;

import javafx.scene.control.*;
import javafx.util.Callback;

public class BirdCellFactory implements Callback<ListView<Bird>, ListCell<Bird>> 
{ 
    @Override 
    public ListCell<Bird> call(ListView<Bird> param) { 
        return new ListCell<>(){ 
            public void updateItem(Bird bird, boolean empty) { 
                super.updateItem(bird, empty); 
                if (empty || bird == null) { 
                    setText(null); 
                } else { 
                    setText(bird.getCommonName()); 
                } 
            }; 
        }; 
    } 
     
}
