package com.example.tomipc.foodzye;
import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomipc.foodzye.adapter.DrawerAdapter;
import com.example.tomipc.foodzye.model.DrawerItem;
import com.example.tomipc.foodzye.model.User;

public class Navigation extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    protected RelativeLayout _completeLayout, _activityLayout;
    private TextView mTitle;
    private com.makeramen.roundedimageview.RoundedImageView mTitleImage;


    public  String[] navMenuTitles ;
    public TypedArray navMenuIcons ;
    android.support.v7.widget.Toolbar toolbar;


String pressed;
    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerAdapter adapter;

    UserLocalStore userLocalStore;
    User user;
    private LinearLayout DrawerLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        // if (savedInstanceState == null) {
        // // on first time display view for first nav item
        // // displayView(0);
        // }
    }

    public void set(android.support.v7.widget.Toolbar toolbar) {
        this.toolbar=toolbar;
        setNavigationTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        DrawerLinear = (LinearLayout) findViewById(R.id.drawerLinearLayout);

        navDrawerItems = new ArrayList<DrawerItem>();
        // adding nav drawer items
        setVisibility();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new DrawerAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 //       getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setIcon(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
               // getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
              //  getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if(getSupportActionBar() != null)
        getSupportActionBar().hide();
        mDrawerToggle.syncState();

//        System.out.println("Child: " + mDrawerList.getAdapter().getItem(4));

        //setVisibility();
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            DrawerItem x = (DrawerItem) mDrawerList.getItemAtPosition(position);
            pressed=x.getTitle();

            displayView(position);
        }
    }

    public void setNavigationTitle() {
        mTitle = (TextView) findViewById(R.id.navigationTextTitle);
        mTitleImage = (com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.navigationProfileImage);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();
        if (user == null) {
            mTitle.setText("Guest");
        }
        else{
            mTitle.setText(user.getUsername());
        }

        if (user == null){
            mTitleImage.setImageResource(R.drawable.user_def);
        }
        else
        {
            if (user.getPicture().equals(""))
                mTitleImage.setImageResource(R.drawable.user_def);
                else
                Glide.with(this).load(Database.URL+user.getPicture()).into(mTitleImage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
  //      boolean drawerOpen = mDrawerLayout.isDrawerOpen(DrawerLinear);
  //     menu.findItem(R.id.).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
System.out.println("Pressed: " + pressed);
        switch (pressed) {
            case "Home":
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;
            case "Profile":
                Intent intent1 = new Intent(this, ProfileActivity.class);
                startActivity(intent1);

                break;
            case "Edit Profile":
                Intent intent2 = new Intent(this, EditProfileActivity.class);
                startActivity(intent2);

                break;
            case "Add Food":
                Intent intent3 = new Intent(this, addFoodActivity.class);
                startActivity(intent3);

                break;
            case "Login":
                Intent intent4 = new Intent(this, loginActivity.class);
                startActivity(intent4);

                break;
            case "Logout":
                //logout
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                set(toolbar);
                onResume();
                break;
            default:
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(DrawerLinear);
    }


    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void setVisibility() {

        user = userLocalStore.getLoggedInUser();


        if (user == null) {
            for (int i = 0; i < navMenuTitles.length; i++) {
                if(navMenuTitles[i].equals("Login") || navMenuTitles[i].equals("Home"))
                if (navMenuIcons == null) {
                    navDrawerItems.add(new DrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                } else {
                    navDrawerItems.add(new DrawerItem(navMenuTitles[i]));
                }
            }
        }
        else
        {
            if (user.getRole() == 1){
                for (int i = 0; i < navMenuTitles.length; i++) {
                    if(navMenuTitles[i].equals("Logout") || navMenuTitles[i].equals("Profile") || navMenuTitles[i].equals("Edit Profile") || navMenuTitles[i].equals("Home"))
                        if (navMenuIcons == null) {
                            navDrawerItems.add(new DrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                        } else {
                            navDrawerItems.add(new DrawerItem(navMenuTitles[i]));
                        }
                }
            }
            else
            {
                for (int i = 0; i < navMenuTitles.length; i++) {
                    if(navMenuTitles[i].equals("Logout") || navMenuTitles[i].equals("Profile") || navMenuTitles[i].equals("Edit Profile") || navMenuTitles[i].equals("Add Food") || navMenuTitles[i].equals("Home"))
                        if (navMenuIcons == null) {
                            navDrawerItems.add(new DrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
                        } else {
                            navDrawerItems.add(new DrawerItem(navMenuTitles[i]));
                        }
                }
            }
        }

    }
}