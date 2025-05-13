package com.example.jobseeker.model;

public class User {
    private int id;
    private final String email;
    private final String password;
    private final String role;


    public User(String email, String password, String role) {

        this.email = email;
        this.password = password;
        this.role = role;
    }



    public String getEmail() {
        return email;
    }
//    public String getName(){
//        return email.split("@")[0].split(".")[0];
//    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
