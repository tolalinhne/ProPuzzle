module ProPuzzle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.swing;
    requires jlatexmath;
    requires java.desktop;
    requires javafx.web;


    opens Controller to javafx.fxml;
    opens Model to javafx.base, com.google.gson;
    exports Controller;

}