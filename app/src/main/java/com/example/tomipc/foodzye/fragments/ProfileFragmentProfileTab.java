package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.DividerItemDecoration;
import com.example.tomipc.foodzye.ProfileActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.ReviewAdapter;
import com.example.tomipc.foodzye.model.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragmentProfileTab extends Fragment {
    public static ProfileFragmentProfileTab newInstance() {
        return new ProfileFragmentProfileTab();
    }

    Database db;
    Context c;

    private RecyclerView recycleReviewList;
    private Button SendReviewButton;
    private AppCompatRatingBar FoodServiceProviderUserReviewRatingBar;
    private EditText ReviewEditText;

    private Review reviewObj;
    private int user_id, logged_in_user_id;

    public ArrayList<Review> arrayOfAllReview = new ArrayList<>();

    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_profile_food_service_provider, null);
        ProfileActivity activity = (ProfileActivity) getActivity();
        user_id = activity.getUserId();
        logged_in_user_id = activity.getLoggedInUserId();

        db = new Database(c);

        SendReviewButton = (Button) view.findViewById(R.id.SendPlaceReviewButton);
        FoodServiceProviderUserReviewRatingBar = (AppCompatRatingBar) view.findViewById(R.id.FoodServiceProviderUserReviewRatingBar);
        ReviewEditText = (EditText) view.findViewById(R.id.ReviewEditTextView);

        SendReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = ReviewEditText.getText().toString();
                double rate = FoodServiceProviderUserReviewRatingBar.getRating();
                String rate2 = Double.toString(rate);
                String place_id = Integer.toString(user_id);
                String user_id = Integer.toString(logged_in_user_id);
                HashMap postdata = new HashMap();
                postdata.put("comment", review);
                postdata.put("rate", rate2);
                postdata.put("place_id", place_id);
                postdata.put("user_id", user_id);
                db.insert(postdata, "postFoodServiceReview");

                //TODO: da se odmah osvjezi tj. prikaze novo dodani review u recyclerviewu
            }
        });

        recycleReviewList = (RecyclerView) view.findViewById(R.id.activity_profile_review_recycler_view);

        //Recycler view
        mAdapter = new ReviewAdapter(reviewList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycleReviewList.setLayoutManager(mLayoutManager);
        recycleReviewList.setItemAnimator(new DefaultItemAnimator());
        recycleReviewList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recycleReviewList.setAdapter(mAdapter);

        prepareReviewData();

        return view;
    }

    private void prepareReviewData() {
        arrayOfAllReview = db.readFoodServiceProviderReviews("getUserReviews", String.valueOf(user_id));

        for(Review value: arrayOfAllReview) {
            reviewObj = new Review(value.getComment(), value.getRate(), value.getUsername(), value.getUserPicture());

            reviewList.add(reviewObj);
        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }
}
