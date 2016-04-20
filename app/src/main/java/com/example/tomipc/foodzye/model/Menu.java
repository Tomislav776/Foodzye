package com.example.tomipc.foodzye.model;

/**
 * Created by Tomi PC on 20.4.2016..
 */
public class Menu {
    private int id,img;
    private String name, description, currency, image;
    private double price, rate;


    public Menu(int id, String name, String description, String currency, String image, double rate, double price){
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.image = image;
        this.rate = rate;
        this.price = price;
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

    public double getrate() {
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

    public void setImage(int img){
        this.img = img;
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
