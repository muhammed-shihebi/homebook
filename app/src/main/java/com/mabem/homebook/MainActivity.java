package com.mabem.homebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mabem.homebook.Database.Database;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_BEFORE_SIGN_OUT = 1000; // 1 Second
    private static final String MAIN_ACTIVITY_TAG = "MainActivity";
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // If the user didn't check remember me, he/she will be logged out.

        database = new Database(getApplication());
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.remember_me_default_value);
        boolean rememberMe = sharedPref.getBoolean(getString(R.string.saved_remember_me_preference), defaultValue);
        if (!rememberMe) {
            Log.i(MAIN_ACTIVITY_TAG, "onStop: The user will be logged out.");
            new CountDownTimer(TIME_BEFORE_SIGN_OUT, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    // This will log out the user and end the activity.
                    // If the user tries to open the app again a new activity will be created again.

                    database.signOut();
                    finish();
                }
            }.start();
        }
    }
}