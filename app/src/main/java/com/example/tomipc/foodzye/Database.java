package com.example.tomipc.foodzye;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.Place;
import com.example.tomipc.foodzye.model.Review;
import com.example.tomipc.foodzye.model.User;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class Database {

    HttpURLConnection connection;
    public static String URL = "http://164.132.228.255/"; //164.132.228.255  10.0.3.2

    private Context context;

    private ArrayList<Food> arrayOfFood = new ArrayList<>();
    private ArrayList<Menu> arrayOfMenu = new ArrayList<>();
    private ArrayList<Place> arrayOfPlace = new ArrayList<>();
    private ArrayList<Review> arrayOfReview = new ArrayList<>();
    private ArrayList<Review> arrayOfSingleReview = new ArrayList<>();

    public Database(){
    }

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

        PostResponseAsyncTask task = new PostResponseAsyncTask(context, data, new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                System.out.println(result);
                if (result.equals("success")) {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "Error. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        try{
            task.execute(URL + route).get();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public User getUserData (String route, String user_id) {
        User user = null;
        try {
            String userJSON;
            userJSON = new Read().execute(URL+route+"/"+user_id).get();
            JSONArray obj = new JSONArray(userJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String email = jObject.getString("email");
                String description = jObject.getString("description");
                String location = jObject.getString("location");
                String phone = jObject.getString("phone");
                String picture = jObject.getString("user_picture");
                String work_time = jObject.getString("work_time");
                String name = jObject.getString("name");
                double rate = jObject.getDouble("rate_total");
                String slug = jObject.getString("slug");

                user = new User(name,email, location, phone, picture, work_time, description,rate,slug);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    public ArrayList<Review> readFoodServiceProviderReviews (String route, String user_idIn) {
        try {
            String MenuReviewJSON;
            MenuReviewJSON = new Read().execute(URL+route+"/"+user_idIn).get();
            JSONArray obj = new JSONArray(MenuReviewJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String comment = jObject.getString("comment");
                double rate = jObject.getDouble("rate");
                String name = jObject.getString("name");
                String picture = jObject.getString("user_picture");
                String dateCreated = jObject.getString("created_at");
                String dateUpdated = jObject.getString("updated_at");

                if(!(dateCreated.equals("null")))
                    dateCreated=""+dateCreated.charAt(8)+dateCreated.charAt(9)+"."+dateCreated.charAt(5)+dateCreated.charAt(6)+"."+dateCreated.charAt(0)+dateCreated.charAt(1)+dateCreated.charAt(2)+dateCreated.charAt(3)+".";
                else
                    dateCreated="";

                if(!(dateUpdated.equals("null")))
                    dateUpdated=""+dateUpdated.charAt(8)+dateUpdated.charAt(9)+"."+dateUpdated.charAt(5)+dateUpdated.charAt(6)+"."+dateUpdated.charAt(0)+dateUpdated.charAt(1)+dateUpdated.charAt(2)+dateUpdated.charAt(3)+".";
                else
                    dateUpdated="";

                Review review = new Review(comment, rate, name, picture, dateCreated, dateUpdated);

                arrayOfReview.add(review);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return arrayOfReview;
    }


    public ArrayList<Review> readUserReview (String route, String menu_idIn, String user_idIn) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/"+menu_idIn+"/"+user_idIn).get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String comment = jObject.getString("comment");
                double rate = jObject.getDouble("rate");

                Review review = new Review(comment, rate);

                arrayOfSingleReview.add(review);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return arrayOfSingleReview;
    }

    public ArrayList<Review> readReview (String route, String menu_idIn) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/"+menu_idIn).get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                String comment = jObject.getString("comment");
                double rate = jObject.getDouble("rate");
                String name = jObject.getString("name");
                String picture = jObject.getString("user_picture");
                String dateCreated = jObject.getString("created_at");
                String dateUpdated = jObject.getString("updated_at");

                if(!(dateCreated.equals("null")))
                    dateCreated=""+dateCreated.charAt(8)+dateCreated.charAt(9)+"."+dateCreated.charAt(5)+dateCreated.charAt(6)+"."+dateCreated.charAt(0)+dateCreated.charAt(1)+dateCreated.charAt(2)+dateCreated.charAt(3)+".";
                else
                    dateCreated="";

                if(!(dateUpdated.equals("null")))
                    dateUpdated=""+dateUpdated.charAt(8)+dateUpdated.charAt(9)+"."+dateUpdated.charAt(5)+dateUpdated.charAt(6)+"."+dateUpdated.charAt(0)+dateUpdated.charAt(1)+dateUpdated.charAt(2)+dateUpdated.charAt(3)+".";
                else
                    dateUpdated="";

                Review review = new Review(comment, rate, name, picture, dateCreated, dateUpdated);

                arrayOfReview.add(review);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return arrayOfReview;
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
                String nameFood = jObject.getString("foodName");
                String description = jObject.getString("description");
                String currency = jObject.getString("currency");
                String image = jObject.getString("food_image");
                double price = jObject.getDouble("price");
                double rate = jObject.getDouble("rate_total");
                int food_id = jObject.getInt("food_id");
                int user_id = jObject.getInt("user_id");

                Menu food = new Menu(id, name, description,  currency,  image,  rate,  price, food_id, nameFood, user_id);
                arrayOfMenu.add(food);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arrayOfMenu;
    }

    public ArrayList<Menu> readUserMenu (String route) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/").get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                int id = jObject.getInt("id");
                String name = jObject.getString("name");
                String nameFood = jObject.getString("foodName");
                String description = jObject.getString("description");
                String currency = jObject.getString("currency");
                String image = jObject.getString("food_image");
                double price = jObject.getDouble("price");
                double rate = jObject.getDouble("rate_total");
                int food_id = jObject.getInt("food_id");
                int user_id = jObject.getInt("user_id");

                Menu food = new Menu(id, name, description,  currency,  image,  rate,  price, food_id, nameFood, user_id);
                arrayOfMenu.add(food);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arrayOfMenu;
    }


    public ArrayList<Place> readPlace (String route) {
        try {
            String foodJSON;
            foodJSON = new Read().execute(URL+route+"/").get();
            JSONArray obj = new JSONArray(foodJSON);

            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);

                int id = jObject.getInt("id");
                int role = jObject.getInt("role");
                String name = jObject.getString("name");
                String email = jObject.getString("email");
                String slug = jObject.getString("slug");
                String address = jObject.getString("location");
                String phone = jObject.getString("phone");
                String picture = jObject.getString("user_picture");
                String time = jObject.getString("work_time");
                String description = jObject.getString("description");
                double rate = jObject.getDouble("rate_total");

                Place place = new Place(id, role, name, email, slug, address, phone, picture, time, rate, description);

                arrayOfPlace.add(place);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arrayOfPlace;
    }



    private class Read extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... urls) {
            //posalji zahtjev
            try {
                URL url = null;
                String response = null;
                //System.out.println("Teest: "+urls[0]);
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
