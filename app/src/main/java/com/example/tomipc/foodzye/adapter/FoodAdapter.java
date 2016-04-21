package com.example.tomipc.foodzye.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Food;

import java.util.ArrayList;

public class FoodAdapter extends ArrayAdapter<Food>{
    int resource;
    ArrayList<Food> items, tempItems, suggestions;


    public FoodAdapter(Context context, int ResourceId,ArrayList<Food> AllFood) {
        super(context, ResourceId, AllFood);
        resource = ResourceId;
        items = AllFood;
        tempItems = new ArrayList<Food>(items);
        suggestions = new ArrayList<Food>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(resource, parent, false);
        }
        Food food = items.get(position);
        if (food != null) {
            TextView lblName = (TextView) view.findViewById(R.id.foodName2);
            if (lblName != null)
                lblName.setText(food.name);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Food) resultValue).name;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Food food : tempItems) {
                    if (food.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(food);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Food> filterList = (ArrayList<Food>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Food food : filterList) {
                    add(food);
                    notifyDataSetChanged();
                }
            }
            if(results.count == 0){
                clear();
                Food addNew = new Food("There is no such food. Click me if you want to add it.");
                add(addNew);
                notifyDataSetChanged();
            }
        }
    };


}