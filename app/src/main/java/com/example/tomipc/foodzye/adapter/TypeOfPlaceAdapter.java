package com.example.tomipc.foodzye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.TypeOfPlace;

import java.util.ArrayList;

public class TypeOfPlaceAdapter extends ArrayAdapter<TypeOfPlace> {
    int resource;
    ArrayList<TypeOfPlace> items;
    TypeOfPlace typeOfPlace;


    public TypeOfPlaceAdapter(Context context, int ResourceId, ArrayList<TypeOfPlace> AllFood) {
        super(context, ResourceId, AllFood);
        resource = ResourceId;
        items = AllFood;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resource, parent, false);
        }
        typeOfPlace = items.get(position);
        System.out.println("tip2 " + typeOfPlace.getType());
        if (typeOfPlace != null) {
            TextView lblName = (TextView) view.findViewById(R.id.foodName2);
            if (lblName != null)
                lblName.setText(typeOfPlace.getType());
        }
        return view;
    }
}
