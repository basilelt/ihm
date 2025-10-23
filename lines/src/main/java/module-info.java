module ensisa.lines {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;


    opens ensisa.lines to javafx.fxml;
    exports ensisa.lines;
}