package com.mabem.homebook.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mabem.homebook.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class Util {

    /**
     * Hide the keyboard form the screen if it was visible
     * @param activity the activity in which to hide the keyboard
     */

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Save the preference of the remember me checkbox
     * @param activity the activity in which to save the preference
     * @param isChecked indicates if the remember me checkbox was checked or not
     */

    public static void saveRememberMePreference(Activity activity, boolean isChecked) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getResources().getString(R.string.saved_remember_me_preference), isChecked);
        editor.apply();
    }

    /**
     * Round the give double value by "places" places
     * @param value the double value to be rounded
     * @param places the number of places after the coma
     * @return the rounded double value
     */

    public static Double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
