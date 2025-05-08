package com.example.jobseeker;

import javafx.application.Platform;
import javafx.scene.chart.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class StatisticsPage extends Page {
    private BarChart<String, Number> barChart;
    private PieChart industryPieChart;
    private BarChart<String, Number> locationChart;
    private VBox chartContainer;

    public StatisticsPage(Dashboard dashboard) throws SQLException {
        super(dashboard);
    }

    @Override
    protected void initialize() throws SQLException {
        initializeChartContainer();
        setPadding(new Insets(40));
        getChildren().add(chartContainer);

        setBackground(new Background(new BackgroundFill(
                Color.web("#0F0E13"),
                CornerRadii.EMPTY,
                null
        )));
    }

    private void initializeChartContainer() throws SQLException {
        // Main title
        HBox title = createSectionTitle("Job Offers Analytics Dashboard");

        // Initialize all charts
        initializeBarChart();
        initializeIndustryPieChart();
        initializeLocationChart();

        // Create bottom container for pie and location charts
        HBox bottomChartsContainer = new HBox(20);
        bottomChartsContainer.setPrefWidth(Double.MAX_VALUE);
        bottomChartsContainer.setAlignment(Pos.CENTER);

        // Create containers for each bottom chart with titles
        VBox industryContainer = new VBox(10);
        VBox locationContainer = new VBox(10);
        HBox.setHgrow(industryContainer, Priority.ALWAYS);
        HBox.setHgrow(locationContainer, Priority.ALWAYS);

        // Add section titles
        industryContainer.getChildren().addAll(
                createSectionTitle("Industry Distribution"),
                createChartContainer(industryPieChart, 600, 350)
        );

        locationContainer.getChildren().addAll(
                createSectionTitle("Jobs by Location"),
                createChartContainer(locationChart, 600, 350)
        );

        bottomChartsContainer.getChildren().addAll(industryContainer, locationContainer);

        // Main container
        chartContainer = new VBox(15);
        chartContainer.setPrefWidth(Double.MAX_VALUE);
        chartContainer.setAlignment(Pos.TOP_CENTER);
        chartContainer.setBackground(new Background(new BackgroundFill(
                Color.web("#0F0E13"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        // Create container for top bar chart
        VBox topChartContainer = createChartContainer(barChart, 800, 350);

        // Add all components to main container
        chartContainer.getChildren().addAll(title, topChartContainer, bottomChartsContainer);

        // Set container properties
        chartContainer.setPrefWidth(800);
        chartContainer.setPrefHeight(800);
        VBox.setMargin(chartContainer, new Insets(10));

        // Add shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.web("#131217"));
        chartContainer.setEffect(dropShadow);
        chartContainer.setBorder(new Border(new BorderStroke(
                Color.web("#19181D"),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderStroke.MEDIUM
        )));
    }

    private HBox createSectionTitle(String titleText) {
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(titleText.toUpperCase());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleContainer.setPrefWidth(Double.MAX_VALUE);
        titleContainer.getChildren().add(titleLabel);
        titleContainer.setPrefHeight(60);
        titleContainer.setPadding(new Insets(5, 10, 5, 10));
        titleContainer.setBackground(new Background(new BackgroundFill(
                Color.web("#19181D"),
                CornerRadii.EMPTY,
                null
        )));
        return titleContainer;
    }

//    private VBox createChartContainer(Chart chart, double width, double height) {
//        VBox container = new VBox();
//        container.setMinWidth(700);
//        container.setAlignment(Pos.CENTER);
//        //container.setPrefSize(width, height);
//        container.getChildren().add(chart);
//        container.setStyle("-fx-background-color: #19181D;");
//        container.setPadding(new Insets(15));
//        return container;
//    }
private VBox createChartContainer(Chart chart, double width, double height) {
    VBox container = new VBox();
    container.setMinWidth(700);
    container.setMinHeight(height); // Add minimum height
    container.setAlignment(Pos.CENTER);
    container.setPrefSize(width, height); // Make sure to set preferred size
    container.getChildren().add(chart);
    container.setStyle("-fx-background-color: #19181D;");
    container.setPadding(new Insets(15));

    // Give the chart some size constraints
    chart.setPrefSize(width - 30, height - 30); // Account for padding
    chart.setMinSize(width - 30, height - 30);

    return container;
}

    private void initializeBarChart() throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 14));
        xAxis.setTickLabelFill(Color.WHITE);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Job Offers");
        yAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 14));
        yAxis.setTickLabelFill(Color.WHITE);

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefSize(750, 350);
        barChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-plot-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-horizontal-grid-lines-visible: true;" +
                        "-fx-horizontal-zero-line-visible: true;" +
                        "-fx-grid-line-color: white;" +
                        "-fx-stroke: white;"
        );

        // Get data for last 12 months
        YearMonth currentYearMonth = YearMonth.now();
        List<Integer> monthCounts = new ArrayList<>(Collections.nCopies(12, 0));
        List<String> monthLabels = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 11; i >= 0; i--) {
            monthLabels.add(currentYearMonth.minusMonths(i).format(formatter));
        }

        // Count job offers
        for (JobOffer offer : DatabaseUtil.getAllJobOffers()) {
            YearMonth offerYearMonth = YearMonth.from(offer.getPublishDate());
            long monthsBetween = currentYearMonth.until(offerYearMonth, ChronoUnit.MONTHS);
            if (monthsBetween >= -11 && monthsBetween <= 0) {
                monthCounts.set(11 + (int)monthsBetween, monthCounts.get(11 + (int)monthsBetween) + 1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 12; i++) {
            series.getData().add(new XYChart.Data<>(monthLabels.get(i), monthCounts.get(i)));
        }

        barChart.getData().add(series);
        series.getData().forEach(data ->
                data.getNode().setStyle("-fx-bar-fill: #7F47DD;")
        );
    }

    private void initializeIndustryPieChart() throws SQLException {
        industryPieChart = new PieChart();
        industryPieChart.setLabelsVisible(true);
        industryPieChart.setLegendVisible(true);
        industryPieChart.setPrefSize(600, 350);

        // Set chart background and text colors
        industryPieChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-legend-text-fill: white;" +
                        "-fx-pie-label-fill: white;"
        );

        Map<String, Integer> industryCount = new HashMap<>();
        for (JobOffer offer : DatabaseUtil.getAllJobOffers()) {
            String industry = offer.getIndustry();
            industryCount.put(industry, industryCount.getOrDefault(industry, 0) + 1);
        }

        // Define colors
        List<String> colors = Arrays.asList(
                "#7F47DD", "#FF6B6B", "#4ECDC4", "#45B7D1",
                "#96CEB4", "#FFEEAD", "#D4A5A5", "#9B59B6",
                "#FF9F43", "#00B894", "#FF6B6B", "#5F27CD",
                "#341f97", "#01a3a4", "#ee5253", "#222f3e"
        );

        // Calculate total for percentages
        int total = industryCount.values().stream().mapToInt(Integer::intValue).sum();

        // Create and add data to chart

        Platform.runLater(() -> {
            int colorIndex = 0;
            for (Map.Entry<String, Integer> entry : industryCount.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / total;
                PieChart.Data slice = new PieChart.Data(
                        String.format("%s (%.1f%%)", entry.getKey(), percentage),
                        entry.getValue()
                );
                industryPieChart.getData().add(slice);
                colorIndex++;
            }
        });


        // Apply colors after data is added
        applyColorsToChart(industryPieChart, colors);
    }

    private void applyColorsToChart(PieChart chart, List<String> colors) {
        // Apply CSS styling to each pie slice
        int colorIndex = 0;
        for (PieChart.Data data : chart.getData()) {
            String color = colors.get(colorIndex % colors.size());

            // Apply color to the slice
            data.getNode().setStyle(
                    "-fx-pie-color: " + color + ";" +
                            "-fx-background-color: " + color + ";"
            );

            // Also set the legend color
            Region legendSymbol = (Region) chart.lookup(".chart-legend-item-symbol");
            if (legendSymbol != null) {
                legendSymbol.setStyle("-fx-background-color: " + color + ";");
            }

            colorIndex++;
        }

        // Set label colors to white
        chart.lookupAll(".chart-pie-label").forEach(node ->
                node.setStyle("-fx-text-fill: white;")
        );

        chart.lookupAll(".chart-legend-item").forEach(node ->
                node.setStyle("-fx-text-fill: white;")
        );
    }

    private void initializeLocationChart() throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 12));
        xAxis.setTickLabelFill(Color.WHITE);
        //xAxis.setTickLabelRotation(45);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Jobs");
        yAxis.setTickLabelFont(Font.font("System", FontWeight.NORMAL, 14));
        yAxis.setTickLabelFill(Color.WHITE);

        locationChart = new BarChart<>(xAxis, yAxis);
        locationChart.setLegendVisible(false);
        locationChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-plot-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-horizontal-grid-lines-visible: true;" +
                        "-fx-horizontal-zero-line-visible: true;" +
                        "-fx-grid-line-color: white;" +
                        "-fx-stroke: white;"
        );

        Map<String, Integer> locationCount = new HashMap<>();
        for (JobOffer offer : DatabaseUtil.getAllJobOffers()) {
            String city = offer.getLocation().getCity();
            locationCount.put(city, locationCount.getOrDefault(city, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        locationCount.forEach((city, count) ->
                series.getData().add(new XYChart.Data<>(city, count))
        );

        locationChart.getData().add(series);
        series.getData().forEach(data ->
                data.getNode().setStyle("-fx-bar-fill: #45B7D1;")
        );
    }
}