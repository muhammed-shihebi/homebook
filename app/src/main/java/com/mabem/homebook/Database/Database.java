package com.mabem.homebook.Database;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.Model.Reminder;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database {

    //========================================= Home_User Collection
    public static final String HOME_USER_COLLECTION = "home_user";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String ROLE = "role";
    public static final String HOME_ID = "home_id";
    //========================================= Home Collection
    public static final String HOME_COLLECTION = "home";
    public static final String HOME_VISIBILITY = "visibility";
    public static final String HOME_NAME = "home_name";
    //========================================= Receipt Collection
    public static final String RECEIPT_COLLECTION = "receipt";
    public static final String RECEIPT_TOTAL = "total";
    public static final String RECEIPT_NAME = "name";
    public static final String RECEIPT_DATE = "date";
    public static final String RECEIPT_MEMBER_ID = "member_id";
    public static final String RECEIPT_MEMBER_NAME = "member_name";
    //========================================= Item Collection
    public static final String ITEM_NAME = "name";
    public static final String ITEM_PRICE = "price";
    //========================================= Reminder Collection
    public static final String REMINDER_COLLECTION = "reminder";
    public static final String REMINDER_DATE = "date";
    public static final String REMINDER_FREQUENCY = "frequency";
    public static final String REMINDER_NAME = "name";


    private static Database instance;

    private final Application application;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> resultMessage = new MutableLiveData<>();
    private final MutableLiveData<Member> currentMember = new MutableLiveData<>();
    private final MutableLiveData<Home> currentHome = new MutableLiveData<>();
    private final MutableLiveData<Receipt> currentReceipt = new MutableLiveData<>();
    private final MutableLiveData<Reminder> currentReminder = new MutableLiveData<>();



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

    //========================================= Updates

    /*
    * These methods should be used when trying to access data right after navigation to a new fragment
    * without being invoked by explicit action.
    * After calling these methods the MutableLiveData will be modified and all observes will be notified about the change.
    * */

    public void updateCurrentUser(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = makeUser(firebaseUser);
        currentUser.postValue(user);
    }

    public void updateCurrentMember(){
        if(currentUser.getValue() == null){
            currentMember.postValue(null);
        }else {
            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(USER_ID, currentUser.getValue().getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            HashMap<Home, Boolean> home_role = new HashMap<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                String homeName = document.getString(HOME_NAME);
                                String homeId = document.getString(HOME_ID);
                                boolean role = document.getBoolean(ROLE);
                                Home home = new Home(homeId, homeName);
                                home_role.put(home, role);
                            }
                            Member member = new Member(currentUser.getValue(), home_role);
                            currentMember.postValue(member);
                        }else {
                            resultMessage.postValue(task.getException().getLocalizedMessage());
                            currentMember.postValue(null);
                        }
                    });
        }
    }

    public void updateCurrentHome(String homeId){
        firestore.collection(HOME_COLLECTION).document(homeId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            firestore.collection(HOME_COLLECTION)
                                    .document(document.getId())
                                    .collection(RECEIPT_COLLECTION)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            ArrayList<Receipt> receipts = new ArrayList<>();
                                            for(QueryDocumentSnapshot document1: task1.getResult()){
                                                Receipt receipt = new Receipt(
                                                        document1.getId(),
                                                        document1.getString(RECEIPT_NAME),
                                                        document1.getDate(RECEIPT_DATE),
                                                        document1.getString(RECEIPT_MEMBER_NAME));
                                                receipts.add(receipt);
                                            }
                                            Home home = new Home(
                                                    document.getId(),
                                                    document.getString(HOME_NAME),
                                                    document.getBoolean(HOME_VISIBILITY),
                                                    receipts
                                            );
                                            currentHome.postValue(home);
                                        }else{
                                            currentHome.postValue(null);
                                            resultMessage.postValue(task1.getException().getMessage());
                                        }
                                    });
                        }
                    }else{
                        currentHome.postValue(null);
                        resultMessage.postValue(task.getException().getMessage());
                    }
                });
    }

    public void updateCurrentReceipt(String receiptId){
        if(currentHome == null){
            currentReceipt.postValue(null);
        }else{
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(receiptId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Receipt receipt = new Receipt(
                                    document.getId(),
                                    document.getString(RECEIPT_NAME),
                                    document.getDate(RECEIPT_DATE),
                                    document.getString(RECEIPT_MEMBER_NAME)
                            );
                            currentReceipt.postValue(receipt);
                        }else {
                            currentReceipt.postValue(null);
                            resultMessage.postValue(task.getException().getMessage());

                        }
                    });
        }
    }

    public void updateCurrentReminder(String reminderId){
        if(currentHome == null){
            currentReceipt.postValue(null);
        }else{
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .document(reminderId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Reminder reminder = new Reminder(
                                    document.getId(),
                                    document.getString(REMINDER_NAME),
                                    document.getString(REMINDER_FREQUENCY),
                                    document.getDate(REMINDER_DATE)
                            );
                            currentReminder.postValue(reminder);
                        }else {
                            currentReminder.postValue(null);
                            resultMessage.postValue(task.getException().getMessage());
                        }
                    });
        }
    }

    public void updateHomeWithReminders(){
        if(currentHome.getValue() == null){
            currentHome.postValue(null);
        }else{
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            ArrayList<Reminder> reminders = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Reminder reminder = new Reminder(
                                        document.getId(),
                                        document.getString(REMINDER_NAME),
                                        document.getString(REMINDER_FREQUENCY),
                                        document.getDate(REMINDER_DATE)
                                );
                                reminders.add(reminder);
                            }
                            Home home = currentHome.getValue();
                            home.setReminders(reminders);
                            currentHome.postValue(home);
                        }else {
                            currentHome.postValue(currentHome.getValue());
                            resultMessage.postValue(task.getException().getMessage());
                        }
                    });
        }
    }

    public void updateHomeWithMembers(){
        // Todo get all members when user want to see the members of home
    }

    //========================================= Getters

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<String> getResultMessage(){
        return resultMessage;
    }

    public MutableLiveData<Member> getCurrentMember(){
        return currentMember;
    }

    public MutableLiveData<Home> getCurrentHome(){
        return currentHome;
    }

    public MutableLiveData<Receipt> getCurrentReceipt() {
        return currentReceipt;
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

    public void setReminder(Home home, Reminder newReminder){

    }

    public void deleteReminder(Home home, Reminder newReminder){}

    public void updateReminder(Home home, Reminder newReminder){}

    //========================================= Receipt Functions

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