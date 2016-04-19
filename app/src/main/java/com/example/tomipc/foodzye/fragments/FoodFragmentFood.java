package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.Food;
import com.example.tomipc.foodzye.R;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodFragmentFood extends Fragment {
    EditText unosProba;
    Database baza;
    Context c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragments_food_food,null);

        //unosProba = (EditText) x.findViewById(R.id.unosProba);




        baza = new Database(c);

        HashMap data = new HashMap();
        data.put("name", "Ime");

        ArrayList<Food> arrayOfFood;

        arrayOfFood = baza.readFood("getFood");
        baza.insert(data , "postFood");


        System.out.println("Tets5 ");
        for(Food value: arrayOfFood){
           System.out.println("Tets5 "+value.name);
        }

        return x;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }
}