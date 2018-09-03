package com.ultimatesoftil.citron.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;


import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ultimatesoftil.citron.FirebaseAuth.EmailLogin;
import com.ultimatesoftil.citron.R;

/**
 * Created by Mike on 19/08/2018.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
        /// /Get Firebase auth instance
       final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
       DatabaseReference myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_primary_light));
        }

        CheckBoxPreference welcome = (CheckBoxPreference) getPreferenceManager().findPreference("welcome");
        welcome.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.preference.Preference preference, Object o) {
                return true;
            }
        });
        Preference changeal=(Preference)getPreferenceManager().findPreference("all");
        changeal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                final EditText edittext = new EditText(getBaseContext());

                alert.setTitle("הזן תוכן להתראה");

                alert.setView(edittext);

                alert.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!TextUtils.isEmpty(edittext.getText().toString()))
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("debt", edittext.getText().toString()).apply();
                        else {
                            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("debt", "null").apply();
                        }
                    }
                });

                alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
                return false;
            }
        });
        Preference changealert=(Preference)getPreferenceManager().findPreference("alr");
       changealert.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
           @Override
           public boolean onPreferenceClick(Preference preference) {
               AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
               final EditText edittext = new EditText(getBaseContext());

               alert.setTitle("הזן תוכן להתראה");

               alert.setView(edittext);

               alert.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       if (!TextUtils.isEmpty(edittext.getText().toString()))
                           PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("alertcon", edittext.getText().toString()).apply();
                       else {
                           PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("alertcon", "null").apply();
                       }
                   }
               });

               alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int whichButton) {
                       // what ever you want to do with No option.
                   }
               });

               alert.show();
               return false;
           }
       });
       Preference log=(Preference)getPreferenceManager().findPreference("logout");
       Preference username=(Preference)getPreferenceManager().findPreference("username");
        if(auth!=null&&user!=null&&user.getEmail()!=null){
            username.setTitle(user.getEmail());
            log.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    auth.signOut();
                    startActivity(new Intent(SettingsActivity.this, EmailLogin.class));
                    finish();

                    return false;
                }
            });

        }else {
            log.setTitle("התחבר");
            log.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(SettingsActivity.this,EmailLogin.class));
                    return false;
                }
            });
        }
    }

    }



