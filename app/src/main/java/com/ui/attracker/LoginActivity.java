package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.ui.attracker.model.User;

public class LoginActivity extends AppCompatActivity {

    private static final String INVALID_USERNAME_MESSAGE = "Enter a valid username";
    public static final String USERNAME_KEY = "com.ui.attracker.USERNAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Intent menuIntent = new Intent(this, MenuActivity.class);
        final EditText usernameEditText = findViewById(R.id.editTextUsername);

        APIRequests.init();


        if (getUsername(getApplicationContext()) != null) {
            APIRequests.login(getUsername(getApplicationContext()), getApplicationContext());
            startActivity(menuIntent);
            finish();
        }

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkUsernameValidity(usernameEditText.getText().toString())) {
                    String username = usernameEditText.getText().toString();
                    saveUsername(username, getApplicationContext());

                    APIRequests.login(username, getApplicationContext());

                    startActivity(menuIntent);
                    finish();
                }
                else
                    Snackbar.make(view, INVALID_USERNAME_MESSAGE, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkUsernameValidity(String username) {
        return username.matches("^[a-z]+.[a-z]+$");
    }

    private void saveUsername(String username, Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }
}