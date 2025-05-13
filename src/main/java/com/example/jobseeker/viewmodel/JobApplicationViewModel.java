package com.example.jobseeker.viewmodel;

import com.example.jobseeker.Dashboard;
import com.example.jobseeker.dao.JobApplicationDAO;
import com.example.jobseeker.dao.UserDAO;
import com.example.jobseeker.model.JobApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobApplicationViewModel {
    private final JobApplicationDAO jobApplicationDAO;
    private final UserDAO userDAO;
    private final StringProperty contactNumber = new SimpleStringProperty("");
    private final StringProperty jobProfile = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty();
    private Map<Integer, JobApplicationViewModel.AppSub> submmittedApplications = new HashMap<Integer, JobApplicationViewModel.AppSub>();

   // private List<JobApplication> applications;
    static public enum AppSub { SUBMITTED, APPROVED, REJECTED }
    public void loadSubmissions(int userId) throws SQLException {
        submmittedApplications.putAll(jobApplicationDAO.loadSubmmittedApplications(userId));
        System.out.println("result: " + submmittedApplications);
    }

    public Map<Integer, JobApplicationViewModel.AppSub> getSubmmittedApplications() {
        return submmittedApplications;
    }

//    public List<JobApplication> getApplications() {
//        return applications;
//    }


    public String getContactNumber() {
        return contactNumber.get();
    }

    public StringProperty contactNumberProperty() {
        return contactNumber;
    }

    public String getJobProfile() {
        return jobProfile.get();
    }

    public StringProperty jobProfileProperty() {
        return jobProfile;
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }


    public JobApplicationViewModel(JobApplicationDAO jobApplicationDAO, UserDAO userDAO) {
        this.jobApplicationDAO = jobApplicationDAO;
        this.userDAO = userDAO;
    }

    public String getUserEmail(int userId) throws SQLException {
        return userDAO.getUserEmail(userId);
    }



    public boolean isValidFile(File file, String fileType) {
        if (file == null) {
            errorMessage.set(fileType + " not selected");
            return false;
        } else if (!file.exists()) {
            errorMessage.set(fileType + " file does not exist");
            return false;
        } else if (file.length() > 5 * 1024 * 1024) { // 5MB file size limit
            errorMessage.set("file size exceeds 5MB limit");
            return false;
        } else if (!file.getName().toLowerCase().endsWith(".pdf")) {
            errorMessage.set(fileType + " must be a PDF file");
            return false;
        }
        return true;
    }

    public boolean isValidContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            errorMessage.set("Contact number is required");
            return false;
        } else if (!contactNumber.matches("^[0-9+\\-\\s]{10,15}$")) {
            errorMessage.set("Please enter a valid contact number");
            return false;
        }
        return true;
    }

    public boolean isValidJobProfile(String jobProfile) {
        if (jobProfile == null || jobProfile.trim().isEmpty()) {
            errorMessage.set("Job profile is required");
            return false;
        } else if (jobProfile.trim().length() < 3) {
            errorMessage.set("Invalid Job profile");
            return false;
        }
        return true;
    }

    // Main validation method that checks all inputs
    public boolean validateJobApplication(
            String contactNumber,
            String jobProfile,
            File cvFile,
            File coverLetterFile) {

        // Clear any previous error messages
        errorMessage.set("");

        // Validate contact number
        if (!isValidContactNumber(contactNumber)) {
            return false;
        }

        // Validate job profile
        if (!isValidJobProfile(jobProfile)) {
            return false;
        }

        // Validate CV file
        if (!isValidFile(cvFile, "CV")) {
            return false;
        }

        // Validate cover letter file
        if (!isValidFile(coverLetterFile, "Cover Letter")) {
            return false;
        }

        return true;
    }

    public boolean submitJobApplication(
            int userId,
            int jobOfferId,
            String contactNumber,
            String jobProfile,
            File cvFile,
            File coverLetterFile
    ) {
        // Validate all inputs first
        if (!validateJobApplication(contactNumber, jobProfile, cvFile, coverLetterFile)) {
            return false;
        }

        try {
            // Read files into byte arrays
            byte[] cvFileBytes = Files.readAllBytes(cvFile.toPath());
            byte[] coverLetterFileBytes = Files.readAllBytes(coverLetterFile.toPath());

            // Get file types
            String cvFilename = cvFile.getName();
            String cvFiletype = "application/pdf";

            String coverLetterFilename = coverLetterFile.getName();
            String coverLetterFiletype = "application/pdf";

            JobApplication jobApplication = new JobApplication(
                    new Timestamp(System.currentTimeMillis()),
                    cvFileBytes,
                    cvFilename,
                    cvFiletype,
                    coverLetterFileBytes,
                    coverLetterFilename,
                    coverLetterFiletype,
                    "SUBMITTED",
                    userId,
                    0
            );

            // Try to insert into the database
            boolean insertSuccess = jobApplicationDAO.insertJobApplication(userId, jobOfferId, jobApplication);
            boolean insertUserDetails = userDAO.insertUserDetails(contactNumber, jobProfile, userId);

            if (!insertSuccess || !insertUserDetails) {
                errorMessage.set("Failed to submit job application. Please try again.");
                return false;
            }

            return true;
        } catch (IOException e) {
            errorMessage.set("Error reading files: " + e.getMessage());
            return false;
        } catch (Exception e) {
            errorMessage.set("Error submitting application: " + e.getMessage());
            return false;
        }

    }

    public List<JobApplication> getApplications(int jobOfferId) {
       return jobApplicationDAO.getApplications(jobOfferId);
    }

    public List<JobApplication> getCandidateApplications(int candidateId) {

        return List.of(jobApplicationDAO.getApplicationsByCandidate(candidateId));
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        return jobApplicationDAO.updateApplicationStatus(applicationId, status);
}
}