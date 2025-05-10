package com.example.jobseeker.view;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.JobOffer;

import com.example.jobseeker.viewmodel.JobOfferViewModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.List;


public class BookmarkedJobOffersPage extends JobOffersList {
    private final Dashboard dashboard;
    private List<JobOffer> filteredOffers;
    public BookmarkedJobOffersPage(Dashboard dashboard, JobOfferViewModel jobOfferViewModel) throws SQLException {
        super("You haven't saved any job offers yet.", jobOfferViewModel);
        this.dashboard = dashboard;
        refreshBookmarkedJobs();
    }

    @Override
    protected void initializeData() throws SQLException {
        // Initially get saved job offers
        viewModel.loadSavedJobOffers();
        filteredOffers = viewModel.getSavedJobOffers();
    }

    @Override
    protected void initialize() {
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
    @Override
    protected void handleSaveButton(Button save, Button bookmarkIcon) throws SQLException {
        // Get the current job offer
        JobOffer jobOffer = viewModel.getSelectedJobOffer();
        if (viewModel.getSelectedJobOffer() == null) return;

        // Toggle the saved state
        //boolean newSavedState = !viewModel.getSelectedJobOffer().getIsSaved();
        viewModel.getSelectedJobOffer().setIsSaved(!viewModel.getSelectedJobOffer().getIsSaved());

        // Update the UI
        save.getStyleClass().clear();
        save.getStyleClass().add( viewModel.getSelectedJobOffer().getIsSaved() ? "details-button-bookmark-saved" : "details-button-bookmark-unsaved");

        if (selectedCard != null) {
            bookmarkIcon.getStyleClass().clear();
            bookmarkIcon.getStyleClass().add(viewModel.getSelectedJobOffer().getIsSaved() ? "job-card-bookmark-saved" : "job-card-bookmark-unsaved");
        }

        // Use dashboard to persist the change and update both pages
        dashboard.toggleBookmark(jobOffer, 2);

        // If we're removing a bookmark from the bookmarked page, refresh immediately
        if (!viewModel.getSelectedJobOffer().getIsSaved()) {
            refreshBookmarkedJobs();
        }
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
                VBox jobCard = createJobCard(offer);
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
        viewModel.loadSavedJobOffers();
        filteredOffers = viewModel.getSavedJobOffers();

        if (filteredOffers.isEmpty()) {
            selectedCard = null;
            showNoJobOffersMessage(message);
        } else {
            // Select the first offer if available
            if (!filteredOffers.isEmpty()) {
                viewModel.selectJobOffer(filteredOffers.getFirst());
            }
            currentPage = 1;
            updateJobListings();
            updatePaginationControls();
            updateJobDetails();
        }
    }



}
