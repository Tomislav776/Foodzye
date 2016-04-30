package com.example.tomipc.foodzye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private AppCompatButton ResetPasswordButton;
    private TextInputEditText EmailResetPasswordTextInputEditText;
    private Database db;
    private Context c;
    private String email;
    private static final String postResetPassword = "postResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        c = this;

        ResetPasswordButton = (AppCompatButton) findViewById(R.id.ResetPasswordButton);
        EmailResetPasswordTextInputEditText = (TextInputEditText) findViewById(R.id.input_email_reset_password);
        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailResetPasswordTextInputEditText.getText().toString();
                if (!validate()) {
                    return;
                }
                HashMap data = new HashMap();
                data.put("email", email);
                db = new Database(c);
                db.insert(data, postResetPassword);
                Intent i = new Intent(c, loginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        if (email.isEmpty()) {
            EmailResetPasswordTextInputEditText.setError("You must enter the email!");
            valid = false;
        } else {
            EmailResetPasswordTextInputEditText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Zavrsila se");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
