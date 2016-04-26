package com.example.tomipc.foodzye;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.fragments.ProfileFragmentMenuTab;
import com.example.tomipc.foodzye.fragments.ProfileFragmentProfileTab;
import com.example.tomipc.foodzye.model.Place;
import com.example.tomipc.foodzye.model.User;

public class ProfileActivity extends AppCompatActivity {
    protected DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    UserLocalStore userLocalStore;
    User user;
    ImageView ProfileImageView;
    TextView UserNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userLocalStore = new UserLocalStore(this);

        Place place = (Place) getIntent().getSerializableExtra("Place");

        Integer user_id = null;

        try {
            user_id = Integer.valueOf(place.getId());
        }
        catch (Exception e) {
            System.out.println("User id wasn't passed to this activity.");
        }

        if (user_id != null) {
            user = new User(place.getId(), place.getRole() ,place.getName(), place.getEmail(), place.getLocation(), place.getPhone(), place.getPicture(), place.getWork_time(), place.getRate());
        }else{
            user = userLocalStore.getLoggedInUser();
        }

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Setup the Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);

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

                if (menuItem.getItemId() == R.id.nav_item_edit_profile) {
                    Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
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
                    Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(i);
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);


        mDrawerToggle.syncState();

        ProfileImageView = (ImageView) findViewById(R.id.ProfilePictureImageView);
        UserNameTextView = (TextView) findViewById(R.id.UserNameTextView);

        if(user.getPicture() != null && !user.getPicture().equals("")){
            ProfileImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load("http://164.132.228.255/"+user.getPicture())
                    .into(ProfileImageView);
        }

        UserNameTextView.setText(user.getUsername());
    }

    public int getUserId(){
        return user.getId();
    }

    public int getUserRole(){
        return user.getRole();
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int pos) {
            if(user.getRole() == 1){
                switch(pos) {
                    case 0: return ProfileFragmentProfileTab.newInstance();
                    default: return ProfileFragmentProfileTab.newInstance();
                }
            }else{
                switch(pos) {
                    case 0: return ProfileFragmentProfileTab.newInstance();
                    case 1: return ProfileFragmentMenuTab.newInstance();
                    default: return ProfileFragmentProfileTab.newInstance();
                }
            }
        }

        @Override
        public int getCount() {
            if(user.getRole() == 1) return 1;
            else return 2;
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
