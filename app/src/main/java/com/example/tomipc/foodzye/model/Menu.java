package com.example.tomipc.foodzye.model;

import java.io.Serializable;


public class Menu implements Serializable {
    private int id, food_id, user_id;
    private String name, nameFood, description, currency, image;
    private double price, rate;

    public Menu(int id, String name, String description, String currency, String image, double rate, double price, int food_id, String nameFood, int user_id){
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.image = image;
        this.rate = rate;
        this.price = price;
        this.food_id = food_id;
        this.nameFood = nameFood;
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRate(double rate){
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCurrency(String currency){
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }



    public String getImage() {
        return image;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public double getPrice() {
        return price;
    }


}
