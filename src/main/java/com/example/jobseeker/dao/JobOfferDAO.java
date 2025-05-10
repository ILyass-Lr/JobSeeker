package com.example.jobseeker.dao;

import com.example.jobseeker.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JobOfferDAO {
    private final Connection connection;

    public JobOfferDAO(Connection connection) {
        this.connection = connection;
    }

    public List<JobOffer> getAllJobOffers() throws SQLException {
        List<JobOffer> jobOffers = new ArrayList<>();
        String sql = """
        SELECT DISTINCT
            j.id, j.title, j.description, j.requirements, j.contract, j.telework,
            j.salary, j.industry, j.post_date, j.deadline, j.is_saved,
            e.elevel as edu_level, e.field as edu_field, e.diploma,
            ex.min_years, ex.max_years, ex.exlevel as exp_level, ex.description as exp_description,
            l.city, l.region, l.country, l.address,
            c.name as company_name
        FROM job_posts j
        LEFT JOIN job_education e ON j.id = e.job_post_id
        LEFT JOIN job_experience ex ON j.id = ex.job_post_id
        LEFT JOIN job_locations l ON j.id = l.job_post_id
        LEFT JOIN companies c ON j.company = c.id
        ORDER BY j.post_date DESC
        """;


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JobOffer offer = mapResultSetToJobOffer(rs);
                    loadAdditionalData(offer);
                    jobOffers.add(offer);
                }
            }
        }
        return jobOffers;
    }

    private void loadAdditionalData(JobOffer offer) throws SQLException {
        int jobId = offer.getId();
        offer.getHardSkills().addAll(getHardSkills(jobId));
        offer.getSoftSkills().addAll(getSoftSkills(jobId));
        offer.getLanguages().putAll(getLanguages(jobId));
    }

    private List<String> getHardSkills(int jobId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM job_hard_skills WHERE job_post_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String skillName = rs.getString("skill_name");
                    if (skillName != null) {
                        skills.add(skillName);
                    }
                }
            }
        }
        return skills;
    }

    private List<String> getSoftSkills(int jobId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM job_soft_skills WHERE job_post_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String skillName = rs.getString("skill_name");
                    if (skillName != null) {
                        skills.add(skillName);
                    }
                }
            }
        }
        return skills;
    }

    private Map<String, String> getLanguages( int jobId) throws SQLException {
        Map<String, String> languages = new HashMap<>();
        String sql = "SELECT language_name, proficiency_level FROM job_languages WHERE job_post_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String langName = rs.getString("language_name");
                    String profLevel = rs.getString("proficiency_level");
                    if (langName != null && profLevel != null) {
                        languages.put(langName, profLevel);
                    }
                }
            }
        }
        return languages;
    }

    public List<JobOffer> searchJobOffersByCompany(String searchText) throws SQLException {
        List<JobOffer> jobOffers = new ArrayList<>();
        String query = """
            SELECT DISTINCT
                j.id, j.title, j.description, j.requirements, j.contract, j.telework,
                j.salary, j.industry, j.post_date, j.deadline, j.is_saved,
                e.elevel AS edu_level, e.field AS edu_field, e.diploma,
                ex.min_years, ex.max_years, ex.exlevel AS exp_level, ex.description AS exp_description,
                l.city, l.region, l.country, l.address,
                c.name AS company_name
            FROM job_posts j
                     LEFT JOIN job_education e ON j.id = e.job_post_id
                     LEFT JOIN job_experience ex ON j.id = ex.job_post_id
                     LEFT JOIN job_locations l ON j.id = l.job_post_id
                     LEFT JOIN companies c ON j.company = c.id
            WHERE  (
                LOWER(c.name) LIKE ?
                OR LOWER(j.title) LIKE ?
                OR LOWER(j.description) LIKE ?
            )
            ORDER BY j.post_date DESC
            """;
        String searchPattern = "%" + searchText.toLowerCase() + "%";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    jobOffers.add(mapResultSetToJobOffer(resultSet));
                }
            }
        }

        return jobOffers;
    }


    public List<JobOffer> searchJobOffers(String searchText, String dateFilter, String contractFilter, String locationFilter, String teleworkFilter) throws SQLException {
        List<JobOffer> jobOffers = new ArrayList<>();
        String query = """
            SELECT DISTINCT
                j.id, j.title, j.description, j.requirements, j.contract, j.telework,
                j.salary, j.industry, j.post_date, j.deadline, j.is_saved,
                e.elevel AS edu_level, e.field AS edu_field, e.diploma,
                ex.min_years, ex.max_years, ex.exlevel AS exp_level, ex.description AS exp_description,
                l.city, l.region, l.country, l.address,
                c.name AS company_name
            FROM job_posts j
                     LEFT JOIN job_education e ON j.id = e.job_post_id
                     LEFT JOIN job_experience ex ON j.id = ex.job_post_id
                     LEFT JOIN job_locations l ON j.id = l.job_post_id
                     LEFT JOIN companies c ON j.company = c.id
            WHERE (
                LOWER(j.title) LIKE ?
                OR LOWER(c.name) LIKE ?
                OR LOWER(j.description) LIKE ?
                )
              AND j.post_date >= SYSDATE - ?
              AND j.contract = ?
              AND j.telework = ?
              AND l.city = ?
            ORDER BY j.post_date DESC
            """;
        String searchPattern = "%" + searchText.toLowerCase() + "%";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            int daysNumber = switch (dateFilter) {
                case "Past 24 hours" -> 1;
                case "Past week" -> 7;
                case "Past month" -> 30;
                default -> 5*356;
            };
            statement.setInt(4, daysNumber);
            statement.setString(5, contractFilter);
            statement.setString(6, locationFilter);
            statement.setString(7, teleworkFilter);



            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    jobOffers.add(mapResultSetToJobOffer(resultSet));
                }
            }
        }

        return jobOffers;
    }

    public void updateJobOfferSavedState(JobOffer jobOffer) throws SQLException {
        String sql = "UPDATE job_posts SET is_saved = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, jobOffer.getIsSaved());
            stmt.setInt(2, jobOffer.getId()); // Assuming JobOffer has an ID field
            stmt.executeUpdate();
        }
    }


    public List<JobOffer> getSavedJobOffers() throws SQLException {
        List<JobOffer> savedJobOffers = new ArrayList<>();
        String sql = """
        SELECT DISTINCT
            j.id, j.title, j.description, j.requirements, j.contract, j.telework,
            j.salary, j.industry, j.post_date, j.deadline, j.is_saved,
            e.elevel as edu_level, e.field as edu_field, e.diploma,
            ex.min_years, ex.max_years, ex.exlevel as exp_level, ex.description as exp_description,
            l.city, l.region, l.country, l.address,
            c.name as company_name
        FROM job_posts j
        LEFT JOIN job_education e ON j.id = e.job_post_id
        LEFT JOIN job_experience ex ON j.id = ex.job_post_id
        LEFT JOIN job_locations l ON j.id = l.job_post_id
        LEFT JOIN companies c ON j.company = c.id
        WHERE j.is_saved = 1
        ORDER BY j.post_date DESC
        """;


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    JobOffer offer = mapResultSetToJobOffer(rs);
                    loadAdditionalData(offer);
                    savedJobOffers.add(offer);
                }
            }
        }
        return savedJobOffers;
    }

    private JobOffer mapResultSetToJobOffer(ResultSet rs) throws SQLException {
        Location location = new Location(
                rs.getString("city"),
                rs.getString("region"),
                rs.getString("country"),
                rs.getString("address")
        );

        Education education = new Education(
                rs.getInt("edu_level"),
                rs.getString("edu_field"),
                rs.getString("diploma")
        );

        Experience experience = new Experience(
                rs.getInt("min_years"),
                rs.getInt("max_years"),
                rs.getString("exp_level"),
                rs.getString("exp_description")
        );

        Timestamp postDate = rs.getTimestamp("post_date");
        Timestamp deadline = rs.getTimestamp("deadline");

        return new JobOffer(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("company_name"),
                location,
                education,
                experience,
                rs.getString("contract"),
                new ArrayList<>(),  // Will be populated later
                new ArrayList<>(),  // Will be populated later
                new HashMap<>(),    // Will be populated later
                rs.getString("description"),
                postDate != null ? postDate.toLocalDateTime() : null,
                rs.getString("salary"),
                rs.getString("requirements"),
                rs.getString("industry"),
                rs.getInt("is_saved") == 1,// isSaved is not stored in the database
                rs.getString("telework"),
                deadline != null ? deadline.toLocalDateTime() : null
        );
    }
}