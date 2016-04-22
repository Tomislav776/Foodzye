package com.example.tomipc.foodzye;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.User;

import java.util.HashMap;

public class FoodActivity extends AppCompatActivity {

    private TextView name;
    private Button reviewButton;
    private TextView description;
    private TextView price;
    private TextView restaurant;
    private RatingBar rating;
    private ImageView imageFood;
    private RecyclerView reviewList;
    private EditText review;

    Database data;
    UserLocalStore userLocalStore;
    User user;

    private Menu food;
    private float ratingSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        name = (TextView) findViewById(R.id.activity_food_name);
        description = (TextView) findViewById(R.id.activity_food_description);
        price = (TextView) findViewById(R.id.activity_food_price);
        restaurant = (TextView) findViewById(R.id.activity_food_restaurant);
        rating = (RatingBar) findViewById(R.id.activity_food_ratingBar);
        imageFood = (ImageView) findViewById(R.id.activity_food_image);
        reviewList = (RecyclerView) findViewById(R.id.activity_food_recycler_view);
        reviewButton = (Button) findViewById(R.id.activity_food_button_review);
        review = (EditText) findViewById(R.id.activity_food_review);

        data = new Database(this);

        Intent i = getIntent();
        food = (Menu)i.getSerializableExtra("Menu");


        name.setText(food.getName());
        description.setText(food.getDescription());
        price.setText(String.valueOf(food.getPrice()) + " " + food.getCurrency());



        //rating.setRating((float) food.getRate());
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //System.out.println(rating);
                ratingSelected=rating;
            }
        });

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//System.out.println(user.id);
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


}
