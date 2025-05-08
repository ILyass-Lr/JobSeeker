package com.example.jobseeker;
import javafx.geometry.Insets;
import javafx.scene.layout.*;

import java.sql.SQLException;


public abstract class Page extends VBox {
    protected Dashboard dashboard;

    public Page(Dashboard dashboard) throws SQLException {
        this.dashboard = dashboard;
        initialize();
        setPadding(new Insets(0, 0, 0, 80));
    }

    protected abstract void initialize() throws SQLException;

}