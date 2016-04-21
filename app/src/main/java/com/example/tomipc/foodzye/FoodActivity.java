package com.example.tomipc.foodzye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tomipc.foodzye.model.Menu;

public class FoodActivity extends AppCompatActivity {

    private TextView name;
    private Button reviewButton;
    private TextView description;
    private TextView price;
    private TextView restaurant;
    private RatingBar rating;
    private ImageView imageFood;
    private RecyclerView review;



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
        review = (RecyclerView) findViewById(R.id.activity_food_recycler_view);
        reviewButton = (Button) findViewById(R.id.activity_food_button_review);

        Intent i = getIntent();
        Menu food = (Menu)i.getSerializableExtra("Menu");
        System.out.println("FOOD: " + food.getName() + food.getPrice());

        name.setText(food.getName());
        description.setText(food.getDescription());
        price.setText(String.valueOf(food.getPrice()) + " " + food.getCurrency());



        rating.setRating((float) food.getRate());
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                System.out.println(rating);
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent reviewActivity = new Intent(FoodActivity.this, ReviewActivity.class);
                startActivity(reviewActivity);
            }
        });



    }


}
