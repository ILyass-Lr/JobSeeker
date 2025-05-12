package com.example.jobseeker.view;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.JobOffer;

import com.example.jobseeker.viewmodel.JobApplicationViewModel;
import com.example.jobseeker.viewmodel.JobOfferViewModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JobListingView extends JobOffersList {
    private JobApplicationViewModel appViewModel;

    private List<JobOffer> filteredOffers;
    public JobListingView(Dashboard dashboard, JobOfferViewModel jobOfferViewModel, JobApplicationViewModel jobApplicationViewModel) throws SQLException {
        super("You haven't saved any job offers yet.", jobOfferViewModel, dashboard);
        refreshBookmarkedJobs();
        this.appViewModel = jobApplicationViewModel;
    }

    @Override
    protected void initializeData() throws SQLException {
        // Initially get saved job offers
        if (dashboard.getCurrentUser() != null){
            viewModel.searchJobOffersByRecruiter(dashboard.getCurrentUser().getId());
            filteredOffers = viewModel.getJobOffers();
        }else{
            filteredOffers = new ArrayList<JobOffer>();
        }

    }

    @Override
    protected void initialize() throws SQLException {
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

            if (viewModel.getSavedJobOffers() == null || viewModel.getSavedJobOffers().isEmpty()) {

                showNoJobOffersMessage(message);
                return;
            }
            int startIdx = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, viewModel.getSavedJobOffers().size());
            if (startIdx >= viewModel.getSavedJobOffers().size()) {
                currentPage = 1;
                startIdx = 0;
                endIdx = Math.min(ITEMS_PER_PAGE, viewModel.getSavedJobOffers().size());
                updatePaginationControls();
            }
            for (int i = startIdx; i < endIdx; i++) {
                JobOffer offer = viewModel.getSavedJobOffers().get(i);
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
        applicationTitleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        applicationTitleLabel.setTextFill(Color.web("#44AAFE"));
        applicationTitleLabel.setUnderline(true);
        VBox.setMargin(applicationTitleLabel, new Insets(20, 0, 30, 0));

        // SUBMISSION
//        for (JobApplication jobApplication: viewModel.submisison ) {
//             LAYOUT.getChildren().add(  createSubmssionCard(JobApplication jobApplication)
//        }
        }

    }


    // CREATE FUNCTION -> CARD  createSubmssionCard(JobApplication jobApplication){      }





