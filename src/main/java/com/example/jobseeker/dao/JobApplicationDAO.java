package com.example.jobseeker.dao;
import com.example.jobseeker.Dashboard;
import com.example.jobseeker.model.*;
import com.example.jobseeker.viewmodel.JobApplicationViewModel;
import oracle.jdbc.proxy.annotation.Pre;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobApplicationDAO {
    private Connection connection;

    public JobApplicationDAO(Connection conn) {
        this.connection = conn;
    }


    public Map<Integer, JobApplicationViewModel.AppSub> loadSubmmittedApplications(int userId) throws SQLException {
        Map<Integer, JobApplicationViewModel.AppSub> submmittedApplications = new HashMap<Integer, JobApplicationViewModel.AppSub>();
        String sql = "SELECT JOB_ID, STATUS from APPLICATIONS WHERE CANDIDATE_ID = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, userId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    submmittedApplications.put(resultSet.getInt("JOB_ID"), JobApplicationViewModel.AppSub.valueOf(resultSet.getString("STATUS")));
                }
            }
        }

        return submmittedApplications;
    }

    public boolean insertJobApplication(int userId, int jobOfferId, JobApplication jobApplication) {
        String sql = """
                INSERT INTO APPLICATIONS (JOB_ID, CANDIDATE_ID, APPLY_DATE, CV_FILE, CV_FILENAME, CV_FILETYPE,
                COVER_LETTER_FILE, COVER_LETTER_FILENAME, COVER_LETTER_FILETYPE, STATUS)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobOfferId);
            stmt.setInt(2, userId);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setBytes(4, jobApplication.getCvFile());
            stmt.setString(5, jobApplication.getCvFilename());
            stmt.setString(6, jobApplication.getCvFiletype());
            stmt.setBytes(7, jobApplication.getCoverLetterFile());
            stmt.setString(8, jobApplication.getCoverLetterFilename());
            stmt.setString(9, jobApplication.getCoverLetterFiletype());
            stmt.setString(10, "SUBMITTED"); // Default status for new applications

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }else{
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JobApplication[] getApplicationsByCandidate(int candidateId) {
        String sql = """
                SELECT a.*, j.TITLE as JOB_TITLE FROM APPLICATIONS a
                JOIN JOB_POSTS j ON a.JOB_ID = j.ID
                WHERE a.CANDIDATE_ID = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setInt(1, candidateId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Get row count for array sizing
                rs.last();
                int rowCount = rs.getRow();
                rs.beforeFirst();

                JobApplication[] applications = new JobApplication[rowCount];
                int index = 0;

                while (rs.next()) {
//

                   applications[index++] = mapResultSetToApplication(rs);
                }

                return applications;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new JobApplication[0];
        }
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        String sql = "UPDATE APPLICATIONS SET STATUS = ? WHERE ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, applicationId);

            int affectedRows = stmt.executeUpdate();
            System.out.println("Updating application :" + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<JobApplication> getApplications(int jobOfferId) {
        List<JobApplication> applications = new ArrayList<JobApplication>();
        String sql = "SELECT * FROM Applications WHERE JOB_ID = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, jobOfferId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    applications.add(mapResultSetToApplication(resultSet));
                }
            }
            return applications;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JobApplication mapResultSetToApplication(ResultSet rs) throws SQLException {


        Timestamp applyDate = rs.getTimestamp("apply_date");


        return  new JobApplication(
                applyDate,
                rs.getBytes("cv_file"),
                rs.getString("cv_filename"),
                rs.getString("cv_filetype"),
                rs.getBytes("cover_letter_file"),
                rs.getString("cover_letter_filename"),
                rs.getString("cover_letter_filetype"),
                rs.getString("status"),
                rs.getInt("CANDIDATE_ID"),
                rs.getInt("ID")
        );
    }
}