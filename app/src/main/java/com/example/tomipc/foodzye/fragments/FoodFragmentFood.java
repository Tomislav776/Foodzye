package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.Food;
import com.example.tomipc.foodzye.FoodAdapter;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.MenuAdapter;
import com.example.tomipc.foodzye.model.Menu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodFragmentFood extends Fragment {

    private List<Menu> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;

    ArrayList<String> menuName;
    ArrayList<Double> menuPrice;
    Menu menu;

    Database baza;
    Context c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_food_food,null);

        baza = new Database(c);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new MenuAdapter(menuList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMenuData();
        //prepareMovieData();


        return view;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        ArrayList<Menu> arrayOfFood;

        baza = new Database(c);
        arrayOfFood = baza.readMenu("getMenu");

        for(Menu value: arrayOfFood) {
            menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getrate(), value.getPrice());
            menuList.add(menu);
        }

        mAdapter.notifyDataSetChanged();*/

        /*
        System.out.println("Menu: ");
        for(Menu value: arrayOfFood){
            System.out.println("Menu " + value.getName());
            System.out.println("Menu " + value.getPrice());
        }*/

/*
        menuName = new ArrayList<String>();
        menuPrice = new ArrayList<Double>();

        for(Menu value: arrayOfFood){
            menuName.add(value.getName());
            menuPrice.add(value.getPrice());
        }


        menu = new String[menuName.size()];
        menu = menuName.toArray(menu);
*/


    }

    private void prepareMenuData() {
        ArrayList<Menu> arrayOfFood;

        baza = new Database(c);
        arrayOfFood = baza.readMenu("getMenu");

        for(Menu value: arrayOfFood) {
            menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getrate(), value.getPrice());
            menuList.add(menu);
        }

        mAdapter.notifyDataSetChanged();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }
}