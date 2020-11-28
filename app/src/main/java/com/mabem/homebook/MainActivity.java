package com.mabem.homebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // If the user didn't check remember me he/she will be logged out
        // After 5 minutes of the app not being display on the screen
        // Even if the user uses Google to log in the checkbox will be taken into account
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.remember_me_default_value);
        boolean rememberMe = sharedPref.getBoolean(getString(R.string.saved_remember_me_preference), defaultValue);
        if (!rememberMe) {
            new CountDownTimer(300000, 1000) {
                public void onTick(long millisUntilFinished) {}
                public void onFinish() {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    if (firebaseAuth.getCurrentUser() != null) {
                        firebaseAuth.signOut();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}