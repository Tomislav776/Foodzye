package com.example.tomipc.foodzye.model;


public class User {

    public int id;
    public String username, email, role, slug;

    public User(String username, String email, String role){
        this.username = username;
        this.email = email;
        if(role.equals("1")){
            this.role = "User";
        }else{
            this.role = "Food Service Provider";
        }
    }

    public User(int id, String name, String slug, String email, String role){
        this.id = id;
        username = name;
        this.email = email;
        if(role.equals("1")){
            this.role = "User";
        }else{
            this.role = "Food Service Provider";
        }
        this.slug = slug;
    }

}
