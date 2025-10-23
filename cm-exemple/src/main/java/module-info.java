module ensisa.ihm {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens ensisa.ihm to javafx.fxml;

    exports ensisa.ihm;
}
