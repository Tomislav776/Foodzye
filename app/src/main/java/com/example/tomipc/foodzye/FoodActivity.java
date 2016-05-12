package com.example.tomipc.foodzye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.adapter.ReviewAdapter;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.Place;
import com.example.tomipc.foodzye.model.Review;
import com.example.tomipc.foodzye.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private TextView name;
    private Button reviewButton;
    private Button logInButton;
    private TextView description;
    private TextView price;
    private TextView restaurantName;
    private TextView restaurantLocation;
    private TextView restaurantWorkHours;
    private TextView restaurantPhoneNumber;
    private AppCompatRatingBar restaurantRating;
    private ImageView restaurantImage;
    private TextView textAddYour;
    private TextView textReview;
    private AppCompatRatingBar rating;
    private AppCompatRatingBar ratingTotal;
    private ImageView imageFood;
    private RecyclerView recycleReviewList;
    private EditText review;
    private RelativeLayout RestaurantClick;

    Toolbar toolbar;

    Database data;
    UserLocalStore userLocalStore;
    User user;

    User userdata;
    Place userdataP;


    private Review reviewObj;
    private Menu food;
    private float ratingSelected;

    public ArrayList<Review> arrayOfReview = new ArrayList<>();
    public ArrayList<Review> arrayOfAllReview = new ArrayList<>();

    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        toolbar = (Toolbar) findViewById(R.id.toolbarFood);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMainActivity= new Intent(FoodActivity.this, MainActivity.class);
                openMainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
                //finish();
            }
        });

        name = (TextView) findViewById(R.id.activity_food_name);
        description = (TextView) findViewById(R.id.activity_food_description);
        price = (TextView) findViewById(R.id.activity_food_price);
        textAddYour = (TextView) findViewById(R.id.activity_food_text_add_your_review);
        rating = (AppCompatRatingBar) findViewById(R.id.activity_food_ratingBar);
        ratingTotal = (AppCompatRatingBar) findViewById(R.id.activity_food_ratingBar_total);
        imageFood = (ImageView) findViewById(R.id.activity_food_image);
        recycleReviewList = (RecyclerView) findViewById(R.id.activity_food_recycler_view);
        reviewButton = (Button) findViewById(R.id.activity_food_button_review);
        logInButton = (Button) findViewById(R.id.activity_food_button_login);
        review = (EditText) findViewById(R.id.activity_food_review);
        textReview= (TextView) findViewById(R.id.activity_food_text_review);



        //Recycler view
        mAdapter = new ReviewAdapter(reviewList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleReviewList.setLayoutManager(mLayoutManager);
        recycleReviewList.setItemAnimator(new DefaultItemAnimator());
        recycleReviewList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycleReviewList.setAdapter(mAdapter);


        data = new Database(this);

        Intent i = getIntent();
        food = (Menu)i.getSerializableExtra("Menu");

        Glide.with(this).load(Database.URL + food.getImage()).into(imageFood);

        prepareReviewData(food.getId());

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        //Set restaurant in Food
        setRestaurant();

        ratingTotal.setRating((float) food.getRate());
        if (user == null){
            reviewButton.setVisibility(View.GONE);
            review.setVisibility(View.GONE);
            logInButton.setVisibility(View.VISIBLE);
            textAddYour.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);

            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FoodActivity.this, loginActivity.class);
                    startActivity(i);
                }
            });


        }
        else{
            reviewButton.setVisibility(View.VISIBLE);
            review.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.GONE);
            rating.setVisibility(View.VISIBLE);
            textAddYour.setVisibility(View.VISIBLE);

            arrayOfReview = data.readUserReview("getUsersReview", String.valueOf(food.getId()), String.valueOf(user.id));

            if (!(arrayOfReview.isEmpty())) {
                rating.setRating((float) arrayOfReview.get(0).getRate());
                review.setText(arrayOfReview.get(0).getComment());
            }
        }

        name.setText(food.getName());
        description.setText(food.getDescription());
        price.setText(String.valueOf(food.getPrice()) + " " + food.getCurrency());


        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingSelected = rating;
            }
        });


        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> dataSend = new HashMap<>();
                //TODO: tu stavi ako nije logiran da se logira
                dataSend.put("user_id", Integer.toString(user.id));
                dataSend.put("menu_id", Integer.toString(food.getId()));
                dataSend.put("rate", Float.toString(ratingSelected));
                dataSend.put("comment", String.valueOf(review.getText()));

                data.insert(dataSend, "postReview");

            }
        });

    }

    private void prepareReviewData( int id) {
        data = new Database(this);
        arrayOfAllReview = data.readReview("getReview", String.valueOf(id));

        for(Review value: arrayOfAllReview) {
            reviewObj = new Review(value.getComment(), value.getRate(), value.getUsername(), value.getUserPicture(), value.getDateCreated(), value.getDateUpdated());

            reviewList.add(reviewObj);
        }

        if (arrayOfAllReview.size() == 0)
            textReview.setVisibility(View.GONE);
        else
            textReview.setVisibility(View.VISIBLE);

        mAdapter.notifyDataSetChanged();

    }

    private void setRestaurant(){
        restaurantName = (TextView) findViewById(R.id.place_restaurant_name);
        restaurantLocation = (TextView) findViewById(R.id.place_restaurant_location);
        restaurantRating = (AppCompatRatingBar) findViewById(R.id.place_restaurant_rating_bar);
        restaurantImage = (ImageView) findViewById(R.id.place_restaurant_picture);
        RestaurantClick = (RelativeLayout) findViewById(R.id.place_restaurant_relative_click);
        restaurantWorkHours = (TextView) findViewById(R.id.place_restaurant_work_hours);
        restaurantPhoneNumber = (TextView) findViewById(R.id.place_restaurant_phone_number);



        userdata = data.getUserData("getUser", String.valueOf(food.getUser_id()));
        restaurantName.setText(userdata.getUsername());
        restaurantLocation.setText(userdata.getLocation());
        restaurantWorkHours.setText(userdata.getWork_time());
        restaurantPhoneNumber.setText(userdata.getPhone());
        restaurantRating.setRating((float) userdata.getRate());
        Glide.with(this).load(Database.URL+userdata.getPicture()).into(restaurantImage);

        RestaurantClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileActivity = new Intent(FoodActivity.this, ProfileActivity.class);
                userdataP = new Place(food.getUser_id(), 2, userdata.getUsername(), userdata.getEmail(), userdata.getSlug(), userdata.getLocation(), userdata.getPhone(), userdata.getPicture(), userdata.getWork_time(), userdata.getRate(), userdata.getDescription());

                profileActivity.putExtra("Place", userdataP);
                startActivity(profileActivity);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        user = userLocalStore.getLoggedInUser();
        if (user != null) {
            reviewButton.setVisibility(View.VISIBLE);
            review.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.GONE);
            rating.setVisibility(View.VISIBLE);
            textAddYour.setVisibility(View.VISIBLE);


            arrayOfReview = data.readUserReview("getUsersReview", String.valueOf(food.getId()), String.valueOf(user.id));

            if (!(arrayOfReview.isEmpty())) {
                rating.setRating((float) arrayOfReview.get(0).getRate());
                review.setText(arrayOfReview.get(0).getComment());
            }
            else{
                rating.setRating(0);
            }
        }
    }



    @Override
    public void onBackPressed() {

        Intent openMainActivity= new Intent(FoodActivity.this, MainActivity.class);
        openMainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openMainActivity);
        super.onBackPressed();

    }

}
