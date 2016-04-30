package com.example.tomipc.foodzye;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.tomipc.foodzye.adapter.DrawerAdapter;
import com.example.tomipc.foodzye.fragments.FoodFragmentFood;
import com.example.tomipc.foodzye.fragments.FoodFragmentPlace;
import com.example.tomipc.foodzye.model.DrawerItem;
import com.example.tomipc.foodzye.model.User;

import java.util.ArrayList;


public class MainActivity extends Navigation {


    UserLocalStore userLocalStore;
    User user;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerAdapter adapter;

    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //navMenuTitles=setVisibility(navMenuTitles);
        set(toolbar);

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup the Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
        tabLayout.setupWithViewPager(viewPager);
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

    @Override
    protected void onResume() {
        super.onResume();
        set(toolbar);
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

