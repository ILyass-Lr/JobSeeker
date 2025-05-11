package com.example.jobseeker.viewmodel;

import com.example.jobseeker.model.User;
import com.example.jobseeker.dao.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SignInViewModel {

    private final UserDAO userDAO;

    // Observable properties
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();

    public SignInViewModel(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public User connectUser() {
        // Clear any previous error messages
        errorMessage.set("");

        // Validate input
        if (!validateInput()) {
            return null;
        }

        // Check if user already exists
        if (!userDAO.userExists(email.get())) {
            errorMessage.set("Email Not Found!");
            return null;
        }else{
            User user = userDAO.findUserByEmailAndPassword(email.get(), password.get());
            if (user == null) {
                errorMessage.set("Invalid Password!");
            }
            return user;
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


        // Check email format
        if (!isValidEmail(email.get())) {
            errorMessage.set("Please enter a valid email address");
            return false;
        }


        return true;
    }


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

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
}