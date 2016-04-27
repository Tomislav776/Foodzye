package com.example.tomipc.foodzye.adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {

    private List<Place> placeList;
    private Context c;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address;
        public ImageView placeImage;
        public AppCompatRatingBar rate;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.row_place_name);
            address = (TextView) view.findViewById(R.id.row_place_location);
            placeImage = (ImageView) view.findViewById(R.id.row_place_picture);
            rate = (android.support.v7.widget.AppCompatRatingBar) view.findViewById(R.id.row_place_rating_bar);
        }
    }


    public PlaceAdapter(List<Place> placeList, Context c) {
        this.placeList = placeList;
        this.c = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = placeList.get(position);

        holder.name.setText(place.getName());
        Glide.with(c)
                .load("http://164.132.228.255/"+place.getPicture())
                .into(holder.placeImage);
        holder.rate.setRating((float) place.getRate());
        holder.address.setText(String.valueOf(place.getLocation()));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

}
