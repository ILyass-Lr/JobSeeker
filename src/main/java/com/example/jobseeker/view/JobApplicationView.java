//package com.example.jobseeker.view;
//
//import com.example.jobseeker.Dashboard;
//import com.example.jobseeker.model.JobOffer;
//import com.example.jobseeker.viewmodel.JobApplicationViewModel;
//
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.stage.FileChooser;
//import java.io.File;
//import java.util.Objects;
//
//public class JobApplicationPage extends VBox {
//    private final TextField emailField;
//    private final TextField contactField;
//    private final TextField profileField;
//    private final ComboBox<String> contractTypeComboBox;
//    private final ComboBox<String> placeComboBox;
//    private final ComboBox<String> teleworkComboBox;
//    private final Label fileNameLabel;
//    private File selectedCvFile;
//    
//
//    public JobApplicationPage(Dashboard dashboard, JobApplicationViewModel viewModel) {
////        this.dashboard = dashboard;
////        this.viewModel = viewModel;
//
//        // Setup main container
//        setAlignment(Pos.CENTER);
//        setPadding(new Insets(40));
//        setSpacing(20);
//        setBackground(new Background(new BackgroundFill(Color.web("#19181D"), CornerRadii.EMPTY, Insets.EMPTY)));
//
//        // Create application container
//        VBox applicationContainer = new VBox(25);
//        applicationContainer.setAlignment(Pos.CENTER);
//        applicationContainer.setPadding(new Insets(40));
//        applicationContainer.setMaxWidth(700);
//        applicationContainer.setMinHeight(500);
//        applicationContainer.setStyle("-fx-background-color: #17193B; -fx-background-radius: 10;");
//
//        // Title
//        Label titleLabel = new Label("Job Application");
//        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 28));
//        titleLabel.setTextFill(Color.WHITE);
//        titleLabel.setPadding(new Insets(0, 0, 20, 0));
//
//        // Email field with icon
//        HBox emailBox = createFieldWithIcon("com/registrationapp/students/demopage/email.png");
//        emailField = createStyledTextField("Enter your Email");
//        emailBox.getChildren().add(emailField);
//
//        // Contact field with icon
//        HBox contactBox = createFieldWithIcon("/com/example/jobseeker/phone.png");
//        contactField = createStyledTextField("Enter your Contact Number");
//        contactBox.getChildren().add(contactField);
//
//        // Profile field with icon
//        HBox profileBox = createFieldWithIcon("/com/example/jobseeker/profile.png");
//        profileField = createStyledTextField("Enter your Job Profile");
//        profileBox.getChildren().add(profileField);
//
//        // ComboBoxes row
//        HBox comboBoxRow = new HBox(15);
//        comboBoxRow.setAlignment(Pos.CENTER);
//
//        contractTypeComboBox = createStyledComboBox("Type of Contract");
//        contractTypeComboBox.getItems().addAll("Full-time", "Part-time", "Contract", "Internship");
//        if (jobOffer != null) {
//            contractTypeComboBox.setValue(jobOffer.getContractType());
//        }
//
//        placeComboBox = createStyledComboBox("Place");
//        placeComboBox.getItems().addAll("Casablanca", "Rabat", "Tangier", "Kenitra", "Temara", "Sale", "Marrakech");
//        if (jobOffer != null && jobOffer.getLocation() != null) {
//            placeComboBox.setValue(jobOffer.getLocation().getCity());
//        }
//
//        teleworkComboBox = createStyledComboBox("TeleWork");
//        teleworkComboBox.getItems().addAll("On-Site", "Remote", "Hybrid");
//        if (jobOffer != null) {
//            teleworkComboBox.setValue(jobOffer.getTeleWork());
//        }
//
//        comboBoxRow.getChildren().addAll(contractTypeComboBox, placeComboBox, teleworkComboBox);
//
//        // CV Upload section
//        HBox cvUploadBox = new HBox(30);
//        cvUploadBox.setAlignment(Pos.CENTER);
//
//        HBox cvLabelBox = createFieldWithIcon("/com/example/jobseeker/upload.png");
//        Label cvLabel = new Label("Uploading your CV");
//        cvLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 16));
//        cvLabel.setTextFill(Color.WHITE);
//        cvLabelBox.getChildren().add(cvLabel);
//
//        Button uploadButton = new Button("Upload CV");
//        uploadButton.getStyleClass().add("upload-button");
//        uploadButton.setPrefWidth(120);
//
//        fileNameLabel = new Label("No file selected");
//        fileNameLabel.setTextFill(Color.web("#cccccc"));
//        fileNameLabel.setFont(Font.font("Inter", 12));
//        fileNameLabel.setVisible(false);
//
//        uploadButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Select CV");
//            fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
//                    new FileChooser.ExtensionFilter("Word Documents", ".doc", ".docx")
//            );
//
//            File file = fileChooser.showOpenDialog(getScene().getWindow());
//            if (file != null) {
//                selectedCvFile = file;
//                fileNameLabel.setText(file.getName());
//                fileNameLabel.setVisible(true);
//            }
//        });
//
//        cvUploadBox.getChildren().addAll(cvLabelBox, uploadButton);
//
//        // Submit button
//        Button submitButton = new Button("Submit Application");
//        submitButton.getStyleClass().add("submit-button");
//        submitButton.setPrefWidth(200);
//        submitButton.setPrefHeight(50);
//
//        submitButton.setOnAction(e -> {
//            // if (validateForm()) {
//            // Process application
//            //    showConfirmationDialog();
//            // Return to job listings
//            //     dashboard.showJobOffers();
//            //  }
//        });
//
//        applicationContainer.getChildren().addAll(
//                titleLabel,
//                emailBox,
//                contactBox,
//                profileBox,
//                comboBoxRow,
//                cvUploadBox,
//                fileNameLabel,
//                submitButton
//        );
//
//        getChildren().add(applicationContainer);
//    }
//
//    private HBox createFieldWithIcon(String iconPath) {
//        HBox box = new HBox(15);
//        box.setAlignment(Pos.CENTER_LEFT);
//
//        try {
//            ImageView icon = new ImageView(new Image(Objects.requireNonNull(
//                    getClass().getResourceAsStream(iconPath))));
//            icon.setFitHeight(24);
//            icon.setFitWidth(24);
//            box.getChildren().add(icon);
//        } catch (Exception e) {
//            System.err.println("Could not load icon: " + iconPath);
//        }
//
//        return box;
//    }
//
//    private TextField createStyledTextField(String promptText) {
//        TextField textField = new TextField();
//        textField.setPromptText(promptText);
//        textField.setPrefHeight(40);
//        textField.setPrefWidth(400);
//        textField.getStyleClass().add("application-text-field");
//        return textField;
//    }
//
//    private ComboBox<String> createStyledComboBox(String promptText) {
//        ComboBox<String> comboBox = new ComboBox<>();
//        comboBox.setPromptText(promptText);
//        comboBox.setPrefWidth(200);
//        comboBox.setPrefHeight(40);
//        comboBox.getStyleClass().add("application-combo-box");
//        return comboBox;
//    }
//
//    private boolean validateForm() {
//        // Basic validation
//        if (emailField.getText().isEmpty() || !emailField.getText().contains("@")) {
//            showAlert("Please enter a valid email address.");
//            return false;
//        }
//
//        if (contactField.getText().isEmpty()) {
//            showAlert("Please enter your contact number.");
//            return false;
//        }
//
//        if (profileField.getText().isEmpty()) {
//            showAlert("Please enter your job profile.");
//            return false;
//        }
//
//        if (contractTypeComboBox.getValue() == null) {
//            showAlert("Please select a contract type.");
//            return false;
//        }
//
//        if (placeComboBox.getValue() == null) {
//            showAlert("Please select a location.");
//            return false;
//        }
//
//        if (teleworkComboBox.getValue() == null) {
//            showAlert("Please select a telework option.");
//            return false;
//        }
//
//        if (selectedCvFile == null) {
//            showAlert("Please upload your CV.");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void showAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Form Error");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    private void showConfirmationDialog() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Application Submitted");
//        alert.setHeaderText(null);
//        alert.setContentText("Your application has been submitted successfully!");
//        alert.showAndWait();
//}
//}