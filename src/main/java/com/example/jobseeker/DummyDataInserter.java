//package com.example.jobseeker;
//
//import java.sql.*;
//
//public class DummyDataInserter {
//    public static void insertDummyData() {
//        try {
//            // Get the dummy data from JobOffer class
//            var dummyOffers = JobOffer.getDummyData();
//
//            // Insert each offer
//            for (JobOffer offer : dummyOffers) {
//                try {
//                    int jobId = DatabaseUtil.insertJobOffer(offer);
//                    System.out.println("Successfully inserted job offer with ID: " + jobId);
//                } catch (SQLException e) {
//                    System.err.println("Error inserting job offer: " + offer.getTitle());
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error in dummy data insertion:");
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        // Insert all dummy data
//        insertDummyData();
//    }
//}