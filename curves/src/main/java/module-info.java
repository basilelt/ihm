module ensisa.curves {
    requires javafx.controls;
    requires javafx.fxml;


    opens ensisa.curves to javafx.fxml;
    exports ensisa.curves;
}