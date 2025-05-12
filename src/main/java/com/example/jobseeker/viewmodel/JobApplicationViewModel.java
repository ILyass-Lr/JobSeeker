package com.example.jobseeker.viewmodel;

import com.example.jobseeker.dao.JobApplicationDAO;
import com.example.jobseeker.model.JobApplication;

import java.sql.Timestamp;
import java.util.List;

public class JobApplicationViewModel {
    private final JobApplicationDAO jobApplicationDAO;

    public JobApplicationViewModel(JobApplicationDAO jobApplicationDAO) {
        this.jobApplicationDAO = jobApplicationDAO;
    }

    public boolean submitJobApplication(
            int userId,
            int jobOfferId,
            String contactNumber,
            String jobProfile,
            byte[] cvFile,
            String cvFilename,
            String cvFiletype,
            byte[] coverLetterFile,
            String coverLetterFilename,
            String coverLetterFiletype
    ) {
        JobApplication jobApplication = new JobApplication(
                new Timestamp(System.currentTimeMillis()),
                cvFile,
                cvFilename,
                cvFiletype,
                coverLetterFile,
                coverLetterFilename,
                coverLetterFiletype,
                "SUBMITTED" // default status
        );

        return jobApplicationDAO.insertJobApplication(userId, jobOfferId, jobApplication);
    }

    public List<JobApplication> getCandidateApplications(int candidateId) {
        return List.of(jobApplicationDAO.getApplicationsByCandidate(candidateId));
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        return jobApplicationDAO.updateApplicationStatus(applicationId, status);
}
}