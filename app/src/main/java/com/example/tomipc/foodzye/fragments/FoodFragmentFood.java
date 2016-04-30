package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.DividerItemDecoration;
import com.example.tomipc.foodzye.FoodActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.MenuAdapter;
import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodFragmentFood extends Fragment implements AdapterView.OnItemSelectedListener {

    public static FoodFragmentFood newInstance() {
        return new FoodFragmentFood();
    }

    private List<Menu> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;
    private Spinner sort;

    AutoCompleteTextView search;
    ArrayList<Menu> arrayOfFood;
    ArrayList<Menu> arrayOfFoodHolder;

    ArrayAdapter<String> adapter;
    private String[] food;
    String sortBy="";

    Menu menu;

    Database baza;
    Context c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_food_food,null);

        baza = new Database(c);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        sort = (Spinner) view.findViewById(R.id.food_fragment_sort);

        setSpinner();

        mAdapter = new MenuAdapter(menuList, c);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        //Autocomplete
        prepareFoodData();
        search = (AutoCompleteTextView) view.findViewById(R.id.food_fragment_search);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, food);
        search.setAdapter(adapter);

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                prepareMenuData(s);
            }
        });

        prepareMenuData("");

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Menu menu = menuList.get(position);
                Toast.makeText(c, menu.getName() + " is selected!", Toast.LENGTH_SHORT).show();

                //Mine improv
                Intent foodActivity = new Intent(getActivity(), FoodActivity.class);
                foodActivity.putExtra("Menu", menu);

                startActivity(foodActivity);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return view;
    }



    private void setSpinner (){
        // Spinner Drop down elements
        sort.setOnItemSelectedListener(this);

        List<String> currency = new ArrayList<String>();
        currency.add("Name");
        currency.add("Price ASC");
        currency.add("Price DSC");
        currency.add("Rating");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, currency);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sort.setAdapter(dataAdapter);

    }

    public class CustomComparator implements Comparator<Menu> {
        @Override
        public int compare(Menu left, Menu right) {

            if (sortBy.equals("Price ASC")){
                return (left.getPrice() < right.getPrice()) ? -1 : (left.getPrice() > right.getPrice()) ? 1:0 ;
            }
            else if (sortBy.equals("Price DSC")){
                return (left.getPrice() > right.getPrice()) ? -1 : (left.getPrice() > right.getPrice()) ? 1:0 ;
            }
            else if (sortBy.equals("Rating")){
                return String.valueOf(right.getRate()).compareTo(String.valueOf(left.getRate()));
            }
            else {
                return left.getName().compareTo(right.getName());
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        sortBy = parent.getItemAtPosition(position).toString();

        Collections.sort(menuList, new CustomComparator());

        mAdapter.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        sortBy = "";
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void prepareMenuData(String search) {

        if (search.equals("")) {
            baza = new Database(c);
            arrayOfFood = baza.readMenu("getMenu");

            for (Menu value : arrayOfFood) {
                menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getRate(), value.getPrice(), value.getFood_id(), value.getNameFood(), value.getUser_id());
                menuList.add(menu);
            }
        }
        else
        {
            menuList.clear();
            for (int i = 0 ; i<arrayOfFood.size();i++){
                if (search.equals(arrayOfFood.get(i).getNameFood()))
                    menuList.add(arrayOfFood.get(i));
            }
        }


        mAdapter.notifyDataSetChanged();
    }

    private void prepareFoodData() {
        int i=0;
        ArrayList<Food> arrayOfFood2;
        baza = new Database(c);

        arrayOfFood2 = baza.readFood("getFood");

        food = new String[arrayOfFood2.size()];

        for(Food value: arrayOfFood2) {
           System.out.println(value.name);
            food[i]=value.name;
            i++;
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FoodFragmentFood.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FoodFragmentFood.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}

