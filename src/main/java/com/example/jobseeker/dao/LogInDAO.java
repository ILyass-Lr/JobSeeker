package com.example.jobseeker.dao;

import com.example.jobseeker.model.JobOffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInDAO {
    private Connection connection;
        public LogInDAO(Connection conn) {
            this.connection = conn;
        }

//    public boolean verifyLogin(String email, String password) {
//        String sql = "select * from users where email = ? and password = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            try (ResultSet rs = stmt.executeQuery()) {
//                stmt.setString(1, email);
//                stmt.setString(2, password);
//
//                while (rs.next()) {
//                    // LIGNE == VIDE
//                    return false;
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
