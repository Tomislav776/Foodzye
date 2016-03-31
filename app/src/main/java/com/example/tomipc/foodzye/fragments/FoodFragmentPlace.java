package com.example.tomipc.foodzye.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomipc.foodzye.R;

/**
 * Created by Tomi Laptop on 31.3.2016..
 */
public class FoodFragmentPlace extends Fragment {

    public FoodFragmentPlace() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragments_food_place, container, false);
    }
}
