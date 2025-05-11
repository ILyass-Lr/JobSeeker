//package com.example.jobseeker.viewmodel;
//
//import com.example.jobseeker.dao.JobApplicationDAO;
//import com.example.jobseeker.model.JobApplication;
//
//import java.util.List;
//
//public class JobApplicationViewModel {
//    private final JobApplicationDAO jobApplicationDAO;
//
//    public JobApplicationViewModel(JobApplicationDAO jobApplicationDAO) {
//        this.jobApplicationDAO = jobApplicationDAO;
//    }
//
//    public int submitJobApplication(JobApplication jobApplication) {
//        return jobApplicationDAO.insertJobApplication(
//                jobApplication.getJobId(),
//                jobApplication.getCandidateId(),
//                jobApplication.getCvFile(),
//                jobApplication.getCvFilename(),
//                jobApplication.getCvFiletype(),
//                jobApplication.getCoverLetterFile(),
//                jobApplication.getCoverLetterFilename(),
//                jobApplication.getCoverLetterFiletype()
//        );
//    }
//
//    public List<JobApplication> getCandidateApplications(int candidateId) {
//        return List.of(jobApplicationDAO.getApplicationsByCandidate(candidateId));
//    }
//
//    public boolean updateApplicationStatus(int applicationId, String status) {
//        return jobApplicationDAO.updateApplicationStatus(applicationId, status);
//}
//}