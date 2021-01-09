package com.mabem.homebook.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mabem.homebook.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

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
     * @param places the number of places after the comma
     * @return the rounded double value
     */

    public static Double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Show the dialog to select the preferred language
     * @param activity the activity in which to show the dialog
     */

    public static void showChangeLanguageDialog(Activity activity) {
        final String[] lang = {"English", "Deutsch"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select a Language")
                .setSingleChoiceItems(lang, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            setLocale(activity, "en");
                            activity.recreate();
                        }else if(which == 1){
                            setLocale(activity,"de");
                            activity.recreate();
                        }

                        dialog.dismiss();
                    }
                });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    /**
     * Save the preference of the selected preferred language
     * @param activity the activity in which to save the preference
     * @param language the string of the selected preferred language
     */
    public static void setLocale(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = activity.getSharedPreferences("settings",  activity.MODE_PRIVATE).edit();
        editor.putString("my_lang",language);
        editor.apply();
    }

}
