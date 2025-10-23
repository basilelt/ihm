module ensisa.cmexemple {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens ensisa.cmexemple to javafx.fxml;

    exports ensisa.cmexemple;
}
