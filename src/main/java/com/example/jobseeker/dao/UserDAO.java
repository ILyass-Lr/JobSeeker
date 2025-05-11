package com.example.jobseeker.dao;
import com.example.jobseeker.model.User;

import java.sql.*;


public class UserDAO {

    private Connection connection;
    public UserDAO(Connection conn) {
        this.connection = conn;
    }




     // Check if a user with the given email already exists
    public boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM APP_USERS WHERE EMAIL = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();


            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
        }

        return false;
    }


     // Save a new user to the database
     public User saveUser(User user) throws SQLException {
         String sql = "BEGIN INSERT INTO APP_USERS (EMAIL, PASS, U_ROLE) VALUES (?, ?, ?) RETURNING ID INTO ?; END;";

         try (CallableStatement stmt = connection.prepareCall(sql)) {
             stmt.setString(1, user.getEmail());
             stmt.setString(2, user.getPassword());
             stmt.setString(3, user.getRole());
             stmt.registerOutParameter(4, Types.INTEGER);

             stmt.execute();
             int generatedId = stmt.getInt(4);
             user.setId(generatedId);

             return user;
         }
     }



    // Find a user by email and password for authentication

    public User findUserByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM APP_USERS WHERE EMAIL = ? AND PASS = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();


            if (rs.next()) {

                User user = new User(
                        rs.getString("EMAIL"),
                        rs.getString("PASS"),
                        rs.getString("U_ROLE")
                );
                user.setId(rs.getInt("ID"));
                System.out.println("Found one");
                return user;
            }else{
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

}