package com.example.tomipc.foodzye;


public class User {

    String username, password, email;

    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

}
