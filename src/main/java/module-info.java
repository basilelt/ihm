module ihm {
    requires javafx.controls;
    requires javafx.fxml;

    opens ihm to javafx.fxml;
    exports ihm;
}
