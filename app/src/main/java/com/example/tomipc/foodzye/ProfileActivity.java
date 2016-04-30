package com.example.tomipc.foodzye;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.fragments.ProfileFragmentMenuTab;
import com.example.tomipc.foodzye.fragments.ProfileFragmentProfileTab;
import com.example.tomipc.foodzye.model.Place;
import com.example.tomipc.foodzye.model.User;

public class ProfileActivity extends Navigation {
    Toolbar toolbar;
    UserLocalStore userLocalStore;
    User user;
    ImageView ProfileImageView;
    TextView UserNameTextView, EmailTextView, LocationtextView, DescriptionTextView;
    AppCompatRatingBar rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        Place place = (Place) getIntent().getSerializableExtra("Place");

        Integer user_id = null;

        try {
            user_id = Integer.valueOf(place.getId());
        }
        catch (Exception e) {
            System.out.println("User id wasn't passed to this activity.");
        }

        //if conditions to render the proper profile layout depending on the role of the user whose profile you are opening
        if (user_id != null) {
            user = new User(place.getId(),place.getName(), place.getSlug() , place.getEmail(), place.getRole() , place.getLocation(), place.getPhone(), place.getWork_time(), place.getPicture(),place.getDescription(),(float) place.getRate());
            setContentView(R.layout.activity_profile);
            // Setup the viewPager
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);

            // Setup the Tabs
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);

            // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
            tabLayout.setupWithViewPager(viewPager);

            toolbar = (Toolbar) findViewById(R.id.toolbar2);
            ProfileImageView = (ImageView) findViewById(R.id.ProfilePictureImageView);
            rating = (AppCompatRatingBar) findViewById(R.id.FoodServiceProviderRatingBar);
            rating.setRating((float)user.getRate());
            UserNameTextView = (TextView) findViewById(R.id.UserNameTextView);
        }else if(user.getRole() == 2){
            setContentView(R.layout.activity_profile);
            // Setup the viewPager
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager2);
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);

            // Setup the Tabs
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);

            // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
            tabLayout.setupWithViewPager(viewPager);

            toolbar = (Toolbar) findViewById(R.id.toolbar2);
            ProfileImageView = (ImageView) findViewById(R.id.ProfilePictureImageView);
            rating = (AppCompatRatingBar) findViewById(R.id.FoodServiceProviderRatingBar);
            rating.setRating((float)user.getRate());
            UserNameTextView = (TextView) findViewById(R.id.UserNameTextView);
        }
        else{
            setContentView(R.layout.activity_profile_user);

            toolbar = (Toolbar) findViewById(R.id.toolbarUserProfile);
            ProfileImageView = (ImageView) findViewById(R.id.ProfilePictureImageViewUserProfile);
            UserNameTextView = (TextView) findViewById(R.id.UserNameTextViewUserProfile);
            EmailTextView = (TextView) findViewById(R.id.textViewEmailShow);
            EmailTextView.setText(user.getEmail());
            LocationtextView = (TextView) findViewById(R.id.textViewLocationShow);
            LocationtextView.setText(user.getLocation());
            DescriptionTextView = (TextView) findViewById(R.id.textDescriptionShow);
            DescriptionTextView.setText(user.getDescription());
        }

        if(user.getPicture() != null && !user.getPicture().equals("")){
            ProfileImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Database.URL + user.getPicture())
                    .into(ProfileImageView);
        }

        UserNameTextView.setText(user.getUsername());

        set(toolbar);
    }

    public int getUserId(){
        return user.getId();
    }

    public int getLoggedInUserId() {
        if (userLocalStore.getLoggedInUser() == null) {
            return 0;
        }
        return userLocalStore.getLoggedInUser().getId();
    }

    public User getUser() { return user; }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

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
