package com.example.tomipc.foodzye.model;


public class Review {
    private int id,user_id, menu_id, place_id;
    private String comment, username, userPicture;
    private double rate;


    public Review(int id, int user_id, int menu_id, String comment, double rate){
        this.id = id;
        this.user_id = user_id;
        this.menu_id = menu_id;
        this.comment = comment;
        this.rate = rate;
    }

    public Review(String comment, double rate, int user_id, String username, String userPicture){
        this.comment = comment;
        this.rate = rate;
        this.user_id = user_id;
        this.username = username;
        this.userPicture = userPicture;
    }

    public Review(String comment, double rate){
        this.comment = comment;
        this.rate = rate;
    }

    public Review(String comment, double rate, String username, String userPicture){
        this.comment = comment;
        this.rate = rate;
        this.username = username;
        this.userPicture = userPicture;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public String getComment() {
        return comment;
    }

    public double getRate() {
        return rate;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}
