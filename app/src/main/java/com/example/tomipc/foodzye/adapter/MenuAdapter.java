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
import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Menu;

import java.util.List;

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

        private List<Menu> foodList;
        private Context c;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, price;
            public ImageView foodImage;
            public AppCompatRatingBar rate;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name_food);
                price = (TextView) view.findViewById(R.id.price_food);
                foodImage = (ImageView) view.findViewById(R.id.image_food);
                rate = (AppCompatRatingBar) view.findViewById(R.id.row_food_ratingBar);
            }
        }


        public MenuAdapter(List<Menu> foodList, Context c) {
            this.foodList = foodList;
            this.c = c;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Menu food = foodList.get(position);

            holder.name.setText(food.getName());
            Glide.with(c)
                    .load(Database.URL+food.getImage())
                    .into(holder.foodImage);
            holder.rate.setRating((float) food.getRate());
            holder.price.setText(String.valueOf(food.getPrice()) + " " + food.getCurrency());
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }


    }
