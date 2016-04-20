package com.example.tomipc.foodzye.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Menu;

import java.util.List;

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

        private List<Menu> foodList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, price;
            public ImageView foodImage, rate;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name_food);
                price = (TextView) view.findViewById(R.id.price_food);
                foodImage = (ImageView) view.findViewById(R.id.image_food);
                rate = (ImageView) view.findViewById(R.id.rate_food);
            }
        }


        public MenuAdapter(List<Menu> foodList) {
            this.foodList = foodList;
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

            //holder.foodImage.setImageResource(food.get(position));
            holder.name.setText(food.getName());
            //holder.rate.setImageResource(food.getYear());
            holder.price.setText(String.valueOf(food.getPrice()));
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

    }
