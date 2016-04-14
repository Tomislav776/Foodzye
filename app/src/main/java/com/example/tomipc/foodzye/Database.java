package com.example.tomipc.foodzye;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tomipc.foodzye.fragments.FoodFragmentFood;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Down on 14.4.2016..
 */
public class Database extends AppCompatActivity {

    private static final String FOOD_URL = "http://10.0.3.2/food2/";

    HttpURLConnection connection;

    String foodJSON;


    public void insert (Context c) {

        System.out.println("Teeeest");

        String food = "nesto";


        final HashMap postData = new HashMap();
        postData.put("food", food);



        PostResponseAsyncTask task = new PostResponseAsyncTask(c, postData, new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                System.out.println(result);
                if (result.equals("success")) {
                   // Toast.makeText(FoodFragmentFood, "You have successfully registered and logged in.", Toast.LENGTH_LONG).show();

                } else {

                }
            }
        });

        task.execute(FOOD_URL);
    }

/*
    public void sendPostRequest() {
        new JSON(this).execute();
    }

    private class JSON extends AsyncTask<String, String, String> {

        private final Context context;

        public JSON(Context c){
            this.context = c;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = null;
                String response = null;
                url = new URL(FOOD_URL);
                //create the connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                //set the request method to GET
                connection.setRequestMethod("POST");
                String line = "";
                //create your inputstream
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write("food=tets");
                writer.flush();
                writer.close();
                os.close();



                //response from server
                int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + "food");
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                System.out.println(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());





                return null;
            } catch (Exception e) {
                Log.e("HTTP POST:", e.toString());
            }
            return "Error. Please try again later.";
        }
    }
*/

}
