package com.example.tomipc.foodzye.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.DividerItemDecoration;
import com.example.tomipc.foodzye.MainActivity;
import com.example.tomipc.foodzye.location.LocationActivity;
import com.example.tomipc.foodzye.ProfileActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.ReviewAdapter;
import com.example.tomipc.foodzye.loginActivity;
import com.example.tomipc.foodzye.model.Review;
import com.example.tomipc.foodzye.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragmentProfileTab extends Fragment {
    public static ProfileFragmentProfileTab newInstance() {
        return new ProfileFragmentProfileTab();
    }

    private static final int REQUEST_CALL = 5;

    Database db;
    Context c;
    User user;

    private RecyclerView recycleReviewList;
    private Button SendReviewButton;
    private Button logInButton;

    private AppCompatRatingBar FoodServiceProviderUserReviewRatingBar;
    private EditText ReviewEditText;
    private TextView DescriptionTextView, EmailTextView, PhoneTextView, WorkHoursTextView, ReviewTextView, ReviewTextView2, AdressTextView;
    private RelativeLayout locationClickableView;

    private Review reviewObj;
    private int user_id, logged_in_user_id, CallingPermission;

    public ArrayList<Review> arrayOfAllReview = new ArrayList<>();

    private List<Review> reviewList = new ArrayList<>();
    public ArrayList<Review> arrayOfReview = new ArrayList<>();
    private ReviewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_profile_food_service_provider, null);
        ProfileActivity activity = (ProfileActivity) getActivity();
        user_id = activity.getUserId();
        logged_in_user_id = activity.getLoggedInUserId();
        user = activity.getUser();

        db = new Database(c);

        locationClickableView = (RelativeLayout) view.findViewById(R.id.place_location_relative_click);
        logInButton = (Button) view.findViewById(R.id.profile_review_login_button);
        SendReviewButton = (Button) view.findViewById(R.id.SendPlaceReviewButton);
        FoodServiceProviderUserReviewRatingBar = (AppCompatRatingBar) view.findViewById(R.id.FoodServiceProviderUserReviewRatingBar);
        ReviewEditText = (EditText) view.findViewById(R.id.ReviewEditTextView);
        DescriptionTextView = (TextView) view.findViewById(R.id.DescriptionTextView);
        EmailTextView = (TextView) view.findViewById(R.id.EmailTextView);
        PhoneTextView = (TextView) view.findViewById(R.id.PhoneTextView);
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            CallingPermission = 0;
        }else{
            CallingPermission = 1;
        }
        PhoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissionForCalling();
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PhoneTextView.getText().toString()));
                startActivity(i);
            }
        });
        WorkHoursTextView = (TextView) view.findViewById(R.id.WorkHoursTextView);
        ReviewTextView = (TextView) view.findViewById(R.id.ReviewTextView);
        ReviewTextView2 = (TextView) view.findViewById(R.id.ReviewTextView2);
        AdressTextView = (TextView) view.findViewById(R.id.AdressTextView);

        DescriptionTextView.setText(user.getDescription());
        EmailTextView.setText(user.getEmail());
        PhoneTextView.setText(user.getPhone());
        WorkHoursTextView.setText(user.getWork_time());
        AdressTextView.setText(user.getLocation());

        if (user_id == logged_in_user_id || logged_in_user_id == 0){
            SendReviewButton.setVisibility(View.GONE);
            ReviewEditText.setVisibility(View.GONE);

            if (user_id == logged_in_user_id)
                logInButton.setVisibility(View.GONE);
            else
                logInButton.setVisibility(View.VISIBLE);

            ReviewTextView.setVisibility(View.GONE);
            FoodServiceProviderUserReviewRatingBar.setVisibility(View.GONE);

            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), loginActivity.class);
                    startActivity(i);
                }
            });


        }
        else{
            SendReviewButton.setVisibility(View.VISIBLE);
            ReviewEditText.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.GONE);
            FoodServiceProviderUserReviewRatingBar.setVisibility(View.VISIBLE);
            ReviewTextView.setVisibility(View.VISIBLE);

            arrayOfReview = db.readUserReview("getUsersReviewPlace", String.valueOf(user_id), String.valueOf(logged_in_user_id));

            if (!(arrayOfReview.isEmpty())) {
                FoodServiceProviderUserReviewRatingBar.setRating((float) arrayOfReview.get(0).getRate());
                ReviewEditText.setText(arrayOfReview.get(0).getComment());
            }
        }

        locationClickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.locationOnBool) {
                    Intent i = new Intent(getActivity(), LocationActivity.class);
                    i.putExtra("Location", user.getLocation());
                    startActivity(i);
                }
            }
        });

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
            reviewObj = new Review(value.getComment(), value.getRate(), value.getUsername(), value.getUserPicture(), value.getDateCreated(), value.getDateUpdated());

            reviewList.add(reviewObj);
        }

        if(arrayOfAllReview.isEmpty()){
            ReviewTextView2.setText("There are no reviews at the moment.");
        }

        mAdapter.notifyDataSetChanged();

    }

    // Called when the user wants to take a picture
    public void getPermissionForCalling() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {
                // Show our own UI to explain to the user why we need to get the Camera permission
                // before actually requesting the permission and showing the default UI

                showMessageOKCancel("You need to grant the calling permission if you want to call this number.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Fire off an async request to actually get the permission
                                // This will show the standard permission request dialog UI
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                        REQUEST_CALL);
                            }
                        });
            }

        }else{
            CallingPermission = 1;
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original REQUEST_CALL request
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CallingPermission = 1;
                Toast.makeText(getActivity(), "Calling permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                CallingPermission = 0;
                Toast.makeText(getActivity(), "Calling permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("I understand", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }
}
