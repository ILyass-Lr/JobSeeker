package com.example.jobseeker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Company {
    private final Long id;
    private final String name;
    private final String type;
    private final String industry;
    private final String status;
    private final Integer foundedYear;
    private final String size;
    private final String revenue;
    private final String headquartersCity;
    private final String headquartersRegion;
    private final String headquartersCountry;
    private final Integer numberOfOffices;
    private final String remoteWorkPolicy;
    private final Double glassdoorRating;
    private final Double employeeRetentionRate;
    private final String benefits;
    private final String csrInitiatives;

    public Company(Long id, String name, String type, String industry, String status,
                   Integer foundedYear, String size, String revenue, String headquartersCity,
                   String headquartersRegion, String headquartersCountry, Integer numberOfOffices,
                   String remoteWorkPolicy, Double glassdoorRating, Double employeeRetentionRate,
                   String benefits, String csrInitiatives) {
        this.id = id;
        this.name = defaultIfNull(name, "Unspecified");
        this.type = defaultIfNull(type, "Unspecified");
        this.industry = defaultIfNull(industry, "Unspecified");
        this.status = defaultIfNull(status, "Unspecified");
        this.foundedYear = foundedYear;
        this.size = defaultIfNull(size, "Unspecified");
        this.revenue = defaultIfNull(revenue, "Unspecified");
        this.headquartersCity = headquartersCity;
        this.headquartersRegion = headquartersRegion;
        this.headquartersCountry = headquartersCountry;
        this.numberOfOffices =  numberOfOffices;
        this.remoteWorkPolicy = defaultIfNull(remoteWorkPolicy, "Unspecified");
        this.glassdoorRating = glassdoorRating;
        this.employeeRetentionRate = employeeRetentionRate;
        this.benefits = defaultIfNull(benefits, "Unspecified");
        this.csrInitiatives = defaultIfNull(csrInitiatives, "Unspecified");
    }

    private String defaultIfNull(String input, String defaultValue) {
        return input == null ? defaultValue : input;
    }


    // Getters
    //public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getIndustry() { return industry; }
    public String getStatus() { return status; }
    public Integer getFoundedYear() { return foundedYear; }
    public String getSize() { return size; }
    public String getRevenue() { return revenue; }
    public String getHeadquartersCity() { return headquartersCity; }
    public String getHeadquartersRegion() { return headquartersRegion; }
    public String getHeadquartersCountry() { return headquartersCountry; }
    public Integer getNumberOfOffices() { return numberOfOffices; }
    public String getRemoteWorkPolicy() { return remoteWorkPolicy; }
    public Double getGlassdoorRating() { return glassdoorRating; }
    public Double getEmployeeRetentionRate() { return employeeRetentionRate; }
    public String getBenefits() { return benefits; }
    public String getCsrInitiatives() { return csrInitiatives; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id) &&
                Objects.equals(name, company.name) &&
                Objects.equals(industry, company.industry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, industry);
    }

    // Method to generate dummy data for testing
    public static List<Company> getDummyData() {
        List<Company> dummyCompanies = new ArrayList<>();

        // Add some detailed companies
        dummyCompanies.add(new Company(
                1L,
                "TechMaster Solutions",
                "Private",
                "IT",
                "Active",
                2010,
                "501-1000",
                "$100M-$500M",
                "Casablanca",
                "Casablanca-Settat",
                "Morocco",
                5,
                "Hybrid",
                4.5,
                92.5,
                "Health Insurance, Dental Coverage, Stock Options, Professional Development, Gym Membership",
                "Environmental Sustainability Program, Local Education Initiative, Community Outreach"
        ));

        dummyCompanies.add(new Company(
                2L,
                "MedLife International",
                "Public",
                "Healthcare",
                "Active",
                1995,
                "1000-5000",
                "$500M-$1B",
                "Rabat",
                "Rabat-Salé-Kénitra",
                "Morocco",
                12,
                "On-site",
                4.2,
                88.0,
                "Medical Insurance, Life Insurance, Performance Bonuses, Continuing Education",
                "Healthcare Access Programs, Medical Research Funding, Community Health Initiatives"
        ));

        // Add more companies with varied data
        String[] types = {"Private", "Public", "Startup", "Non-Profit"};
        String[] industries = {"IT", "Healthcare", "Finance", "Education", "Manufacturing"};
        String[] cities = {"Tangier", "Marrakech", "Agadir", "Fes", "Meknes"};
        String[] sizes = {"1-10", "11-50", "51-200", "201-500", "501-1000", "1000+"};
        String[] policies = {"Remote", "Hybrid", "On-site"};

        for (int i = 3; i <= 20; i++) {
            dummyCompanies.add(new Company(
                    (long) i,
                    "Company " + i,
                    types[i % types.length],
                    industries[i % industries.length],
                    "Active",
                    2000 + (i % 23),
                    sizes[i % sizes.length],
                    "$" + (10 + i * 5) + "M-$" + (50 + i * 5) + "M",
                    cities[i % cities.length],
                    "Region " + (i % 5 + 1),
                    "Morocco",
                    i % 10 + 1,
                    policies[i % policies.length],
                    3.5 + (i % 15) / 10.0,
                    75.0 + (i % 20),
                    "Standard Benefits Package " + i,
                    "CSR Initiative " + i
            ));
        }

        return dummyCompanies;
    }
}