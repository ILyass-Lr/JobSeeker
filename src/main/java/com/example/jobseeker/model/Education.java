package com.example.jobseeker.model;

public class Education{
    private final int level;
    private final String field;
    private final String diploma;

    public Education(int level, String field, String diploma) {
        this.level = level;
        this.field = field;
        this.diploma = diploma;
    }

    public int getLevel() {
        return level;
    }

    public String getField() {
        return field;
    }

    public String getDiploma() {
        return diploma;
    }


    public Education() {
        this.level = 0;
        this.field = "";
        this.diploma = "";
    }
}

