package com.mabem.homebook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.SearchResultListener;
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.MainActivityViewModel;
import com.mabem.homebook.Views.Main.EditProfileFragment;
import com.mabem.homebook.Views.Main.SearchedHomeDialog;
import com.mabem.homebook.databinding.MainActivityBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchResultListener, NavigationDrawer {

    private static final int TIME_BEFORE_SIGN_OUT = 1000; // 1 Second
    private static final String MAIN_ACTIVITY_TAG = "MainActivity";
    TextView userName;
    ImageView userImage;
    private DrawerLayout drawerLayout;
    private MainActivityBinding mainActivityBinding;
    private Toolbar toolbar;
    private MainActivityViewModel mainActivityViewModel; // what about using ViewModelFactory?
    private NavController navController;
    private EditText search_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loadLocale();
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

        //=========================================

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Util.hideKeyboard(MainActivity.this);
            }
        });

        //========================================= Set up header

        View header = mainActivityBinding.navView.getHeaderView(0);
        userName = header.findViewById(R.id.receipt_name);
        userImage = header.findViewById(R.id.profile_image);
        TextView editProfileTextView = header.findViewById(R.id.header_edit_profile);
        ImageView searchButton = header.findViewById(R.id.search_button);
        search_edit_text = header.findViewById(R.id.search_edit_text);
        ProgressBar searchProgressBar = header.findViewById(R.id.search_progressBar);

        editProfileTextView.setOnClickListener(v -> {
            navController.navigate(R.id.editProfileFragment);
            drawerLayout.close();
        });

        //========================================= Handle sign out button

        mainActivityBinding.navView.getMenu().findItem(R.id.loginFragment).setOnMenuItemClickListener(menuItem -> {
            search_edit_text.setText("");
            mainActivityViewModel.signOut();
            navController.popBackStack();
            return false;
        });

        //========================================= Search Home

        searchButton.setOnClickListener(v -> {
            mainActivityViewModel.setShowResultDialog(true);
            searchProgressBar.setVisibility(View.VISIBLE);
            String homeCode = search_edit_text.getText().toString().trim();
            mainActivityViewModel.searchHome(homeCode);
            mainActivityViewModel.getSearchResult().observe(this, homes -> {
                if (homes != null) {
                    searchProgressBar.setVisibility(View.INVISIBLE);
                    Util.hideKeyboard(this);
                    if (mainActivityViewModel.isShowResultDialog()) {
                        SearchedHomeDialog searchedHomeDialog = new SearchedHomeDialog(homes, this, getBaseContext());
                        searchedHomeDialog.show(getSupportFragmentManager(), "Test");
                        mainActivityViewModel.setShowResultDialog(false);
                    }
                    mainActivityViewModel.clearSearchResults();
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        // If the back button is pressed while the navigation drawer is open
        // the application will not close and the drawer will be closed instead.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
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
        } else {
            EditProfileFragment.setAutomaticallyLoggedIn(true);
        }
    }

    //========================================= Language

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("my_lang", "");
        Util.setLocale(this, lang);
    }

    //========================================= NavDrawer Functions

    @Override
    public void disableNavDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void enableNavDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void setCurrentMember(Member member) {
        if (member != null) {
            userName.setText(member.getName());
            if (member.getImageURI() != null) {
                Glide.with(this)
                        .load(member.getImageURI())
                        .into(userImage);
            }
        }
    }

    //========================================= Search Listener

    @Override
    public void onHomeSelected(String homeId) {
        mainActivityViewModel.sendJoinRequest(homeId);
        Observer<String> messageObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    drawerLayout.close();
                    search_edit_text.setText("");
                    Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                    mainActivityViewModel.getResultMessage().removeObserver(this);
                }
            }
        };
        mainActivityViewModel.getResultMessage().observe(this, messageObserver);
    }

    @Override
    public void onOkPressed() {
        drawerLayout.close();
        search_edit_text.setText("");
    }
}