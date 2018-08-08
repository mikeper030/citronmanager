package com.ultimatesoftil.citron.ui.activities;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mike on 07/08/2018.
 */

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
