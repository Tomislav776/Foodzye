package com.example.tomipc.foodzye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.example.tomipc.foodzye.adapter.DrawerAdapter;
import com.example.tomipc.foodzye.fragments.FoodFragmentFood;
import com.example.tomipc.foodzye.fragments.FoodFragmentPlace;
import com.example.tomipc.foodzye.model.DrawerItem;
import com.example.tomipc.foodzye.model.Place;
import com.example.tomipc.foodzye.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Navigation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    UserLocalStore userLocalStore;
    User user;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<DrawerItem> navDrawerItems;
    private DrawerAdapter adapter;

    private Toolbar toolbar;

    //Distance
    public double latitude, longitude;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private Database baza;
    private List<Place> placeList = new ArrayList<>();

    public static HashMap<Integer, String> hashMap  = new HashMap<Integer, String>();
    public static boolean locationOnBool = false;
    private static final int REQUEST_LOCATION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            setMainScreen();
        } else {
            // permission has been granted, continue as usual
            if (!((LocationManager) this.getSystemService(this.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder
                        .setMessage(
                                "GPS is disabled in your device, distance and maps won't be able to show. Would you like to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Go to Settings Page To Enable GPS",

                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        MainActivity.this.startActivity(callGPSSettingIntent);

                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        System.exit(1);
                                    }
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        setMainScreen();
                  }
              });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            }else{
                locationOnBool = true;
                mGoogleApiClient = new GoogleApiClient.Builder( this )
                        .addConnectionCallbacks( this )
                        .addOnConnectionFailedListener(this)
                        .addApi( LocationServices.API )
                        .build();
                mGoogleApiClient.connect();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                mGoogleApiClient = new GoogleApiClient.Builder( this )
                        .addConnectionCallbacks( this )
                        .addOnConnectionFailedListener(this)
                        .addApi( LocationServices.API )
                        .build();

                mGoogleApiClient.connect();

            } else {
                // Permission was denied or request was cancelled
            }
        }
    }


    private LatLng convertAdressToLocation (String location){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName( location, 1);
            if(addresses.size() > 0) {
                latitude= addresses.get(0).getLatitude();
                longitude= addresses.get(0).getLongitude();
                System.out.println("Lokacija:"+latitude+" "+longitude);
                LatLng latLng = new LatLng(latitude, longitude);
                return latLng;
            }

        } catch (Exception e) {
            System.out.print(e.getMessage());

        }
        return null;
    }


    private void setHashMap(){
        double dist;
        String distS="";
        baza = new Database(this);
        placeList = baza.readPlace("getPlace");

        for(Place value: placeList) {
            if (!(value.getLocation().equals("null")) && !(value.getLocation().equals(""))){
                dist = SphericalUtil.computeDistanceBetween(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), convertAdressToLocation(value.getLocation()));
                if( dist < 1000 ) {
                    distS = String.format( "%4.2f%s", dist, "m" );
                } else {
                    distS = String.format("%4.3f%s", dist/1000, "km");
                }
                hashMap.put(value.getId(), distS);

            }else{
                    distS="0m";
                hashMap.put(value.getId(), distS);
            }
        }
        System.out.println("LokacijaM:" + hashMap.get(16));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (authenticate()) {
            displayUserDetails();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            setHashMap();
        }
        setMainScreen();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Create a default location if the Google API Client fails. Placing location at Googleplex
        mCurrentLocation = new Location( "" );
        mCurrentLocation.setLatitude( 37.422535 );
        mCurrentLocation.setLongitude(-122.084804);

    }


    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }


    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
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

    private void setMainScreen(){

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

    @Override
    protected void onStart() {

        super.onStart();

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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destroyed");
    }
}

