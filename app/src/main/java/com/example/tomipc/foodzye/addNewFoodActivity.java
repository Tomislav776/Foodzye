package com.example.tomipc.foodzye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class addNewFoodActivity extends AppCompatActivity {

    private EditText NewFoodEditText;
    private Button AddNewTypeOfFoodButton;
    private String NewFoodType;
    private static final String postFood = "postFood";

    Database db;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_food);
        c = this;
        NewFoodEditText = (EditText) findViewById(R.id.NewFoodEditText);
        AddNewTypeOfFoodButton = (Button) findViewById(R.id.AddNewTypeOfFoodButton);
        AddNewTypeOfFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFoodType = NewFoodEditText.getText().toString();
                if (!validate()) {
                    return;
                }
                HashMap data = new HashMap();
                data.put("name", NewFoodType);
                db = new Database(c);
                db.insert(data, postFood);

                Intent intent = new Intent(addNewFoodActivity.this, addFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        if (NewFoodType.isEmpty()) {
            NewFoodEditText.setError("You must enter the food type name!");
            valid = false;
        } else {
            NewFoodEditText.setError(null);
        }

        return valid;
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(addNewFoodActivity.this, addFoodActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }
}
