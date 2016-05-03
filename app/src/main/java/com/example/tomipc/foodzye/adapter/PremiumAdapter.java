package com.example.tomipc.foodzye.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Premium;

import java.util.ArrayList;

public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.PremiumHolder> {
    private static String LOG_TAG = "PremiumAdapter";
    private ArrayList<Premium> mDataset;
    private Context c;

    public static class PremiumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView description, price, duration;
        ImageView image;

        public PremiumHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.PremiumDescriptionTextView);
            price = (TextView) itemView.findViewById(R.id.PremiumPriceTextView);
            duration = (TextView) itemView.findViewById(R.id.PremiumDurationTextView);
            image = (ImageView) itemView.findViewById(R.id.PremiumImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public PremiumAdapter(ArrayList<Premium> myDataset, Context c) {
        mDataset = myDataset;
        this.c = c;
    }

    @Override
    public PremiumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_get_premium_account_card_view, parent, false);

        PremiumHolder premiumHolder = new PremiumHolder(view);
        return premiumHolder;
    }

    @Override
    public void onBindViewHolder(PremiumHolder holder, int position) {
        Premium premium = mDataset.get(position);
        holder.description.setText(premium.getDescription());
        String price = premium.getPrice() + " " + premium.getCurrency();
        holder.price.setText(price);
        String x;
        if(premium.getMonths_duration().equals("1")){
            x = "month";
        }else{
            x = "months";
        }
        String duration = "Duration: " + premium.getMonths_duration() + " " + x;
        holder.duration.setText(duration);
        if(premium.getImage_url().equals(""))
            holder.image.setImageResource(R.drawable.user_profile);
        else
            Glide.with(c).load(Database.URL + premium.getImage_url()).thumbnail(0.3f).into(holder.image);
    }

    public void addItem(Premium dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
