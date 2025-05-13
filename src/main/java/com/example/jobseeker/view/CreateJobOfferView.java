package com.example.jobseeker.view;

import com.example.jobseeker.Dashboard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CreateJobOfferView extends VBox {
    private Dashboard dashboard;

    public CreateJobOfferView(Dashboard dashboard) {
        this.dashboard = dashboard;
        setBackground(new Background(new BackgroundFill(Color.web("#1e1e2f"), null, null)));
        setup();
    }



    public void setup() {
        // Main container with title
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20, 20, 20, 20)); // Extra left padding for sidebar
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Form title
        Text formTitle = new Text("CREATE JOB OFFER FORM");
        formTitle.setFont(Font.font("Inter", FontWeight.BOLD, 64));
        formTitle.setFill(Color.web("#EDEDED"));

        // --- Left Column: General Job Description ---
        VBox leftColumn = new VBox(15);
        leftColumn.setPadding(new Insets(20));
        leftColumn.getStyleClass().add("card");

        Label generalTitle = new Label("General Job Description");
        generalTitle.getStyleClass().add("section-title");

        GridPane generalGrid = new GridPane();
        generalGrid.setVgap(15);
        generalGrid.setHgap(15);

        // Create fields
        TextField jobTitle = new TextField();
        jobTitle.setPrefSize(424, 66);
        jobTitle.getStyleClass().add("application-text-field");

        ComboBox<String> contractType = new ComboBox<>();
        contractType.getItems().addAll("Internship", "Full-time", "Part-time","Contract");
        contractType.getStyleClass().add("filter");
        contractType.setValue("Internship");
        contractType.setPadding(new Insets(0, 10, 0, 10));

        ComboBox<String> workType = new ComboBox<>();
        workType.getStyleClass().add("filter");
        workType.setPadding(new Insets(0, 10, 0, 10));
        workType.getItems().addAll("Hybrid", "Remote", "On-Site");
        workType.setValue("Hybrid");

        TextArea postDescription = new TextArea();
        postDescription.setPromptText("Post Description");
        postDescription.setPrefRowCount(4); // Reduced size
        postDescription.setPrefColumnCount(30);

        TextArea postSpecification = new TextArea();
        postSpecification.setPromptText("Post Specification");
        postSpecification.setPrefRowCount(4); // Reduced size
        postSpecification.setPrefColumnCount(30);

        TextField numberOfP = new TextField();
        TextField salary = new TextField();
        DatePicker deadline = new DatePicker();

        // Add fields with labels on top
        int row = 0;

        Label jTitle = new Label("Job Title:");
        jTitle.getStyleClass().add("section-sub-title");
        // Job Title
        generalGrid.add(jTitle, 0, row);
        generalGrid.add(jobTitle, 0, row+1);

        Label cLabel = new Label("Contract Type:");
        cLabel.getStyleClass().add("section-sub-title");
        // Contract Type
        generalGrid.add(cLabel, 0, row+2);
        generalGrid.add(contractType, 0, row+3);

        Label wLabel = new Label("Work Type:");
        wLabel.getStyleClass().add("section-sub-title");
        // Work Type
        generalGrid.add(wLabel, 1, row + 2);
        generalGrid.add(workType, 1, row+3);

        // Next row
        row += 5;

        Label pLabel = new Label("Post Description:");
        pLabel.getStyleClass().add("section-sub-title");
        // Post Description
        generalGrid.add(pLabel, 0, row);
        generalGrid.add(postDescription, 0, row+1, 3, 1);

        // Next row
        row += 2;

        Label dLabel = new Label("Post Specification:");
        dLabel.getStyleClass().add("section-sub-title");
        // Post Specification
        generalGrid.add(dLabel, 0, row);
        generalGrid.add(postSpecification, 0, row+1, 3, 1);

        // Next row
        row += 2;

        Label nPLabel = new Label("Posts: ");
        nPLabel.getStyleClass().add("section-sub-title");
        // Number of P
        generalGrid.add(nPLabel, 0, row);
        generalGrid.add(numberOfP, 0, row+1);

        Label sLabel = new Label("Salary:");
        sLabel.getStyleClass().add("section-sub-title");
        // Salary
        generalGrid.add(sLabel, 1, row);
        generalGrid.add(salary, 1, row+1);

        Label deadlineLabel = new Label("Deadline: ");
        deadlineLabel.getStyleClass().add("section-sub-title");

        // Deadline
        generalGrid.add(deadlineLabel, 2, row);
        generalGrid.add(deadline, 2, row+1);

        Button postButton = new Button("Post the Job Offer");
        postButton.getStyleClass().add("primary-btn");
        postButton.setPrefWidth(200);
        postButton.setPrefHeight(40);
        postButton.setFont(Font.font("System", FontWeight.BOLD, 14));

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));
        buttonContainer.getChildren().add(postButton);

        leftColumn.getChildren().addAll(generalTitle, generalGrid, buttonContainer);

        // --- Right Column: Additional Specifications ---
        VBox rightColumn = new VBox(15);
        rightColumn.setPadding(new Insets(20));
        rightColumn.getStyleClass().add("card");

        Label additionalTitle = new Label("Additional specifications");
        additionalTitle.getStyleClass().add("section-title");

        // Location
        GridPane locationGrid = new GridPane();
        locationGrid.setHgap(15);
        locationGrid.setVgap(15);

        TextField countryField = new TextField();
        TextField regionField = new TextField();
        TextField cityField = new TextField();
        TextField addressField = new TextField();

        locationGrid.add(new Label("Country:"), 0, 0);
        locationGrid.add(countryField, 0, 1);
        locationGrid.add(new Label("Region:"), 1, 0);
        locationGrid.add(regionField, 1, 1);
        locationGrid.add(new Label("City:"), 0, 2);
        locationGrid.add(cityField, 0, 3);
        locationGrid.add(new Label("Address:"), 1, 2);
        locationGrid.add(addressField, 1, 3);

        TitledPane locationPane = new TitledPane("Location", locationGrid);

        // Education
        GridPane eduGrid = new GridPane();
        eduGrid.setHgap(15);
        eduGrid.setVgap(15);

        TextField educationField = new TextField();
        TextField levelField = new TextField();
        TextField diplomaField = new TextField();

        eduGrid.add(new Label("Education Field:"), 0, 0);
        eduGrid.add(educationField, 0, 1);
        eduGrid.add(new Label("Level:"), 1, 0);
        eduGrid.add(levelField, 1, 1);
        eduGrid.add(new Label("Diploma:"), 2, 0);
        eduGrid.add(diplomaField, 2, 1);

        TitledPane eduPane = new TitledPane("Education Requirements", eduGrid);

        // Experience
        GridPane expGrid = new GridPane();
        expGrid.setHgap(15);
        expGrid.setVgap(15);

        TextField minYearsField = new TextField();
        TextField maxYearsField = new TextField();
        TextField expLevelField = new TextField();
        TextArea expDescField = new TextArea();
        expDescField.setPrefRowCount(3);

        expGrid.add(new Label("Min Years:"), 0, 0);
        expGrid.add(minYearsField, 0, 1);
        expGrid.add(new Label("Max Years:"), 1, 0);
        expGrid.add(maxYearsField, 1, 1);
        expGrid.add(new Label("Level:"), 2, 0);
        expGrid.add(expLevelField, 2, 1);
        expGrid.add(new Label("Description:"), 0, 2);
        expGrid.add(expDescField, 0, 3, 3, 1);

        TitledPane expPane = new TitledPane("Experience Requirements", expGrid);

        // Skills - Changed TextField to TextArea for Hard and Soft Skills
        GridPane skillsGrid = new GridPane();
        skillsGrid.setHgap(15);
        skillsGrid.setVgap(15);

        TextField language1Field = new TextField("English");
        ComboBox<String> proficiency1 = new ComboBox<>();
        proficiency1.getItems().addAll("Basic", "Intermediate", "Advanced", "Fluent");

        TextField language2Field = new TextField("French");
        ComboBox<String> proficiency2 = new ComboBox<>();
        proficiency2.getItems().addAll("Basic", "Intermediate", "Advanced", "Fluent");

        TextArea hardSkills = new TextArea();
        hardSkills.setPrefRowCount(3);
        hardSkills.setPrefColumnCount(20);

        TextArea softSkills = new TextArea();
        softSkills.setPrefRowCount(3);
        softSkills.setPrefColumnCount(20);

        skillsGrid.add(new Label("Language 1:"), 0, 0);
        skillsGrid.add(language1Field, 0, 1);
        skillsGrid.add(new Label("Proficiency:"), 1, 0);
        skillsGrid.add(proficiency1, 1, 1);

        skillsGrid.add(new Label("Language 2:"), 0, 2);
        skillsGrid.add(language2Field, 0, 3);
        skillsGrid.add(new Label("Proficiency:"), 1, 2);
        skillsGrid.add(proficiency2, 1, 3);

        skillsGrid.add(new Label("Hard Skills:"), 0, 4);
        skillsGrid.add(hardSkills, 0, 5, 2, 1);

        skillsGrid.add(new Label("Soft Skills:"), 0, 6);
        skillsGrid.add(softSkills, 0, 7, 2, 1);

        TitledPane skillsPane = new TitledPane("Skills Requirements", skillsGrid);

        rightColumn.getChildren().addAll(additionalTitle, locationPane, eduPane, expPane, skillsPane);

        // Layout Container
        HBox layout = new HBox(20, leftColumn, rightColumn);
        layout.setAlignment(Pos.CENTER);

        // Add all components to the main container
        mainContainer.getChildren().addAll(formTitle, layout);

        // Add main container to the CreateJobOfferView
        getChildren().add(mainContainer);

        // recupérer les donnees


    }
}