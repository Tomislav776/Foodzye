package com.example.tomipc.foodzye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity extends Navigation {

    private static String getRoute = "getUser";
    private static String postRoute = "postUserUpdate";

    // Identifier for the camera and external storage permission request
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    int CameraPermission, StoragePermission;

    EditText EmailEditText, DescriptionEditText , LocationEditText, PhoneEditText, WorkTimeEditText;
    Button EditProfileButton, TakePictureButton, ChoosePictureButton;
    ImageView imgPreview;
    String email, description, location, phone, workTime, foodImage, filePath;
    String encoded_picture_string = null;
    UserLocalStore userLocalStore;
    User user;
    Database db;
    HashMap<String, String> data;
    private File file;
    private Uri file_uri;
    private Bitmap bitmap;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        set(toolbar);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();
        getUserData();

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            CameraPermission = 0;
            StoragePermission = 0;
        }else{
            CameraPermission = 1;
            StoragePermission = 1;
        }

        EmailEditText = (EditText) findViewById(R.id.EmailEditText);
        DescriptionEditText = (EditText) findViewById(R.id.DescriptionEditText);
        LocationEditText = (EditText) findViewById(R.id.LocationEditText);
        PhoneEditText = (EditText) findViewById(R.id.PhoneEditText);
        WorkTimeEditText = (EditText) findViewById(R.id.WorkHoursEditText);
        TakePictureButton = (Button) findViewById(R.id.TakePictureButton);
        ChoosePictureButton = (Button) findViewById(R.id.ChoosePictureButton);
        EditProfileButton = (Button) findViewById(R.id.EditProfileButton);
        imgPreview = (ImageView) findViewById(R.id.imageView2);

        if(user.getRole() == 1){
            WorkTimeEditText.setVisibility(View.GONE);
        }

        if(user.getEmail() != null) EmailEditText.setText(user.getEmail());
        if(user.getDescription() != null) DescriptionEditText.setText(user.getDescription());
        if(user.getLocation() != null) LocationEditText.setText(user.getLocation());
        if(user.getPhone() != null) PhoneEditText.setText(user.getPhone());
        if(user.getWork_time() != null) WorkTimeEditText.setText(user.getWork_time());
        if(user.getPicture() != null){
            imgPreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Database.URL + user.getPicture())
                    .into(imgPreview);
        }

        TakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            public void onClick(View v) {
                getPermissionForStorage();

                if(StoragePermission == 1){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
                }
            }
        });


        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailEditText.getText().toString();
                description = DescriptionEditText.getText().toString();
                location = LocationEditText.getText().toString();
                phone = PhoneEditText.getText().toString();
                workTime = WorkTimeEditText.getText().toString();
                data = new HashMap<String, String>();
                data.put("email", email);
                data.put("description", description);
                data.put("slug", user.getSlug());
                data.put("location", location);
                data.put("phone", phone);
                data.put("work_time", workTime);
                data.put("user_id", Integer.toString(user.id));
                if(encoded_picture_string != null)
                {
                    data.put("encoded_string", encoded_picture_string);
                    data.put("image_name", foodImage);
                }
                db.insert(data, postRoute);
                setUserData();
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
                                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA},
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
                                ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
        new AlertDialog.Builder(EditProfileActivity.this)
                .setMessage(message)
                .setPositiveButton("I understand", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void getUserData() {
        db = new Database(EditProfileActivity.this);
        User user2 = db.getUserData(getRoute, Integer.toString(user.getId()));
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalStore.userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("email", user2.getEmail());
        user.setEmail(user2.getEmail());
        userLocalDatabaseEditor.putString("description", user2.getDescription());
        user.setDescription(user2.getDescription());
        userLocalDatabaseEditor.putString("location", user2.getLocation());
        user.setLocation(user2.getLocation());
        userLocalDatabaseEditor.putString("phone", user2.getPhone());
        user.setPhone(user2.getPhone());
        userLocalDatabaseEditor.putString("work_time", user2.getWork_time());
        user.setWork_time(user2.getWork_time());
        userLocalDatabaseEditor.putString("user_picture", user2.getPicture());
        user.setPicture(user2.getPicture());
        userLocalDatabaseEditor.commit();
    }

    private void setUserData() {
        db = new Database(EditProfileActivity.this);
        User user2 = db.getUserData(getRoute, Integer.toString(user.getId()));
        System.out.println("Opis " + user2.getDescription());
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalStore.userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("email", user2.getEmail());
        user.setEmail(user2.getEmail());
        userLocalDatabaseEditor.putString("description", user2.getDescription());
        user.setDescription(user2.getDescription());
        userLocalDatabaseEditor.putString("location", user2.getLocation());
        user.setLocation(user2.getLocation());
        userLocalDatabaseEditor.putString("phone", user2.getPhone());
        user.setPhone(user2.getPhone());
        userLocalDatabaseEditor.putString("work_time", user2.getWork_time());
        user.setWork_time(user2.getWork_time());
        userLocalDatabaseEditor.putString("user_picture", user2.getPicture());
        user.setPicture(user2.getPicture());
        userLocalDatabaseEditor.commit();
    }

    private void previewMedia() {
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

        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageBitmap(bitmap);
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
                encoded_picture_string = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                resizedBitmap.recycle();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getOutputMediaFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        foodImage = "IMG_" + timeStamp + ".jpg";

        // External sdcard location - create a media file name
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + foodImage);

        return file;
    }
}
