package com.example.jobseeker;

import com.example.jobseeker.dao.CompanyDAO;
import com.example.jobseeker.dao.JobOfferDAO;
import com.example.jobseeker.dao.UserDAO;
import com.example.jobseeker.model.JobOffer;
import com.example.jobseeker.model.User;
import com.example.jobseeker.util.DatabaseUtil;
import com.example.jobseeker.view.*;
import com.example.jobseeker.viewmodel.CompanyViewModel;
import com.example.jobseeker.viewmodel.JobOfferViewModel;
import com.example.jobseeker.viewmodel.SignInViewModel;
import com.example.jobseeker.viewmodel.SignUpViewModel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Dashboard extends Application {
    static protected List<JobOffer> masterJobOffersList;
    static protected StackPane root;
    static public Map<String, VBox> pages;
    public ScrollPane contentPane;
    private VBox sideBar;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 85;
    private static final double SIDEBAR_EXPANDED_WIDTH = 255;
    private boolean isSidebarExpanded = false;

    // User tracking
    private User currentUser = null;
    public enum UserRole { NONE, CANDIDATE, RECRUITER }
    private UserRole currentRole = UserRole.NONE;

    // Page references by role
    private final Map<UserRole, List<String>> rolePages = new HashMap<>();

    // Sidebar elements
    private Map<Button, Boolean> clickedButtons;
    private Map<Button, String> buttonNamePair;

    // Switch Pages
    static public SignInView signInView;
    static public SignUpView signUpView;
    static public JobOffersPage jobOffersPage;

    @Override
    public void start(Stage stage) throws SQLException {
        Connection connection = DatabaseUtil.getConnection();

        CompanyDAO companyDAO = new CompanyDAO(connection);
        JobOfferDAO jobOfferDAO = new JobOfferDAO(connection);
        UserDAO userDAO = new UserDAO(connection);

        CompanyViewModel companyViewModel = new CompanyViewModel(companyDAO);
        JobOfferViewModel jobOfferViewModel = new JobOfferViewModel(jobOfferDAO);
        SignUpViewModel signUpViewModel = new SignUpViewModel(userDAO);
        SignInViewModel signInViewModel = new SignInViewModel(userDAO);

        jobOfferViewModel.loadJobOffers();
        masterJobOffersList = jobOfferViewModel.getJobOffers();

        // Switched Pages
        signInView = new SignInView(signInViewModel, this);
        signUpView = new SignUpView(signUpViewModel, this);
        jobOffersPage = new JobOffersPage(this, jobOfferViewModel);

        // Initialize all possible pages
        initializeAllPages(jobOfferViewModel, companyViewModel, signInViewModel);

        // Set up role-based page access
        initializeRolePages();

        try {
            Image image = new Image(Objects.requireNonNull(Dashboard.class.getResource("/com/example/jobseeker/logo.png")).toExternalForm());
            stage.getIcons().add(image);
        } catch(NullPointerException e) {
            System.out.println("Logo Image not found");
        }

        // Create the main layout as StackPane
        root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);

        // Initialize content
        contentPane = new ScrollPane(pages.get("Home"));
        contentPane.setFitToWidth(true);
        contentPane.setPrefWidth(1440);

        root.getChildren().add(contentPane);

        // Initialize sidebar for default role (not logged in)
        refreshSidebar();

        Scene scene = new Scene(root, 1800, 990);
        stage.setResizable(true);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        stage.setTitle("Job Seeker");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeAllPages(JobOfferViewModel jobOfferViewModel, CompanyViewModel companyViewModel, SignInViewModel signInViewModel) throws SQLException {
        pages = new HashMap<String, VBox>();

        // Common pages for all users
        pages.put("Home", new HomePage(this));
        pages.put("Log in - Log out", signInView);

        // Candidate specific pages
        pages.put("Companies", new CompaniesPage(this, companyViewModel));
        pages.put("Job Offers", jobOffersPage);
        pages.put("Statistics", new StatisticsPage(this, jobOfferViewModel));
        pages.put("Saved Job Offers", new BookmarkedJobOffersPage(this, jobOfferViewModel));

        // Recruiter specific pages
        pages.put("Create Job Offer", new CreateJobOfferView(this));
        pages.put("My Job Listings", new MyJobListingsView(this));
    }

    private void initializeRolePages() {
        // Pages visible when no user is logged in
        rolePages.put(UserRole.NONE, Arrays.asList(
                "Home",
                "Companies",
                "Job Offers",
                "Log in - Log out"
        ));

        // Pages visible to logged in candidates
        rolePages.put(UserRole.CANDIDATE, Arrays.asList(
                "Home",
                "Companies",
                "Job Offers",
                "Statistics",
                "Saved Job Offers"
        ));

        // Pages visible to logged in recruiters
        rolePages.put(UserRole.RECRUITER, Arrays.asList(
                "Home",
                "Create Job Offer",
                "My Job Listings"
        ));
    }

    public void switchPage(String pageName) throws SQLException {
        // Special handling based on user role and requested page


        VBox page = pages.get(pageName);

        if (page != null) {
            if(pageName.equals("Saved Job Offers") || pageName.equals("Job Offers") || pageName.equals("My Job Listings")) {
                if (pageName.equals("Saved Job Offers")) {
                    ((BookmarkedJobOffersPage)page).refreshBookmarkedJobs();
                }else if (pageName.equals("Job Offers")) {
                    jobOffersPage.updateUIAfterFiltering();
                }
                contentPane.setContent(page);
            } else {
                contentPane.setContent(page);
                contentPane.setPrefHeight(1024);
                contentPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                contentPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        } else {
            System.out.println("Page not found: " + pageName);
        }
    }

    // Method to update the user and refresh UI accordingly
    public void setCurrentUser(User user) {
        this.currentUser = user;

        if (user == null) {
            currentRole = UserRole.NONE;
        } else {
            // Set role based on user type
            currentRole = user.getRole().equalsIgnoreCase("RECRUITER") ? UserRole.RECRUITER : UserRole.CANDIDATE;
        }

        // Refresh the sidebar with appropriate options
        refreshSidebar();

        try {
            // Switch to home page after login/logout
            switchPage("Home");
        } catch (SQLException e) {
            System.err.println("Error switching to home page: " + e.getMessage());
        }
    }

    // Method to refresh sidebar based on current user role
    private void refreshSidebar() {
        // Remove current sidebar if it exists
        if (sideBar != null) {
            root.getChildren().remove(sideBar);
        }

        // Create new sidebar with appropriate options
        sideBar = initializeSideBar();
        sideBar.setPrefWidth(SIDEBAR_COLLAPSED_WIDTH);
        sideBar.setMinWidth(SIDEBAR_COLLAPSED_WIDTH);
        sideBar.setMaxWidth(SIDEBAR_COLLAPSED_WIDTH);

        // Style the sidebar
        sideBar.setBackground(new Background(new BackgroundFill(
                Color.web("#252C48"),
                new CornerRadii(0, 20, 20, 0, false),
                null
        )));
        sideBar.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 0);");

        root.getChildren().add(sideBar);
    }

    public VBox initializeSideBar() {
        VBox sideBar = new VBox(30);
        sideBar.setAlignment(Pos.CENTER);
        sideBar.setPadding(new javafx.geometry.Insets(20, 10, 20, 10));

        // Get pages for current role
        List<String> pagesForRole = rolePages.get(currentRole);

        buttonNamePair = new HashMap<>();
        clickedButtons = new HashMap<>();

        boolean firstButton = true;
        for (String pageName : pagesForRole) {
            // Load icon
            ImageView icon = loadIcon(pageName);

            Button clickableIcon = new Button(pageName, icon);
            clickableIcon.setText("");
            clickableIcon.setFont(Font.font("Inter", FontWeight.BOLD, 22));
            clickableIcon.getStyleClass().add("sidebar-button");
            clickableIcon.setMaxWidth(Double.MAX_VALUE);
            clickableIcon.setAlignment(Pos.CENTER_LEFT);

            buttonNamePair.put(clickableIcon, pageName);

            if (firstButton) {
                // Highlight first button
                Image originalImage = ((ImageView)clickableIcon.getGraphic()).getImage();
                Image coloredImage = changeImageColor(originalImage, Color.web("#CE7AFA"));
                ImageView newImage = new ImageView(coloredImage);
                newImage.setFitHeight(45);
                newImage.setFitWidth(45);
                clickableIcon.setUnderline(true);
                clickableIcon.setTextFill(Color.web("#CE7AFA"));
                clickableIcon.setGraphic(newImage);
                clickedButtons.put(clickableIcon, true);
                firstButton = false;
            } else {
                clickableIcon.setText("");
                clickableIcon.setTextFill(Color.web("#7B4B94"));
                clickedButtons.put(clickableIcon, false);
            }

            // Click handler for the button
            clickableIcon.setOnAction(event -> {
                try {
                    handleButtonClick(clickableIcon, sideBar);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            clickableIcon.setOnMouseEntered(_ -> {
                if (!clickedButtons.get(clickableIcon)) {
                    Image originalImage = ((ImageView)clickableIcon.getGraphic()).getImage();
                    Image coloredImage = changeImageColor(originalImage, Color.web("#CE7AFA"));
                    ImageView newImage = new ImageView(coloredImage);
                    clickableIcon.setGraphic(newImage);
                    clickableIcon.setUnderline(true);
                    clickableIcon.setTextFill(Color.web("#CE7AFA"));
                    clickableIcon.setText(buttonNamePair.get(clickableIcon));
                    clickableIcon.setFont(Font.font("Inter", FontWeight.BOLD, 22));
                    clickableIcon.setUnderline(true);
                }
            });

            clickableIcon.setOnMouseExited(_ -> {
                if (!clickedButtons.get(clickableIcon)) {
                    clickableIcon.setGraphic(icon);
                    clickableIcon.setUnderline(false);
                    clickableIcon.setTextFill(Color.web("#7B4B94"));
                }
            });

            sideBar.getChildren().add(clickableIcon);
        }

        // Add mouse enter/exit handlers for the entire sidebar
        sideBar.setOnMouseEntered(e -> expandSidebar());
        sideBar.setOnMouseExited(e -> collapseSidebar());

        return sideBar;
    }

    // Helper method to load icons, with fallback handling
    private ImageView loadIcon(String pageName) {
        ImageView icon;
        try {
            icon = new ImageView(new Image(Objects.requireNonNull(
                    Dashboard.class.getResource("/com/example/jobseeker/" + pageName + ".png")
            ).toExternalForm()));
        } catch (Exception e) {
            // Fallback for icons that don't exist yet
            System.out.println("Icon not found for: " + pageName + ". Using default icon.");
            try {
                // Use a generic icon as fallback
                icon = new ImageView(new Image(Objects.requireNonNull(
                        Dashboard.class.getResource("/com/example/jobseeker/Home.png")
                ).toExternalForm()));
            } catch (Exception ex) {
                // Create an empty ImageView if all fails
                icon = new ImageView();
                System.out.println("Fallback icon not found either. Using empty icon.");
            }
        }
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        icon.setPreserveRatio(true);
        return icon;
    }

    private void handleButtonClick(Button clickableIcon, VBox sideBar) throws SQLException {
        clickedButtons.forEach((button, wasClicked) -> clickedButtons.put(button, button.equals(clickableIcon)));

        for (Node node : sideBar.getChildren()) {
            if (node instanceof Button btn) {
                if (clickedButtons.get(btn)) {
                    Image originalImage = ((ImageView)btn.getGraphic()).getImage();
                    Image coloredImage = changeImageColor(originalImage, Color.web("#CE7AFA"));
                    ImageView newImage = new ImageView(coloredImage);
                    newImage.setFitHeight(45);
                    newImage.setFitWidth(45);
                    btn.setGraphic(newImage);
                    btn.setTextFill(Color.web("#CE7AFA"));
                    btn.setUnderline(true);
                    if (isSidebarExpanded) {
                        btn.setText(buttonNamePair.get(btn));
                    }
                } else {
                    btn.setGraphic(loadIcon(buttonNamePair.get(btn)));
                    btn.setStyle("-fx-background-color: transparent;");
                    btn.setText(isSidebarExpanded ? buttonNamePair.get(btn) : "");
                    btn.setTextFill(Color.web("#7B4B94"));
                    btn.setUnderline(false);
                }
            }
        }

        String pageName = buttonNamePair.get(clickableIcon);
        switchPage(pageName);
    }

    private void expandSidebar() {
        if (!isSidebarExpanded) {
            // Create animation for smooth width transition
            Timeline timeline = new Timeline();

            // Animate all width properties simultaneously
            KeyValue prefWidthValue = new KeyValue(sideBar.prefWidthProperty(), SIDEBAR_EXPANDED_WIDTH);
            KeyValue minWidthValue = new KeyValue(sideBar.minWidthProperty(), SIDEBAR_EXPANDED_WIDTH);
            KeyValue maxWidthValue = new KeyValue(sideBar.maxWidthProperty(), SIDEBAR_EXPANDED_WIDTH);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(300),
                    prefWidthValue, minWidthValue, maxWidthValue
            );

            timeline.getKeyFrames().add(keyFrame);

            // Show text for all buttons with a slight delay to make it smoother
            timeline.setOnFinished(e -> {
                for (Node node : sideBar.getChildren()) {
                    if (node instanceof Button btn) {
                        btn.setText(buttonNamePair.get(btn));
                        if (clickedButtons.get(btn)) {
                            btn.setTextFill(Color.web("#CE7AFA"));
                        } else {
                            btn.setTextFill(Color.web("#7B4B94"));
                        }
                    }
                }
            });

            timeline.play();
            isSidebarExpanded = true;
        }
    }

    private void collapseSidebar() {
        if (isSidebarExpanded) {
            // Hide text immediately when starting to collapse
            for (Node node : sideBar.getChildren()) {
                if (node instanceof Button btn) {
                    btn.setText("");
                }
            }

            // Create animation for smooth width transition
            Timeline timeline = new Timeline();

            // Animate all width properties simultaneously
            KeyValue prefWidthValue = new KeyValue(sideBar.prefWidthProperty(), SIDEBAR_COLLAPSED_WIDTH);
            KeyValue minWidthValue = new KeyValue(sideBar.minWidthProperty(), SIDEBAR_COLLAPSED_WIDTH);
            KeyValue maxWidthValue = new KeyValue(sideBar.maxWidthProperty(), SIDEBAR_COLLAPSED_WIDTH);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(200),
                    prefWidthValue, minWidthValue, maxWidthValue
            );

            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            isSidebarExpanded = false;
        }
    }

    public void resetSideBar(String name) {
        clickedButtons.forEach((clickableButton, clicked) -> {
            if(clicked){
                clickedButtons.put(clickableButton, false);
            }
        });
        Button clickableButton = null;
        for(Map.Entry<Button, String> entry : buttonNamePair.entrySet()){
            if(entry.getValue().equalsIgnoreCase(name) ){
                clickableButton = entry.getKey();
                clickedButtons.put(clickableButton, true);
                break;
            }
        }
        if(clickableButton != null){
            Image originalImage = ((ImageView)clickableButton.getGraphic()).getImage();
            Image coloredImage = changeImageColor(originalImage, Color.web("#CE7AFA"));
            ImageView newImage = new ImageView(coloredImage);
            clickableButton.setGraphic(newImage);
            clickableButton.setText(buttonNamePair.get(clickableButton));
            clickableButton.setTextFill(Color.web("#CE7AFA"));
            clickableButton.setUnderline(true);
        }
        for(Node node : sideBar.getChildren()){
            if(node instanceof Button btn) {
                if(!clickedButtons.get(btn)){
                    btn.setGraphic(loadIcon(buttonNamePair.get(btn)));
                    btn.setStyle("-fx-background-color: transparent;");
                    btn.setText("");
                    btn.getStyleClass().remove(".sidebar-button-selected");
                } else {
                    btn.getStyleClass().add(".sidebar-button-selected");
                }
            }
        }
    }

    public void toggleBookmark(JobOffer jobOffer, int source) throws SQLException {
        try {
            // Update the JobOffersPage if we're coming from there
            if (source == 1) {
                jobOffersPage.updateUIAfterFiltering();
            }

            // Update the BookmarkedJobOffersPage regardless of source
            if (pages.containsKey("Saved Job Offers")) {
                ((BookmarkedJobOffersPage) pages.get("Saved Job Offers")).refreshBookmarkedJobs();
            }
        } catch (SQLException e) {
            System.err.println("Error toggling bookmark: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Get current user for other components to check
    public User getCurrentUser() {
        return currentUser;
    }

    // Check if user is logged in and has specific role
    public boolean isUserInRole(UserRole role) {
        return currentRole == role;
    }

    private static Image changeImageColor(Image originalImage, Color newColor) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = pixelReader.getColor(x, y);
                Color modifiedColor = new Color(
                        newColor.getRed(),
                        newColor.getGreen(),
                        newColor.getBlue(),
                        originalColor.getOpacity()
                );
                pixelWriter.setColor(x, y, modifiedColor);
            }
        }

        return writableImage;
    }

    public static void main(String[] args) {
        launch();
    }
}