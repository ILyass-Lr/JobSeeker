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
    ///  /////////
    public static List<JobOffer> getAllJobOffers() throws SQLException {
        List<JobOffer> offers = new ArrayList<>();
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

        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        JobOffer offer = createJobOfferFromResultSet(rs);
                        loadAdditionalData(conn, offer);
                        offers.add(offer);
                    }
                }
            }
        }
        return offers;
    }

    private static void loadAdditionalData(Connection conn, JobOffer offer) throws SQLException {
        int jobId = offer.getId();
        offer.getHardSkills().addAll(getHardSkills(conn, jobId));
        offer.getSoftSkills().addAll(getSoftSkills(conn, jobId));
        offer.getLanguages().putAll(getLanguages(conn, jobId));
    }

    private static List<String> getHardSkills(Connection conn, int jobId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM job_hard_skills WHERE job_post_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private static List<String> getSoftSkills(Connection conn, int jobId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM job_soft_skills WHERE job_post_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private static Map<String, String> getLanguages(Connection conn, int jobId) throws SQLException {
        Map<String, String> languages = new HashMap<>();
        String sql = "SELECT language_name, proficiency_level FROM job_languages WHERE job_post_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private static JobOffer createJobOfferFromResultSet(ResultSet rs) throws SQLException {
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
                false,  // isSaved is not stored in the database
                rs.getString("telework"),
                deadline != null ? deadline.toLocalDateTime() : null
        );
    }
    ///  /////









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

    ///  COMPANIES
    public static List<Company> getAllCompanies() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = """
    SELECT 
        id, name, type, industry, status, founded_year, 
        csize as "size", revenue, 
        headquarters_city, headquarters_region, headquarters_country,
        number_of_offices, remote_work_policy,
        glassdoor_rating, employee_retention_rate,
        benefits, csr_initiatives
    FROM companies
    ORDER BY name ASC
""";

        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Company company = createCompanyFromResultSet(rs);
                        companies.add(company);
                    }
                }
            }
        }
        return companies;
    }

    private static Company createCompanyFromResultSet(ResultSet rs) throws SQLException {
        return new Company(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("industry"),
                rs.getString("status"),
                getIntegerFromResultSet(rs, "founded_year"),
                rs.getString("size"),
                rs.getString("revenue"),
                rs.getString("headquarters_city"),
                rs.getString("headquarters_region"),
                rs.getString("headquarters_country"),
                getIntegerFromResultSet(rs, "number_of_offices"),
                rs.getString("remote_work_policy"),
                getDoubleFromResultSet(rs, "glassdoor_rating"),
                getDoubleFromResultSet(rs, "employee_retention_rate"),
                rs.getString("benefits"),
                rs.getString("csr_initiatives")
        );
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

    // Méthode optionnelle pour récupérer une entreprise par son ID
    public static Company getCompanyById(Long companyId) throws SQLException {
        String sql = """
        SELECT 
            id, name, type, industry, status, founded_year, 
            csize as size, revenue, 
            headquarters_city, headquarters_region, headquarters_country,
            number_of_offices, remote_work_policy,
            glassdoor_rating, employee_retention_rate,
            benefits, csr_initiatives
        FROM companies
        WHERE id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCompanyFromResultSet(rs);
                }
                return null;
            }
        }
    }

    // Méthode optionnelle pour rechercher des entreprises par nom
    public static List<Company> searchCompaniesByName(String searchTerm) throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = """
        SELECT 
            id, name, type, industry, status, founded_year, 
            csize as size, revenue, 
            headquarters_city, headquarters_region, headquarters_country,
            number_of_offices, remote_work_policy,
            glassdoor_rating, employee_retention_rate,
            benefits, csr_initiatives
        FROM companies
        WHERE LOWER(name) LIKE LOWER(?)
        ORDER BY name ASC
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Company company = createCompanyFromResultSet(rs);
                    companies.add(company);
                }
            }
        }
        return companies;
    }

}