module com.example.mediaa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.base;


    opens com.example.mediaa to javafx.fxml;
    exports com.example.mediaa;
}