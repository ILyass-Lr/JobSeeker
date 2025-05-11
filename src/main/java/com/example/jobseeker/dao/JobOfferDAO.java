package com.example.jobseeker.dao;
import com.example.jobseeker.model.Education;
import com.example.jobseeker.model.Experience;
import com.example.jobseeker.model.JobOffer;
import com.example.jobseeker.model.Location;

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
            j.salary, j.industry, j.post_date, j.deadline,
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
                j.salary, j.industry, j.post_date, j.deadline,
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


    public List<JobOffer> searchJobOffers(String searchText, int dateFilter, String contractFilter, String locationFilter, String teleworkFilter) throws SQLException {
        List<JobOffer> jobOffers = new ArrayList<>();
        String query = """
                SELECT DISTINCT
                j.id, j.title, j.description, j.requirements, j.contract, j.telework,
                j.salary, j.industry, j.post_date, j.deadline,
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
              AND LOWER(j.contract) LIKE ?
              AND LOWER(j.telework) LIKE ?
              AND LOWER(l.city) LIKE ?
            ORDER BY j.post_date DESC
            """;


        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);
            statement.setDouble(4, (double) dateFilter);
            statement.setString(5, contractFilter.toLowerCase());

            statement.setString(6, teleworkFilter.toLowerCase());
            statement.setString(7, locationFilter.toLowerCase());


            try (ResultSet resultSet = statement.executeQuery()) {


                while (resultSet.next()) {
                    jobOffers.add(mapResultSetToJobOffer(resultSet));
                }
            }catch (Exception e) {
                e.printStackTrace(); // make sure you see errors
            }
        }

            System.out.println("Found " + jobOffers.size() + " job offers");


        return jobOffers;
    }

    public boolean isJobOfferSaved(int userId, int jobPostId) throws SQLException {
        String sql = "SELECT 1 FROM saved_offers WHERE ID_USER = ? AND ID_OFFER = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, jobPostId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a record exists, job offer is saved
            }
        }
    }


    public void updateJobOfferSavedState(int jobOfferId, int userId) throws SQLException {
        if (!isJobOfferSaved(userId, jobOfferId)) {
            // Save the offer (Insert)
            String insertSql = "INSERT INTO saved_offers (ID_USER, ID_OFFER) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, jobOfferId);
                stmt.executeUpdate();
            }
        } else {
            // Unsave the offer (Delete)
            String deleteSql = "DELETE FROM saved_offers WHERE ID_USER = ? AND ID_OFFER = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, jobOfferId);
                stmt.executeUpdate();
            }
        }
    }



    public List<JobOffer> getSavedJobOffers(int userId) throws SQLException {
        List<JobOffer> savedJobOffers = new ArrayList<>();
        System.out.println(userId);
        String sql = """
        SELECT DISTINCT
        j.id, j.title, j.description, j.requirements, j.contract, j.telework,
        j.salary, j.industry, j.post_date, j.deadline,
        e.elevel AS edu_level, e.field AS edu_field, e.diploma,
        ex.min_years, ex.max_years, ex.exlevel AS exp_level, ex.description AS exp_description,
        l.city, l.region, l.country, l.address,
        c.name AS company_name
        FROM saved_offers s
        JOIN job_posts j ON s.ID_OFFER = j.id
        LEFT JOIN job_education e ON j.id = e.job_post_id
        LEFT JOIN job_experience ex ON j.id = ex.job_post_id
        LEFT JOIN job_locations l ON j.id = l.job_post_id
        LEFT JOIN companies c ON j.company = c.id
        WHERE s.ID_USER = ?
        ORDER BY j.post_date DESC
        """;


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
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

        return  new JobOffer(
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
                rs.getString("telework"),
                deadline != null ? deadline.toLocalDateTime() : null
        );
    }
}