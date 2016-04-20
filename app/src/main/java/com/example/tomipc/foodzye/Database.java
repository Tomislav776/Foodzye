package com.example.tomipc.foodzye;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.Menu;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URL;


public class Database {


    HttpURLConnection connection;
    private static String URL = "http://10.0.3.2/";

    private final Context context;

    public ArrayList<Food> arrayOfFood = new ArrayList<>();
    public ArrayList<Menu> arrayOfMenu = new ArrayList<>();


    public Database(Context c){
        this.context = c;
    }

    /**
     * data is Hash map that is going to be inserted into database
     * route is the name of the route, only last part, http://10.0.3.2/route
     * @param data
     * @param route
     */
    public void insert (HashMap data, String route) {

        //System.out.println(URL+route+" "+data);

        PostResponseAsyncTask task = new PostResponseAsyncTask(context, data, new AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result.equals("success")) {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "Error. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        task.execute(URL + route);
    }


    public ArrayList<Food> readFood (String route) {
                try {
                    String foodJSON;
                    foodJSON = new Read().execute(URL+route+"/").get();
                    JSONArray obj = new JSONArray(foodJSON);

                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jObject = obj.getJSONObject(i);

                        int id = jObject.getInt("id");
                        String name = jObject.getString("name");

                        Food food = new Food(id, name);

                        arrayOfFood.add(food);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }

        return arrayOfFood;
    }

    //TODO: Znam da mogu imat univerzalnu read funkciju samo moram imat poziv konstruktora koji prima univerzalni parametar, mozak mi ne radi trenutno glupo rijesenje.
    public ArrayList<Menu> readMenu (String route) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/").get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                int id = jObject.getInt("id");
                String name = jObject.getString("name");
                String description = jObject.getString("description");
                String currency = jObject.getString("currency");
                String image = jObject.getString("food_image");
                double price = jObject.getDouble("price");
                double rate = jObject.getDouble("rate_total");

                Menu food = new Menu(id, name, description,  currency,  image,  rate,  price);
                arrayOfMenu.add(food);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arrayOfMenu;
    }



    private class Read extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... urls) {
            //posalji zahtjev
            try {
                URL url = null;
                String response = null;
                System.out.println("Teest: "+urls[0]);
                url = new URL(urls[0]);

                //create the connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setRequestMethod("GET");

                String line = "";
                //create your inputstream
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                //read in the data from input stream
                BufferedReader reader = new BufferedReader(in);
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                //get the string version of the response data
                response = sb.toString();
                //close input streams
                in.close();

                reader.close();
                return response;
            }
            catch (Exception e) {
                Log.e("HTTP GET:", e.toString());
            }

            return "Error. Please try again later.";
        }
    }
}
