module ensisa.birds {
    requires javafx.controls;
    requires javafx.fxml;

    opens ensisa.birds to javafx.fxml;
    exports ensisa.birds;
}