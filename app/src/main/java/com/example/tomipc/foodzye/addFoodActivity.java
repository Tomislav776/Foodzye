package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.FoodAdapter;
import com.example.tomipc.foodzye.model.Food;
import com.example.tomipc.foodzye.model.User;

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

    private static final String FOOD_URL = "http://164.132.228.255/getFood";

    UserLocalStore userLocalStore;
    User user;
    HttpURLConnection connection;
    AutoCompleteTextView ACText;
    EditText AddFoodNameEditText, FoodPrice, FoodDescription;
    Button addFoodButton, CapturePictureButton, ChoosePictureButton;
    Spinner spinner;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    ProgressDialog progressDialog;
    ImageView imgPreview;
    ArrayList<Food> arrayOfFood;
    private String foodJSON, foodImage, encoded_string, filePath, name, description, price, currency;
    private int food_id;
    Food chosenFood;
    private Bitmap bitmap;
    private File file;
    private Uri file_uri;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        set( toolbar);

        mFragmentManager = getSupportFragmentManager();

        /**
         * Setup click events on the Navigation View Items.
         */


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
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
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
                name = AddFoodNameEditText.getText().toString();
                price = FoodPrice.getText().toString();
                description = FoodDescription.getText().toString();
                progressDialog.show();
                new Upload_Food().execute("http://164.132.228.255/postMenu");
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

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            file_uri = data.getData();
            foodImage = getFileName(file_uri);
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
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
                        ExifInterface exif = new ExifInterface(getAbsolutePath(file_uri));
                        int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (exifRotation) {
                            case ExifInterface.ORIENTATION_ROTATE_90: {
                                bitmap = rotateImage(bitmap, 90.0f);
                                break;
                            }
                            case ExifInterface.ORIENTATION_ROTATE_180: {
                                bitmap = rotateImage(bitmap, 180.0f);
                                break;
                            }
                            case ExifInterface.ORIENTATION_ROTATE_270: {
                                bitmap = rotateImage(bitmap, 270.0f);
                                break;
                            }
                        }
                        imgPreview.setVisibility(View.VISIBLE);
                        imgPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
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
        options.inPurgeable = true;

        filePath = file_uri.getPath();

        bitmap = BitmapFactory.decodeFile(filePath, options);

        try{
            ExifInterface exif = new ExifInterface(Uri.fromFile(getOutputMediaFile()).getPath());
            int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (exifRotation) {
                case ExifInterface.ORIENTATION_UNDEFINED: {
                    bitmap = rotateImage(bitmap, 90.0f);
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    bitmap = rotateImage(bitmap, 90.0f);
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    bitmap = rotateImage(bitmap, 180.0f);
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    bitmap = rotateImage(bitmap, 270.0f);
                    break;
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        imgPreview.setImageBitmap(bitmap);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        //set image rotation value to an angle in degrees in matrix
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public String getAbsolutePath(Uri uri) {
        if(Build.VERSION.SDK_INT >= 19){
            String id = uri.getLastPathSegment().split(":")[1];
            final String[] imageColumns = {MediaStore.Images.Media.DATA };
            final String imageOrderBy = null;
            Uri tempUri = getUri();
            Cursor imageCursor = getContentResolver().query(tempUri, imageColumns,
                    MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);
            if (imageCursor.moveToFirst()) {
                return imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }else{
                return null;
            }
        }else{
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }

    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
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

            HashMap<String,String> data = new HashMap<>();
            data.put("encoded_string", encoded_string);
            data.put("food_id", Integer.toString(food_id));
            data.put("image_name", foodImage);
            data.put("user_id", Integer.toString(user.getId()));
            data.put("user_slug", user.getSlug());
            data.put("name", name);
            data.put("price", price);
            data.put("currency", currency);
            data.put("description", description);

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
}
