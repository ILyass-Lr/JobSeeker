package com.example.jobseeker.dao;

import com.example.jobseeker.model.Company;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    private final Connection connection;

    public CompanyDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Company> getAllCompanies() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT * FROM companies";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                companies.add(mapResultSetToCompany(resultSet));
            }
        }

        return companies;
    }

    public List<Company> searchCompanies(String searchText) throws SQLException {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT * FROM companies WHERE LOWER(name) LIKE ? OR LOWER(industry) LIKE ? OR LOWER(headquarters_city) LIKE ?";
        String searchPattern = "%" + searchText.toLowerCase() + "%";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    companies.add(mapResultSetToCompany(resultSet));
                }
            }
        }

        return companies;
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("industry"),
                rs.getString("status"),
                rs.getInt("founded_year"),
                rs.getString("csize"),
                rs.getString("revenue"),
                rs.getString("headquarters_city"),
                rs.getString("headquarters_region"),
                rs.getString("headquarters_country"),
                rs.getInt("number_of_offices"),
                rs.getString("remote_work_policy"),
                rs.getDouble("glassdoor_rating"),
                rs.getDouble("employee_retention_rate"),
                rs.getString("benefits"),
                rs.getString("csr_initiatives")
        );
    }
}