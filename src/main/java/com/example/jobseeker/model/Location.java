package com.example.jobseeker.model;



public class Location {
    private final String city;
    private final String region;
    private final String country;
    private final String address;

    public Location(String city, String region, String country, String address) {
        this.city = city;
        this.region = region;
        this.country = country;
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }
}

