package com.carassist.carassist.activities;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 6/7/2016.
 */
public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Gets an instance of the firebase database that will be global to the application
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
