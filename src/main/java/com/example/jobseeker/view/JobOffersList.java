package com.example.jobseeker.view;

import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.JobOffer;
import com.example.jobseeker.viewmodel.JobApplicationViewModel;
import com.example.jobseeker.viewmodel.JobOfferViewModel;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class JobOffersList extends VBox {
    protected final int ITEMS_PER_PAGE = 15;
    protected int currentPage = 1;
    //protected List<JobOffer> filteredOffers;
    protected VBox selectedCard = null;
    //protected JobOffer selectedJobOffer;
    protected ScrollPane listingContainer;
    protected VBox detailsContainer;
    protected String message;
    protected VBox listingVBox;
    protected HBox pagination;
    protected Dashboard dashboard;
    protected JobOfferViewModel viewModel;
    protected JobApplicationViewModel jobApplicationViewModel;
    protected JobOffersList(String message, JobOfferViewModel jobOfferViewModel, Dashboard dashboard, JobApplicationViewModel jobApplicationViewModel) throws SQLException {
        this.dashboard = dashboard;
        this.jobApplicationViewModel = jobApplicationViewModel;

        this.viewModel = jobOfferViewModel;
        pagination = new HBox(10);
        setAlignment(Pos.TOP_CENTER);
        setMinHeight(1024);
        setPrefWidth(1440);
        setPadding(new Insets(39, 103, 39, 103));
        setSpacing(38);
        setBackground(new Background(new BackgroundFill(Color.web("#D7FDF0"), CornerRadii.EMPTY, Insets.EMPTY)));
        viewModel.loadJobOffers();
        initializeData();
        listingContainer = createJobListingsSection();
        detailsContainer = new VBox(5);
        detailsContainer.setPadding(new Insets(32, 30, 34, 32));
        detailsContainer.setPrefWidth(590);
        detailsContainer.setBackground(new Background(new BackgroundFill(Color.web("#252C48"),new CornerRadii(15), Insets.EMPTY)));
        this.message = message;

        initialize();
        Platform.runLater(() -> {
            if (!viewModel.getJobOffers().isEmpty()) {

                JobOffer firstOffer = jobOfferViewModel.getJobOffers().getFirst();
                VBox firstCard = null;
                try {
                    firstCard = createJobCard(firstOffer);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    selectJobOffer(firstOffer, firstCard);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                updateJobListings();
            } else {
                showNoJobOffersMessage(message);
            }
        });
    }
    public void loadData() throws SQLException {
        if (dashboard.isUserInRole(Dashboard.UserRole.CANDIDATE)){
            System.out.println("Is candidate : YES");
            jobApplicationViewModel.loadSubmissions(dashboard.getCurrentUser().getId());
        }
    }
    protected abstract void initializeData() throws SQLException;
    protected abstract void initialize() throws SQLException;
    protected abstract void handleSaveButton(Button save, Button bookmark) throws SQLException;
    protected abstract void updateJobListings();

    private ScrollPane createJobListingsSection() {
        ScrollPane listingsSectionPane = new ScrollPane();
        listingsSectionPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        listingsSectionPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //listingsSectionPane.setMaxHeight(750);
        listingsSectionPane.setFitToWidth(true);


        listingVBox = new VBox(17);
        listingVBox.setAlignment(Pos.TOP_CENTER);


        //paginationContainer = new VBox();
        updatePaginationControls();  // Initialize pagination

        VBox mainContent = new VBox(17, listingVBox, pagination);
        mainContent.setAlignment(Pos.TOP_CENTER);
        listingsSectionPane.setContent(mainContent);
        return listingsSectionPane;
    }

    protected void updatePaginationControls() {
        Platform.runLater(() -> {
            int totalPages = (int) Math.ceil((double) viewModel.getJobOffers().size() / ITEMS_PER_PAGE);

            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

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
                    updateJobListings();
                    updatePaginationControls();
                }
            });

            nextButton.setOnAction(_ -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    updateJobListings();
                    updatePaginationControls();
                }
            });

            prevButton.setStyle("-fx-background-color: #C9C7C7; -fx-text-fill: #615E5E;");
            nextButton.setStyle("-fx-background-color: #C9C7C7; -fx-text-fill: #615E5E;");

            pagination.getChildren().setAll(prevButton, pageLabel, nextButton);

        });
    }

    protected void updateJobDetailsForApplication() throws SQLException {
        if (viewModel.getSelectedJobOffer() == null) return;

        detailsContainer.getChildren().clear();

        // Back button
        Button backButton = new Button();
        backButton.setPrefSize(45, 45);
        backButton.getStyleClass().add("back-button");

        // Use a placeholder for the back icon
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

        // Job Application title
        Label applicationTitleLabel = new Label("Job Application:");
        applicationTitleLabel.getStyleClass().add("application-title-label");

        // Contact Number field
        ImageView phoneIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/phoneNumber.png")).toExternalForm()));
        phoneIcon.setFitHeight(45);
        phoneIcon.setFitWidth(45);
        phoneIcon.setPreserveRatio(true);

        TextField contactNumberField = new TextField();
        contactNumberField.setPrefSize(424, 66);
        contactNumberField.setPromptText("Enter your Contact Number");
        contactNumberField.getStyleClass().add("application-text-field");
        detailsContainer.setMargin(contactNumberField, new Insets(10, 0, 10, 0));

        HBox contactNumberContainer = new HBox(15);
        contactNumberContainer.setAlignment(Pos.CENTER_LEFT);
        contactNumberContainer.getChildren().addAll(phoneIcon, contactNumberField);
        VBox.setMargin(contactNumberContainer, new Insets(0, 0, 20, 0));

        // Job Profile field
        ImageView profileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/profileIcon.png")).toExternalForm()));
        profileIcon.setFitHeight(45);
        profileIcon.setFitWidth(45);
        profileIcon.setPreserveRatio(true);

        TextField jobProfileField = new TextField();
        jobProfileField.setPrefSize(424, 66);
        jobProfileField.setPromptText("Enter your Job Profile");
        jobProfileField.getStyleClass().add("application-text-field");
        detailsContainer.setMargin(jobProfileField, new Insets(10, 0, 10, 0));

        HBox jobProfileContainer = new HBox(15);
        jobProfileContainer.setAlignment(Pos.CENTER_LEFT);
        jobProfileContainer.getChildren().addAll(profileIcon, jobProfileField);
        VBox.setMargin(jobProfileContainer, new Insets(0, 0, 30, 0));

        // Upload buttons container
        HBox uploadButtonsContainer = new HBox(20);
        uploadButtonsContainer.setAlignment(Pos.CENTER);

        // File status labels
        Label cvStatusLabel = new Label("No CV selected");
        cvStatusLabel.setTextFill(Color.GRAY);

        Label coverLetterStatusLabel = new Label("No Cover Letter selected");
        coverLetterStatusLabel.setTextFill(Color.GRAY);

        // References for selected files
        final File[] selectedCVFile = {null};
        final File[] selectedCoverLetterFile = {null};

        // Upload CV button
        ImageView uploadCVIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/upload.png")).toExternalForm()));
        uploadCVIcon.setFitHeight(24);
        uploadCVIcon.setFitWidth(24);
        uploadCVIcon.setPreserveRatio(true);

        Button uploadCVButton = new Button("CV", uploadCVIcon);
        uploadCVButton.setPrefSize(187.61, 44);
        uploadCVButton.getStyleClass().add("signIn-button");
        uploadCVButton.setGraphicTextGap(10);
        uploadCVButton.setContentDisplay(ContentDisplay.LEFT);
        uploadCVButton.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("  Select CV");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showOpenDialog(detailsContainer.getScene().getWindow());
            if (file != null) {
                selectedCVFile[0] = file;
                cvStatusLabel.setText(file.getName());
                cvStatusLabel.setTextFill(Color.GREEN);
            }
        });

        // Upload Letter button
        ImageView uploadLetterIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/upload.png")).toExternalForm()));
        uploadLetterIcon.setFitHeight(24);
        uploadLetterIcon.setFitWidth(24);
        uploadLetterIcon.setPreserveRatio(true);

        Button uploadLetterButton = new Button("Letter", uploadLetterIcon);
        uploadLetterButton.setPrefSize(187.61, 44);
        uploadLetterButton.getStyleClass().add("signIn-button");
        uploadLetterButton.setGraphicTextGap(10);
        uploadLetterButton.setContentDisplay(ContentDisplay.LEFT);
        uploadLetterButton.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Cover Letter");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showOpenDialog(detailsContainer.getScene().getWindow());
            if (file != null) {
                selectedCoverLetterFile[0] = file;
                coverLetterStatusLabel.setText(file.getName());
                coverLetterStatusLabel.setTextFill(Color.GREEN);
            }
        });

        VBox cV = new VBox(5);
        cV.setAlignment(Pos.CENTER);
        cV.getChildren().addAll(uploadCVButton, cvStatusLabel);

        VBox covLet = new VBox(5);
        covLet.setAlignment(Pos.CENTER);
        covLet.getChildren().addAll(uploadLetterButton, coverLetterStatusLabel);

        uploadButtonsContainer.getChildren().addAll(cV, covLet);


        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        errorMessageLabel.setPrefHeight(30);
        errorMessageLabel.setFont(Font.font("Inter", 20));
        errorMessageLabel.setAlignment(Pos.CENTER);
        VBox.setMargin(errorMessageLabel, new Insets(12, 0, 12, 0));

        // Success message
        Label successMessageLabel = new Label();
        successMessageLabel.setTextFill(Color.GREEN);
        successMessageLabel.setPrefHeight(30);
        successMessageLabel.setFont(Font.font("Inter", 20));
        successMessageLabel.setAlignment(Pos.CENTER);
        VBox.setMargin(successMessageLabel, new Insets(12, 0, 12, 0));

        Button submitButton = new Button("Submit");
        submitButton.setPrefWidth(180);
        submitButton.setPrefHeight(44);
        submitButton.getStyleClass().add("details-button");
        submitButton.setOnAction(event -> {
            // Clear previous messages
            errorMessageLabel.setText("");
            //successMessageLabel.setText("");

            // Get the current user ID and job offer ID
            int userId = dashboard.getCurrentUser().getId();
            int jobOfferId = viewModel.getSelectedJobOffer().getId(); // Assuming this exists

            // Get input values
            String contactNumber = contactNumberField.getText();
            String jobProfile = jobProfileField.getText();

            // Submit the application
            boolean success = jobApplicationViewModel.submitJobApplication(
                    userId,
                    jobOfferId,
                    contactNumber,
                    jobProfile,
                    selectedCVFile[0],
                    selectedCoverLetterFile[0]
            );

            if (success) {

                    System.out.println("In");
                    successMessageLabel.setText("Application submitted successfully!");

                    // Reset fields
                    contactNumberField.clear();
                    jobProfileField.clear();
                    selectedCVFile[0] = null;
                    selectedCoverLetterFile[0] = null;
                    cvStatusLabel.setText("No CV selected");
                    cvStatusLabel.setTextFill(Color.GRAY);
                    coverLetterStatusLabel.setText("No Cover Letter selected");
                    coverLetterStatusLabel.setTextFill(Color.GRAY);

                    jobApplicationViewModel.getSubmmittedApplications().put(viewModel.getSelectedJobOffer().getId(), JobApplicationViewModel.AppSub.SUBMITTED);
                javafx.application.Platform.runLater(() -> {
                    try {
                        // Add a small delay if needed
                        PauseTransition delay = new PauseTransition(Duration.millis(645));
                        delay.setOnFinished(evt -> {
                            try {
                                updateJobDetails();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        delay.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


            }
        });

        HBox sub = new HBox();
        sub.setAlignment(Pos.CENTER_LEFT);
        Label lastDate = new Label("DeadLine: " + viewModel.getSelectedJobOffer().formatDateTime());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        sub.getChildren().addAll(submitButton,spacer, lastDate);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        // Adding all components to the detailsContainer
        detailsContainer.getChildren().addAll(
                backButton_Title,
                companyLabel,
                locationLabel,
                applicationTitleLabel,
                contactNumberContainer,
                jobProfileContainer,
                uploadButtonsContainer,
                errorMessageLabel,
                successMessageLabel,
                spacer2,
                sub
        );

        // Bind properties
        jobApplicationViewModel.contactNumberProperty().bindBidirectional(contactNumberField.textProperty());
        jobApplicationViewModel.jobProfileProperty().bindBidirectional(jobProfileField.textProperty());

        // Error message listener
        jobApplicationViewModel.errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            errorMessageLabel.setText(newValue);
            if (!newValue.isEmpty()) {
                errorMessageLabel.setTextFill(Color.RED);
                successMessageLabel.setText("");
            }
        });
    }


    protected VBox createJobCard(JobOffer offer) throws SQLException {
        if (offer == null) return null;
        int maxCharacters = 200;

        VBox card = new VBox(6);
        card.setPadding(new Insets(30, 21, 20, 32));
        card.setPrefWidth(470);
        card.setPrefHeight(239);
        card.setMaxHeight(240);

        Label titleLabel = new Label(offer.getTitle());
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#EDEDEF"));

        ImageView bookmarkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/blueBookmark.png")).toExternalForm()));
        bookmarkIcon.setFitHeight(28);
        bookmarkIcon.setFitWidth(28);
        bookmarkIcon.setPreserveRatio(true);







        Label companyLabel = new Label(offer.getCompany());
        companyLabel.setFont(Font.font("Inter", FontWeight.LIGHT, 14));
        companyLabel.setTextFill(Color.web("#F1F1F1"));


        VBox title_company = new VBox(0);
        title_company.setAlignment(Pos.TOP_LEFT);
        title_company.setMaxHeight(50);
        title_company.getChildren().addAll(titleLabel, companyLabel);
        HBox cardHeader = new HBox();

        if(!dashboard.isUserInRole(Dashboard.UserRole.RECRUITER)){

            cardHeader.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(title_company, Priority.ALWAYS);

            Button save = new Button("", bookmarkIcon);
            save.setPrefSize(60, 60);
            save.setMinSize(60, 60);
            save.setMaxSize(60, 60);
            if (dashboard.getCurrentUser() != null) {
                save.getStyleClass().add(viewModel.isSaved(dashboard.getCurrentUser().getId(), offer) ? "job-card-bookmark-saved" : "job-card-bookmark-unsaved");
            }else{
                save.getStyleClass().add("job-card-bookmark-unsaved");
            }

            save.setAlignment(Pos.CENTER);


            cardHeader.getChildren().addAll(title_company, save);
        }


        Label locationLabel = new Label(offer.getLocation().getCity());
        locationLabel.setFont(Font.font("Inter", FontWeight.LIGHT, 14));
        locationLabel.setTextFill(Color.web("#F6F6F6"));

        Text description = new Text();
        if(offer.getDescription().length()<maxCharacters){
            description.setText(offer.getDescription());
        }else{
            description.setText(offer.getDescription().substring(0, maxCharacters) + "...");
        }
        description.setFont(Font.font("Inter", FontWeight.NORMAL, 15));
        description.setFill(Color.web("#EDEDEF"));
        description.setWrappingWidth(410);

        Label publishDate = new Label(offer.getFormattedPublishDate());
        publishDate.setFont(Font.font("Inter", FontWeight.LIGHT, 14));
        publishDate.setTextFill(Color.web("#F6F6F6"));

        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);

        card.getChildren().addAll((dashboard.isUserInRole(Dashboard.UserRole.RECRUITER))? titleLabel : cardHeader, locationLabel, description,region, publishDate);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("job-card");
        card.setOnMouseClicked(_ -> {
            try {
                selectJobOffer(offer, card);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        return card;
    }
    protected void selectJobOffer(JobOffer offer, VBox card) throws SQLException {
        if (offer == null) return;
        if (selectedCard != null) {
            Platform.runLater(()->{
                selectedCard.getStyleClass().clear();
                selectedCard.getStyleClass().add("job-card");
            });
        }
        if (card != null) {
            Platform.runLater(()->{
                card.getStyleClass().clear();
                card.getStyleClass().add("job-card-selected");
                selectedCard = card;
            });
        }
        viewModel.selectJobOffer(offer);
        updateJobDetails();
    }
    protected void updateJobDetails() throws SQLException {

        if (viewModel.getSelectedJobOffer() == null) return;


        detailsContainer.getChildren().clear();


        Label titleLabel = new Label(viewModel.getSelectedJobOffer().getTitle());
        titleLabel.getStyleClass().add("details-title");

        Label companyLabel = new Label(viewModel.getSelectedJobOffer().getCompany());
        companyLabel.getStyleClass().add("details-company");

        Label locationLabel = new Label(viewModel.getSelectedJobOffer().getLocation().getCity());
        locationLabel.getStyleClass().add("details-location");
        //locationLabel.setTextFill(Color.web("#f6f6f6"));
        JobApplicationViewModel.AppSub status = null;

        if(dashboard.isUserInRole(Dashboard.UserRole.CANDIDATE)){
            status = jobApplicationViewModel.getSubmmittedApplications().get(viewModel.getSelectedJobOffer().getId());
        }

        Button apply = new Button((dashboard.isUserInRole(Dashboard.UserRole.RECRUITER)) ? "Submissions" : status == null ? "Apply Now" : String.valueOf(status));
        apply.setPrefWidth(160);
        apply.setPrefHeight(44);
        apply.setDisable((dashboard.isUserInRole(Dashboard.UserRole.CANDIDATE) && (viewModel.getSelectedJobOffer().getDeadline().isBefore(LocalDateTime.now()) || status != null)) || dashboard.isUserInRole(Dashboard.UserRole.NONE));
        switch (status) {
            case JobApplicationViewModel.AppSub.APPROVED -> apply.getStyleClass().add("details-button-green");
            case JobApplicationViewModel.AppSub.REJECTED -> apply.getStyleClass().add("details-button-red");
            case null -> apply.getStyleClass().add("details-button");
            default -> apply.getStyleClass().add("details-button");
        }
        apply.setOnAction(_ -> {
            try {
                updateJobDetailsForApplication();
                } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        HBox buttons = null;
        Button save;
        if(dashboard.isUserInRole(Dashboard.UserRole.CANDIDATE)){
            ImageView bookmark = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/whiteBookmark.png")).toExternalForm()));
            bookmark.setFitHeight(28);
            bookmark.setFitWidth(28);
            bookmark.setPreserveRatio(true);

            save = new Button("", bookmark);
            save.setPrefWidth(44);
            save.setPrefHeight(44);
            save.setAlignment(Pos.CENTER);
            if (dashboard.getCurrentUser() != null) {
                save.getStyleClass().add(viewModel.isSaved(dashboard.getCurrentUser().getId(), viewModel.getSelectedJobOffer()) ? "details-button-bookmark-saved" : "details-button-bookmark-unsaved");
            }else{
                save.getStyleClass().add("details-button-bookmark-unsaved");
                save.setDisable(true);
                save.setTooltip(new Tooltip("You need to create an account"));
            }

            save.setBackground(new Background(new BackgroundFill(Color.web("#CA5656"), new CornerRadii(10), Insets.EMPTY)));
            save.setOnAction(_ -> {
                try {
                    handleSaveButton(save, (Button)((HBox)selectedCard.getChildren().getFirst()).getChildren().getLast());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            buttons = new HBox(25);
            buttons.getChildren().addAll(apply, save);
            VBox.setMargin(buttons, new Insets(0, 0, 22, 0));
        }



        // Job Details Content
        // First Section
        ImageView contract = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Contract.png")).toExternalForm()));
        contract.setFitHeight(23);
        contract.setFitWidth(23);
        contract.setPreserveRatio(true);

        Button contractType = new Button("Contract Type", contract);
        contractType.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        contractType.setTextFill(Color.web("#FFFFFF"));
        contractType.setStyle("-fx-background-color: transparent;");

        Button typeOfContract = new Button(viewModel.getSelectedJobOffer().getContractType());
        typeOfContract.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        typeOfContract.setTextFill(Color.web("#615E5E"));
        typeOfContract.setStyle("-fx-background-color: #C9C7C7;");
        typeOfContract.setMaxHeight(20);
        VBox.setMargin(typeOfContract, new Insets(0, 0, 0, 14));

        VBox firstSection = new VBox(3, contractType, typeOfContract);
        firstSection.setAlignment(Pos.CENTER_LEFT);
        firstSection.getStyleClass().add("jobDetail");
        // Second Section
        ImageView business = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Business.png")).toExternalForm()));
        business.setFitHeight(23);
        business.setFitWidth(23);
        business.setPreserveRatio(true);

        Button industryType = new Button("Industry", business);
        industryType.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        industryType.setTextFill(Color.web("#F1F1F1"));
        industryType.setStyle("-fx-background-color: transparent;");

        Button typeOfIndustry = new Button(viewModel.getSelectedJobOffer().getIndustry());
        typeOfIndustry.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        typeOfIndustry.setTextFill(Color.web("#615E5E"));
        typeOfIndustry.setStyle("-fx-background-color: #C9C7C7;");
        VBox.setMargin(typeOfIndustry, new Insets(0, 0, 0, 14));

        VBox secondSection = new VBox(3, industryType, typeOfIndustry);
        secondSection.setAlignment(Pos.CENTER_LEFT);
        secondSection.getStyleClass().add("jobDetail");
        // Third Section
        ImageView telework = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/TeleWork.png")).toExternalForm()));
        telework.setFitHeight(23);
        telework.setFitWidth(23);
        telework.setPreserveRatio(true);

        Button teleworkType = new Button("TeleWork", telework);
        teleworkType.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        teleworkType.setTextFill(Color.web("#F1F1F1"));
        teleworkType.setStyle("-fx-background-color: transparent;");

        Button typeOfTelework = new Button(viewModel.getSelectedJobOffer().getTeleWork());
        typeOfTelework.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        typeOfTelework.setTextFill(Color.web("#615E5E"));
        typeOfTelework.setStyle("-fx-background-color: #C9C7C7;");
        VBox.setMargin(typeOfTelework, new Insets(0, 0, 0, 14));

        VBox thirdSection = new VBox(3, teleworkType, typeOfTelework);
        thirdSection.setAlignment(Pos.CENTER_LEFT);
        thirdSection.getStyleClass().add("jobDetail");
        // Fourth Section
        ImageView salary = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Salary.png")).toExternalForm()));
        salary.setFitHeight(23);
        salary.setFitWidth(23);
        salary.setPreserveRatio(true);

        Button salaryQtt = new Button("Salary", salary);
        salaryQtt.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        salaryQtt.setTextFill(Color.web("#F1F1F1"));
        salaryQtt.setStyle("-fx-background-color: transparent;");

        Button money = new Button(viewModel.getSelectedJobOffer().getSalary());
        money.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        money.setTextFill(Color.web("#615E5E"));
        money.setStyle("-fx-background-color: #C9C7C7;");
        VBox.setMargin(money, new Insets(0, 0, 0, 14));

        VBox salarySection = new VBox(3, salaryQtt, money);
        salarySection.setAlignment(Pos.CENTER_LEFT);
        salarySection.getStyleClass().add("jobDetail");
        // Fifth Section
        ImageView deadline = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Deadline.png")).toExternalForm()));
        deadline.setFitHeight(23);
        deadline.setFitWidth(23);
        deadline.setPreserveRatio(true);

        Button deadlineTime = new Button("Deadline", deadline);
        deadlineTime.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        deadlineTime.setTextFill(Color.web("#F1F1F1"));
        deadlineTime.setStyle("-fx-background-color: transparent;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a");
        Button time = new Button(formatter.format(viewModel.getSelectedJobOffer().getDeadline()));
        time.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        time.setTextFill(Color.web("#615E5E"));
        time.setStyle("-fx-background-color: #C9C7C7;");
        VBox.setMargin(time, new Insets(0, 0, 0, 14));

        VBox deadlineSection = new VBox(3, deadlineTime, time);
        deadlineSection.setAlignment(Pos.CENTER_LEFT);
        deadlineSection.getStyleClass().add("jobDetail");


        HBox salaryDeadlineSection = new HBox(36, salarySection, deadlineSection);
        salaryDeadlineSection.setAlignment(Pos.CENTER);
        HBox contractIndustryTeleworkSection = new HBox(26, firstSection, secondSection, thirdSection);
        VBox titlePaneContent = new VBox(10, contractIndustryTeleworkSection, salaryDeadlineSection);
        titlePaneContent.setAlignment(Pos.CENTER);
        // Final First Titled Pane
        TitledPane jobDetails = new TitledPane("Job Details", titlePaneContent);

        // Location Titled Pane
        ImageView place = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Location.png")).toExternalForm()));
        place.setFitHeight(31);
        place.setFitWidth(31);
        place.setPreserveRatio(true);
        Button location = new Button(viewModel.getSelectedJobOffer().getLocation().getCity() + ", " + viewModel.getSelectedJobOffer().getLocation().getRegion() + ", " + viewModel.getSelectedJobOffer().getLocation().getCountry(), place);
        Label address = new Label("             " + ((viewModel.getSelectedJobOffer().getLocation().getAddress() == null) ? "": viewModel.getSelectedJobOffer().getLocation().getAddress()));
        address.setStyle("-fx-text-fill: #000000;");
        address.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        location.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        location.setTextFill(Color.web("#000000"));
        location.setStyle("-fx-background-color: transparent;");
        VBox locationDetails = new VBox(3, location, address);
        TitledPane Place = new TitledPane("Location", locationDetails);

        // Education Titled Pane
        ImageView diploma = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/diploma.png")).toExternalForm()));
        diploma.setFitHeight(31);
        diploma.setFitWidth(31);
        diploma.setPreserveRatio(true);
        Button diplomaB = new Button(viewModel.getSelectedJobOffer().getEducation().getDiploma(), diploma);
        Label field = new Label("             " + viewModel.getSelectedJobOffer().getEducation().getField());
        field.setStyle("-fx-text-fill: #000000;");
        field.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        diplomaB.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        diplomaB.setTextFill(Color.web("#000000"));
        diplomaB.setStyle("-fx-background-color: transparent;");
        Label level = new Label("Baccalaureate + " + viewModel.getSelectedJobOffer().getEducation().getLevel());
        level.setStyle("-fx-text-fill: #000000;");
        level.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox educationLevel = new HBox(3, diplomaB, spacer, level);
        educationLevel.setAlignment(Pos.CENTER_LEFT);
        VBox educationDetails = new VBox(0, educationLevel, field);
        TitledPane Education = new TitledPane("Education", educationDetails);

        // Experience Titled Pane
        ImageView experience = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/experience.png")).toExternalForm()));
        experience.setFitHeight(31);
        experience.setFitWidth(31);
        experience.setPreserveRatio(true);
        Button experienceB = new Button(viewModel.getSelectedJobOffer().getExperience().getLevel() + " with " + viewModel.getSelectedJobOffer().getExperience().getMinYears() + " to " + viewModel.getSelectedJobOffer().getExperience().getMaxYears() + " of experience", experience);
        experienceB.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        experienceB.setTextFill(Color.web("#000000"));
        experienceB.setStyle("-fx-background-color: transparent;");
        Text descriptionExp = new Text(viewModel.getSelectedJobOffer().getExperience().getDescription());
        descriptionExp.setWrappingWidth(500);
        descriptionExp.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        descriptionExp.setSelectionFill(Color.web("000000"));
        VBox experienceDetails = new VBox(3, experienceB, descriptionExp);
        TitledPane Experience = new TitledPane("Experience", experienceDetails);



        // Job Description Titled Pane
        Text descriptionText = new Text(viewModel.getSelectedJobOffer().getDescription());
        descriptionText.setWrappingWidth(500);
        descriptionText.setFont(Font.font("Inter", FontWeight.NORMAL, 12));
        descriptionText.setSelectionFill(Color.web("000000"));
        TitledPane Description = new TitledPane("Job Description", descriptionText);

        // Skills Titled Pane
        String content = "";
        for (int i = 0; i< viewModel.getSelectedJobOffer().getHardSkills().size(); i++){
            content += "● " + viewModel.getSelectedJobOffer().getHardSkills().get(i) + "\n";
        }
        Label hardSkills = new Label("Hard Skills:\n");
        hardSkills.getStyleClass().add("skills");
        Text hardContent = new Text(content);
        hardContent.setFont(Font.font("Inter", FontWeight.NORMAL, 12));

        VBox hardSkillsDetails = new VBox(3, hardSkills, hardContent);
        hardSkillsDetails.setAlignment(Pos.TOP_LEFT);


        content = "";
        for (int i = 0; i< viewModel.getSelectedJobOffer().getSoftSkills().size(); i++){
            content += "● " + viewModel.getSelectedJobOffer().getSoftSkills().get(i) + "\n";
        }
        Label softSkills = new Label("Soft Skills:\n");
        softSkills.getStyleClass().add("skills");
        Text softContent = new Text(content);
        softContent.setFont(Font.font("Inter", FontWeight.NORMAL, 12));

        VBox softSkillsDetails = new VBox(3, softSkills, softContent);
        hardSkillsDetails.setAlignment(Pos.TOP_LEFT);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox skillsContents = new HBox(hardSkillsDetails, spacer2, softSkillsDetails);
        TitledPane Skills = new TitledPane("Skills", skillsContents);

        // Languages Titled Pane
        ImageView language = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/languages.png")).toExternalForm()));
        language.setFitHeight(31);
        language.setFitWidth(31);
        language.setPreserveRatio(true);
        content = ""; int i = 0;
        String otherLanguages = "";
        for (Map.Entry entry : viewModel.getSelectedJobOffer().getLanguages().entrySet()){
            if(i > 2){
                otherLanguages += "- " + entry.getKey() + ": " + entry.getValue() + "         ";
            }else{
                content += "- " + entry.getKey() + ": " + entry.getValue() + "         ";
                i++;
            }
        }
        Button languageB = new Button(content, language);
        languageB.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        languageB.setTextFill(Color.web("#000000"));
        languageB.setStyle("-fx-background-color: transparent;");
        Label otherLang = new Label("      " + otherLanguages);
        otherLang.setStyle("-fx-text-fill: #000000;");
        otherLang.setFont(Font.font("Inter", FontWeight.BOLD, 12));


        VBox languagesDetails = new VBox(3, languageB, otherLang);
        TitledPane Languages = new TitledPane("Languages", languagesDetails);



        VBox.setMargin(locationLabel, new Insets(0, 0, 22, 0));


        detailsContainer.getChildren().addAll(
                titleLabel,
                companyLabel,
                locationLabel,
                dashboard.isUserInRole(Dashboard.UserRole.CANDIDATE) ? buttons : apply,
                jobDetails,
                Place,
                Education,
                Experience,
                Description,
                Skills,
                Languages
        );

    }
    protected void showNoJobOffersMessage(String message){
        VBox messageContainer = new VBox();
        messageContainer.setAlignment(Pos.BOTTOM_CENTER);
        messageContainer.setPrefHeight(400);
        messageContainer.setPrefWidth(533);

        Label noResultsLabel = new Label(message);
        noResultsLabel.getStyleClass().add("title");
        noResultsLabel.setWrapText(true);

        messageContainer.setPadding(new Insets(50));
        messageContainer.getChildren().add(noResultsLabel);

        listingVBox.getChildren().clear();
        listingVBox.getChildren().add(messageContainer);
        ((VBox) listingContainer.getContent()).getChildren().getLast().setVisible(false);
        //selectedJobOffer = null;
        //selectedCard = null;

        detailsContainer.getChildren().clear();
        TitledPane jobDetails = new TitledPane("Job Details", new VBox());
        jobDetails.setExpanded(false);
        jobDetails.setCollapsible(true);
        TitledPane locationDetails = new TitledPane("Location", new VBox());
        locationDetails.setExpanded(false);
        locationDetails.setCollapsible(true);
        TitledPane jobDescription = new TitledPane("Job Description", new VBox());
        jobDescription.setExpanded(false);
        jobDescription.setCollapsible(true);
        Region spacer = new Region();
        VBox noResultContent = new VBox(spacer,jobDetails,locationDetails,jobDescription);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        noResultContent.setPrefHeight(325);
        noResultsLabel.setAlignment(Pos.BOTTOM_LEFT);
        detailsContainer.getChildren().add(noResultContent);
    }



}
