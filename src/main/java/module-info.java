module com.example.videocutapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires kotlin.stdlib;


    opens com.example.videocutapp to javafx.fxml;
    exports com.example.videocutapp;
}
