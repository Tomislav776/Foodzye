package com.example.tomipc.foodzye;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.tomipc.foodzye.fragments.FoodFragmentTab;
import com.example.tomipc.foodzye.fragments.ProfileFragmentMenuTab;
import com.example.tomipc.foodzye.fragments.ProfileFragmentProfileTab;
import com.example.tomipc.foodzye.model.User;

public class ProfileActivity extends AppCompatActivity {
    protected DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    UserLocalStore userLocalStore;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout2);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff2);*/

        // Set a toolbar which will replace the action bar.
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar2);
        setSupportActionBar(toolbar);*/

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup the Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);
        // By using this method the tabs will be populated according to viewPager's count and
        // with the name from the pagerAdapter getPageTitle()
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
        tabLayout.setupWithViewPager(viewPager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout2);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff2);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                if (menuItem.getItemId() == R.id.nav_item_home) {
                    Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(i);
                }

                if (menuItem.getItemId() == R.id.nav_item_login) {
                    Intent i = new Intent(ProfileActivity.this, loginActivity.class);
                    startActivity(i);

                }

                if (menuItem.getItemId() == R.id.nav_item_food) {
                    Intent i = new Intent(ProfileActivity.this, addFoodActivity.class);
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

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        mDrawerToggle.syncState();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return ProfileFragmentProfileTab.newInstance();
                case 1: return ProfileFragmentMenuTab.newInstance();
                default: return ProfileFragmentProfileTab.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Profile";
                case 1 :
                    return "Menu";
            }
            return null;
        }

    }

}
