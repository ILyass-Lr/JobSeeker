package com.example.jobseeker;

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

    @Override
    public void start(Stage stage) throws SQLException {
        pages = new HashMap<>();
        pages.put("Home", new HomePage(this));
        try{
            pages.put("Companies", new CompaniesPage(this));
        }catch(SQLException e){
            e.printStackTrace();
        }

        pages.put("Job Offers", new JobOffersPage(this));
        pages.put("Statistics", new StatisticsPage(this));
        pages.put("Saved Job Offers", new BookmarkedJobOffersPage(this));

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
        //contentPane.setFitToHeight(true);
        contentPane.setPrefWidth(1440);

        // Initialize sidebar
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

        // Add both to root StackPane
        root.getChildren().addAll(contentPane, sideBar);

        Scene scene = new Scene(root, 1800, 990);
        stage.setResizable(true);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        stage.setTitle("Job Seeker");
        stage.setScene(scene);
        stage.show();
    }

    public void switchPage(String pageName) {
        VBox page = pages.get(pageName);

        if (page != null) {
            if(pageName.equals("Saved Job Offers") || pageName.equals("Job Offers")) {
                contentPane.setContent(page);
            } else {
                contentPane.setContent(page);
                contentPane.setPrefHeight(1024);
                contentPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                contentPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        } else {
            System.out.println("Page not found: " + pageName);
        }
    }

    private Map<Button, Boolean> clickedButtons;
    private Map<Button, String> buttonNamePair;

    public VBox initializeSideBar() {
        VBox sideBar = new VBox(30);
        sideBar.setAlignment(Pos.CENTER);
        sideBar.setPadding(new javafx.geometry.Insets(20, 10, 20, 10));

        String[] iconNames = {"Home", "Companies", "Job Offers", "Statistics", "Predictions", "Saved Job Offers"};
        buttonNamePair = new HashMap<>();
        clickedButtons = new HashMap<>();

        for (String iconName : iconNames) {
            ImageView icon = new ImageView(new Image(Objects.requireNonNull(Dashboard.class.getResource("/com/example/jobseeker/" + iconName + ".png")).toExternalForm()));
            icon.setFitHeight(45);
            icon.setFitWidth(45);
            icon.setPreserveRatio(true);

            Button clickableIcon = new Button(iconName, icon);
            clickableIcon.setText("");
            clickableIcon.setFont(Font.font("Inter", FontWeight.BOLD, 22));
            clickableIcon.getStyleClass().add("sidebar-button");
            clickableIcon.setMaxWidth(Double.MAX_VALUE);
            clickableIcon.setAlignment(Pos.CENTER_LEFT);

            buttonNamePair.put(clickableIcon, iconName);

            if(clickedButtons.isEmpty()) {
                Image originalImage = ((ImageView)clickableIcon.getGraphic()).getImage();
                Image coloredImage = changeImageColor(originalImage, Color.web("#CE7AFA"));
                ImageView newImage = new ImageView(coloredImage);
                newImage.setFitHeight(45);
                newImage.setFitWidth(45);
                clickableIcon.setUnderline(true);
                clickableIcon.setTextFill(Color.web("#CE7AFA"));
                clickableIcon.setGraphic(newImage);
                clickedButtons.put(clickableIcon, true);
            } else {
                clickableIcon.setText("");
                clickableIcon.setTextFill(Color.web("#7B4B94"));
                clickedButtons.put(clickableIcon, false);
            }

            // Click handler for the button
            clickableIcon.setOnAction(event -> handleButtonClick(clickableIcon, sideBar));

            clickableIcon.setOnMouseEntered(_ ->{
                if(!clickedButtons.get(clickableIcon)){
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

            clickableIcon.setOnMouseExited(_ ->{
                if(!clickedButtons.get(clickableIcon)) {
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

    private void handleButtonClick(Button clickableIcon, VBox sideBar) {
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
                    //btn.setText("");
                    if (isSidebarExpanded) {
                        btn.setText(buttonNamePair.get(btn));
                    }
                } else {
                    btn.setGraphic(new ImageView(new Image(Objects.requireNonNull(
                            Dashboard.class.getResource("/com/example/jobseeker/" + buttonNamePair.get(btn) + ".png")
                    ).toExternalForm())));
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
    public void resetSideBar(String name){

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
        for( Node btn : sideBar.getChildren()){
            if(!clickedButtons.get((Button) btn)){
                ((Button) btn).setGraphic(new ImageView(new Image(Objects.requireNonNull(Dashboard.class.getResource("/com/example/jobseeker/" + buttonNamePair.get(btn) + ".png")).toExternalForm())));
                btn.setStyle("-fx-background-color: transparent;");
                ((Button) btn).setText("");
                ((Button) btn).getStyleClass().remove(".sidebar-button-selected");
            }else{
                ((Button) btn).getStyleClass().add(".sidebar-button-selected");
            }
        }

    }
    public void toggleBookmark(JobOffer jobOffer, int index) {
        // Find and update the job offer in the master list
        for (JobOffer offer : masterJobOffersList) {
            if (offer.equals(jobOffer)) {
                offer.setIsSaved(offer.getIsSaved()); // Toggle the bookmark
                break;
            }
        }
        if(index == 1){
            ((JobOffersPage) pages.get("Job Offers")).refreshJobOffers(jobOffer);
        }else{
            ((BookmarkedJobOffersPage)pages.get("Saved Job Offers")).refreshBookmarkedJobs(masterJobOffersList);
        }
    }

    public static void main(String[] args) throws SQLException {
        //masterJobOffersList = new ArrayList<>( JobOffer.getDummyData());
        try{
            masterJobOffersList = DatabaseUtil.getAllJobOffers();
            launch();
        }catch(SQLException e){
            System.out.println("Problem de SQL !");
            e.printStackTrace();
        }


    }
}