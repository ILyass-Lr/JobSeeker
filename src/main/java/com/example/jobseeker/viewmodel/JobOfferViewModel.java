package com.example.jobseeker.viewmodel;

import com.example.jobseeker.dao.JobOfferDAO;

import com.example.jobseeker.model.JobOffer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class JobOfferViewModel {
    private final JobOfferDAO jobOfferDAO;

    // Observable collections for data binding
    private final ObservableList<JobOffer> jobOffers = FXCollections.observableArrayList();
    private final ObservableList<JobOffer> savedJobOffers = FXCollections.observableArrayList();
    private final ObjectProperty<JobOffer> selectedJobOffer = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> currentPage = new SimpleObjectProperty<>(1);
    private final ObjectProperty<Integer> totalPages = new SimpleObjectProperty<>(1);
    private final int itemsPerPage = 15;

    public JobOfferViewModel(JobOfferDAO jobOfferDAO) throws SQLException {
        this.jobOfferDAO = jobOfferDAO;
        loadJobOffers();
    }

    // Load all job offers from database
    public void loadJobOffers() throws SQLException {
        List<JobOffer> jobOfferList = jobOfferDAO.getAllJobOffers();
        jobOffers.clear();
        jobOffers.addAll(jobOfferList);

        // Set default selection if available
        if (!jobOffers.isEmpty()) {
            selectJobOffer(jobOffers.getFirst());
        }

        // Calculate total pages
        updateTotalPages();
    }

    // Load only saved job offers
    public void loadSavedJobOffers(int userId) throws SQLException {

        List<JobOffer> savedList = jobOfferDAO.getSavedJobOffers(userId);
        savedJobOffers.clear();
        savedJobOffers.addAll(savedList);

        // Update the selection if we're in the saved offers view
        if (!savedJobOffers.isEmpty()) {
            selectJobOffer(savedJobOffers.getFirst());
        }
    }

    public ObservableList<JobOffer> getSavedJobOffers() {
        //loadSavedJobOffers();
        return savedJobOffers;
    }

    // Toggle the saved state of a job offer and persist to database
    public void toggleJobOfferSavedState(int userId, JobOffer jobOffer) throws SQLException {
        boolean newSavedState = !jobOfferDAO.isJobOfferSaved(userId, jobOffer.getId());
        jobOfferDAO.updateJobOfferSavedState(jobOffer.getId(), userId);


        // Update our collections to reflect the change
        if (newSavedState) {
            if (!savedJobOffers.contains(jobOffer)) {
                savedJobOffers.add(jobOffer);
            }
        } else {
            savedJobOffers.remove(jobOffer);
        }
    }

    // Search companies by name, industry, or location
    public void searchJobOffers(String searchText, String dateFilter, String contractFilter, String locationFilter, String teleworkFilter) throws SQLException {
        String contractSearch, locationSearch, teleworkSearch;
        int daysNumber = switch (dateFilter) {
            case "Past 24 hours" -> 1;
            case "Past week" -> 7;
            case "Past month" -> 30;
            default -> 5*356;
        };
        String searchPattern = "%" + searchText.toLowerCase() + "%";
        contractSearch = (!contractFilter.equalsIgnoreCase("Any Contract Type") && !contractFilter.isEmpty()) ? contractFilter : "%";
        locationSearch = (!locationFilter.equalsIgnoreCase("Any Location") && !locationFilter.isEmpty()) ? locationFilter : "%";
        teleworkSearch = (!teleworkFilter.equalsIgnoreCase("Any TeleWork Type") && !teleworkFilter.isEmpty()) ? teleworkFilter : "%";


        List<JobOffer> filteredJobOffers = jobOfferDAO.searchJobOffers(searchPattern, daysNumber, contractSearch, locationSearch, teleworkSearch);
        jobOffers.clear();
        jobOffers.addAll(filteredJobOffers);

        // Reset to first page and update total pages
        currentPage.set(1);
        updateTotalPages();
    }

    public void searchJobOffersByName(String searchText) throws SQLException {
        List<JobOffer> filteredJobOffers = jobOfferDAO.searchJobOffersByCompany(searchText);


        jobOffers.clear();
        jobOffers.addAll(filteredJobOffers);

        // Reset to first page and update total pages
        currentPage.set(1);
        updateTotalPages();
    }

    public void searchJobOffersByRecruiter(int recruiterId) throws SQLException {
        List<JobOffer> filteredJobOffers = jobOfferDAO.searchJobOffersByRecruiter(recruiterId);


        jobOffers.clear();
        jobOffers.addAll(filteredJobOffers);

        // Reset to first page and update total pages
        currentPage.set(1);
        updateTotalPages();
    }

    // Select a job offer
    public void selectJobOffer(JobOffer jobOffer) {
        selectedJobOffer.set(jobOffer);
    }

    // Navigation methods
    public void nextPage() {
        if (currentPage.get() < totalPages.get()) {
            currentPage.set(currentPage.get() + 1);
        }
    }

    public void previousPage() {
        if (currentPage.get() > 1) {
            currentPage.set(currentPage.get() - 1);
        }
    }

    public JobOffer getFirstJobOffer() {
        return getJobOffers().getFirst();
    }

    private void updateTotalPages() {
        int total = (int) Math.ceil((double) jobOffers.size() / itemsPerPage);
        totalPages.set(Math.max(1, total));

        // Make sure current page is valid
        if (currentPage.get() > totalPages.get()) {
            currentPage.set(totalPages.get());
        }
    }

    // Get JobOffers for current page
    public List<JobOffer> getCurrentPageJobOffers() {
        int startIndex = (currentPage.get() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, jobOffers.size());

        if (startIndex >= jobOffers.size()) {
            startIndex = 0;
            endIndex = Math.min(itemsPerPage, jobOffers.size());
            currentPage.set(1);
        }

        return jobOffers.subList(startIndex, endIndex);
    }

    // Getters for observable collections and properties
    public ObservableList<JobOffer> getJobOffers() {
        return jobOffers;
    }
    public boolean isSaved(int userId, JobOffer jobOffer) throws SQLException {
        return jobOfferDAO.isJobOfferSaved(userId, jobOffer.getId());
    }

    public ObjectProperty<JobOffer> selectedJobOfferProperty() {
        return selectedJobOffer;
    }

    public JobOffer getSelectedJobOffer() {
        return selectedJobOffer.get();
    }

    public ObjectProperty<Integer> currentPageProperty() {
        return currentPage;
    }

    public int getCurrentPage() {
        return currentPage.get();
    }

    public ObjectProperty<Integer> totalPagesProperty() {
        return totalPages;
    }

    public int getTotalPages() {
        return totalPages.get();
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }
}