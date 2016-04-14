package com.example.tomipc.foodzye.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.R;

public class FoodFragmentFood extends Fragment {
    EditText unosProba;
    Database baza;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragments_food_food,null);

        unosProba = (EditText) x.findViewById(R.id.unosProba);

         baza = new Database();
        baza.sendPostRequest();

        return x;
    }
}