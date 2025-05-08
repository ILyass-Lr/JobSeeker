package com.example.jobseeker;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class BookmarkedJobOffersPage extends JobOffersList {
    private final Dashboard dashboard;
    //private List<JobOffer> unfilteredJobOffers;
    public BookmarkedJobOffersPage(Dashboard dashboard) {
        super("No Saved Job Offers Found");
        this.dashboard = dashboard;

    }
    @Override
    protected void initializeData() {
        //unfilteredJobOffers = new ArrayList<>(filteredOffers);
        filteredOffers = filteredOffers.stream()
                .filter(JobOffer::getIsSaved)
                .collect(Collectors.toList());
        if (!filteredOffers.isEmpty()) {
            selectedJobOffer = filteredOffers.getFirst();
        }

    }
    @Override
    protected void initialize() {
        HBox mainContent = new HBox(14, listingContainer, detailsContainer);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxHeight(950);
        setBackground(new Background(new BackgroundFill(Color.web("#19181D"), CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().add(
                mainContent
        );
        if (filteredOffers.isEmpty()) {
            showNoJobOffersMessage(message);
        } else {
            updateJobListings();
            updateJobDetails();
        }
    }
    @Override
    protected void handleSaveButton(Button save, Button bookmarkIcon) {
        if (selectedJobOffer == null) return;

        // Toggle save state
        selectedJobOffer.setIsSaved(false);
        selectedCard.getStyleClass().clear();
        selectedCard.getStyleClass().add("job-card");
        // Update button styles
        save.getStyleClass().clear();
        save.getStyleClass().add("details-button-bookmark-unsaved");
        // Update the card's bookmark button style if it exists
        Platform.runLater(() -> {
            if (selectedCard != null) {
                bookmarkIcon.getStyleClass().clear();
                bookmarkIcon.getStyleClass().add("job-card-bookmark-unsaved");
            }
        });
        // Remove the offer from our filtered list
        boolean found = false;
        for(int i = 0; i < filteredOffers.size(); i++) {
            if (selectedJobOffer.equals(filteredOffers.get(i))) {
                filteredOffers.remove(i);
                found = true;
                break;
            }
        }
        System.out.println(found ? "Found" : "Not Found");
        dashboard.toggleBookmark(selectedJobOffer, 1);
        // Handle the UI update
        if (filteredOffers.isEmpty()) {
            // No more saved offers
            selectedJobOffer = null;
            selectedCard = null;
            showNoJobOffersMessage(message);
        } else {
            selectedJobOffer = filteredOffers.getFirst();
            updateJobListings();
            //currentPage = 1;
            updateJobDetails();

        }

    }
    public void refreshBookmarkedJobs(List<JobOffer> jobOffers) {
        // Create a new filtered list from the master list
        filteredOffers = jobOffers.stream()
                .filter(JobOffer::getIsSaved)
                .collect(Collectors.toList());

        if (filteredOffers.isEmpty()) {
            selectedJobOffer = null;
            selectedCard = null;
            showNoJobOffersMessage(message);
        } else {
            selectedJobOffer = filteredOffers.getFirst();
            currentPage = 1;
            updateJobListings();
            updatePaginationControls();  // Update pagination
            updateJobDetails();
        }
    }
}
