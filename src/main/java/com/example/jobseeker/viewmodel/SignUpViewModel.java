package com.example.jobseeker.viewmodel;

import com.example.jobseeker.model.User;
import com.example.jobseeker.dao.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SignUpViewModel {

    private final UserDAO userDAO;

    // Observable properties
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty confirmPassword = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();

    public SignUpViewModel(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public boolean registerUser() {
        // Clear any previous error messages
        errorMessage.set("");

        // Validate input
        if (!validateInput()) {
            return false;
        }

        // Check if user already exists
        if (userDAO.userExists(email.get())) {
            errorMessage.set("Email already registered. Please use a different email.");
            return false;
        }

        // Create and save new user
        try {
            User newUser = new User(
                    email.get(),
                    password.get(),
                    "Candidate"
            );
            userDAO.saveUser(newUser);

            return true;
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            errorMessage.set("Error registering user!");
            return false;
        }
    }


    private boolean validateInput() {
        // Check for empty fields
        if (email.get() == null || email.get().trim().isEmpty()) {
            errorMessage.set("Email cannot be empty");
            return false;
        }

        if (password.get() == null || password.get().trim().isEmpty()) {
            errorMessage.set("Password cannot be empty");
            return false;
        }

        if (confirmPassword.get() == null || confirmPassword.get().trim().isEmpty()) {
            errorMessage.set("Please confirm your password");
            return false;
        }

        // Check email format
        if (!isValidEmail(email.get())) {
            errorMessage.set("Please enter a valid email address");
            return false;
        }

        // Check password length
        if (password.get().length() < 6) {
            errorMessage.set("Password must be at least 6 characters long");
            return false;
        }

        // Check if passwords match
        if (!password.get().equals(confirmPassword.get())) {
            errorMessage.set("Passwords do not match");
            return false;
        }

        return true;
    }

    /**
     * Simple email validation
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    // Getter methods for properties
    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty confirmPasswordProperty() {
        return confirmPassword;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }


}