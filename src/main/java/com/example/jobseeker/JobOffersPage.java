package com.example.jobseeker;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JobOffersPage extends JobOffersList {
    private ComboBox<String> dateFilter ;
    private ComboBox<String> contractFilter;
    private ComboBox<String> locationFilter;
    private ComboBox<String> teleworkFilter;
    private TextField searchField;
    private final Dashboard dashboard;
    public JobOffersPage(Dashboard dashboard) {
        super("No Job Offers Found");
        this.dashboard = dashboard;
    }
    @Override
    protected void initializeData() {
        //unfilteredJobOffers = JobOffer.getDummyData();
        filteredOffers = new ArrayList<>(Dashboard.masterJobOffersList);
        if (!filteredOffers.isEmpty()) {
            selectedJobOffer = filteredOffers.getFirst();
        }
    }
    @Override
    protected void initialize() {
        dateFilter = new ComboBox<>();
        dateFilter = createDateFilter();
        contractFilter = createContractFilter();
        locationFilter = createLocationFilter();
        teleworkFilter = createTeleworkFilter();
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

    private ComboBox<String> createDateFilter() {
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Any Date", "Past 24 hours", "Past week", "Past month");
        filter.setValue("Any Date");
        filter.getStyleClass().add("filter");
        filter.setPadding(new Insets(0, 10, 0, 10));
        filter.setOnAction(_ -> applyAllFilters());
        return filter;
    }

    private ComboBox<String> createContractFilter() {
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Any Contract Type", "Full-time", "Part-time", "Contract", "Internship");
        filter.setValue("Any Contract Type");
        filter.getStyleClass().add("filter");
        filter.setPadding(new Insets(0, 10, 0, 10));
        filter.setOnAction(_ -> applyAllFilters());
        return filter;
    }

    private ComboBox<String> createLocationFilter() {
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Any Location", "Casablanca", "Rabat", "Tangier", "Kenitra", "Temara", "Sale", "Marrakech");
        filter.setValue("Any Location");
        filter.getStyleClass().add("filter");
        filter.setPadding(new Insets(0, 10, 0, 10));
        filter.setOnAction(_ -> applyAllFilters());
        return filter;
    }

    private ComboBox<String> createTeleworkFilter() {
        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("Any Telework Type", "On-Site", "Remote", "Hybrid");
        filter.setValue("Any Telework Type");
        filter.getStyleClass().add("filter");
        filter.setPadding(new Insets(0, 10, 0, 10));
        filter.setOnAction(_ -> applyAllFilters());
        filter.setMinWidth(260);
        return filter;
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
        searchField.setOnAction(_ -> applyAllFilters());

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
        dateFilter.setOnAction(_ -> applyAllFilters());

        contractFilter = new ComboBox<>();
        contractFilter.getItems().addAll("Any Contract Type", "Full-time", "Part-time", "Contract", "Internship");
        contractFilter.setValue("Any Contract Type");
        contractFilter.getStyleClass().add("filter");
        contractFilter.setPadding(new Insets(0, 10, 0, 10));
        contractFilter.setOnAction(_ -> applyAllFilters());

        locationFilter = new ComboBox<>();
        locationFilter.getItems().addAll("Any Location", "Casablanca", "Rabat", "Tangier", "Kenitra", "Temara", "Sale", "Marrakech");
        locationFilter.setValue("Any Location");
        locationFilter.getStyleClass().addAll("filter");
        locationFilter.setPadding(new Insets(0, 10, 0, 10));
        locationFilter.setOnAction(_ -> applyAllFilters());

        teleworkFilter = new ComboBox<>();
        teleworkFilter.getItems().addAll("Any Telework Type", "On-Site", "Remote", "Hybrid");
        teleworkFilter.setValue("Any Telework Type");
        teleworkFilter.getStyleClass().addAll("filter");
        teleworkFilter.setPadding(new Insets(0, 10, 0, 10));
        teleworkFilter.setOnAction(_ -> applyAllFilters());
        teleworkFilter.setMinWidth(260);

        filterButtons.getChildren().addAll(dateFilter, contractFilter, locationFilter, teleworkFilter);
        searchSection.getChildren().addAll(hbox, filterButtons);
        return searchSection;
    }

    @Override
    protected void handleSaveButton(Button save, Button bookmarkIcon){
        boolean newSavedState = !selectedJobOffer.getIsSaved();

        selectedJobOffer.setIsSaved(newSavedState);
        save.getStyleClass().clear();
        save.getStyleClass().add(newSavedState ? "details-button-bookmark-saved" : "details-button-bookmark-unsaved");

        Platform.runLater(() -> {
            if (selectedCard != null) {
                bookmarkIcon.getStyleClass().clear();
                bookmarkIcon.getStyleClass().add(newSavedState ? "job-card-bookmark-saved" : "job-card-bookmark-unsaved");
            }
        });

        dashboard.toggleBookmark(selectedJobOffer, 2);
    }
    public void refreshJobOffers(JobOffer jobOffer) {
        for (Node child : listingVBox.getChildren()) {
            // if child is VBox cast it and put it in a VBox variable named childVBox
            if (child instanceof VBox childVBox) {
                // Compare the children of both VBoxes
                ObservableList<Node> targetChildren = createJobCard(jobOffer).getChildren();
                ObservableList<Node> childrenToCompare = childVBox.getChildren();
                // First check if they have same number of children
                if (targetChildren.size() == childrenToCompare.size()) {

                    // Compare each child
                    Node targetChild = targetChildren.getFirst();
                    Node compareChild = childrenToCompare.getFirst();
                    // If they're Labels, compare their text
                    if (targetChild instanceof HBox targetHBox && compareChild instanceof HBox compareHBox) {
                        if (targetHBox.getChildren().size() == compareHBox.getChildren().size()) {
                            if (!(targetHBox.getChildren().getFirst() instanceof Label targetLabel)
                                    || !(compareHBox.getChildren().getFirst() instanceof Label compareLabel)) {
                                continue;
                            }
                            if (!targetLabel.getText().equals(compareLabel.getText())) {
                                continue;
                            }

                        }
                    }
                    targetChild = targetChildren.get(1);
                    compareChild = childrenToCompare.get(1);
                    if(targetChild instanceof Label targetLabel && compareChild instanceof Label compareLabel) {
                        if (!targetLabel.getText().equals(compareLabel.getText())) {
                            continue;
                        }
                    }
                    targetChild = targetChildren.get(2);
                    compareChild = childrenToCompare.get(2);
                    if(targetChild instanceof Label targetLabel && compareChild instanceof Label compareLabel) {
                        if (!targetLabel.getText().equals(compareLabel.getText())) {
                            continue;
                        }
                    }
                    targetChild = targetChildren.get(3);
                    compareChild = childrenToCompare.get(3);
                    if(targetChild instanceof Text targetLabel && compareChild instanceof Text compareLabel) {
                        if (!targetLabel.getText().equals(compareLabel.getText())) {
                            continue;
                        }
                    }

                    ((HBox) childrenToCompare.getFirst()).getChildren().getLast().getStyleClass().remove("job-card-bookmark-saved");
                    ((HBox) childrenToCompare.getFirst()).getChildren().getLast().getStyleClass().add("job-card-bookmark-unsaved");

                    break;
                }
            }
        }
    }

    public void applyExternalFilter(String companyName){
        companyName = companyName.toLowerCase();
        filteredOffers = new ArrayList<>(Dashboard.masterJobOffersList);
        if (!companyName.isEmpty()) {
            String finalCompanyName = companyName;
            filteredOffers = filteredOffers.stream()
                    .filter(offer ->
                            offer.getTitle().toLowerCase().contains(finalCompanyName) ||
                                    offer.getCompany().toLowerCase().contains(finalCompanyName) ||
                                    offer.getDescription().toLowerCase().contains(finalCompanyName))
                    .collect(Collectors.toList());

        }
        currentPage = 1;
        updateUIAfterFiltering();
    }




    private void applyAllFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedDateFilter = dateFilter.getValue();
        String selectedContractFilter = contractFilter.getValue();
        String selectedLocationFilter = locationFilter.getValue();
        String selectedTeleworkFilter = teleworkFilter.getValue();

        // Start with master list and apply filters sequentially
        filteredOffers = new ArrayList<>(Dashboard.masterJobOffersList);

        // Apply text search if not empty
        if (!searchText.isEmpty()) {
            filteredOffers = filteredOffers.stream()
                    .filter(offer ->
                            offer.getTitle().toLowerCase().contains(searchText) ||
                                    offer.getCompany().toLowerCase().contains(searchText) ||
                                    offer.getDescription().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

        }


        // Apply date filter
        if (!"Any Date".equals(selectedDateFilter)) {
            System.out.println("date filter");
            LocalDateTime cutoffDate = switch (selectedDateFilter) {
                case "Past 24 hours" -> LocalDateTime.now().minusDays(1);
                case "Past week" -> LocalDateTime.now().minusWeeks(1);
                case "Past month" -> LocalDateTime.now().minusMonths(1);
                default -> null;
            };
            if (cutoffDate != null) {
                LocalDateTime finalCutoffDate = cutoffDate;
                filteredOffers = filteredOffers.stream()
                        .filter(offer -> offer.getPublishDate().isAfter(finalCutoffDate))
                        .collect(Collectors.toList());
            }
        }

        // Apply contract filter
        if (!"Any Contract Type".equals(selectedContractFilter)) {
            System.out.println("contract filter");
            filteredOffers = filteredOffers.stream()
                    .filter(offer -> offer.getContractType().equalsIgnoreCase(selectedContractFilter))
                    .collect(Collectors.toList());
        }

        // Apply location filter
        if (!"Any Location".equals(selectedLocationFilter)) {
            System.out.println("location filter");
            filteredOffers = filteredOffers.stream()
                    .filter(offer -> offer.getLocation().getCity().equalsIgnoreCase(selectedLocationFilter))
                    .collect(Collectors.toList());
        }

        // Apply telework filter
        if (!"Any Telework Type".equals(selectedTeleworkFilter)) {
            System.out.println("telework filter");
            filteredOffers = filteredOffers.stream()
                    .filter(offer -> offer.getTeleWork().equalsIgnoreCase(selectedTeleworkFilter))
                    .collect(Collectors.toList());
        }

        // Reset to first page and update UI
        currentPage = 1;
        updateUIAfterFiltering();
    }

    private void updateUIAfterFiltering() {
        Platform.runLater(() -> {
            if (filteredOffers.isEmpty()) {
                showNoJobOffersMessage(message);
            } else {
                // Update pagination visibility
                if (listingContainer.getContent() instanceof VBox content && content.getChildren().size() > 1) {
                    content.getChildren().getLast().setVisible(true);
                }

                // Handle selection
                if (selectedJobOffer != null && filteredOffers.contains(selectedJobOffer)) {
                    updateJobListings();
                    updateJobDetails();
                } else {
                    selectedJobOffer = filteredOffers.getFirst();
                    //selectedCard = createJobCard(selectedJobOffer);
                    updateJobListings();
                    updateJobDetails();
                    //selectJobOffer(selectedJobOffer, selectedCard);
                }

                updatePaginationControls();
            }
        });
    }
}