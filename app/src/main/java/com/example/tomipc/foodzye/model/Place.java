package com.example.tomipc.foodzye.model;


import java.io.Serializable;

public class Place implements Serializable {
    private int id, role;
    private String name, email, location, phone, picture, work_time, description, slug;
    private double rate;


    public Place(int id, int role, String name, String email, String slug, String location, String phone, String picture, String work_time, double rate, String description){
        this.id = id;
        this.role = role;
        this.name = name;
        this.email = email;
        this.slug = slug;
        this.location = location;
        this.phone = phone;
        this.picture = picture;
        this.work_time = work_time;
        this.rate = rate;
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
