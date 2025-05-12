package com.example.jobseeker.view;

import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.JobOffer;
import com.example.jobseeker.viewmodel.JobApplicationViewModel;
import com.example.jobseeker.viewmodel.JobOfferViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.Objects;

public class JobOffersPage extends JobOffersList {
    private ComboBox<String> dateFilter ;
    private ComboBox<String> contractFilter;
    private ComboBox<String> locationFilter;
    private ComboBox<String> teleworkFilter;
    private TextField searchField;
    private JobApplicationViewModel appViewModel;
    //private ObservableList<JobOffer> jobOffers;


    public JobOffersPage(Dashboard dashboard, JobOfferViewModel jobOfferViewModel, JobApplicationViewModel jobApplicationViewModel) throws SQLException {
        super("No job offers found matching your criteria.", jobOfferViewModel, dashboard);
        this.appViewModel = jobApplicationViewModel;
    }
    @Override
    protected void initializeData() throws SQLException {
        // Load all job offers for this page
        viewModel.loadJobOffers();
       // jobOffers = viewModel.getJobOffers();
    }
    @Override
    protected void initialize() {
        //dateFilter = new ComboBox<>();
//        dateFilter = createDateFilter();
//        contractFilter = createContractFilter();
//        locationFilter = createLocationFilter();
//        teleworkFilter = createTeleworkFilter();
        searchField = new TextField();
        VBox searchSection = createSearchSection();
        HBox mainContent = new HBox(14, listingContainer, detailsContainer);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxHeight(800);
        //setAlignment(Pos.TOP_CENTER);
        setBackground(new Background(new BackgroundFill(Color.web("#19181D"), CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().addAll(
                searchSection,
                mainContent
        );
    }



    private VBox createSearchSection() {
        VBox searchSection = new VBox(38);
        searchSection.setAlignment(Pos.TOP_CENTER);

        searchField.setPromptText("                                                      Search jobs by title, keywords...");
        searchField.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        searchField.setStyle("-fx-stroke: 0px;");
        searchField.setBackground(new Background(new BackgroundFill(
                Color.web("#7B7B7B"),
                new CornerRadii(20),
                null
        )));
        searchField.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0px"
        );
        searchField.setPrefWidth(700);
        searchField.setPrefHeight(75);
        searchField.setOnAction(_ -> {
            try {
                applyAllFilters();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(Dashboard.class.getResource("/com/example/jobseeker/search.png")).toExternalForm()));
        icon.setFitHeight(33);
        icon.setFitWidth(33);
        icon.setPreserveRatio(true);

        HBox hbox = new HBox();
        hbox.setSpacing(20); // Spacing between the icon and text
        hbox.setPadding(new Insets(20)); // Add padding around the HBox
        hbox.setBackground(new Background(new BackgroundFill(
                Color.web("#F9F9F9"),
                new CornerRadii(20),
                null
        )));
        hbox.setPrefWidth(900);
        hbox.setMaxWidth(1134);
        hbox.setPrefHeight(40);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-border-width: 0;" +
                "-fx-border-radius: 20;"
        );

        hbox.getChildren().addAll(icon, searchField);


        // Filter buttons
        HBox filterButtons = new HBox(17);
        filterButtons.setAlignment(Pos.CENTER);
        filterButtons.setPrefHeight(100);
        filterButtons.setMinHeight(101);

        dateFilter = new ComboBox<>();
        dateFilter.getItems().addAll("Any Date", "Past 24 hours", "Past week", "Past month");
        dateFilter.setValue("Any Date");
        dateFilter.getStyleClass().add("filter");
        dateFilter.setPadding(new Insets(0, 10, 0, 10));
        dateFilter.setOnAction(_ -> {
            try {
                applyAllFilters();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        contractFilter = new ComboBox<>();
        contractFilter.getItems().addAll("Any Contract Type", "Full-time", "Part-time", "Contract", "Internship");
        contractFilter.setValue("Any Contract Type");
        contractFilter.getStyleClass().add("filter");
        contractFilter.setPadding(new Insets(0, 10, 0, 10));
        contractFilter.setOnAction(_ -> {
            try {
                applyAllFilters();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        locationFilter = new ComboBox<>();
        locationFilter.getItems().addAll("Any Location", "Casablanca", "Rabat", "Tangier", "Kenitra", "Temara", "Sale", "Marrakech");
        locationFilter.setValue("Any Location");
        locationFilter.getStyleClass().addAll("filter");
        locationFilter.setPadding(new Insets(0, 10, 0, 10));
        locationFilter.setOnAction(_ -> {
            try {
                applyAllFilters();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        teleworkFilter = new ComboBox<>();
        teleworkFilter.getItems().addAll("Any Telework Type", "On-Site", "Remote", "Hybrid");
        teleworkFilter.setValue("Any Telework Type");
        teleworkFilter.getStyleClass().addAll("filter");
        teleworkFilter.setPadding(new Insets(0, 10, 0, 10));
        teleworkFilter.setOnAction(_ -> {
            try {
                applyAllFilters();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        teleworkFilter.setMinWidth(260);

        filterButtons.getChildren().addAll(dateFilter, contractFilter, locationFilter, teleworkFilter);
        searchSection.getChildren().addAll(hbox, filterButtons);
        return searchSection;
    }

    @Override
    protected void handleSaveButton(Button save, Button bookmarkIcon) throws SQLException {
        JobOffer jobOffer = viewModel.getSelectedJobOffer();
        if (jobOffer == null) return;

        viewModel.toggleJobOfferSavedState(dashboard.getCurrentUser().getId() ,jobOffer);
        //viewModel.loadSavedJobOffers();

        // Toggle the saved state
        boolean newSavedState = viewModel.isSaved(dashboard.getCurrentUser().getId() ,jobOffer);

        // Update the UI
        save.getStyleClass().clear();
        save.getStyleClass().add(newSavedState ? "details-button-bookmark-saved" : "details-button-bookmark-unsaved");

        if (selectedCard != null) {
            bookmarkIcon.getStyleClass().clear();
            bookmarkIcon.getStyleClass().add(newSavedState ? "job-card-bookmark-saved" : "job-card-bookmark-unsaved");
        }

        // Use dashboard to persist the change and update both pages

        dashboard.toggleBookmark(jobOffer, 1);
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

//public void refreshJobOffers() throws SQLException {
//    // Refresh the display for a specific job offer that was updated
//
//    if (updatedOffer != null && selectedCard != null) {
//        // Update UI elements for the specific job card if it's currently selected
//        if (updatedOffer.equals(viewModel.getSelectedJobOffer())) {
//            updateJobDetails();
//        }
//    }
//}





    private void applyAllFilters() throws SQLException {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedDateFilter = dateFilter.getValue();
        String selectedContractFilter = contractFilter.getValue();
        String selectedLocationFilter = locationFilter.getValue();
        String selectedTeleworkFilter = teleworkFilter.getValue();

        // Start with master list and apply filters sequentially
        viewModel.searchJobOffers(searchText,selectedDateFilter, selectedContractFilter, selectedLocationFilter,selectedTeleworkFilter);

        // Reset to first page and update UI

        updateUIAfterFiltering();
    }



    public void updateUIAfterFiltering() {
        Platform.runLater(() -> {
            if (viewModel.getJobOffers().isEmpty()) {
                showNoJobOffersMessage(message);
            } else {
                // Update pagination visibility
                if (listingContainer.getContent() instanceof VBox content && content.getChildren().size() > 1) {
                    content.getChildren().getLast().setVisible(true);
                }

                // Handle selection
                if (viewModel.getSelectedJobOffer() != null && viewModel.getJobOffers().contains(viewModel.getSelectedJobOffer())) {
                    updateJobListings();
                    try {
                        updateJobDetails();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    viewModel.selectJobOffer(viewModel.getFirstJobOffer());
                    //selectedCard = createJobCard(selectedJobOffer);
                    updateJobListings();
                    try {
                        updateJobDetails();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    //selectJobOffer(selectedJobOffer, selectedCard);
                }

                updatePaginationControls();
            }
        });
    }

    public JobOfferViewModel getViewModel() {
        return viewModel;
    }
}