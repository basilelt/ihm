package ensisa.curves;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;

import java.io.*;

public class CurvesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CurvesApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("TP 2025");
        stage.setScene(scene);
        stage.show();
    }
}
