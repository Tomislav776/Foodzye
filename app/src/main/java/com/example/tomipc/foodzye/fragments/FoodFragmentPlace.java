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
import com.example.tomipc.foodzye.ProfileActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.PlaceAdapter;
import com.example.tomipc.foodzye.model.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodFragmentPlace extends Fragment implements AdapterView.OnItemSelectedListener{

    public static FoodFragmentPlace newInstance() {
        return new FoodFragmentPlace();
    }

    private List<Place> placeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PlaceAdapter mAdapter;

    private Spinner sort;

    AutoCompleteTextView search;
    ArrayList<Place> arrayOfPlace;

    ArrayAdapter<String> adapter;
    private String[] placeSearch;
    String sortBy="";

    Place place;

    Database baza;
    Context c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_food_place,null);

        baza = new Database(c);

        sort = (Spinner) view.findViewById(R.id.place_fragment_sort);

        setSpinner();

        recyclerView = (RecyclerView) view.findViewById(R.id.place_fragment_recycler_view);

        mAdapter = new PlaceAdapter(placeList, c);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        preparePlaceData("");

        search = (AutoCompleteTextView) view.findViewById(R.id.place_fragment_search);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, placeSearch);
        search.setAdapter(adapter);

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getItemAtPosition(position).toString();
                preparePlaceData(s);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Place place = placeList.get(position);

                //Mine improv
                Intent profileActivity = new Intent(getActivity(), ProfileActivity.class);
                profileActivity.putExtra("Place", place);

                startActivity(profileActivity);
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
        currency.add("Rating");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, currency);

        // attaching data adapter to spinner
        sort.setAdapter(dataAdapter);

    }

    public class CustomComparator implements Comparator<Place> {
        @Override
        public int compare(Place left, Place right) {
            if (sortBy.equals("Rating")){
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

        Collections.sort(placeList, new CustomComparator());

        mAdapter.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        sortBy = "";
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void preparePlaceData(String search) {
    int i = 0;

        if (search.equals("")) {
            baza = new Database(c);
            arrayOfPlace = baza.readPlace("getPlace");

            placeSearch = new String[arrayOfPlace.size()];
            for(Place value: arrayOfPlace) {
                placeSearch[i]=value.getName();
                i++;
            }

            for (Place value : arrayOfPlace) {
                place = new Place(value.getId(), value.getRole(), value.getName(), value.getEmail(), value.getSlug(), value.getLocation(), value.getPhone(), value.getPicture(), value.getWork_time(), value.getRate(), value.getDescription());

                placeList.add(place);
            }
        }else
        {
            placeList.clear();
            for (i = 0 ; i<arrayOfPlace.size();i++){
                if (search.equals(arrayOfPlace.get(i).getName()))
                    placeList.add(arrayOfPlace.get(i));
            }
        }

        mAdapter.notifyDataSetChanged();
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
        private FoodFragmentPlace.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FoodFragmentPlace.ClickListener clickListener) {
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
