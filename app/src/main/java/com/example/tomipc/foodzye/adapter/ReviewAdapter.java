package com.example.tomipc.foodzye.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.Review;

import java.util.List;


    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

        private List<Review> reviewList;
        private Context c;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView comment, username;
            public ImageView userImage;
            public RatingBar rate;

            public MyViewHolder(View view) {
                super(view);
                comment = (TextView) view.findViewById(R.id.review_review_text);
                username = (TextView) view.findViewById(R.id.review_name_user);
                userImage = (ImageView) view.findViewById(R.id.review_image_user);
                rate = (RatingBar) view.findViewById(R.id.review_food_rate);
            }
        }


        public ReviewAdapter(List<Review> reviewList, Context c) {
            this.reviewList = reviewList;
            this.c = c;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Review review = reviewList.get(position);

            holder.comment.setText(review.getComment());
            Glide.with(c)
                    .load("http://164.132.228.255/"+review.getUserPicture())
                    .into(holder.userImage);
            holder.rate.setRating((float) review.getRate());
            holder.username.setText(review.getUsername());
        }

        @Override
        public int getItemCount() {

            return reviewList.size();
        }
}
