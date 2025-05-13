package com.example.jobseeker.model;

import java.sql.Timestamp;

public class JobApplication {
    private int id;
    //private int jobId;
    private int candidateId;
    private Timestamp applyDate;
    private byte[] cvFile;
    private String cvFilename;
    private String cvFiletype;
    private byte[] coverLetterFile;
    private String coverLetterFilename;
    private String coverLetterFiletype;
    private String status;

    public JobApplication(Timestamp applyDate, byte[] cvFile, String cvFilename, String cvFiletype, byte[] coverLetterFile, String coverLetterFilename, String coverLetterFiletype, String status, int candidateId, int id) {
        //this.jobId = jobId;

        this.candidateId = candidateId;
        this.applyDate = applyDate;
        this.cvFile = cvFile;
        this.cvFilename = cvFilename;
        this.cvFiletype = cvFiletype;
        this.coverLetterFile = coverLetterFile;
        this.coverLetterFilename = coverLetterFilename;
        this.coverLetterFiletype = coverLetterFiletype;
        this.status = status;
        this.id = id;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }



    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public Timestamp getApplyDate() { return applyDate; }
    public void setApplyDate(Timestamp applyDate) { this.applyDate = applyDate; }

    public byte[] getCvFile() { return cvFile; }
    public void setCvFile(byte[] cvFile) { this.cvFile = cvFile; }

    public String getCvFilename() { return cvFilename; }
    public void setCvFilename(String cvFilename) { this.cvFilename = cvFilename; }

    public String getCvFiletype() { return cvFiletype; }
    public void setCvFiletype(String cvFiletype) { this.cvFiletype = cvFiletype; }

    public byte[] getCoverLetterFile() { return coverLetterFile; }
    public void setCoverLetterFile(byte[] coverLetterFile) { this.coverLetterFile = coverLetterFile; }

    public String getCoverLetterFilename() { return coverLetterFilename; }
    public void setCoverLetterFilename(String coverLetterFilename) { this.coverLetterFilename = coverLetterFilename; }

    public String getCoverLetterFiletype() { return coverLetterFiletype; }
    public void setCoverLetterFiletype(String coverLetterFiletype) { this.coverLetterFiletype = coverLetterFiletype; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status;}
}