package com.example.tomipc.foodzye;

import android.app.ProgressDialog;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String REGISTER_URL = "http://164.132.228.255/register";

    UserLocalStore userLocalStore;


    @Bind(R.id.input_name) TextInputEditText _nameText;
    @Bind(R.id.input_email) TextInputEditText _emailText;
    @Bind(R.id.input_password) TextInputEditText _passwordText;
    @Bind(R.id.RoleSpinner) Spinner RoleSpinner;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        userLocalStore = new UserLocalStore(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
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

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String username = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
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
                System.out.println(result);
                if(result.equals("fail")) {
                    progressDialog.dismiss();
                    onSignupFailed();
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
                    Toast.makeText(SignupActivity.this, "You have successfully registered and logged in.", Toast.LENGTH_LONG).show();
                    onSignupSuccess();
                }
            }
        });

        task.execute(REGISTER_URL);

    }


    public void onSignupSuccess() {
        /*String name = (String)postData.get("name");
        String email = (String)postData.get("email");
        String role = (String)postData.get("role");
        User user = new User(name, email, role);
        logUserIn(user);*/
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "An error happened. Please try again later.", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("Your name must have at least 4 characters.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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