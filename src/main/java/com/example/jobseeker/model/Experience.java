package com.example.jobseeker.model;

public class Experience{
    private final int minYears;
    private final int maxYears;
    private final String level;
    private final String description;

    public Experience(int minYears, int maxYears, String level, String description) {
        this.minYears = minYears;
        this.maxYears = maxYears;
        this.level = level;
        this.description = description;
    }

    public int getMinYears() {
        return minYears;
    }

    public int getMaxYears() {
        return maxYears;
    }

    public String getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }


    public Experience() {
        this.minYears = 0;
        this.maxYears = 0;
        this.description = new String();
        this.level = new String();
    }
}

