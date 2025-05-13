package com.example.jobseeker.view;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.JobApplication;
import com.example.jobseeker.model.JobOffer;

import com.example.jobseeker.viewmodel.JobApplicationViewModel;
import com.example.jobseeker.viewmodel.JobOfferViewModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JobListingView extends JobOffersList {


    private List<JobOffer> filteredOffers;
    public JobListingView(Dashboard dashboard, JobOfferViewModel jobOfferViewModel, JobApplicationViewModel jobApplicationViewModel) throws SQLException {
        super("Your Company doesn't have any job Posts yet!.", jobOfferViewModel, dashboard, jobApplicationViewModel);
        refreshBookmarkedJobs();

    }

    @Override
    protected void initializeData() throws SQLException {
        // Initially get saved job offers
        if (dashboard.getCurrentUser() != null){
            System.out.println("USER IS RECRUITER");
            viewModel.searchJobOffersByRecruiter(dashboard.getCurrentUser().getId());
            filteredOffers = viewModel.getJobOffers();
        }else{
            filteredOffers = new ArrayList<JobOffer>();
        }

    }

    @Override
    public void initialize() throws SQLException {
        //System.out.println("This is a JobListingView");
        initializeData();
        HBox mainContent = new HBox(14, listingContainer, detailsContainer);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxHeight(950);
        setBackground(new Background(new BackgroundFill(Color.web("#19181D"), CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().add(mainContent);

        if (filteredOffers.isEmpty()) {
            showNoJobOffersMessage(message);
        } else {
            updateJobListings();
            updateJobDetails();
        }
    }
    @Override  // A SUPPRIMER
    protected void handleSaveButton(Button save, Button bookmarkIcon) throws SQLException {

    }

    @Override
    protected void updateJobListings() {
        if (listingVBox == null) return;

        Platform.runLater(() -> {

            listingVBox.getChildren().clear();

            if (viewModel.getJobOffers() == null || viewModel.getJobOffers().isEmpty()) {

                showNoJobOffersMessage(message);
                return;
            }
            int startIdx = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, viewModel.getJobOffers().size());
            if (startIdx >= viewModel.getJobOffers().size()) {
                currentPage = 1;
                startIdx = 0;
                endIdx = Math.min(ITEMS_PER_PAGE, viewModel.getJobOffers().size());
                updatePaginationControls();
            }
            for (int i = startIdx; i < endIdx; i++) {
                JobOffer offer = viewModel.getJobOffers().get(i);
                VBox jobCard = null;
                try {
                    jobCard = createJobCard(offer);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (offer.equals(viewModel.getSelectedJobOffer())) {
                    jobCard.getStyleClass().clear();
                    jobCard.getStyleClass().add("job-card-selected");
                    selectedCard = jobCard;
                }
                listingVBox.getChildren().add(jobCard);
            }
        });
    }



    public void refreshBookmarkedJobs() throws SQLException {
        // Get a fresh list of saved job offers from the database
        //viewModel.loadSavedJobOffers();
        initialize();
        //filteredOffers = viewModel.getSavedJobOffers();

        if (filteredOffers.isEmpty()) {
            selectedCard = null;
            showNoJobOffersMessage(message);
        } else {
            // Select the first offer if available
            viewModel.selectJobOffer(filteredOffers.getFirst());
            currentPage = 1;
            updateJobListings();
            updatePaginationControls();
            updateJobDetails();
        }
    }

    @Override
    public  void updateJobDetailsForApplication() throws SQLException {
        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        errorMessageLabel.setPrefHeight(30);
        errorMessageLabel.setFont(Font.font("Inter", 20));
        errorMessageLabel.setAlignment(Pos.CENTER);


        if (viewModel.getSelectedJobOffer() == null) return;

        detailsContainer.getChildren().clear();

        // Back button
        Button backButton = new Button();
        backButton.setPrefSize(45, 45);
        backButton.getStyleClass().add("back-button");

        // Use a placeholder for the back icon - you'll need to replace with your icon
        ImageView backIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Back.png")).toExternalForm()));
        backIcon.setFitHeight(24);
        backIcon.setFitWidth(24);
        backIcon.setPreserveRatio(true);

        backButton.setGraphic(backIcon);
        backButton.setOnAction(_ -> {
            try {
                updateJobDetails();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Job title
        Label titleLabel = new Label(viewModel.getSelectedJobOffer().getTitle());
        titleLabel.getStyleClass().add("details-title");

        HBox backButton_Title = new HBox(15);
        backButton_Title.setAlignment(Pos.CENTER_LEFT);
        backButton_Title.getChildren().addAll(backButton, titleLabel);


        // Company name
        Label companyLabel = new Label(viewModel.getSelectedJobOffer().getCompany());
        companyLabel.getStyleClass().add("details-company");

        // Location
        Label locationLabel = new Label(viewModel.getSelectedJobOffer().getLocation().getCity());
        locationLabel.getStyleClass().add("details-location");

        // Job SUBMISSIONS title
        Label applicationTitleLabel = new Label("Job Submissions:");
        applicationTitleLabel.getStyleClass().add("application-title-label");


        detailsContainer.getChildren().addAll(
                backButton_Title,
                companyLabel,
                locationLabel,
                applicationTitleLabel);
        for(JobApplication jobApplication : jobApplicationViewModel.getApplications(viewModel.getSelectedJobOffer().getId())){
            detailsContainer.getChildren().add(createApplicationCard(jobApplicationViewModel.getUserEmail(jobApplication.getCandidateId()), jobApplication));
        }

        }


    public VBox createApplicationCard(String email, JobApplication jobApplication) {
        VBox card = new VBox(15);
        card.setPrefSize(506, 219);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-radius: 15; -fx-background-color: linear-gradient(to bottom right, #c67ff3, #9b64f3);");

        // Full Name and Email
//        HBox infoBox = new HBox(40);
//        VBox nameBox = new VBox(4);
//        Label nameLabel = new Label("Full Name:");
//        nameLabel.setFont(Font.font("Inter", FontWeight.BOLD, 14));
//        Label nameText = new Label(fullName);

        HBox emailBox = new HBox(6);
        emailBox.setAlignment(Pos.BOTTOM_LEFT);
        Label emailLabel = new Label("Email: ");
        emailLabel.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        Label emailText = new Label(email);
        emailText.setFont(Font.font("Inter", FontWeight.NORMAL, 20));

//        nameBox.getChildren().addAll(nameLabel, nameText);
        emailBox.getChildren().addAll(emailLabel, emailText);
        HBox.setMargin(emailBox, new Insets(0, 0, 0, 15));
//        infoBox.getChildren().addAll(nameBox, emailBox);
//        infoBox.setAlignment(Pos.CENTER);

        // Download Buttons
        HBox downloadBox = new HBox(30);
        Button cvButton = new Button("Cv", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/download.png")).toExternalForm())));
        Button coverButton = new Button("Lettre", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/download.png")).toExternalForm())));

        cvButton.getStyleClass().add("download-button");
        coverButton.getStyleClass().add("download-button");

        cvButton.setOnAction(e -> {
            downloadFile(jobApplication.getCvFile(),
                    jobApplication.getCvFilename(),
                    jobApplication.getCvFiletype(),
                    cvButton);
        });

        coverButton.setOnAction(e -> {
            downloadFile(jobApplication.getCoverLetterFile(),
                    jobApplication.getCoverLetterFilename(),
                    jobApplication.getCoverLetterFiletype(),
                    coverButton);
        });

        downloadBox.getChildren().addAll(cvButton, coverButton);
        downloadBox.setAlignment(Pos.CENTER);

        // Approve/Reject Buttons
        HBox actionBox = new HBox(10);
        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");

        approveBtn.setPrefWidth(120);
        rejectBtn.setPrefWidth(120);

        approveBtn.getStyleClass().add("details-button-green");
        rejectBtn.getStyleClass().add("details-button-red");

        approveBtn.setOnAction(e -> {
            System.out.println("Approve button pressed for jobId: " + jobApplication.getId());
            boolean updated = jobApplicationViewModel.updateApplicationStatus(jobApplication.getId(), "APPROVED");
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Application approved successfully!");
                approveBtn.setText("Approved");
                approveBtn.setDisable(true);
                rejectBtn.setText("Reject");
                rejectBtn.setDisable(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve application.");
            }
        });

        rejectBtn.setOnAction(e -> {
            boolean updated = jobApplicationViewModel.updateApplicationStatus(jobApplication.getId(), "REJECTED");
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Application rejected.");
                rejectBtn.setText("Rejected");
                rejectBtn.setDisable(true);
                approveBtn.setText("Approve");
                approveBtn.setDisable(false);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject application.");
            }
        });

        actionBox.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actionBox.getChildren().addAll(spacer, approveBtn, rejectBtn);

        card.getChildren().addAll(emailBox, downloadBox, actionBox);

        return card;
    }

    private void downloadFile(byte[] fileBytes, String fileName, String fileType, Node sourceNode) {
        if (fileBytes == null || fileBytes.length == 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "File data is not available.");
            return;
        }

        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(fileName);

        // Set extension filter based on file type
        if (fileType != null && fileType.equals("application/pdf")) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
        } else {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        }

        // Show save dialog
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Write bytes to the file
                Files.write(file.toPath(), fileBytes);
                showAlert(Alert.AlertType.INFORMATION, "Success", "File saved successfully!");
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    }








