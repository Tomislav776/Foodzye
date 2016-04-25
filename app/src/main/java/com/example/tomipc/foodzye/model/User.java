package com.example.tomipc.foodzye.model;


public class User {

    public int id;
    public String username, email, role, slug, location, phone, picture, work_time;

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

    public User(String email, String location, String phone, String picture, String work_time){
        this.email = email;
        this.location = location;
        this.phone = phone;
        this.picture = picture;
        this.work_time = work_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }
}
