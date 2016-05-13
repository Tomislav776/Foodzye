package com.example.tomipc.foodzye.model;


import java.io.Serializable;

public class User implements Serializable {

    private int id, role, premium, type;
    private String username, email, slug, location, phone, picture, work_time, description, premium_until;
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

    public User(int id, String name, String slug, String email, int role, int premium, String premium_until){
        this.id = id;
        username = name;
        this.email = email;
        this.role = role;
        this.slug = slug;
        this.premium = premium;
        this.premium_until = premium_until;
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

    public User(int id, String name, String slug, String email, int role, String location, String phone, String work_time, String user_picture, String description, float rate, int premium, String premium_until){
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
        this.premium = premium;
        this.premium_until = premium_until;
    }

    public User(int id, String name, String slug, String email, int role, String location, String phone, String work_time, String user_picture, String description, float rate, int premium, String premium_until, int type){
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
        this.premium = premium;
        this.premium_until = premium_until;
        this.type = type;
    }

    public User(String username, String email, String location, String phone, String picture, String work_time, String description, double rate, String slug, int type){
        this.email = email;
        this.location = location;
        this.phone = phone;
        this.picture = picture;
        this.work_time = work_time;
        this.description = description;
        this.username = username;
        this.rate = rate;
        this.slug = slug;
        this.type = type;
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

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public String getPremium_until() {
        return premium_until;
    }

    public void setPremium_until(String premium_until) {
        this.premium_until = premium_until;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
