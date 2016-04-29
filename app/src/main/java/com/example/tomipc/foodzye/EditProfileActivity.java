package com.example.tomipc.foodzye;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class EditProfileActivity extends AppCompatActivity {

    private static String getRoute = "getUser";
    private static String postRoute = "postUserUpdate";

    protected DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    EditText EmailEditText, LocationEditText, PhoneEditText, WorkTimeEditText;
    Button EditProfileButton, TakePictureButton, ChoosePictureButton;
    ImageView imgPreview;
    String email, location, phone, workTime, foodImage, filePath;
    String encoded_picture_string = null;
    UserLocalStore userLocalStore;
    User user;
    Database db;
    HashMap<String, String> data;
    private File file;
    private Uri file_uri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout3);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff3) ;

        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                if (menuItem.getItemId() == R.id.nav_item_home) {
                    Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_login) {
                    Intent i = new Intent(EditProfileActivity.this, loginActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_food) {
                    Intent i = new Intent(EditProfileActivity.this, addFoodActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLoggedIn(false);
                    Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(i);
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar3);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.app_name, R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().hide();

        mDrawerToggle.syncState();

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();
        getUserData();

        EmailEditText = (EditText) findViewById(R.id.EmailEditText);
        LocationEditText = (EditText) findViewById(R.id.LocationEditText);
        PhoneEditText = (EditText) findViewById(R.id.PhoneEditText);
        WorkTimeEditText = (EditText) findViewById(R.id.WorkHoursEditText);
        TakePictureButton = (Button) findViewById(R.id.TakePictureButton);
        ChoosePictureButton = (Button) findViewById(R.id.ChoosePictureButton);
        EditProfileButton = (Button) findViewById(R.id.EditProfileButton);
        imgPreview = (ImageView) findViewById(R.id.imageView2);

        if(user.getEmail() != null) EmailEditText.setText(user.getEmail());
        if(user.getLocation() != null) LocationEditText.setText(user.getLocation());
        if(user.getPhone() != null) PhoneEditText.setText(user.getPhone());
        if(user.getWork_time() != null) WorkTimeEditText.setText(user.getWork_time());
        if(user.getPicture() != null){
            imgPreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load("http://164.132.228.255/"+user.getPicture())
                    .into(imgPreview);
        }

        TakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file_uri = Uri.fromFile(getOutputMediaFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, 10);
            }
        });

        ChoosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });


        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailEditText.getText().toString();
                location = LocationEditText.getText().toString();
                phone = PhoneEditText.getText().toString();
                workTime = WorkTimeEditText.getText().toString();
                data = new HashMap<String, String>();
                data.put("email", email);
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
                System.out.println(data.get("encoded_string"));
                db.insert(data, postRoute);
            }
        });
    }

    private void getUserData() {
        db = new Database(EditProfileActivity.this);
        User user2 = db.getUserData(getRoute, Integer.toString(user.id));
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalStore.userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("email", user2.getEmail());
        user.setEmail(user2.getEmail());
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
