module ihm {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens ihm to javafx.fxml;
    exports ihm;
}
