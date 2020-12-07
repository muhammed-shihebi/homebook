package com.mabem.homebook.Database;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;

public class Database {
    public static final String usersCollection = "users";
    public static final String homeUserCollection = "home_user";
    public static final String homeCollection = "home";
    public static final String userName = "name";
    public static final String userId = "userId";
    public static final String ImageURL = "ImageURL";
    public static final String DATABASE_TAG = "Database";

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<User> currentUser;
    private final Application application;
    private final MutableLiveData<String> resultMessage;

    public Database(Application application) {
        this.application = application;
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = new MutableLiveData<>();
        resultMessage = new MutableLiveData<>();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = makeUser(firebaseUser);
        currentUser.postValue(user);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<String> getResultMessage(){
        return resultMessage;
    }

    //========================================= Log in/Sign up Methods

    public void loginWithEmail(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        User user = makeUser(firebaseUser);
                        currentUser.postValue(user);
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                        currentUser.postValue(null);
                    }
                });
    }

    public void signUpWithEmail(String email, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                .Builder()
                                .setDisplayName(name)
                                .setPhotoUri(Uri.parse(""))
                                .build();
                        if(firebaseUser != null ){
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    User user = makeUser(firebaseUser);
                                    currentUser.postValue(user);
                                } else {
                                    resultMessage.postValue(task2.getException().getLocalizedMessage());
                                    currentUser.postValue(null);
                                }
                            });
                        }
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                        currentUser.postValue(null);
                    }
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
        currentUser.postValue(null);
    }

    public void loginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = makeUser(firebaseAuth.getCurrentUser());
                currentUser.postValue(user);
            } else {
                resultMessage.postValue(task.getException().getLocalizedMessage());
                currentUser.postValue(null);
            }
        });
    }

    public void forgotPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultMessage.postValue(application.getResources().getString(R.string.email_was_sent_message));
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                    }
                });
    }

    //========================================= Helper Functions

    public User makeUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
                return new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        firebaseUser.getPhotoUrl().toString()
                );
            } else {
                return new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        ""
                );
            }
        }
        return null;
    }


}

