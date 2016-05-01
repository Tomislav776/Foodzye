package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.model.User;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    UserLocalStore userLocalStore;
    TextInputEditText UsernameText;
    TextInputEditText EmailText;
    TextInputEditText PasswordText;
    Spinner RoleSpinner;
    Button SignUpButton;
    TextView LoginLink;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        UsernameText = (TextInputEditText) findViewById(R.id.input_name);
        EmailText = (TextInputEditText) findViewById(R.id.input_email);
        PasswordText = (TextInputEditText) findViewById(R.id.input_password);
        RoleSpinner = (Spinner) findViewById(R.id.RoleSpinner);
        SignUpButton = (Button) findViewById(R.id.btn_signup);
        LoginLink = (TextView) findViewById(R.id.link_login);

        userLocalStore = new UserLocalStore(this);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        SignUpButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String username = UsernameText.getText().toString();
        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();
        String StringRole = RoleSpinner.getSelectedItem().toString();

        String role;

        if(StringRole.equals("User")){
            role = "1";
        }else{
            role = "2";
        }

        final HashMap postData = new HashMap();
        postData.put("name", username);
        postData.put("email", email);
        postData.put("password", password);
        postData.put("role", role);

        PostResponseAsyncTask task = new PostResponseAsyncTask(SignupActivity.this, postData , new AsyncResponse() {
            /*@Override
            public void processFinish(String result) {
                if(result.equals("success")) {
                    Toast.makeText(SignupActivity.this, "You have successfully registered and logged in.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    onSignupSuccess(postData);
                }
                else{
                    onSignupFailed();
                    progressDialog.dismiss();
                }
            }*/
            @Override
            public void processFinish(String result) {
                if(result.equals("fail")) {
                    progressDialog.dismiss();
                    onSignupFailed();
                }
                else{
                    try {
                        JSONArray obj = new JSONArray(result);
                        JSONObject jObject = obj.getJSONObject(0);
                        System.out.println(jObject);
                        String name = jObject.getString("name");
                        String email = jObject.getString("email");
                        int role = jObject.getInt("role");
                        String user_slug = jObject.getString("slug");
                        int user_id = jObject.getInt("id");
                        User user = new User(user_id, name, user_slug, email, role);
                        logUserIn(user);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "You have successfully registered and logged in.", Toast.LENGTH_LONG).show();
                    onSignupSuccess();
                }
            }
        });

            task.execute(Database.URL + "register");

    }


    public void onSignupSuccess() {
        SignUpButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "An error happened. Please try again later.", Toast.LENGTH_LONG).show();
        SignUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = UsernameText.getText().toString();
        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            UsernameText.setError("Your name must have at least 4 characters.");
            valid = false;
        } else {
            UsernameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailText.setError("Enter a valid email address.");
            valid = false;
        } else {
            EmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 15) {
            PasswordText.setError("Your password must be between 5 and 15 alphanumeric characters.");
            valid = false;
        } else {
            PasswordText.setError(null);
        }

        return valid;
    }

    private void logUserIn(User user) {
        userLocalStore.storeUserData(user);
        userLocalStore.setUserLoggedIn(true);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}