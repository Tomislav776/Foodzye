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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.User;

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

public class EditFoodActivity extends Navigation implements AdapterView.OnItemSelectedListener {

    private static final String postMenu = "postEditMenu";

    // Identifier for the camera and external storage permission request
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    int CameraPermission, StoragePermission;

    private Toolbar toolbar;
    UserLocalStore userLocalStore;
    User user;
    private Menu food;
    EditText AddFoodNameEditText, FoodPrice, FoodDescription;
    Button addFoodButton, CapturePictureButton, ChoosePictureButton;
    Spinner spinner;
    ProgressDialog progressDialog;
    ImageView imgPreview;
    private String foodImage, encoded_string, filePath, name, description, price, currency;
    private Bitmap bitmap = null;
    private File file;
    private Uri file_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            CameraPermission = 0;
            StoragePermission = 0;
        }else{
            CameraPermission = 1;
            StoragePermission = 1;
        }

        toolbar = (Toolbar) findViewById(R.id.EditFoodToolbar);
        set(toolbar);

        Intent i = getIntent();
        food = (Menu) i.getSerializableExtra("Menu");

        AddFoodNameEditText = (EditText) findViewById(R.id.AddFoodNameEditText2);
        FoodPrice = (EditText) findViewById(R.id.FoodPrice2);
        FoodDescription = (EditText) findViewById(R.id.FoodDescription2);
        addFoodButton = (Button) findViewById(R.id.AddFoodButton2);
        CapturePictureButton = (Button) findViewById(R.id.take_picture2);
        ChoosePictureButton = (Button) findViewById(R.id.choose_picture2);
        spinner = (Spinner) findViewById(R.id.spinnerCurrency2);
        imgPreview = (ImageView) findViewById(R.id.imgPreview2);

        AddFoodNameEditText.setText(food.getName());
        FoodPrice.setText(Double.toString(food.getPrice()));
        FoodDescription.setText(food.getDescription());

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

        if (!food.getCurrency().equals(null)) {
            if(food.getCurrency().equals("HRK")){
                spinner.setSelection(0);
            } else if(food.getCurrency().equals("EUR")){
                spinner.setSelection(1);
            } else if(food.getCurrency().equals("USD")){
                spinner.setSelection(2);
            }
        }

        if(food.getImage() != null && !food.getImage().equals(""))
        {
            imgPreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(Database.URL + food.getImage()).into(imgPreview);
        }

        progressDialog = new ProgressDialog(EditFoodActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Editing the menu...");

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
                new Upload_Food().execute(Database.URL + postMenu);
            }
        });


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
                                ActivityCompat.requestPermissions(EditFoodActivity.this, new String[]{Manifest.permission.CAMERA},
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
                                ActivityCompat.requestPermissions(EditFoodActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
        new AlertDialog.Builder(EditFoodActivity.this)
                .setMessage(message)
                .setPositiveButton("I understand", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        currency = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        currency = "HRK";
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
            HashMap<String,String> data = new HashMap<>();
            data.put("menu_id", Integer.toString(food.getId()));
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
                Log.d("Debug", "Menu id " + data.get("menu_id"));
                Log.d("Debug", "Slika " + data.get("encoded_string"));
                Log.d("Debug", "Ime " + data.get("image_name"));
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
                Toast.makeText(EditFoodActivity.this, "Your menu has been edited", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

    }

}
