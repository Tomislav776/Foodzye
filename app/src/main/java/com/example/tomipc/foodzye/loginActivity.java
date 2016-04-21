package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.User;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class loginActivity extends AppCompatActivity  {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String LOGIN_URL = "http://164.132.228.255/login";

    UserLocalStore userLocalStore;

    @Bind(R.id.input_email) TextInputEditText _emailText;
    @Bind(R.id.input_password) TextInputEditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        userLocalStore = new UserLocalStore(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(loginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        HashMap postData = new HashMap();
        postData.put("email", email);
        postData.put("password", password);

        PostResponseAsyncTask task = new PostResponseAsyncTask(loginActivity.this, postData , new AsyncResponse() {
            @Override
            public void processFinish(String result) {
                if(result.equals("fail")) {
                    progressDialog.dismiss();
                    onLoginFailed();
                }
                else{
                    try {
                        JSONArray obj = new JSONArray(result);
                        JSONObject jObject = obj.getJSONObject(0);
                        String name = jObject.getString("name");
                        String email = jObject.getString("email");
                        String role = jObject.getString("role");
                        String user_slug = jObject.getString("slug");
                        int user_id = jObject.getInt("id");
                        User user = new User(user_id, name, user_slug, email, role);
                        logUserIn(user);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(loginActivity.this, "You have successfully logged in.", Toast.LENGTH_LONG).show();
                    onLoginSuccess();
                }
            }
        });

        task.execute(LOGIN_URL);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 15) {
            _passwordText.setError("Your password must be between 5 and 15 alphanumeric characters.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void logUserIn(User user) {
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);
    }

}