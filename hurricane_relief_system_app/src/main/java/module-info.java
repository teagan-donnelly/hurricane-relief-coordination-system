module com.hurricane_relief_system {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.hurricane_relief_system to javafx.fxml;
    exports com.hurricane_relief_system;
}
