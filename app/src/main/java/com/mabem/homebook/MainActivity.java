package com.mabem.homebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mabem.homebook.ViewModels.MainActivityViewModel;
import com.mabem.homebook.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {

    private static final int TIME_BEFORE_SIGN_OUT = 1000; // 1 Second
    private static final String MAIN_ACTIVITY_TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private MainActivityBinding mainActivityBinding;
    private Toolbar toolbar;
    private MainActivityViewModel mainActivityViewModel; // what about using ViewModelFactory?
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //========================================= Init DataBinding

        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        drawerLayout = mainActivityBinding.drawerLayout;

        //========================================= Init ViewModel

        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.updateCurrentUser();

        //========================================= Set up the toolbar and the navigation drawer
        toolbar = mainActivityBinding.toolbar;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.mainFragment)
                .setDrawerLayout(drawerLayout)
                .build();
        // NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mainActivityBinding.navView, navController);

        //========================================= Handle sign out butten

        mainActivityBinding.navView.getMenu().findItem(R.id.loginFragment).setOnMenuItemClickListener(menuItem ->{
            mainActivityViewModel.signOut();
            return false;
        });

        //========================================= Set up header

        View header = mainActivityBinding.navView.getHeaderView(0);
        TextView editProfileTextView = header.findViewById(R.id.header_edit_profile);
        TextView userName = header.findViewById(R.id.receipt_name);


        editProfileTextView.setOnClickListener(v -> {
            navController.navigate(R.id.editProfileFragment);
            drawerLayout.close();
        });

        mainActivityViewModel.getCurrentUser().observe(this, user -> {
            if(user != null){
                userName.setText(user.getName());
            }
        });



//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if(destination.getId() != R.id.mainFragment){
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        // If the back button is pressed while the navigation drawer is open
        // the application will not close and the drawer will be closed instead.
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // If the user didn't check remember me, he/she will be logged out.

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
                    mainActivityViewModel.signOut();
                    finish();
                }
            }.start();
        }
    }
}