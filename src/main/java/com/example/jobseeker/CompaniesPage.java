package com.example.jobseeker;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompaniesPage extends VBox {
    private final int ITEMS_PER_PAGE = 15;
    private int currentPage = 1;
    private HBox pagination;
    private List<Company> companies;
    private Company selectedCompany;
    private VBox selectedCompanyVBox;
    private ScrollPane scrollPane;
    private VBox detailsContainer;
    private TextField searchField;
    private GridPane companiesGrid;
    private Dashboard dashboard;

    public CompaniesPage(Dashboard dashboard) throws SQLException {
        companies = DatabaseUtil.getAllCompanies();
        selectedCompany = companies.getFirst();
        this.dashboard = dashboard;
        initialize();
    }

    private void initialize() {
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10,114,10,114));
        setBackground(new Background(new BackgroundFill(
                Color.web("#19181D"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        searchField = new TextField();
        pagination = new HBox(10);


        // Create main content section
        HBox mainContent = new HBox(20);
        mainContent.setPrefHeight(1000);
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Create scrollable companies grid
        scrollPane = new ScrollPane();
//        scrollPane.setFitToWidth(true);
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.setPrefWidth(711);
        //scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");



        scrollPane.setContent(companiesGrid);

        // Create details section
        detailsContainer = createDetailsSection();
        updatePaginationControls();
        mainContent.getChildren().addAll(createCompaniesListingsSection(), detailsContainer);
        if (!companies.isEmpty()) {
            selectedCompanyVBox = createCompanyCard(companies.getFirst(), true);
            selectedCompanyVBox.getStyleClass().add("company-stroke");
        }

        updateCompanyGrid();
        getChildren().addAll(createSearchSection(), mainContent);

    }

    private ScrollPane createCompaniesListingsSection() {
        scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //listingsSectionPane.setMaxHeight(750);
        scrollPane.setFitToWidth(true);


        companiesGrid = new GridPane();
        companiesGrid.setHgap(15);
        companiesGrid.setVgap(28);
        companiesGrid.setAlignment(Pos.TOP_CENTER);


        updatePaginationControls();  // Initialize pagination

        VBox mainContent = new VBox(17);
        mainContent.getChildren().addAll(companiesGrid, pagination);
        mainContent.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(mainContent);
        return scrollPane;
    }

    protected void updatePaginationControls() {
        Platform.runLater(() -> {
            int totalPages = (int) Math.ceil((double) companies.size() / ITEMS_PER_PAGE);

            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            pagination.getChildren().clear();
            pagination.setAlignment(Pos.CENTER);

            Button prevButton = new Button("<");
            Button nextButton = new Button(">");
            Label pageLabel = new Label(String.format("%d/%d", currentPage, Math.max(totalPages, 1)));

            pageLabel.setFont(Font.font("Inter", FontWeight.BOLD, 12));
            pageLabel.setStyle("-fx-text-fill: white;");

            prevButton.setDisable(currentPage <= 1);
            nextButton.setDisable(currentPage >= totalPages);

            prevButton.setOnAction(_ -> {
                if (currentPage > 1) {
                    currentPage--;
                    updateCompanyGrid(); // Add this line
                }
            });

            nextButton.setOnAction(_ -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    updateCompanyGrid(); // Add this line
                }
            });

            prevButton.setStyle("-fx-background-color: #C9C7C7; -fx-text-fill: #615E5E;");
            nextButton.setStyle("-fx-background-color: #C9C7C7; -fx-text-fill: #615E5E;");

            pagination.getChildren().addAll(prevButton, pageLabel, nextButton);
        });
    }

    private HBox createSearchSection() {


        searchField.setPromptText("                                                      Search Companies...");
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

        searchField.setOnAction(e -> {
            // Implement search functionality
            try {
                filterCompanies(searchField.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });


        return hbox;
    }

    private VBox createCompanyCard(Company company, boolean isLarge) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(28, 17, 28, 17));
        card.setPrefWidth(isLarge ? 709 : 347);
        //card.setPrefHeight(isLarge ? 288 : 346);
        RadialGradient gradient = new RadialGradient(
                0,                          // focusAngle
                0,                          // focusDistance
                0.5,                        // centerX (0.5 means center of the region)
                0.5,                        // centerY (0.5 means center of the region)
                0.5,                        // radius (0.5 means half the size of the region)
                true,                       // proportional
                CycleMethod.NO_CYCLE,       // cycleMethod
                new Stop(0, Color.web("#8C37B8")),  // inner color (purple)
                new Stop(1, Color.web("#525FC5"))   // outer color (blue)
        );
        card.setBackground(new Background(new BackgroundFill(
                gradient,
                new CornerRadii(15),
                Insets.EMPTY
        )));

        // Company name
        Button companyName = new Button(company.getName());
        companyName.getStyleClass().add("company-name-button");

        // Company Status
        Button companyStatus = new Button(company.getStatus());
        companyStatus.getStyleClass().add("company-status-button");

        // Industry and type
        Label industryLabel = new Label("   " + company.getIndustry());
        industryLabel.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        industryLabel.setTextFill(Color.web("#EDEDED"));

        HBox companyHeader = null;
        HBox industry_status = null;
        if(isLarge) {
            Region Spacer = new Region();
            HBox.setHgrow(Spacer, Priority.ALWAYS);

            //Company Header
            companyHeader = new HBox();
            companyHeader.setAlignment(Pos.CENTER_LEFT);
            companyHeader.getChildren().addAll(companyName, Spacer, companyStatus);
        }else{
            Region Spacer = new Region();
            HBox.setHgrow(Spacer, Priority.ALWAYS);

            //Company Header
            industry_status = new HBox();
            industry_status.setAlignment(Pos.CENTER_LEFT);
            industry_status.getChildren().addAll(industryLabel, Spacer, companyStatus);
        }

        //Type and Founded Year
        Label type_founded_year = new Label(((company.getType() == null) ? "   ": "   " + company.getType())  + (company.getFoundedYear() == null ? "" : " Company Founded in " + company.getFoundedYear()));
        type_founded_year.setFont(Font.font("Inter", FontWeight.LIGHT, 14));
        type_founded_year.setTextFill(Color.web("#F1F1F1"));

        // Location
        ImageView place = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/whiteLocation.png")).toExternalForm()));
        place.setFitHeight(27);
        place.setFitWidth(27);
        place.setPreserveRatio(true);
        Button location = new Button((company.getHeadquartersCity() == null ? "" : company.getHeadquartersCity()+", ") + (company.getHeadquartersRegion() == null ? "" : company.getHeadquartersRegion()+", ") + (company.getHeadquartersCountry() == null ? "" : company.getHeadquartersCountry()), place);
        location.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        location.setTextFill(Color.web("#EDEDEF"));
        location.setStyle("-fx-background-color: transparent;");

        // nb Of Offices
        ImageView office = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/nbOffice.png")).toExternalForm()));
        office.setFitHeight(27);
        office.setFitWidth(27);
        office.setPreserveRatio(true);
        Button nbOffices = new Button((company.getNumberOfOffices() == null) ? "Unspecified Number Of Offices": company.getNumberOfOffices().toString() + " Offices", office);
        nbOffices.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        nbOffices.setTextFill(Color.web("#EDEDEF"));
        nbOffices.setStyle("-fx-background-color: transparent;");

        HBox size_financial = new HBox(27);
        size_financial.setAlignment(Pos.CENTER_LEFT);
        if(isLarge) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            size_financial.getChildren().addAll(nbOffices, spacer, createChip("Size:", company.getSize()), createChip("Revenue:", company.getRevenue()), createChip("Work Policy:", company.getRemoteWorkPolicy()));

            Button checkJobsButton = new Button("Check Job Offers");
            checkJobsButton.setPrefWidth(191);
            checkJobsButton.setPrefHeight(44);
            checkJobsButton.getStyleClass().add("details-button");
            checkJobsButton.onActionProperty().set(event -> {
                ((JobOffersPage)(dashboard.pages.get("Job Offers"))).applyExternalFilter(company.getName());
                dashboard.switchPage("Job Offers");
                dashboard.resetSideBar("Job Offers");
            });

            HBox button_ratings = new HBox(16);
            button_ratings.setAlignment(Pos.CENTER);
            button_ratings.getChildren().addAll(checkJobsButton, createGlassdoorChip("Glassdoor Rating", company.getGlassdoorRating(), true), createRetentionChip("Retention Rate", company.getEmployeeRetentionRate(), true));

            card.getChildren().addAll(
                    companyHeader,
                    industryLabel,
                    type_founded_year,
                    location,
                    size_financial,
                    button_ratings
            );
        }else{
            size_financial.getChildren().addAll(createChip(null, company.getSize()), createChip(null, company.getRevenue()), createChip(null, company.getRemoteWorkPolicy()));
            HBox ratings = new HBox(16);
            ratings.setAlignment(Pos.CENTER);
            ratings.getChildren().addAll(createGlassdoorChip("Glassdoor Rating", company.getGlassdoorRating(), false), createRetentionChip("Retention Rate", company.getEmployeeRetentionRate(), false));
            card.getChildren().addAll(
                    companyName,
                    industry_status,
                    type_founded_year,
                    location,
                    nbOffices,
                    size_financial,
                    ratings
            );
        }


        // Click handler
        card.setOnMouseClicked(e -> {
            selectCompany(company, card);  // Now passing the current card
        });

        return card;
    }

    protected void selectCompany(Company company, VBox selectedCompanyCard) {
        if (company == null) return;

        Platform.runLater(() -> {
            // Remove stroke from previous selection
            if (selectedCompanyVBox != null) {
                selectedCompanyVBox.getStyleClass().remove("company-stroke");
            }

            // Update selection and add stroke
            selectedCompany = company;
            selectedCompanyVBox = selectedCompanyCard;
            selectedCompanyVBox.getStyleClass().add("company-stroke");

            // Update details section
            updateDetailsSection();
        });
    }

    private VBox createGlassdoorChip(String title, Double rating, boolean isLarge) {
        VBox chip = new VBox(8);
        chip.getStyleClass().add("rating-chip");
        //chip.setPrefHeight(58);
        chip.setPrefWidth(isLarge ? 205 : 125);
        Label chipTitle = new Label(title);
        chipTitle.getStyleClass().add("rating-chip-title");

        String content = null;
        if(rating != null) {
            if(rating >= 1 && rating <2){
                content = "★☆☆☆☆";
            }else if(rating >= 2 && rating <3){
                content = "★★☆☆☆";
            }else if(rating >= 3 && rating <4){
                content = "★★★☆☆";
            }else if(rating >= 4 && rating <5){
                content = "★★★★☆";
            }else if(rating == 5){
                content = "★★★★★";
            }else{
                content = "☆☆☆☆☆";
            }
        }
        Label chipContent = new Label(content == null ? "☆☆☆☆☆" : content);
        chipContent.getStyleClass().add("rating-chip-content");

        chip.getChildren().addAll(chipTitle, chipContent);

        return chip;
    }

    private VBox createRetentionChip(String title, Double rating, boolean isLarge) {
        VBox chip = new VBox(8);
        chip.getStyleClass().add("rating-chip");
        chip.setPrefWidth(isLarge ? 205 : 125);
        Label chipTitle = new Label(title);
        chipTitle.getStyleClass().add("rating-chip-title");

        Label chipContent = new Label((rating == null) ? " " : rating.toString()  + "%");
        chipContent.getStyleClass().add("rating-chip-content");

        chip.getChildren().addAll(chipTitle, chipContent);

        return chip;
    }

    private VBox createChip(String title, String content){
        VBox chip = new VBox(1);
        chip.getStyleClass().add("financial-chip");
        chip.setMaxHeight(45);
        //chip.setPrefHeight(36);
        Label chipTitle = null;
        if(title != null){
            chipTitle = new Label(title);
            chipTitle.getStyleClass().add("label-chip-title");
        }
        Label chipContent = new Label((content==null)? "Unspecified" : content);
        chipContent.getStyleClass().add("label-chip-content");
        if(title != null) {
            chip.getChildren().addAll(chipTitle, chipContent);
        }else{
            chip.getChildren().add(chipContent);
        }
        return chip;
    }

    private VBox createDetailsSection() {
        VBox details = new VBox(0);
        details.setAlignment(Pos.TOP_CENTER);
        details.setPrefWidth(346);
        details.setPadding(new Insets(25));
        details.setStyle("-fx-background-color: #272B4A; -fx-background-radius: 15;");

        updateDetailsSection();

        return details;
    }

    private void updateDetailsSection() {
        if (selectedCompany == null) return;

        Platform.runLater(() -> {
            detailsContainer.getChildren().clear();

            // Company name
            Button companyName = new Button(selectedCompany.getName());
            companyName.getStyleClass().add("company-name-button");
            VBox.setMargin(companyName, new Insets(25, 5, 25, 5));

            // CSR section
            TitledPane csrSection = new TitledPane();
            csrSection.setText("Corporate Social \nResponsibility \ninitiatives");
            Text csrText = new Text((selectedCompany.getCsrInitiatives() == null) ? "" : selectedCompany.getCsrInitiatives());
            csrText.setFill(Color.BLACK);
            csrText.setWrappingWidth(243);
            csrSection.setContent(csrText);
            csrSection.setExpanded(true);

            // Benefits section
            TitledPane benefitsSection = new TitledPane();
            benefitsSection.setText("Benefits provided");
            Text benefitsText = new Text((selectedCompany.getBenefits()== null) ? "" : selectedCompany.getBenefits());
            benefitsText.setFill(Color.BLACK);
            benefitsText.setWrappingWidth(243);
            benefitsSection.setContent(benefitsText);
            benefitsSection.setExpanded(true);

            // Check Job Offers button
            Button checkJobsButton = new Button("Check Job Offers");
            checkJobsButton.setPrefWidth(180);
            checkJobsButton.setPrefHeight(44);
            checkJobsButton.getStyleClass().add("details-button");
            checkJobsButton.onActionProperty().set(event -> {
                ((JobOffersPage)(Dashboard.pages.get("Job Offers"))).applyExternalFilter(selectedCompany.getName());
                dashboard.switchPage("Job Offers");
                dashboard.resetSideBar("Job Offers");
            });
            VBox.setMargin(checkJobsButton, new Insets(25, 5, 25, 5));

            detailsContainer.getChildren().addAll(
                    companyName,
                    csrSection,
                    benefitsSection,
                    checkJobsButton
            );
        });
    }

    private void updateCompanyGrid() {
        Platform.runLater(() -> {
            companiesGrid.getChildren().clear();

            int startIdx = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, companies.size());

            if (startIdx >= companies.size()) {
                currentPage = 1;
                startIdx = 0;
                endIdx = Math.min(ITEMS_PER_PAGE, companies.size());
            }

            // First item spans full width only on first page
            if (currentPage == 1 && !companies.isEmpty()) {
                Company firstCompany = companies.get(0);
                VBox firstCard = createCompanyCard(firstCompany, true);

                // If this is the selected company, add the stroke effect
                if (firstCompany.equals(selectedCompany)) {
                    selectedCompanyVBox = firstCard;
                    firstCard.getStyleClass().add("company-stroke");
                }

                GridPane.setColumnSpan(firstCard, 2);
                companiesGrid.add(firstCard, 0, 0);
                startIdx = 1;
            }

            // Add remaining items in a 2-column grid
            int row = (currentPage == 1) ? 1 : 0;
            int col = 0;
            for (int i = startIdx; i < endIdx; i++) {
                Company company = companies.get(i);
                VBox card = createCompanyCard(company, false);

                // If this is the selected company, add the stroke effect
                if (company.equals(selectedCompany)) {
                    selectedCompanyVBox = card;
                    card.getStyleClass().add("company-stroke");
                }

                companiesGrid.add(card, col, row);
                col = (col + 1) % 2;
                if (col == 0) row++;
            }

            updatePaginationControls();
        });
    }

    private void filterCompanies(String searchText) throws SQLException {
        if (searchText == null || searchText.trim().isEmpty()) {
            companies = DatabaseUtil.getAllCompanies();
        } else {
            searchText = searchText.toLowerCase();
            final String searchQuery = searchText;
            companies = DatabaseUtil.getAllCompanies().stream()
                    .filter(company ->
                            company.getName().toLowerCase().contains(searchQuery) ||
                                    company.getIndustry().toLowerCase().contains(searchQuery) ||
                                    company.getHeadquartersCity().toLowerCase().contains(searchQuery))
                    .toList();
        }
        currentPage = 1;
        updateCompanyGrid();
    }
}