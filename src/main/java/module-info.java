module com.example.jobseeker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.jobseeker to javafx.fxml;
    exports com.example.jobseeker;
}