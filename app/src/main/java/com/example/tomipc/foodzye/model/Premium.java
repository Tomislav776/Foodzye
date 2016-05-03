package com.example.tomipc.foodzye.model;


public class Premium {
    private String description, image_url, price, currency, months_duration;

    public Premium (String description, String image_url, String price, String currency, String months_duration){
        this.description = description;
        this.image_url = image_url;
        this.price = price;
        this.currency = currency;
        this.months_duration = months_duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMonths_duration() {
        return months_duration;
    }

    public void setMonths_duration(String months_duration) {
        this.months_duration = months_duration;
    }
}
