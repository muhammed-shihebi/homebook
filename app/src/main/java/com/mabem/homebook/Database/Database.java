package com.mabem.homebook.Database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mabem.homebook.Model.User;

public class Database {

    //========================================= Attributes

    private static final String DATABASE_TAG = "Database";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

}
