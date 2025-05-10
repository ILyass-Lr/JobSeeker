package com.example.jobseeker.util;

import com.example.jobseeker.model.Education;
import com.example.jobseeker.model.Experience;
import com.example.jobseeker.model.Location;
import com.example.jobseeker.model.Company;
import com.example.jobseeker.model.JobOffer;

import java.sql.*;
import java.util.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/freepdb1";
    private static final String USER = "ILyass";
    private static final String PASSWORD = "Good33bqy";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }













    public static int insertJobOffer(JobOffer offer) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            int jobId = insertJobPost(conn, offer);
            insertJobLocation(conn, jobId, offer.getLocation());
            insertJobEducation(conn, jobId, offer.getEducation());
            insertJobExperience(conn, jobId, offer.getExperience());
            insertHardSkills(conn, jobId, offer.getHardSkills());
            insertSoftSkills(conn, jobId, offer.getSoftSkills());
            insertLanguages(conn, jobId, offer.getLanguages());
            conn.commit();
            return jobId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    private static int insertJobPost(Connection conn, JobOffer offer) throws SQLException {
        String sql = """
        INSERT INTO job_posts (title, industry, contract, telework, description
                             , salary, post_date, deadline)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, offer.getTitle());
            stmt.setString(2, offer.getIndustry());
            stmt.setString(3, offer.getContractType());
            stmt.setString(4, offer.getTeleWork());
            stmt.setString(5, offer.getDescription());
            //stmt.setString(6, offer.getRequirements());
            stmt.setString(6, offer.getSalary());
            stmt.setTimestamp(7, Timestamp.valueOf(offer.getPublishDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(offer.getDeadline()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated ID");
            }
        }
    }

    private static void insertJobLocation(Connection conn, int jobId, Location location) throws SQLException {
        String sql = "INSERT INTO job_locations (job_post_id, city, region, country, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setString(2, location.getCity());
            stmt.setString(3, location.getRegion());
            stmt.setString(4, location.getCountry());
            stmt.setString(5, location.getAddress());
            stmt.executeUpdate();
        }
    }

    private static void insertJobEducation(Connection conn, int jobId, Education education) throws SQLException {
        String sql = "INSERT INTO job_education (job_post_id, elevel, field, diploma) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setInt(2, education.getLevel());
            stmt.setString(3, education.getField());
            stmt.setString(4, education.getDiploma());
            stmt.executeUpdate();
        }
    }

    private static void insertJobExperience(Connection conn, int jobId, Experience experience) throws SQLException {
        String sql = "INSERT INTO job_experience (job_post_id, min_years, max_years, exlevel, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            stmt.setInt(2, experience.getMinYears());
            stmt.setInt(3, experience.getMaxYears());
            stmt.setString(4, experience.getLevel());
            stmt.setString(5, experience.getDescription());
            stmt.executeUpdate();
        }
    }

    private static void insertHardSkills(Connection conn, int jobId, List<String> skills) throws SQLException {
        String sql = "INSERT INTO job_hard_skills (job_post_id, skill_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String skill : skills) {
                stmt.setInt(1, jobId);
                stmt.setString(2, skill);
                stmt.executeUpdate();
            }
        }
    }

    private static void insertSoftSkills(Connection conn, int jobId, List<String> skills) throws SQLException {
        String sql = "INSERT INTO job_soft_skills (job_post_id, skill_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String skill : skills) {
                stmt.setInt(1, jobId);
                stmt.setString(2, skill);
                stmt.executeUpdate();
            }
        }
    }

    private static void insertLanguages(Connection conn, int jobId, Map<String, String> languages) throws SQLException {
        String sql = "INSERT INTO job_languages (job_post_id, language_name, proficiency_level) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : languages.entrySet()) {
                stmt.setInt(1, jobId);
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.executeUpdate();
            }
        }
    }





    // Méthodes utilitaires pour gérer les valeurs NULL dans la base de données
    private static Integer getIntegerFromResultSet(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private static Double getDoubleFromResultSet(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }


}