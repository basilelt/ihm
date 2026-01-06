module ensisa.curves {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ensisa.curves to javafx.fxml;

    exports ensisa.curves;
}