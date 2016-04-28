package com.example.tomipc.foodzye;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.fragments.FoodFragmentTab;
import com.example.tomipc.foodzye.model.User;


public class MainActivity extends AppCompatActivity {


    UserLocalStore userLocalStore;
    User user;

    //Navigacija
    protected DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    MenuItem item;

    private TextView usernameNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();

        checkForPermissions();


        /**
         * Setup click events on the Navigation View Items.
         */

        /**
         * Set name of logged user
         */
        setNavigationName();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_edit_profile) {
                    Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_login) {
                    Intent i = new Intent(MainActivity.this, loginActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_food) {
                    Intent i = new Intent(MainActivity.this, addFoodActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_logout) {
                    userLocalStore.clearUserData();
                    userLocalStore.setUserLoggedIn(false);
                    setNavigationName();

                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().hide();

        mDrawerToggle.syncState();

    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void checkForPermissions() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                showMessageOKCancel("You need to allow access to the Camera",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.CAMERA},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "CAMERA Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private boolean authenticate() {
        if (userLocalStore.getLoggedInUser() == null) {

            return false;
        }

        return true;
    }

    private void displayUserDetails() {
        user = userLocalStore.getLoggedInUser();
    }


    @Override
    protected void onStart() {
        super.onStart();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new FoodFragmentTab()).commit();

        if (authenticate()) {
            displayUserDetails();
        }
    }

    public void setNavigationName() {
        user = userLocalStore.getLoggedInUser();

        View header = mNavigationView.getHeaderView(0);

        usernameNav = (TextView) header.findViewById(R.id.navigation_name);

        if (user == null) {
            usernameNav.setText("Guest");
            mNavigationView.getMenu().findItem(R.id.nav_item_login).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(false);
        } else {
            usernameNav.setText(user.getUsername());
            mNavigationView.getMenu().findItem(R.id.nav_item_login).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(true);

            if (user.getRole() == 1){
                mNavigationView.getMenu().findItem(R.id.nav_item_food).setVisible(false);
            }
            else
            {
                mNavigationView.getMenu().findItem(R.id.nav_item_food).setVisible(true);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavigationName();
    }


}
