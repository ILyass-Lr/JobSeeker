//package com.example.jobseeker.dao;
//import com.example.jobseeker.model.JobApplication;
//
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//
//public class JobApplicationDAO {
//    private Connection connection;
//
//    public JobApplicationDAO(Connection conn) {
//        this.connection = conn;
//    }
//
//    public int insertJobApplication(int jobId, int candidateId,
//                                    byte[] cvFile, String cvFilename, String cvFiletype,
//                                    byte[] coverLetterFile, String coverLetterFilename, String coverLetterFiletype) {
//        String sql = "INSERT INTO ILYASS.APPLICATIONS (JOB_ID, CANDIDATE_ID, APPLY_DATE, " +
//                "CV_FILE, CV_FILENAME, CV_FILETYPE, " +
//                "COVER_LETTER_FILE, COVER_LETTER_FILENAME, COVER_LETTER_FILETYPE, STATUS) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setInt(1, jobId);
//            stmt.setInt(2, candidateId);
//            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
//            stmt.setBytes(4, cvFile);
//            stmt.setString(5, cvFilename);
//            stmt.setString(6, cvFiletype);
//            stmt.setBytes(7, coverLetterFile);
//            stmt.setString(8, coverLetterFilename);
//            stmt.setString(9, coverLetterFiletype);
//            stmt.setString(10, "SUBMITTED"); // Default status for new applications
//
//            int affectedRows = stmt.executeUpdate();
//
//            if (affectedRows == 0) {
//                return -1;
//            }
//
//            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    return generatedKeys.getInt(1);
//                } else {
//                    return -1;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    public JobApplication[] getApplicationsByCandidate(int candidateId) {
//        String sql = "SELECT a.*, j.TITLE as JOB_TITLE FROM ILYASS.APPLICATIONS a " +
//                "JOIN ILYASS.JOB_POSTS j ON a.JOB_ID = j.ID " +
//                "WHERE a.CANDIDATE_ID = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql,
//                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
//            stmt.setInt(1, candidateId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                // Get row count for array sizing
//                rs.last();
//                int rowCount = rs.getRow();
//                rs.beforeFirst();
//
//                JobApplication[] applications = new JobApplication[rowCount];
//                int index = 0;
//
//                while (rs.next()) {
//                    JobApplication app = new JobApplication();
//                    app.setId(rs.getInt("ID"));
//                    app.setJobId(rs.getInt("JOB_ID"));
//                    app.setCandidateId(rs.getInt("CANDIDATE_ID"));
//                    app.setApplyDate(rs.getTimestamp("APPLY_DATE"));
//                    app.setCvFilename(rs.getString("CV_FILENAME"));
//                    app.setCoverLetterFilename(rs.getString("COVER_LETTER_FILENAME"));
//                    app.setStatus(rs.getString("STATUS"));
//                    //app.setJobTitle(rs.getString("JOB_TITLE")); // From join
//
//                    applications[index++] = app;
//                }
//
//                return applications;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return new JobApplication[0];
//        }
//    }
//
//    public boolean updateApplicationStatus(int applicationId, String status) {
//        String sql = "UPDATE ILYASS.APPLICATIONS SET STATUS = ? WHERE ID = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, status);
//            stmt.setInt(2, applicationId);
//
//            int affectedRows = stmt.executeUpdate();
//            return affectedRows > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//}
//}
//}