package com.example.tomipc.foodzye.model;


import java.io.Serializable;

public class User implements Serializable {

    public int id, role;
    public String username, email, slug, location, phone, picture, work_time, description;
    private double rate;

    public User(String username, String email, int role){
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public User(int id, int role, String name, String email, String location, String phone, String picture, String work_time, double rate){
        this.id = id;
        this.role = role;
        this.username = name;
        this.email = email;
        this.location = location;
        this.phone = phone;
        this.picture = picture;
        this.work_time = work_time;
        this.rate = rate;
    }

    public User(int id, String name, String slug, String email, int role){
        this.id = id;
        username = name;
        this.email = email;
        this.role = role;
        this.slug = slug;
    }

    public User(int id, String name, String slug, String email, int role, String location, String phone, String work_time, String user_picture, String description, float rate){
        this.id = id;
        username = name;
        this.email = email;
        this.role = role;
        this.slug = slug;
        this.location = location;
        this.phone = phone;
        this.work_time = work_time;
        picture = user_picture;
        this.description = description;
        this.rate = rate;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
