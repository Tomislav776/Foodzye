package com.example.tomipc.foodzye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    UserLocalStore userLocalStore;
    Button loginButton;
    Button logoutButton;
    EditText ETusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.LoginButton);
        logoutButton = (Button) findViewById(R.id.LogoutButton);
        ETusername = (EditText) findViewById(R.id.username);
        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            displayUserDetails();
        }
    }

    private boolean authenticate() {
        if (userLocalStore.getLoggedInUser() == null) {
            // do something if you want
            return false;
        }
        return true;
    }

    private void displayUserDetails() {
        User user = userLocalStore.getLoggedInUser();
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
        ETusername.setVisibility(View.VISIBLE);
        ETusername.setText(user.username + " (" + user.role + ")");
    }

    public void onLoginButtonClick(View v){
        Intent i = new Intent(this, loginActivity.class);
        startActivity(i);
    }

    public void onLogoutButtonClick(View v){
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
