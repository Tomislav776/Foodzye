package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.FoodAdapter;
import com.example.tomipc.foodzye.fragments.FoodFragmentTab;
import com.example.tomipc.foodzye.fragments.LoginFragment;
import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class addFoodActivity extends AppCompatActivity {

    private static final String FOOD_URL = "http://164.132.228.255/getFood";

    UserLocalStore userLocalStore;
    User user;
    HttpURLConnection connection;
    AutoCompleteTextView ACText;
    Button addFoodButton, addNewFoodButton, CapturePictureButton, ChoosePictureButton;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    ProgressDialog progressDialog;
    ImageView imgPreview;
    ArrayList<Food> arrayOfFood;
    private String foodJSON, foodImage, encoded_string, filePath;
    Food chosenFood;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food2);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout2);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff2);

        mFragmentManager = getSupportFragmentManager();

        /**
         * Setup click events on the Navigation View Items.
         */


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_login) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new LoginFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_food) {
                    /*FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new AddFoodFragment()).commit();*/
                    Intent i = new Intent(addFoodActivity.this, MainActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLoggedIn(false);

                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();
                }

                return false;
            }

        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().hide();

        mDrawerToggle.syncState();

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        // Construct the data source
        arrayOfFood = new ArrayList<Food>();
        try {
            foodJSON = new getFoodJSON().execute(FOOD_URL).get();
            JSONArray obj = new JSONArray(foodJSON);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jObject = obj.getJSONObject(i);
                int id = jObject.getInt("id");
                String name = jObject.getString("name");
                Food food = new Food(id, name);
                arrayOfFood.add(food);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ACText = (AutoCompleteTextView) findViewById(R.id.acText2);
        addFoodButton = (Button) findViewById(R.id.AddFoodButton4);
        addNewFoodButton = (Button) findViewById(R.id.AddFoodButton3);
        CapturePictureButton = (Button) findViewById(R.id.take_picture2);
        ChoosePictureButton = (Button) findViewById(R.id.choose_picture);
        progressDialog = new ProgressDialog(addFoodActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading the menu...");
        imgPreview = (ImageView) findViewById(R.id.imgPreview3);

        CapturePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file_uri = Uri.fromFile(getOutputMediaFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, 10);
            }
        });

        ChoosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                new Upload_Food().execute("http://164.132.228.255/food_image");
            }
        });

        // Create the adapter to convert the array to views
        FoodAdapter adapter = new FoodAdapter(addFoodActivity.this, R.layout.item_food2, arrayOfFood);
        // Attach the adapter to a AutoCompleteTextView
        ACText.setAdapter(adapter);

        //OnItemClickListener for the AutoCompleteTextView
        ACText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                chosenFood = (Food) parent.getItemAtPosition(position);
                if(chosenFood.name.equals("There is no such food. Click me if you want to add it.")){
                    addNewFoodButton.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(addFoodActivity.this, chosenFood.name, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onPause(){

        super.onPause();
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    private class getFoodJSON extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                URL url = null;
                String response = null;
                url = new URL(FOOD_URL);
                //create the connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(false);
                //set the request method to GET
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
            } catch (Exception e) {
                Log.e("HTTP GET:", e.toString());
            }
            return "Error. Please try again later.";
        }
    }

    private File getOutputMediaFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        foodImage = "IMG_" + timeStamp + ".jpg";

        // External sdcard location - create a media file name
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + foodImage);

        return file;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {
            previewMedia();
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            file_uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_uri);
                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 8;

        filePath = file_uri.getPath();

        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        imgPreview.setImageBitmap(bitmap);
    }

    private class Upload_Food extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();

        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator var4 = params.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry entry = (Map.Entry)var4.next();
                if(first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_uri);
                foodImage = getFileName(file_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final int maxSize = 1280;
            int outWidth;
            int outHeight;
            int inWidth = bitmap.getWidth();
            int inHeight = bitmap.getHeight();
            if(inWidth > inHeight){
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            byte[] imageBytes = stream.toByteArray();
            encoded_string = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            HashMap<String,String> data = new HashMap<>();
            data.put("encoded_string", encoded_string);
            data.put("image_name", foodImage);
            data.put("username", user.username);

            URL url;
            String response = "";
            try {
                url = new URL(params[0]);

                Log.d("Debug", "URL je " + url);
                Log.d("Debug", "Slika " + data.get("encoded_string"));
                Log.d("Debug", "Ime " + data.get("image_name"));
                Log.d("Debug", "Username " + data.get("username"));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(data));

                writer.flush();
                writer.close();
                os.close();

                Log.d("Debug", "poslano");

                int responseCode = conn.getResponseCode();

                Log.d("Debug", "Response code je " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    response = br.readLine();
                } else {
                    response = "Error";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Debug", "Response from server: " + result);
            progressDialog.dismiss();
            if(result.equals("success")){
                Toast.makeText(addFoodActivity.this, "Your menu has been added", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

    }
}
