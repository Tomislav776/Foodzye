package com.example.tomipc.foodzye;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class FoodAdapter extends ArrayAdapter<Food> implements Filterable {
    private ArrayList<Food> items;
    private ArrayList<Food> itemsAll;
    private ArrayList<Food> suggestions;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
    }

    public FoodAdapter(Context context, ArrayList<Food> AllFood) {
        super(context, R.layout.item_food, AllFood);
        /*itemsAll = AllFood;
        for (Food element : itemsAll) {
            System.out.println(element.name);
        }*/

    }

   /*public MenuAdapter(Context context, int resource,
                               int textViewResourceId, ArrayList<Food> fullList) {
        super(context, resource, textViewResourceId, fullList);
        this.fullList = fullList;
        mOriginalValues = new ArrayList<Food>(fullList);
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Food food = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_food, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.foodName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(food.name);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Food)(resultValue)).name;
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Food customer : itemsAll) {
                    if(customer.name.toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new Filter.FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Food> filteredList = (ArrayList<Food>) results.values;
            /*if(results != null && results.count > 0) {
                clear();
                for (Food c : filteredList) {
                    add(c);
                    System.out.println(c.name);
                }
                notifyDataSetChanged();
            }
            else{
                clear();
                Food foodY = new Food("Add new food");
                add(foodY);
                notifyDataSetChanged();
            }*/

            System.out.println("sdoipfkdsfkosr");

            for(Food value: filteredList){
                System.out.println(value.name);
            }

            if(results.count == 0){
                //System.out.println("rez " + results.count);
                clear();
                String x = String.valueOf(results.count);
                Log.d("Konj", x);
                Food foodY = new Food("Add new food");
                add(foodY);
                notifyDataSetChanged();
            }else{
                //System.out.println("rez2 " + results.count);
                clear();
                String x = String.valueOf(results.count);
                Log.d("Konj", x);

                for (Food c : filteredList) {
                    add(c);
                    System.out.println(c.name);
                }
                notifyDataSetChanged();
            }

        }
    };
}