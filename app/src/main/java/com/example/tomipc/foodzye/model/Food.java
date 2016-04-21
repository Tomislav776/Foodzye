package com.example.tomipc.foodzye.model;

public class Food {
    public int id;
    public String name;

    public Food(String name){
        this.name = name;
    }

    public Food(int id, String name){
        this.id = id;
        this.name = name;
    }

}