package com.example.jobseeker.view;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.Page;
import com.example.jobseeker.SectorCarousel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

import java.sql.SQLException;
import java.util.Objects;

public class HomePage extends Page {
    public HomePage(Dashboard dashboard) throws SQLException {
        super(dashboard);
    }

    @Override
    protected void initialize() {
        setPadding(new Insets(0, 70, 0, 70));
        HBox header = createHeader();
        HBox aboutSection = createAboutSection();
        HBox companiesSection = createCompaniesSection();
        HBox statisticsSection = createStatisticsSection();
        VBox jobOffersSection = createJobOffersSection();
        HBox predictionsSection = createPredictionSection();

        // Make sections expand
        VBox.setVgrow(header, Priority.ALWAYS);
        VBox.setVgrow(aboutSection, Priority.ALWAYS);
        VBox.setVgrow(companiesSection, Priority.ALWAYS);
        VBox.setVgrow(statisticsSection, Priority.ALWAYS);
        VBox.setVgrow(jobOffersSection, Priority.ALWAYS);
        VBox.setVgrow(predictionsSection, Priority.ALWAYS);

        header.prefWidthProperty().bind(this.widthProperty());
        aboutSection.prefWidthProperty().bind(this.widthProperty());
        companiesSection.prefWidthProperty().bind(this.widthProperty());
        jobOffersSection.prefWidthProperty().bind(this.widthProperty());
        statisticsSection.prefWidthProperty().bind(this.widthProperty());
        predictionsSection.prefWidthProperty().bind(this.widthProperty());

        getChildren().addAll(header, aboutSection, companiesSection,jobOffersSection, statisticsSection, predictionsSection);
    }





    private HBox createHeader() {
        HBox header = new HBox(557);
        header.setMaxHeight(180);
        header.setPrefWidth(1440);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(40, 120, 35, 120 ));
        header.setBackground(new Background(new BackgroundFill(
                Color.web("#5042A3"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        VBox textContent = new VBox(0);
        Label title = new Label("SaaY");
        title.getStyleClass().add("title");


        Label subtitle = new Label("Find your desired job in one click !");
        subtitle.setFont(Font.font("Inter", FontWeight.NORMAL, 24));
        subtitle.setTextFill(Color.web("#F1F1F1"));

        textContent.getChildren().addAll(title, subtitle);

        // Add logo on the right
        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/logo.png")).toExternalForm()
        ));
        logo.setFitHeight(122);
        logo.setFitWidth(130);
        logo.setPreserveRatio(true);
        logo.setClip(new Circle(logo.getFitWidth() / 2 , logo.getFitHeight() / 2, logo.getFitWidth() / 2 - 6));
        logo.setStyle("-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.25), 5, 0, 4, 4);");

        HBox.setHgrow(textContent, Priority.ALWAYS);
        textContent.setMaxWidth(Double.MAX_VALUE);
        header.setMaxHeight(Double.MAX_VALUE);

        header.getChildren().addAll(textContent, logo);
        return header;
    }

    private HBox createAboutSection() {
        VBox content = new VBox(30);

        Label title1 = new Label("What is SaaY?");
        title1.getStyleClass().add("title");

        Text mainText1 = new Text(
                "Are you tired of spending countless hours searching for job offers that fit your preferences? " +
                        "The process of jumping from one website to another can feel overwhelming and time-consuming, " +
                        "especially when you're focused on finding opportunities in your desired field and location.\n" +
                        "\nThat's why we created SaaY, a powerful desktop application where you can streamline your" +
                        " search and discover your dream job with minimal effort."
        );
        mainText1.setFontSmoothingType(FontSmoothingType.LCD);
        mainText1.setWrappingWidth(600);
        mainText1.setFill(Color.web("#EDEDEF"));
        mainText1.setFont(Font.font("Inter", FontWeight.NORMAL, 24));


        content.getChildren().addAll(title1, mainText1);

        ImageView logo = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/logo.png")).toExternalForm()
        ));
        logo.setFitHeight(400);
        logo.setFitWidth(400);
        logo.setPreserveRatio(true);


        HBox section = new HBox(230, content, logo);
        section.setPrefWidth(1440);
//        section.setMaxHeight(629);
        section.setPadding(new Insets(100, 120, 100, 120));
        section.setBackground(new Background(new BackgroundFill(
                Color.web("#0F0E13"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        section.setAlignment(Pos.CENTER);

        HBox.setHgrow(content, Priority.ALWAYS);
        HBox.setHgrow(logo, Priority.NEVER); // Prevent logo from resizing
        content.setMaxWidth(Double.MAX_VALUE);

        section.setMaxWidth(Double.MAX_VALUE);
        return section;
    }

    private HBox createCompaniesSection() {
        VBox companiesSection = new VBox(30);

        Label title2 = new Label("Companies");
        title2.getStyleClass().add("title");


        Text mainText2 = new Text(
                "Discover the most in-demand positions and skills at your preferred companies, whether they involve" +
                        " soft skills or technical expertise. Dive deeper into company profiles and explore their " +
                        "latest job offers to find the perfect opportunity tailored to your aspirations."
        );
        mainText2.setFontSmoothingType(FontSmoothingType.LCD);
        mainText2.setWrappingWidth(580);
        mainText2.setFill(Color.web("#EDEDEF"));
        mainText2.setFont(Font.font("Inter", FontWeight.NORMAL, 24));

        Button button1 = new Button("Go to Companies Tab");
        button1.setPrefWidth(318);
        button1.setPrefHeight(55);
        button1.getStyleClass().add("section-button");
        button1.onActionProperty().set(event -> {
           getDashboard().switchPage("Companies");
           getDashboard().resetSideBar("Companies");
        });

        companiesSection.getChildren().addAll(title2, mainText2, button1);

        ImageView image2 = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/Entreprises.jpg")).toExternalForm()
        ));
        image2.setFitHeight(410);
        image2.setFitWidth(410);
        image2.setPreserveRatio(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox section = new HBox(230, image2,spacer, companiesSection);
        section.setAlignment(Pos.CENTER);

        section.setPrefHeight(400);
        section.setMaxHeight(629);
        section.setPrefWidth(1440);
        section.setPadding(new Insets(100, 120, 100, 120));
        section.setBackground(new Background(new BackgroundFill(
                Color.web("#19181D"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        HBox.setHgrow(companiesSection, Priority.ALWAYS);
        HBox.setHgrow(image2, Priority.NEVER); // Prevent logo from resizing
        companiesSection.setMaxWidth(Double.MAX_VALUE);

        section.setMaxWidth(Double.MAX_VALUE);
        //section.setPadding(new Insets(100, 160, 100, 210));
        section.setAlignment(Pos.CENTER);
        return section;
    }

    private VBox createJobOffersSection() {
        VBox section = new VBox(50);
        section.setAlignment(Pos.CENTER);
        section.setBackground(new Background(new BackgroundFill(
                Color.web("#0F0E13"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        Label title3 = new Label("Job Offers");
        title3.getStyleClass().add("title");

        HBox sectors = new HBox(15);
        sectors.setAlignment(Pos.CENTER);
        sectors.getChildren().addAll(createSector("Agriculture Sector","farmer.jpg"), createSector("Graphic Design Sector","graphic designer.jpg"), createSector("Education Sector", "Teacher.png"));

        StackPane sector4 = createSector("Finance Sector", "manager.jpg");
        StackPane sector5 = createSector("IT Sector", "software Engineer.jpg");

        // Initialize the carousel
        SectorCarousel carousel = new SectorCarousel(sectors);

        // Add additional sectors to the carousel
        carousel.addSector(sector4);
        carousel.addSector(sector5);

        // Start the animation
        carousel.startAnimation();

        HBox textContent = new HBox();
        textContent.setAlignment(Pos.CENTER);

        VBox saveJO = new VBox(33);
        saveJO.setAlignment(Pos.TOP_LEFT);
        Label subtitle1 = new Label("Save your Job Offer");
        subtitle1.setFont(Font.font("Inter", FontWeight.BOLD, 36));
        subtitle1.setTextFill(Color.web("#EDEDEF"));
        HBox saveIcon = new HBox(22);
        saveIcon.setAlignment(Pos.CENTER_LEFT);
        ImageView bookmarkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/blueBookmark.png")).toExternalForm()));
        bookmarkIcon.setFitHeight(37);
        bookmarkIcon.setFitWidth(37);
        bookmarkIcon.setPreserveRatio(true);
        Button save = new Button("", bookmarkIcon);
        save.setPrefSize(80, 80);
        save.setMinSize(80, 80);
        save.setMaxSize(80, 80);
        save.getStyleClass().add("job-card-bookmark-saved");
        save.setAlignment(Pos.CENTER);
        Label text1 = new Label("Found the job offers you’ve \nbeen looking for? Fantastic! \nSave them to your");
        text1.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        text1.setTextFill(Color.web("#EDEDEF"));
        Label bold = new Label("Saved Job");
        bold.setFont(Font.font("Inter", FontWeight.BOLD, 22));
        bold.setTextFill(Color.web("#EDEDEF"));
        Label tabs = new Label(" tab");
        tabs.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        tabs.setTextFill(Color.web("#EDEDEF"));
        TextFlow textflow = new TextFlow(text1, bold, tabs );
        textflow.setMaxHeight(108);
        textflow.setMaxWidth(310);
        saveIcon.getChildren().addAll(save, textflow);
        Label end = new Label("Revisit them later to explore the details \nor take the next steps");
        end.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        end.setTextFill(Color.web("#EDEDEF"));
        saveJO.getChildren().addAll(subtitle1, saveIcon, end);

        VBox filterJO = new VBox(33);
        filterJO.setAlignment(Pos.TOP_LEFT);
        Label subtitle2 = new Label("Filter Job Offers");
        subtitle2.setFont(Font.font("Inter", FontWeight.BOLD, 36));
        subtitle2.setTextFill(Color.web("#EDEDEF"));
        Label text2 = new Label("Narrow down the available job offers using a \nvariety of filters to find exactly what you’re \nlooking for:");
        text2.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        text2.setTextFill(Color.web("#EDEDEF"));
        HBox filters = new HBox(22);
        filters.setAlignment(Pos.CENTER_LEFT);
        Label filters1 = new Label("● Location\n" +
                "● Type of Contract\n" +
                "● Industry or Sector\n" +
                "● Soft Skills required");
        filters1.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        filters1.setTextFill(Color.web("#EDEDEF"));
        Label filters2 = new Label("● Remote work options\n" +
                "● Source Website\n" +
                "● Date of publication\n" +
                "● Hard Skills required");
        filters2.setFont(Font.font("Inter", FontWeight.NORMAL, 22));
        filters2.setTextFill(Color.web("#EDEDEF"));
        filters.getChildren().addAll(filters1, filters2);
        filterJO.getChildren().addAll(subtitle2, text2, filters);

        textContent.setMaxWidth(1037);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        textContent.getChildren().addAll(saveJO,spacer, filterJO);

        Button button2 = new Button("Go to Job Offers Tab");
        button2.setPrefWidth(318);
        button2.setPrefHeight(55);
        button2.getStyleClass().add("section-button");
        button2.onActionProperty().set(event -> {
            getDashboard().switchPage("Job Offers");
            getDashboard().resetSideBar("Job Offers");
        });

        section.getChildren().addAll(title3, sectors, textContent, button2);
        section.setPadding(new Insets(125, 0, 125, 0));

        return section;
    }

    private StackPane createSector(String title, String ImageName){
        StackPane sector = new StackPane();
        ImageView image = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/" + ImageName)).toExternalForm()));
        image.setFitHeight(229);
        image.setFitWidth(349);
        image.setPreserveRatio(true);
        HBox small_title = new HBox();
        Label subtitle = new Label(title);
        subtitle.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        subtitle.setTextFill(Color.WHITE);
        small_title.getChildren().addAll(subtitle);
        small_title.setAlignment(Pos.CENTER);
        small_title.setMaxHeight(73);
        small_title.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 66, 163, 0.72),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        sector.getChildren().addAll(image, small_title);
        sector.setAlignment(Pos.BOTTOM_CENTER);
        return sector;
    }

    private HBox createStatisticsSection() {
        VBox content = new VBox(45);

        Label title2 = new Label("Statistics");
        title2.getStyleClass().add("title");
//        title1.setTextFill(Color.web("#000000"));
//        title1.setFont(Font.font("Inter", FontWeight.BOLD, 48));


        Text mainText3 = new Text(
                "Explore key market trends, including the most in-demand positions, top skills, and locations with the highest job demand, and explore much more data to guide your job search effectively."
        );
        mainText3.setFontSmoothingType(FontSmoothingType.LCD);
        mainText3.setWrappingWidth(580);
        mainText3.setFill(Color.web("#EDEDEF"));
        mainText3.setFont(Font.font("Inter", FontWeight.NORMAL, 24));



        Button button3 = new Button("Go to Statistics Tab");
        button3.setPrefWidth(318);
        button3.setPrefHeight(55);
        button3.getStyleClass().add("section-button");
        button3.onActionProperty().set(event -> {
            getDashboard().switchPage("Statistics");
            getDashboard().resetSideBar("Statistics");
        });

        content.getChildren().addAll(title2, mainText3, button3);

        CategoryAxis xAxis = new CategoryAxis();

        NumberAxis yAxis = new NumberAxis(0, 250, 50);
        //yAxis.setTickUnit(50);

        //yAxis.setMinorTickCount(2);
        //yAxis.setMinorTickVisible(true);



        //xAxis.setTickLabelFont(Font.font("System", 14));
        //yAxis.setTickLabelFont(Font.font("System", 14));

        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue()); // Force integers
            }
        });
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setBarGap(0);
        barChart.setHorizontalGridLinesVisible(true);

        barChart.setLegendSide(Side.RIGHT);  // Move legend to right side
        barChart.setLegendVisible(true);
        barChart.setVerticalGridLinesVisible(false);

        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("Software Engineers");
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Jan", 80));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Feb", 100));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Mar", 190));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Apr", 210));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("May", 190));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Jun", 160));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Jul", 190));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Aug", 230));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Sep", 210));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Oct", 190));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Nov", 160));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Dec", 140));
        barChart.getData().add(dataSeries1);
        XYChart.Series dataSeries2 = new XYChart.Series();
        dataSeries2.setName("Doctors");
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Jan", 100));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Feb", 120));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Mar", 220));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Apr", 190));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("May", 170));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Jun", 130));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Jul", 180));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Aug", 190));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Sep", 180));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Oct", 220));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Nov", 180));
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Dec", 150));
        barChart.getData().add(dataSeries2);




        HBox section = new HBox(160, content, barChart);

        section.setPrefWidth(1440);
        section.setMaxHeight(629);
        section.setPadding(new Insets(100, 120, 100, 120));
        section.setBackground(new Background(new BackgroundFill(
                Color.web("#19181D"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        HBox.setHgrow(content, Priority.ALWAYS);
        HBox.setHgrow(barChart, Priority.ALWAYS);
        //content.setMaxWidth(Double.MAX_VALUE);

        section.setMaxWidth(Double.MAX_VALUE);

        return section;
    }
    private HBox createPredictionSection() {
        VBox predictionsSection = new VBox(30);

        Label title4 = new Label("Predictions");
        title4.getStyleClass().add("title");


        Text mainText4 = new Text(
                "Curious about what the future holds? \nThe Predictions page provides detailed \nforecasts on market demand, helping you \nanticipate trends in skills, jobs, and \nindustries. Stay ahead of the competition \nby gaining valuable insights into \nemerging opportunities."
        );
        mainText4.setFontSmoothingType(FontSmoothingType.LCD);
        mainText4.setWrappingWidth(580);
        mainText4.setFill(Color.web("#EDEDEF"));
        mainText4.setFont(Font.font("Inter", FontWeight.NORMAL, 24));

        Button button3 = new Button("Go to predictions page");
        button3.setPrefWidth(318);
        button3.setPrefHeight(55);
        button3.getStyleClass().add("section-button");
        button3.onActionProperty().set(event -> {
            getDashboard().switchPage("Companies");
            getDashboard().resetSideBar("Companies");
        });

        predictionsSection.getChildren().addAll(title4, mainText4, button3);

        ImageView image2 = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/future.png")).toExternalForm()
        ));
        image2.setFitHeight(410);
        image2.setFitWidth(410);
        image2.setPreserveRatio(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox section = new HBox(230, image2,spacer, predictionsSection);
        section.setAlignment(Pos.CENTER);

        section.setPrefHeight(400);
        section.setMaxHeight(629);
        section.setPrefWidth(1440);
        section.setPadding(new Insets(100, 120, 100, 120));
        section.setBackground(new Background(new BackgroundFill(
                Color.web("#0F0E13"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        HBox.setHgrow(predictionsSection, Priority.ALWAYS);
        HBox.setHgrow(image2, Priority.NEVER); // Prevent logo from resizing
        predictionsSection.setMaxWidth(Double.MAX_VALUE);

        section.setMaxWidth(Double.MAX_VALUE);
        //section.setPadding(new Insets(100, 160, 100, 210));
        section.setAlignment(Pos.CENTER);
        return section;
    }

    private Dashboard getDashboard() {
        return dashboard;
    }
}