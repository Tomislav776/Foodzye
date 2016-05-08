package com.example.tomipc.foodzye;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.FoodAdapter;
import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class addFoodActivity extends Navigation implements AdapterView.OnItemSelectedListener {

    private static final String FOOD_URL = Database.URL + "getFood";

    // Identifier for the camera and external storage permission request
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    int CameraPermission, StoragePermission;

    UserLocalStore userLocalStore;
    User user;
    HttpURLConnection connection;
    AutoCompleteTextView ACText;
    EditText AddFoodNameEditText, FoodPrice, FoodDescription;
    Button addFoodButton, CapturePictureButton, ChoosePictureButton;
    Spinner spinner;
    ProgressDialog progressDialog;
    RoundedImageView imgPreview;
    ArrayList<Food> arrayOfFood;
    private String foodJSON, foodImage, encoded_string, filePath, name, description, price, currency;
    private int food_id;
    Food chosenFood;
    private Bitmap bitmap = null;
    private File file;
    private Uri file_uri;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        set(toolbar);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            CameraPermission = 0;
            StoragePermission = 0;
        }else{
            CameraPermission = 1;
            StoragePermission = 1;
        }

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

        ACText = (AutoCompleteTextView) findViewById(R.id.acText);
        AddFoodNameEditText = (EditText) findViewById(R.id.AddFoodNameEditText);
        FoodPrice = (EditText) findViewById(R.id.FoodPrice);
        FoodDescription = (EditText) findViewById(R.id.FoodDescription);
        addFoodButton = (Button) findViewById(R.id.AddFoodButton);
        CapturePictureButton = (Button) findViewById(R.id.take_picture);
        ChoosePictureButton = (Button) findViewById(R.id.choose_picture);
        progressDialog = new ProgressDialog(addFoodActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading the menu...");
        imgPreview = (RoundedImageView) findViewById(R.id.imgPreview);
        spinner = (Spinner) findViewById(R.id.spinnerCurrency);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> currency = new ArrayList<String>();
        currency.add("HRK");
        currency.add("EUR");
        currency.add("USD");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currency);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        CapturePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionForCamera();
                getPermissionForStorage();

                if(CameraPermission == 1 && StoragePermission == 1) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file_uri = Uri.fromFile(getOutputMediaFile());
                    i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                    startActivityForResult(i, 10);
                }
            }
        });

        ChoosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionForStorage();

                if(StoragePermission == 1){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
                }
            }
        });

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = AddFoodNameEditText.getText().toString();
                price = FoodPrice.getText().toString();
                description = FoodDescription.getText().toString();
                progressDialog.show();
                new Upload_Food().execute(Database.URL + "postMenu");

                Intent intent = new Intent(addFoodActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
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
                if (chosenFood.name.equals("There is no such food. Click me if you want to add it.")) {
                    //addNewFoodButton.setVisibility(View.VISIBLE);
                    Intent i = new Intent(view.getContext(), addNewFoodActivity.class);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(addFoodActivity.this, chosenFood.name, Toast.LENGTH_LONG).show();
                    food_id = chosenFood.id;
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        currency = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        currency = "HRK";
    }

    @Override
    public void onPause(){

        super.onPause();
        if(progressDialog != null)
            progressDialog.dismiss();
    }


    // Called when the user wants to take a picture
    public void getPermissionForCamera() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    // Show our own UI to explain to the user why we need to get the Camera permission
                    // before actually requesting the permission and showing the default UI

                    showMessageOKCancel("You need to allow access to the camera if you want to take a picture.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        // Fire off an async request to actually get the permission
                                        // This will show the standard permission request dialog UI
                                        ActivityCompat.requestPermissions(addFoodActivity.this, new String[]{Manifest.permission.CAMERA},
                                                REQUEST_CAMERA);
                                    }
                            });
                }

        }else{
            CameraPermission = 1;
        }
    }

    // Called when the user wants to get a picture from storage
    public void getPermissionForStorage(){

        //checking for the storage permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show our own UI to explain to the user why we need to get the Camera permission
                    // before actually requesting the permission and showing the default UI

                    showMessageOKCancel("You need to allow access to the external storage to get the picture from your phone.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        // Fire off an async request to actually get the permission
                                        // This will show the standard permission request dialog UI
                                        ActivityCompat.requestPermissions(addFoodActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_WRITE_EXTERNAL_STORAGE);
                                }
                            });
                }
        }else{
            StoragePermission = 1;
        }

    }


    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original REQUEST_CAMERA request
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CameraPermission = 1;
                Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                CameraPermission = 0;
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


        // Make sure it's our original REQUEST_WRITE_EXTERNAL_STORAGE request
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StoragePermission = 1;
                Toast.makeText(this, "External storage permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                StoragePermission = 0;
                Toast.makeText(this, "External storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(addFoodActivity.this)
                .setMessage(message)
                .setPositiveButton("I understand", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
            CropImage.activity(file_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
            previewMedia();
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            file_uri = data.getData();
            CropImage.activity(file_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                file_uri = resultUri;
                foodImage = getFileName(file_uri);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inSampleSize = 8;
                    options.inPurgeable = true;
                    AssetFileDescriptor fileDescriptor = null;
                    try {
                        fileDescriptor = this.getContentResolver().openAssetFileDescriptor(file_uri, "r");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    finally{
                        try {
                            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                            fileDescriptor.close();
                            imgPreview.setOval(true);
                            imgPreview.setVisibility(View.VISIBLE);
                            imgPreview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                System.out.println(e.getMessage());
            }
        }
    }

    private void previewMedia() {
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger images
        //options.inSampleSize = 8;
        options.inPurgeable = true;

        filePath = file_uri.getPath();

        bitmap = BitmapFactory.decodeFile(filePath, options);

        imgPreview.setOval(true);
        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageBitmap(bitmap);
    }


    private class Upload_Food extends AsyncTask<String, Integer, String> {

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
            HashMap<String,String> data = new HashMap<>();
            data.put("food_id", Integer.toString(food_id));
            data.put("user_id", Integer.toString(user.getId()));
            data.put("user_slug", user.getSlug());
            data.put("name", name);
            data.put("price", price);
            data.put("currency", currency);
            data.put("description", description);

            if(bitmap != null){
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
                resizedBitmap.recycle();
                data.put("encoded_string", encoded_string);
                data.put("image_name", foodImage);
            }

            URL url;
            String response = "";
            try {
                url = new URL(params[0]);

                Log.d("Debug", "URL je " + url);
                Log.d("Debug", "Slika " + data.get("encoded_string"));
                Log.d("Debug", "Ime " + data.get("image_name"));
                Log.d("Debug", "user_id " + data.get("user_id"));
                Log.d("Debug", "user_slug " + data.get("user_slug"));
                Log.d("Debug", "name " + data.get("name"));
                Log.d("Debug", "price " + data.get("price"));
                Log.d("Debug", "currency " + data.get("currency"));
                Log.d("Debug", "description " + data.get("description"));


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
            //bitmap.recycle();
            progressDialog.dismiss();
            if(result.equals("success")){
                Toast.makeText(addFoodActivity.this, "Your menu has been added", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(addFoodActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
