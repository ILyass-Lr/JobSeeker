module com.example.jobseeker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.jobseeker to javafx.fxml;
    exports com.example.jobseeker;
    exports com.example.jobseeker.model;
    opens com.example.jobseeker.model to javafx.fxml;
    exports com.example.jobseeker.view;
    opens com.example.jobseeker.view to javafx.fxml;
    exports com.example.jobseeker.util;
    opens com.example.jobseeker.util to javafx.fxml;
}