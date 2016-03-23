package com.example.tomipc.foodzye;


public class User {

    String username, email, role;

    public User(String username, String email, String role){
        this.username = username;
        this.email = email;
        if(role.equals("1")){
            this.role = "User";
        }else{
            this.role = "Food Service Provider";
        }
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

}
