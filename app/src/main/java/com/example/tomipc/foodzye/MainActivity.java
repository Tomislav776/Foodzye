package com.example.tomipc.foodzye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.DrawerAdapter;
import com.example.tomipc.foodzye.fragments.FoodFragmentFood;
import com.example.tomipc.foodzye.fragments.FoodFragmentPlace;
import com.example.tomipc.foodzye.model.DrawerItem;
import com.example.tomipc.foodzye.model.User;

import java.util.ArrayList;


public class MainActivity extends Navigation {


    UserLocalStore userLocalStore;
    User user;
    // nav drawer title
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerAdapter adapter;

    private android.support.v7.widget.Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        System.out.println("naslov"+navMenuTitles[0]);
        set(navMenuTitles, navMenuIcons, toolbar);

        checkForPermissions();

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup the Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
        tabLayout.setupWithViewPager(viewPager);
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

        if (authenticate()) {
            displayUserDetails();
        }
    }
/*
    public void setNavigationName() {
        user = userLocalStore.getLoggedInUser();

        View header = mNavigationView.getHeaderView(0);

        usernameNav = (TextView) header.findViewById(R.id.navigation_name);
        mNavigationView.getMenu().findItem(R.id.nav_item_home).setVisible(false);

        if (user == null) {
            usernameNav.setText("Guest");
            mNavigationView.getMenu().findItem(R.id.nav_item_login).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_item_food).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_item_profile).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_item_edit_profile).setVisible(false);
        } else {
            usernameNav.setText(user.getUsername());
            mNavigationView.getMenu().findItem(R.id.nav_item_login).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_item_profile).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_item_edit_profile).setVisible(true);

            if (user.getRole() == 1){
                mNavigationView.getMenu().findItem(R.id.nav_item_food).setVisible(false);
            }
            else
            {
                mNavigationView.getMenu().findItem(R.id.nav_item_food).setVisible(true);
            }
        }

    }*/

    @Override
    protected void onResume() {
        super.onResume();
      //  setNavigationName();
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int pos) {

                switch(pos) {
                    case 0: return FoodFragmentFood.newInstance();
                    case 1: return FoodFragmentPlace.newInstance();
                    default: return FoodFragmentFood.newInstance();

            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Food";
                case 1 :
                    return "Place";
            }
            return null;
        }

    }



}

