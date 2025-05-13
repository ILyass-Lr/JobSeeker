package com.example.jobseeker.view;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.User;
import com.example.jobseeker.viewmodel.SignInViewModel;
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
import javafx.scene.text.TextAlignment;

import java.sql.SQLException;
import java.util.Objects;

public class SignInView extends VBox {

    private TextField emailTextField;
    private PasswordField passwordField;
    private Button signInButton;
    private Button signUpButton;
    private Label errorMessageLabel;
    private SignInViewModel viewModel;
    private Dashboard dashboard;

    public SignInView(SignInViewModel viewModel, Dashboard dashboard) {
        this.viewModel = viewModel;
        this.dashboard = dashboard;
        setupView();
        bindViewModel();
        setupEventHandlers();
    }

    private void bindViewModel() {
        // Bind text fields to view model properties
        viewModel.emailProperty().bindBidirectional(getEmailTextField().textProperty());
        viewModel.passwordProperty().bindBidirectional(getPasswordField().textProperty());
        viewModel.errorMessageProperty().addListener((observable, oldValue, newValue) -> {
            errorMessageLabel.setText(newValue);
            errorMessageLabel.setTextFill(Color.RED);
        });
    }

    private void setupEventHandlers() {
        // Handle register button click
        signInButton.setOnAction(event -> {
            User user = viewModel.connectUser();
            if (user != null) {
                    dashboard.setCurrentUser(user);
            }
        });

        signUpButton.setOnAction(event -> {
                Dashboard.pages.put("Sign up - Sign in", Dashboard.signUpView);
            try {
                dashboard.switchPage("Sign up - Sign in");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void clearView(){
        emailTextField.setText("");
        passwordField.setText("");

    }

    private void setupView() {
        this.setMinHeight(1018);
        // Main container setup
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(28, 50, 30, 50));
        this.setSpacing(50);
        this.setStyle("-fx-background-color: #19181D;");

        // Page title
        Text pageTitle = new Text("Sign In");
        pageTitle.setFont(Font.font("Inter", FontWeight.BOLD, 64));
        pageTitle.setFill(Color.web("#EDEDED"));

        // Main content container
        HBox contentContainer = new HBox();
        contentContainer.setSpacing(70);
        contentContainer.setAlignment(Pos.CENTER);

        // Left side - form container
        VBox formContainer = createFormContainer();

        // Right side - information container
        VBox infoContainer = createInfoContainer();

        contentContainer.getChildren().addAll(formContainer, infoContainer);

        this.getChildren().addAll(pageTitle, contentContainer);
    }

    private VBox createFormContainer() {
        VBox formContainer = new VBox();
        formContainer.setPrefSize(710, 750);
        formContainer.setSpacing(20);
        formContainer.setAlignment(Pos.TOP_LEFT);
        formContainer.setPadding(new Insets(40));
        formContainer.setStyle("-fx-background-color: #151B45; -fx-border-color: #7B4B94; -fx-border-width: 2; -fx-border-radius: 15; -fx-background-radius: 15;");

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        emailLabel.setTextFill(Color.web("#EDEDED"));



        emailTextField = new TextField();
        emailTextField.setMaxWidth(612);
        emailTextField.setPrefHeight(70);
        emailTextField.setStyle("-fx-background-radius: 15; -fx-font-size: 20px;");
        emailTextField.setPromptText("example@gmail.com");
        emailTextField.setPadding(new Insets(0, 0, 0, 25));
//        emailTextField.setTextFormatter();

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Inter", FontWeight.BOLD, 30));
        passwordLabel.setTextFill(Color.web("#EDEDED"));

        passwordField = new PasswordField();
        passwordField.setMaxWidth(612);
        passwordField.setPrefHeight(70);
        passwordField.setStyle("-fx-background-radius: 15; -fx-font-size: 18px;");
        passwordField.setPadding(new Insets(0, 0, 0, 25));


        // Error message label
        errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        errorMessageLabel.setPrefHeight(30);
        errorMessageLabel.setFont(Font.font("Inter", 20));
        errorMessageLabel.setAlignment(Pos.CENTER);

        // Register Button
        signInButton = new Button("Sign In");
        signInButton.setPrefSize(612, 70);
        signInButton.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        signInButton.getStyleClass().add("signIn-button");

        // Or separator
        HBox orSeparator = new HBox();
        orSeparator.setAlignment(Pos.CENTER);
        orSeparator.setPrefWidth(612);

        Label orLabel = new Label("Or - Don't Have an account?");
        orLabel.setFont(Font.font("Inter", 18));
        orLabel.setTextFill(Color.web("#EDEDED"));

        orSeparator.getChildren().add(orLabel);

        // Google Sign in Button
        signUpButton = new Button("Sign Up");
        signUpButton.setPrefSize(612, 70);
        signUpButton.setFont(Font.font("Inter", FontWeight.BOLD, 24));
        signUpButton.getStyleClass().add("section-button");


        formContainer.getChildren().addAll(
                emailLabel, emailTextField,
                passwordLabel, passwordField,
                errorMessageLabel,
                signInButton,
                orSeparator,
                signUpButton
        );

        return formContainer;
    }

    private VBox createInfoContainer() {
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(25);
        infoContainer.setAlignment(Pos.CENTER);

        // Title
        Text infoTitle = new Text("Find Your\nDream Job");
        infoTitle.setFont(Font.font("Inter", FontWeight.BOLD, 48));
        infoTitle.setFill(Color.WHITE);
        infoTitle.setTextAlignment(TextAlignment.CENTER);



        ImageView image = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResource("/com/example/jobseeker/dreamJob.png")).toExternalForm()
        ));
        image.setFitHeight(525);
        image.setFitWidth(477);
        image.setPreserveRatio(true);



        Label bulletPoint = new Label(
                """
                ● Save Job Offers You Love.\n
                ● Quick and Easy Application Process.\n
                ● Track Your Job Application Status."""
        );

        bulletPoint.setFont(Font.font("Inter", FontWeight.NORMAL, 20));
        bulletPoint.setTextFill(Color.WHITE);



        infoContainer.getChildren().addAll(infoTitle, image, bulletPoint);

        return infoContainer;
    }

    // Getter methods for controllers to access UI components
    public TextField getEmailTextField() {
        return emailTextField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public Button getSignUpButton() {
        return signUpButton;
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }
}
