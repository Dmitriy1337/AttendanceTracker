package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import com.ui.attracker.model.EventsList;

import java.io.File;
import java.util.Objects;

import static com.ui.attracker.LoginActivity.USERNAME_KEY;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        final Intent loginIntent = new Intent(this, LoginActivity.class);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // TODO: logout logic
        deleteUsername(sharedPreferences);
        deleteFiles(getApplicationContext());
        EventsList.discardEvents();

        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }

    private void deleteUsername(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(USERNAME_KEY);
        editor.apply();
    }

    private void deleteFiles(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        for (final File fileEntry : Objects.requireNonNull(directory.listFiles()))
            fileEntry.delete();
    }
}