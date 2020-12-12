package com.mabem.homebook.Database;

import android.app.Application;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.Model.Reminder;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;

import java.util.Date;

public class Database {
    public static final String usersCollection = "users";
    public static final String homeUserCollection = "home_user";
    public static final String homeCollection = "home";
    public static final String userName = "name";
    public static final String userId = "userId";
    public static final String ImageURL = "ImageURL";
    public static final String DATABASE_TAG = "Database";
    private static Database instance;

    private final Application application;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> resultMessage = new MutableLiveData<>();

    private Database(Application application) {
        this.application = application;
    }

    public static Database getInstance(Application application){
        if(instance == null){
            return new Database(application);
        }else{
            return instance;
        }
    }

    public void updateCurrentUser(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = makeUser(firebaseUser);
        currentUser.postValue(user);
    }

    //========================================= Getters

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

    //========================================= Reminder Functions

    public void getAllReminders(Home home){

    }

    public void setReminder(Home home, Reminder newReminder){

    }

    public void deleteReminder(Home home, Reminder newReminder){}

    public void updateReminder(Home home, Reminder newReminder){}

    //========================================= Receipt Functions

    public void getAllReceipts(Home home){

    }

    public void addReceipt(Home home, Member member){}

    public void deleteReceipt(Home home){}

    public void updateReceipt(Home home, Receipt receipt){}

    //========================================= Admin Functions

    public void declineRequestToJoin(Home home, User user){}

    public void acceptRequestToJoin(Home home, User user){}

    public void removeMemberFromHome(Home home, Member member){}

    public void inviteUser(Home home, String userEmail){}

    public void deleteHome(Home home){}

    public void updateHome(Home home){}

    public void makeAdmin(Home home, Member member){}

    //========================================= Member Functions

    public void leaveHome(Home home, Member member){}

    public void getStatistics(Home home, Date date){}

    //========================================= User Functions

    public void sendRequestToJoinHome(String homeId, User user){}

    public void updateUser(User user){}

    public void searchHome(String homeId){}

    public void createHome(String name, User user){}

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

