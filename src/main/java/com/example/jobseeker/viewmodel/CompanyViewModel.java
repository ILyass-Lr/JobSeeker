package com.example.jobseeker.viewmodel;

import com.example.jobseeker.dao.CompanyDAO;
import com.example.jobseeker.model.Company;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class CompanyViewModel {
    private final CompanyDAO companyDAO;

    // Observable collections for data binding
    private final ObservableList<Company> companies = FXCollections.observableArrayList();
    private final ObjectProperty<Company> selectedCompany = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> currentPage = new SimpleObjectProperty<>(1);
    private final ObjectProperty<Integer> totalPages = new SimpleObjectProperty<>(1);
    private final int itemsPerPage = 15;

    public CompanyViewModel(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    // Load all companies from database
    public void loadCompanies() throws SQLException {
        List<Company> companyList = companyDAO.getAllCompanies();
        companies.clear();
        companies.addAll(companyList);

        // Calculate total pages
        updateTotalPages();

        // Set default selection if available
        if (!companies.isEmpty()) {
            selectedCompany.set(companies.get(0));
        }
    }

    // Search companies by name, industry, or location
    public void searchCompanies(String searchText) throws SQLException {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadCompanies();
        } else {
            List<Company> filteredCompanies = companyDAO.searchCompanies(searchText);
            companies.clear();
            companies.addAll(filteredCompanies);

            // Reset to first page and update total pages
            currentPage.set(1);
            updateTotalPages();
        }
    }

    // Select a company
    public void selectCompany(Company company) {
        selectedCompany.set(company);
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

    private void updateTotalPages() {
        int total = (int) Math.ceil((double) companies.size() / itemsPerPage);
        totalPages.set(Math.max(1, total));

        // Make sure current page is valid
        if (currentPage.get() > totalPages.get()) {
            currentPage.set(totalPages.get());
        }
    }

    // Get companies for current page
    public List<Company> getCurrentPageCompanies() {
        int startIndex = (currentPage.get() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, companies.size());

        if (startIndex >= companies.size()) {
            startIndex = 0;
            endIndex = Math.min(itemsPerPage, companies.size());
            currentPage.set(1);
        }

        return companies.subList(startIndex, endIndex);
    }

    // Getters for observable collections and properties
    public ObservableList<Company> getCompanies() {
        return companies;
    }

    public ObjectProperty<Company> selectedCompanyProperty() {
        return selectedCompany;
    }

    public Company getSelectedCompany() {
        return selectedCompany.get();
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