package com.example.jobseeker.viewmodel;

public class LogInViewModel {
    String email;
    String password;
    //LogInDAO loginDAO;


    public LogInViewModel(String email, String password) {
        this.email = email;
        this.password = password;
      //  this.loginDAO= loginDAO;
    }



    public void logIn(String email, String password) {
        // VALIDATION
        if(email.isEmpty() || password.isEmpty()){
            /// //
        }
      //  boolean userExist = loginDAO.verifyLogin(email, password);
       // if(userExist){


       // }



    }
}
