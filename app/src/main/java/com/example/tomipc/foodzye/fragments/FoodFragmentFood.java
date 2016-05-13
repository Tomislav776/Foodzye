package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.DividerItemDecoration;
import com.example.tomipc.foodzye.FoodActivity;
import com.example.tomipc.foodzye.MainActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.MenuAdapter;
import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.User;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodFragmentFood extends Fragment implements AdapterView.OnItemSelectedListener
       {

    public static FoodFragmentFood newInstance() {
        return new FoodFragmentFood();
    }

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

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

           User user;
    Menu menu;


    Database db;
    Context c;

           private double latitude, longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_food_food, null);

        db = new Database(c);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        sort = (Spinner) view.findViewById(R.id.food_fragment_sort);

        mAdapter = new MenuAdapter(menuList, c);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        food = new String[1];
        food[0] = "";
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



        return view;
    }

    @Override
    public void onResume() {
        setSpinner();
        //Autocomplete
        prepareFoodData();
        menuList.clear();
        mAdapter.notifyDataSetChanged();
        prepareMenuData("");

        super.onResume();
    }


    private void setSpinner (){
        // Spinner Drop down elements
        sort.setOnItemSelectedListener(this);

        List<String> sortParameters = new ArrayList<String>();
        sortParameters.add("Name");
        sortParameters.add("Price ASC");
        sortParameters.add("Price DSC");
        sortParameters.add("Rating");
        if (MainActivity.locationOnBool)
            sortParameters.add("Distance");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, sortParameters);

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
            else if (sortBy.equals("Distance") && MainActivity.locationOnBool){

                int i;
                String leftD="", rightD="";
                double leftDD, rightDD;
                double times1 = 1, times2 = 1;

                for ( i = 0; i < right.getDistance().length(); i++){
                    if (!(right.getDistance().charAt(i) == 'k' || right.getDistance().charAt(i) == 'm')){
                        rightD += right.getDistance().charAt(i);
                    }
                    else{
                        break;
                    }
                }

                if (right.getDistance().charAt(i)=='k')
                    times2=1000;

                for ( i = 0; i < left.getDistance().length(); i++){
                    if (!(left.getDistance().charAt(i) == 'k' || left.getDistance().charAt(i) == 'm')){
                        leftD += left.getDistance().charAt(i);
                    }else{
                        break;
                    }

                }
                if (left.getDistance().charAt(i)=='k')
                    times1=1000;

                leftDD = Double.parseDouble(leftD) * times1;
                rightDD = Double.parseDouble(rightD) * times2;

                return (leftDD < rightDD) ? -1 : (leftDD < rightDD) ? 1:0 ;
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
        String distance;

        if (search.equals("")) {
            db = new Database(c);
            arrayOfFood = db.readMenu("getMenu");

            for (Menu value : arrayOfFood) {

                if (MainActivity.hashMap.isEmpty()){
                    menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getRate(), value.getPrice(), value.getFood_id(), value.getNameFood(), value.getUser_id());
                }else{
                    distance = MainActivity.hashMap.get(value.getUser_id());
                    menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getRate(), value.getPrice(), value.getFood_id(), value.getNameFood(), value.getUser_id(), distance);
                }
                menuList.add(menu);
            }
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Menu menu = menuList.get(position);

                    Intent openMainActivity= new Intent(getActivity(), FoodActivity.class);
                    openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    openMainActivity.putExtra("Menu", menu);
                    startActivity(openMainActivity);



                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
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
        db = new Database(c);

        arrayOfFood2 = db.readFood("getFood");

        food = new String[arrayOfFood2.size()];

        for(Food value: arrayOfFood2) {
            food[i]=value.name;
            i++;
        }

    }



    @Override
    public void onStart() {
        super.onStart();

    }



    @Override
    public void onStop() {
        super.onStop();
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

