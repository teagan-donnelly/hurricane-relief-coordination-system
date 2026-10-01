module com.hurricane_relief_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.hurricane_relief_system to javafx.fxml;
    exports com.hurricane_relief_system;

    opens com.model to javafx.fxml;
    exports com.model;
}
