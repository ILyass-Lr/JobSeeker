package com.example.jobseeker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class JobOffer {
    private static int count;
    private final int id;


    private final String title;
    private final String company;
    private final Location location;
    private final Education education;
    private final Experience experience;
    private List<String> hardSkills;
    private List<String> softSkills;
    private Map<String,String> languages;
    private final String contractType;
    private final String description;
    private final LocalDateTime publishDate;
    private final LocalDateTime deadline;
    private final String salary;
    private final String requirements;
    private boolean isSaved;
    private final String industry;
    private final String teleWork;

    public JobOffer(int id,String title, String company, Location location, Education education, Experience experience, String contractType, List<String> hardSkills, List<String> softSkills, Map<String, String> languages,
                    String description, LocalDateTime publishDate, String salary, String requirements, String industry, boolean isSaved, String teleWork, LocalDateTime deadline) {
        this.id = id;
        this.title = defaultIfNull(title, "Unspecified");
        this.company = defaultIfNull(company, "Unspecified");
        this.location = location;
        this.education = education;
        this.experience = experience;
        this.contractType = defaultIfNull(contractType, "Unspecified");
        this.hardSkills = hardSkills;
        this.softSkills = softSkills;
        this.languages = languages;
        this.description = defaultIfNull(description, "Unspecified");
        this.publishDate = publishDate;
        this.salary = defaultIfNull(salary, "Unspecified");
        this.requirements = defaultIfNull(requirements, "Unspecified");
        this.isSaved = isSaved;
        this.industry = defaultIfNull(industry, "Unspecified");
        this.teleWork = defaultIfNull(teleWork, "Unspecified");
        this.deadline = deadline;
    }

    private String defaultIfNull(String input, String defaultValue) {
        return input == null ? defaultValue : input;
    }
    private String formatRelativeDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long months = ChronoUnit.MONTHS.between(dateTime, now);

        if (months > 0) {
            return months == 1 ? "1 month ago" : months + " months ago";
        } else if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }

    // Factory method to create from database ResultSet
//    public static JobOffer fromResultSet(ResultSet rs) throws SQLException {
//        return new JobOffer(
//                rs.getString("title"),
//                rs.getString("company"),
//                rs.getString("location"),
//                rs.getString("contract_type"),
//                rs.getString("description"),
//                rs.getTimestamp("publish_date").toLocalDateTime(),
//                rs.getString("salary"),
//                rs.getString("requirements"),
//                rs.getString("industry"),
//                Boolean.parseBoolean(rs.getString("isSaved")),
//                rs.getString("teleWork"),
//                rs.getTimestamp("deadline").toLocalDateTime()
//        );
//    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public LocalDateTime getDeadline() {
        return deadline;
    }
    public String getCompany() {
        return company;
    }
    public Location getLocation() {
        return location;
    }
    public Education getEducation() {
        return education;
    }
    public Experience getExperience() {
        return experience;
    }
    public String getContractType() {
        return contractType;
    }
    public List<String> getHardSkills() {
        return hardSkills;
    }
    public List<String> getSoftSkills() {
        return softSkills;
    }
    public Map<String, String> getLanguages() {
        return languages;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getPublishDate() {
        return publishDate;
    }
    public String getFormattedPublishDate() {
        return formatRelativeDate(getPublishDate());
    }
    public String getSalary() {
        return salary;
    }
    public boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean saved) {
        isSaved = saved;
    }
    public String getIndustry() {
        return industry;
    }
    public String getTeleWork() {
        return teleWork;
    }
    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobOffer jobOffer = (JobOffer) o;
        return Objects.equals(title, jobOffer.title) &&
                Objects.equals(company, jobOffer.company) &&
                Objects.equals(location, jobOffer.location) &&
                Objects.equals(description, jobOffer.description) &&
                Objects.equals(publishDate, jobOffer.publishDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, company, location, description, publishDate);
    }



    // Method to generate dummy data for testing
     static public List<JobOffer> getDummyData() {
        List<JobOffer> dummyOffers = new ArrayList<>();


        dummyOffers.add(new JobOffer(
                0,
                "Java Senior Developer",
                "INGEFOR CONSULTORES, S.L",
                new Location("Casablanca" , "Casablanca, Setat", "Morocco", "Bd. Zerktouni n°108"),
                new Education(5, "Software Engineer", "ENSAK"),
                new Experience(2, 5,"Senior Developper", "Maitrise de JavaFX et FXML, ainsi que OrcaleDB."),
                "Internship",
                new ArrayList<>(Arrays.asList("Programmation Java, Python, ou C++", "Développement Web (HTML, CSS, JavaScript)", "Utilisation de frameworks (Spring Boot, Angular, React)")), // Initialize hard skills
                new ArrayList<>(Arrays.asList("Communication efficace","Travail en équipe", "Gestion du temps" )),
                new HashMap<>(Map.of(
                        "Anglais", "Fluent",
                        "Français", "Bilingue",
                        "Espagnol", "Intermédiaire"
                )),
                "We are looking for a Full Stack Developer to produce scalable software solutions. " +
                        "You'll be part of a cross-functional team that's responsible for the full software " +
                        "development life cycle, from conception to deployment.",

                LocalDateTime.now().minusDays(3),
                "Competitive",
                "- 5+ years experience in Java\n- Spring Boot\n- React/Angular",
                "IT",
                true,
                "Yes",
                LocalDateTime.now().plusDays(10)
        ));
        dummyOffers.add(new JobOffer(
                1,
                "Hôtesse d'accueil",
                "BEINTERIM",
                new Location("Rabat" , "Rabat, Sale", "Morocco", "Bd. Karima n°19"),
                new Education(3, "Hôtelerie", "License professionnelle"),
                new Experience(2, 5,"Chef serveurs", "Bonne orientation d'équipe dans le grandes cérémonies"),
                "Internship",
                new ArrayList<>(Arrays.asList(
                        "Gestion des réservations",
                        "Connaissance des logiciels de gestion hôtelière (ex. Opera, Fidelio)",
                        "Gestion des événements et banquets",
                        "Compétences en restauration et service de table",
                        "Gestion des plaintes clients",
                        "Maîtrise des normes de sécurité et d’hygiène",
                        "Langues étrangères (anglais, français, espagnol)"
                )),
                new ArrayList<>(Arrays.asList(
                        "Excellente communication",
                        "Accueil chaleureux et courtois",
                        "Gestion du stress",
                        "Capacité à résoudre les problèmes",
                        "Travail en équipe",
                        "Adaptabilité à un environnement multiculturel",
                        "Attention aux détails",
                        "Leadership et gestion d’équipe"
                )),
                new HashMap<>(Map.of(
                        "Anglais académique", "Expert",
                        "Latin", "Intermédiaire",
                        "Allemand", "Débutant",
                        "Italien", "Intermédiaire"
                )),

                "En tant qu'Assistant(e) RH, vous jouerez un rôle essentiel dans notre gestion du capital humain, en vous assurant du bien-être et de la santé de nos collaborateurs. Vos principales responsabilités incluront :\n" +
                        "\n" +
                        "● Gestion de la santé et de la sécurité :\n" +
                        "● Gérer les dossiers médicaux et les prises en charge avec les assureurs.\n" +
                        "● Suivre les absences et gérer les accidents de travail.\n" +
                        "● Collaborer avec le médecin du travail et l’assistante sociale.\n" +
                        "● Accompagnement des collaborateurs :\n" +
                        "● Être le contact privilégié pour toute question relative à la santé et aux assurances.\n" +
                        "● Traiter les réclamations et maintenir la relation avec notre courtier d'assurance concernant les accidents de travail et autres réclamations.\n" +
                        "● Fournir un soutien personnalisé lors d'hospitalisations et d'urgences médicales.\n" +
                        "● Administration du personnel et reporting :\n" +
                        "● Effectuer les déclarations auprès des organismes sociaux.\n" +
                        "● Collaborer avec les gestionnaires RH pour gérer les départs du personnel.\n" +
                        "● Gérer les remboursements et les déclarations auprès de la CNSS.\n" +
                        "● Assurer le suivi des dossiers d'accidents de travail et de maladies.\n" +
                        "● Assurer le backup du gestionnaire RH.\n" +
                        "● Assistanat :\n" +
                        "● Gérer l’accueil physique et téléphonique.\n" +
                        "● Organiser les rendez-vous pour la Directrice des Ressources Humaines.\n" +
                        "● Réceptionner et traiter le courrier, en veillant à son dispatching auprès de l’équipe.\n" +
                        "● Gérer le stock de fournitures.\n",


                LocalDateTime.of(LocalDate.of(2024, 10, 10), LocalTime.now()),
                "6000 DH",
                "● Diplôme Bac+3 dans le domaine de la santé ou équivalent.\n" +
                        "● 3 à 4 ans d'expérience dans un poste similaire en entreprise.\n" +
                        "● Maîtrise des aspects administratifs liés à la gestion des RH.\n" +
                        "● Compétences en bureautique (Word, Excel, etc.).",
                "Hospitality",
                false,
                "No",
                LocalDateTime.now().plusDays(5)
        ));

        // Add more dummy data
        for (int i = 1; i <= 17; i++) {
            dummyOffers.add(new JobOffer(
                    i+1,
                    "Software Engineer " + i,
                    "Company " + i,
                    i % 2 == 0 ?  new Location("Kenitra" , "Rabat, Sale, Zemmour Zaair", "Morocco", "Bd. Hassan n°12") :  new Location("Temara" , "Temara, Sekhirat", "Morocco", "Bd. Bojamaa n°98"),
                    i % 2 == 0 ? new Education(7, "Medecine", "FAC"):new Education(4, "Teaching", "ESEF"),
                    i % 2 == 0? new Experience(1, 3,"Habitué", "Bon esprit de travaille !"): new Experience(4, 10,"Expert", "Ponctuel et spontanné !"),
                    i % 2 == 0 ? "Full-time" : "Contract",
                    new ArrayList<>(Arrays.asList(
                            "Maîtrise des outils informatiques (MS Office, Google Workspace)",
                            "Gestion de projet",
                            "Compétences en analyse de données",
                            "Connaissances en marketing digital",
                            "Rédaction de rapports et documents professionnels",
                            "Utilisation des CRM (Customer Relationship Management)",
                            "Techniques de négociation"
                    )),
                    new ArrayList<>(Arrays.asList(
                            "Esprit critique et analytique",
                            "Créativité et innovation",
                            "Gestion efficace du temps",
                            "Empathie",
                            "Capacité d'écoute active",
                            "Prise de décision rapide",
                            "Motivation et autonomie"
                    )),
                    new HashMap<>(Map.of(
                            "Anglais", "Fluent",
                            "Mandarin", "Débutant",
                            "Hindi", "Courant",
                            "Arabe", "Intermédiaire",
                            "Russe", "Notions de base"
                    )),
                    "Description for position " + i,
                    LocalDateTime.now().minusMonths(i),
                    "$" + (80000 + i * 5000) + " - $" + (100000 + i * 5000),
                    "Requirements for position " + i,
                    i % 2 == 0 ? "Health & Care" : "Teaching",
                    i % 2 == 0,
                    i % 2 == 0 ? "No" : "Hybrid",
                    LocalDateTime.now().plusDays(i * 2)
            ));
        }

        return dummyOffers;
    }
}