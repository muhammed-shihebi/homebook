package com.mabem.homebook.Model.Database;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mabem.homebook.Model.Objects.AdminNotification;
import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.Model.Objects.Item;
import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.Model.Objects.Notification;
import com.mabem.homebook.Model.Objects.Receipt;
import com.mabem.homebook.Model.Objects.Reminder;
import com.mabem.homebook.Model.Objects.User;
import com.mabem.homebook.Model.Objects.UserNotification;
import com.mabem.homebook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is dedicated to define an interface to communicate with the external database, image storage and authentication service.
 * All these Services are Components of Firebase.
 */

public class Database {

    //========================================= Home_User Collection
    public static final String HOME_USER_COLLECTION = "home_user";
    public static final String MEMBER_ID = "member_id";
    public static final String MEMBER_NAME = "member_name";
    public static final String MEMBER_ROLE = "member_role";
    public static final String HOME_ID = "home_id";
    //========================================= Home Collection
    public static final String HOME_COLLECTION = "home";
    public static final String HOME_VISIBILITY = "visibility";
    public static final String HOME_NAME = "home_name";
    public static final String HOME_CODE = "home_code";
    //========================================= Receipt Collection
    public static final String RECEIPT_COLLECTION = "receipt";
    public static final String RECEIPT_TOTAL = "total";
    public static final String RECEIPT_NAME = "name";
    public static final String RECEIPT_DATE = "date";
    //========================================= Item Collection
    public static final String ITEM_COLLECTION = "item";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_PRICE = "price";
    //========================================= Reminder Collection
    public static final String REMINDER_COLLECTION = "reminder";
    public static final String REMINDER_DATE = "date";
    public static final String REMINDER_FREQUENCY = "frequency";
    public static final String REMINDER_NAME = "name";
    //========================================= Admin Notification Collection
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_COLLECTION = "notification";
    public static final String USER_ID = "user_id";
    //========================================= User Notification Collection
    public static final String USER_NOTIFICATION_COLLECTION = "user_notification";
    //========================================= Storage
    public static final String PROFILE_IMAGES = "profile_images";
    public static final String USER_NAME = "user_name";
    public static final String TAG = "Database";


    private static Database instance;

    private final Application application;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference(PROFILE_IMAGES);


    /*
    Object that can be observed from outside this class get the
    results after  calling one of this class's functions.
    */
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> resultMessage = new MutableLiveData<>();
    private final MutableLiveData<Member> currentMember = new MutableLiveData<>();
    private final MutableLiveData<Home> currentHome = new MutableLiveData<>();
    private final MutableLiveData<Receipt> currentReceipt = new MutableLiveData<>();
    private final MutableLiveData<Notification> currentNotification = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Home>> searchResult = new MutableLiveData<>();


    private Database(Application application) {
        this.application = application;
    }

    /**
     * Takes an application and checks if there is an initialized object of the database.
     * If there is an object of the database, it will be returned.
     * If not, a new instance is initialized and returned.
     *
     * @param application
     * @return An instance of the database object.
     */

    public static Database getInstance(Application application) {
        if (instance == null) {
            instance = new Database(application);
        }
        return instance;
    }

    //========================================= Updates

    /*
     * These methods should be used when trying to access data right after navigation to a new fragment
     * without being invoked by an explicit action.
     * After calling these methods the MutableLiveData will be modified and all observes will be notified about the change.
     * */


    /**
     * updateCurrentUser will check if there is a logged in user.
     * If so, a new user is made and the live data currentUser will be updated.
     * If not, the value of the current user will be null.
     */

    public void updateCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = makeUser(firebaseUser);
        currentUser.postValue(user);
    }

    /**
     * updateCurrentUser will check if there is a logged in user.
     * If so, a new Member with all of his Homes as homeName and homeId will be created.
     * If not, currentMember will be null.
     */

    public void updateCurrentMember() {
        if (currentMember.getValue() != null) {
            updateMemberWithMember();
        } else if (currentUser.getValue() != null) {
            updateMemberWithUser();
        }
    }

    private void updateMemberWithUser() {

        // 1. Get all homes of teh current user

        firestore.collection(HOME_USER_COLLECTION)
                .whereEqualTo(MEMBER_ID, currentUser.getValue().getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        HashMap<Home, Boolean> home_role = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String homeName = document.getString(HOME_NAME);
                            String homeId = document.getString(HOME_ID);
                            Boolean role = document.getBoolean(MEMBER_ROLE);
                            Home home = new Home(homeId, homeName);
                            home_role.put(home, role);
                        }
                        // 2. Post the value of the new current member.
                        Member member = new Member(currentUser.getValue(), home_role);
                        currentMember.postValue(member);
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                    }
                });
    }

    private void updateMemberWithMember() {

        // 1. Get all homes of teh current user

        firestore.collection(HOME_USER_COLLECTION)
                .whereEqualTo(MEMBER_ID, currentMember.getValue().getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        HashMap<Home, Boolean> home_role = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String homeName = document.getString(HOME_NAME);
                            String homeId = document.getString(HOME_ID);
                            Boolean role = document.getBoolean(MEMBER_ROLE);
                            Home home = new Home(homeId, homeName);
                            home_role.put(home, role);
                        }
                        currentMember.getValue().setHomeRole(home_role);

                        // 2. Post value of the current Member

                        currentMember.postValue(currentMember.getValue());
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                    }
                });
    }

    /**
     * updateCurrentNotification will get the notification of the currentMember
     */

    public void updateCurrentNotification() {
        if (currentMember.getValue() != null) {

            Notification notification = new Notification();

            // 1. Get all admin notification for this user.

            for (Home home : currentMember.getValue().getHome_role().keySet()) {

                if (currentMember.getValue().getHome_role().get(home) == Member.ADMIN_ROLE) {
                    firestore.collection(HOME_COLLECTION)
                            .document(home.getId())
                            .collection(NOTIFICATION_COLLECTION)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                ArrayList<AdminNotification> adminNotifications = new ArrayList<>();

                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    AdminNotification an = new AdminNotification(
                                            queryDocumentSnapshot.getString(USER_ID),
                                            home.getId(),
                                            home.getName(),
                                            queryDocumentSnapshot.getString(USER_NAME)
                                    );
                                    adminNotifications.add(an);
                                }

                                notification.setAdminNotifications(adminNotifications);
                            })
                            .addOnFailureListener(e -> {
                                resultMessage.postValue(e.getMessage());
                                Log.w(TAG, "updateCurrentNotification: ", e);
                            });
                }

            }

            // 2. Get all user notification for this user.

            ArrayList<UserNotification> userNotifications = new ArrayList<>();

            firestore.collection(USER_NOTIFICATION_COLLECTION)
                    .whereEqualTo(USER_ID, currentMember.getValue().getId())
                    .get()
                    .addOnSuccessListener(userNotificationsSnapshots -> {

                        for (QueryDocumentSnapshot userNotificationSnapshot : userNotificationsSnapshots) {
                            UserNotification un = new UserNotification(
                                    userNotificationSnapshot.getString(HOME_NAME),
                                    userNotificationSnapshot.getString(NOTIFICATION_TYPE),
                                    userNotificationSnapshot.getString(HOME_ID)
                            );
                            userNotifications.add(un);
                        }

                        notification.setUserNotifications(userNotifications);

                        currentNotification.postValue(notification);

                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "updateCurrentMember: ", e);
                    });


        }
    }

    /**
     * Try to get the home associated with @param homeId.
     * If successful, currentHome will be updated with the associated Receipts.
     * If not, currentHome will be null.
     *
     * @param homeId homeId to get form the database.
     */

    public void updateCurrentHome(String homeId) {
        if (currentMember.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(homeId)
                    .get()
                    .addOnSuccessListener(homeDocumentSnapshot -> {

                        // 2. Get all Receipts of this home
                        firestore.collection(HOME_COLLECTION)
                                .document(homeDocumentSnapshot.getId())
                                .collection(RECEIPT_COLLECTION)
                                .get()
                                .addOnSuccessListener(receiptDocumentSnapshots -> {
                                    ArrayList<Receipt> receipts = new ArrayList<>();
                                    for (QueryDocumentSnapshot receiptDocument : receiptDocumentSnapshots) {
                                        Receipt receipt = new Receipt(
                                                receiptDocument.getId(),
                                                receiptDocument.getString(RECEIPT_NAME),
                                                receiptDocument.getDate(RECEIPT_DATE),
                                                receiptDocument.getDouble(RECEIPT_TOTAL),
                                                receiptDocument.getString(MEMBER_NAME),
                                                receiptDocument.getString(MEMBER_ID)
                                        );
                                        receipts.add(receipt);
                                    }

                                    // 3. Create new Home
                                    Home home = new Home(
                                            homeDocumentSnapshot.getId(),
                                            homeDocumentSnapshot.getString(HOME_NAME),
                                            homeDocumentSnapshot.getString(HOME_CODE),
                                            homeDocumentSnapshot.getBoolean(HOME_VISIBILITY),
                                            receipts
                                    );

                                    // 4. Post the value of the new home
                                    currentHome.postValue(home);
                                })
                                .addOnFailureListener(e -> {
                                    resultMessage.postValue(e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                    });
        }
    }

    /**
     * Try to get the receipt associated with @param receiptId.
     * If successful, currentReceipt will be updated with receipt data.
     * If not, currentReceipt will be null.
     *
     * @param receiptId receiptId to get from the database.
     */

    public void updateCurrentReceipt(String receiptId) {
        if (currentHome.getValue() != null) {

            // 1. Get the Receipt related to receiptId

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(receiptId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            // 2. Get All items Related to this receipt

                            firestore.document(document.getReference().getPath())
                                    .collection(ITEM_COLLECTION)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {

                                        ArrayList<Item> items = new ArrayList<>();

                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                            Double itemPrice = queryDocumentSnapshot.getDouble(ITEM_PRICE);
                                            String itemName = queryDocumentSnapshot.getString(ITEM_NAME);
                                            String itemId = queryDocumentSnapshot.getId();
                                            items.add(new Item(itemId, itemName, itemPrice));
                                        }

                                        // 3. Create a Receipt

                                        Receipt receipt = new Receipt(
                                                document.getId(),
                                                document.getString(RECEIPT_NAME),
                                                document.getDate(RECEIPT_DATE),
                                                document.getDouble(RECEIPT_TOTAL),
                                                document.getString(MEMBER_NAME),
                                                document.getString(MEMBER_ID)
                                        );
                                        receipt.setItems(items);
                                        // 4. Post the value of the receipt

                                        currentReceipt.postValue(receipt);
                                    })
                                    .addOnFailureListener(e -> {
                                        resultMessage.postValue(e.getMessage());
                                        Log.w(TAG, "Error getting items collection", e);
                                    });
                        } else {
                            resultMessage.postValue(task.getException().getMessage());
                            Log.w(TAG, "Error getting Receipt", task.getException());
                        }
                    });
        }
    }

    /**
     * Try to get the reminders associated with the currentHome.
     * If successful, currentHome will be updated with the associated reminders.
     * If not, currentHome will not be changed.
     */

    public void updateHomeWithReminders() {
        if (currentHome.getValue() != null) {

            // 1. Get All reminder related to this home

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // 2. Create the reminders

                            ArrayList<Reminder> reminders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Reminder reminder = new Reminder(
                                        document.getId(),
                                        document.getString(REMINDER_NAME),
                                        document.getString(REMINDER_FREQUENCY),
                                        document.getDate(REMINDER_DATE)
                                );
                                reminders.add(reminder);
                            }

                            // 3. Add reminders to home

                            Home home = currentHome.getValue();
                            home.setReminders(reminders);

                            // 4. Post value of new home

                            currentHome.postValue(home);
                        } else {
                            resultMessage.postValue(task.getException().getMessage());
                        }
                    });
        }
    }

    /**
     * Try to get the members associated with the currentHome.
     * If successful, currentHome will be updated with the associated members.
     * If not, currentHome will be null.
     */

    public void updateHomeWithMembers() {
        if (currentMember.getValue() != null && currentHome.getValue() != null) {

            // 1. Get all members related to current home

            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // 2. Create Members

                            HashMap<Member, Boolean> memberRole = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String memberName = document.getString(MEMBER_NAME);
                                String memberId = document.getString(MEMBER_ID); // different then HOME_CODE
                                Boolean role = document.getBoolean(MEMBER_ROLE);
                                Member member = new Member(memberName, memberId);
                                memberRole.put(member, role);
                            }

                            // 3. Add members to current home

                            Home home = currentHome.getValue();
                            home.setMember_role(memberRole);

                            // 4. Post value of new home

                            currentHome.postValue(home);
                        } else {
                            resultMessage.postValue(task.getException().getLocalizedMessage());
                        }
                    });
        }
    }

    //========================================= Getters

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<String> getResultMessage() {
        return resultMessage;
    }

    public MutableLiveData<Member> getCurrentMember() {
        return currentMember;
    }

    public MutableLiveData<Home> getCurrentHome() {
        return currentHome;
    }

    public MutableLiveData<Receipt> getCurrentReceipt() {
        return currentReceipt;
    }

    public MutableLiveData<Notification> getCurrentNotification() {
        return currentNotification;
    }

    public MutableLiveData<ArrayList<Home>> getSearchResult() {
        return searchResult;
    }

    //========================================= Log in/Sign up Methods

    /**
     * Try to login with the given email and password.
     * If the login was successful the currentUser and resultMessage will be updated
     * If the login was unsuccessful the currentUser will not be updated and the resultMessage
     * will be updated with the appropriate message
     *
     * @param email    of the user
     * @param password of the user
     */

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

    /**
     * Try to sign up with the given email, password and name.
     * If the sign up was successful the currentUser and resultMessage will be updated
     * If the sign up was unsuccessful the currentUser will not be updated and the resultMessage
     * will be updated with the appropriate message
     *
     * @param email    of the new user
     * @param password of the new user
     * @param name     of the new user
     */

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
                        if (firebaseUser != null) {
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    User user = makeUser(firebaseUser);
                                    currentUser.postValue(user);
                                } else {
                                    resultMessage.postValue(task2.getException().getLocalizedMessage());
                                }
                            });
                        }
                    } else {
                        resultMessage.postValue(task.getException().getLocalizedMessage());
                    }
                });
    }

    /**
     * Sign out the current user
     * All the LiveDate objects of the database will be null after this call
     */

    public void signOut() {
        currentUser.setValue(null);
        resultMessage.setValue(null);
        currentMember.setValue(null);
        currentHome.setValue(null);
        currentReceipt.setValue(null);
        currentNotification.setValue(null);
        searchResult.setValue(null);
        firebaseAuth.signOut();
    }

    /**
     * Try to login the user with the given idToken
     * If the login was successful the currentUser and resultMessage will be updated
     * If the login was unsuccessful the currentUser will not be updated and the resultMessage
     * will be updated with the appropriate message
     *
     * @param idToken linked to the user's google account
     */

    public void loginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = makeUser(firebaseAuth.getCurrentUser());
                currentUser.postValue(user);
            } else {
                resultMessage.postValue(task.getException().getLocalizedMessage());
            }
        });
    }

    /**
     * Send an email with the needed information to reset the password of the account
     * linked to this email.
     *
     * @param email linked with the account, which its password must be updated
     */

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

    /**
     * Add the given reminder to the list of the reminders of the currentHome
     *
     * @param newReminder the new reminder to be added to the currentHome
     */

    public void setReminder(Reminder newReminder) {
        if (currentHome.getValue() != null) {

            // 1. Get reminder data

            Map<String, Object> data = new HashMap<>();
            data.put(REMINDER_DATE, newReminder.getDate());
            data.put(REMINDER_FREQUENCY, newReminder.getFrequency());
            data.put(REMINDER_NAME, newReminder.getName());

            // 2. set reminder data

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
                        resultMessage.postValue(application.getString(R.string.new_reminder_added_message));
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error adding document", e);
                    });
        }
    }

    /**
     * Delete the reminder of the currentHome with the given Id
     *
     * @param reminderId of the reminder to be deleted
     */

    public void deleteReminder(String reminderId) {
        if (currentHome.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .document(reminderId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Reminder successfully deleted!");
                        resultMessage.postValue(application.getString(R.string.reminder_deleted_message));
                    })

                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error deleting document", e);
                    });

        }
    }

    /**
     * Update the given reminder of the currentHome
     *
     * @param updatedReminder the reminder to be updated
     */

    public void updateReminder(Reminder updatedReminder) {
        Map<String, Object> data = new HashMap<>();
        data.put(REMINDER_DATE, updatedReminder.getDate());
        data.put(REMINDER_FREQUENCY, updatedReminder.getFrequency());
        data.put(REMINDER_NAME, updatedReminder.getName());

        if (currentHome.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .document(updatedReminder.getId())
                    .update(data)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Reminder successfully updated!");
                        resultMessage.postValue(application.getString(R.string.reminder_updated_message));
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating document", e);
                        resultMessage.postValue(e.getMessage());
                    });
        }
    }

    //========================================= Receipt Functions

    /**
     * Add new receipt to the currentHome
     *
     * @param receipt to be added
     */

    public void addReceipt(Receipt receipt) {
        if (currentHome.getValue() != null && currentMember.getValue() != null) {

            // 1. Get Receipt data

            Map<String, Object> data = new HashMap<>();
            data.put(RECEIPT_DATE, receipt.getDate());
            data.put(MEMBER_ID, currentMember.getValue().getId());
            data.put(MEMBER_NAME, currentMember.getValue().getName());
            data.put(RECEIPT_NAME, receipt.getName());


            double total = 0.0;

            // 2. calculate the total

            for (Item item : receipt.getItems()) {
                total += item.getPrice();
            }

            data.put(RECEIPT_TOTAL, total);

            // 3. Add Receipt to the database

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .add(data)
                    .addOnSuccessListener(documentReference -> {

                        // 4. Add Items to the database

                        for (Item item : receipt.getItems()) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put(ITEM_NAME, item.getName());
                            itemMap.put(ITEM_PRICE, item.getPrice());

                            documentReference.collection(ITEM_COLLECTION)
                                    .add(item);
                        }
                        resultMessage.postValue(application.getString(R.string.new_receipt_added_message));
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error adding document", e);
                    });
        }
    }

    /**
     * Delete the receipt of the currentHome with the given Id
     *
     * @param receiptId of the receipt to be deleted
     */

    public void deleteReceipt(String receiptId) {
        if (currentHome.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(receiptId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        firestore.collection(HOME_COLLECTION)
                                .document(currentHome.getValue().getId())
                                .collection(RECEIPT_COLLECTION)
                                .document(receiptId)
                                .collection(ITEM_COLLECTION)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        queryDocumentSnapshot.getReference().delete();
                                    }
                                })
                                .addOnFailureListener(e -> {

                                });

                        Log.d(TAG, "Receipt successfully deleted!");
                        resultMessage.postValue("Receipt successfully deleted!");
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error deleting document", e);
                    });

        }
    }

    /**
     * Update the given receipt of the currentHome
     *
     * @param updatedReceipt the receipt to be updated
     */

    public void updateReceipt(Receipt updatedReceipt) {
        if (currentHome.getValue() != null) {

            // 1. Get Receipt values

            Map<String, Object> data = new HashMap<>();
            data.put(RECEIPT_DATE, updatedReceipt.getDate());
            data.put(MEMBER_ID, updatedReceipt.getMemberId());
            data.put(MEMBER_NAME, updatedReceipt.getMemberName());
            data.put(RECEIPT_NAME, updatedReceipt.getName());

            double total = 0.0;

            for (Item item : updatedReceipt.getItems()) {
                total += item.getPrice();
            }

            data.put(RECEIPT_TOTAL, total);

            // 2. Update Receipt in the database

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(updatedReceipt.getId())
                    .update(data)
                    .addOnSuccessListener(aVoid -> {

                        // 3. Delete All Items in the Receipt

                        firestore.collection(HOME_COLLECTION)
                                .document(currentHome.getValue().getId())
                                .collection(RECEIPT_COLLECTION)
                                .document(updatedReceipt.getId())
                                .collection(ITEM_COLLECTION)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        queryDocumentSnapshot.getReference().delete();
                                    }

                                    // 4. Add all items

                                    for (Item item : updatedReceipt.getItems()) {
                                        Map<String, Object> newItem = new HashMap<>();
                                        newItem.put(ITEM_NAME, item.getName());
                                        newItem.put(ITEM_PRICE, item.getPrice());

                                        firestore.collection(HOME_COLLECTION)
                                                .document(currentHome.getValue().getId())
                                                .collection(RECEIPT_COLLECTION)
                                                .document(updatedReceipt.getId())
                                                .collection(ITEM_COLLECTION)
                                                .add(newItem)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Log.d(TAG, "Receipt successfully updated!");
                                                    resultMessage.postValue(application.getString(R.string.receipt_updated_message));
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w(TAG, "Error updating item collection", e);
                                                    resultMessage.postValue(e.getMessage());
                                                });
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error updating item collection", e);
                                    resultMessage.postValue(e.getMessage());
                                });

                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating receipt Collection", e);
                        resultMessage.postValue(e.getMessage());
                    });
        }
    }

    //========================================= Admin Functions

    /**
     * Decline the join request linked with the given AdminNotification
     *
     * @param adminNotification AdminNotification object with the request to be declined
     */

    public void declineJoinRequest(AdminNotification adminNotification) {

        // 1. Delete the notification form the home

        deleteNotificationFromHome(adminNotification);

        // 2. Add Notification to the user

        Map<String, Object> data = new HashMap<>();
        data.put(HOME_NAME, adminNotification.getHomeName());
        data.put(NOTIFICATION_TYPE, UserNotification.TYPE_DECLINE);
        data.put(USER_ID, adminNotification.getUserId());
        data.put(HOME_ID, adminNotification.getHomeId());

        firestore.collection(USER_NOTIFICATION_COLLECTION)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    resultMessage.postValue(application.getString(R.string.join_request_declined_message));
                })
                .addOnFailureListener(e -> {
                    resultMessage.postValue(e.getMessage());
                    Log.w(TAG, "declineRequestToJoin: ", e);
                });

    }

    /**
     * Accept the join request linked with the given AdminNotification
     *
     * @param adminNotification AdminNotification object with the request to be accepted
     */

    public void acceptJoinRequest(AdminNotification adminNotification) {

        // 1. Delete the notification form the home

        deleteNotificationFromHome(adminNotification);

        // 2. Add Notification to the user

        Map<String, Object> user_notification = new HashMap<>();
        user_notification.put(HOME_NAME, adminNotification.getHomeName());
        user_notification.put(NOTIFICATION_TYPE, UserNotification.TYPE_ACCEPT);
        user_notification.put(USER_ID, adminNotification.getUserId());
        user_notification.put(HOME_ID, adminNotification.getHomeId());

        firestore.collection(USER_NOTIFICATION_COLLECTION)
                .add(user_notification)
                .addOnSuccessListener(documentReference -> {

                    Map<String, Object> homeUserData = new HashMap<>();
                    homeUserData.put(HOME_NAME, adminNotification.getHomeName());
                    homeUserData.put(HOME_ID, adminNotification.getHomeId());
                    homeUserData.put(MEMBER_ID, adminNotification.getUserId());
                    homeUserData.put(MEMBER_NAME, adminNotification.getUserName());
                    homeUserData.put(MEMBER_ROLE, Member.MEMBER_ROLE);

                    firestore.collection(HOME_USER_COLLECTION)
                            .add(homeUserData)
                            .addOnSuccessListener(documentReference1 -> {
                                resultMessage.postValue(application.getString(R.string.join_request_accepted_message));
                            })
                            .addOnFailureListener(e -> {
                                resultMessage.postValue(e.getMessage());
                                Log.w(TAG, "acceptRequestToJoin: ", e);
                            });
                })
                .addOnFailureListener(e -> {
                    resultMessage.postValue(e.getMessage());
                    Log.w(TAG, "acceptRequestToJoin: ", e);
                });
    }

    private void deleteNotificationFromHome(AdminNotification adminNotification) {

        firestore.collection(HOME_COLLECTION)
                .document(adminNotification.getHomeId())
                .collection(NOTIFICATION_COLLECTION)
                .whereEqualTo(USER_ID, adminNotification.getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        queryDocumentSnapshot.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    resultMessage.postValue(e.getMessage());
                    Log.w(TAG, "declineRequestToJoin: ", e);
                });
    }

    /**
     * Delete the currentHome with all of the collections of data linked to it
     */

    public void deleteHome() {
        if (currentHome.getValue() != null) {

            // 1. Delete home collection

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        // 2. Delete home_user collection related to this home

                        firestore.collection(HOME_USER_COLLECTION)
                                .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        firestore.collection(HOME_USER_COLLECTION)
                                                .document(queryDocumentSnapshot.getId())
                                                .delete();
                                    }

                                    // 3. Delete receipts related to this home

                                    firestore.collection(HOME_COLLECTION)
                                            .document(currentHome.getValue().getId())
                                            .collection(RECEIPT_COLLECTION)
                                            .get()
                                            .addOnSuccessListener(receiptDocuments -> {

                                                // 4. Delete items of this receipt

                                                for (QueryDocumentSnapshot receiptDocument : receiptDocuments) {
                                                    receiptDocument
                                                            .getReference()
                                                            .collection(ITEM_COLLECTION)
                                                            .get()
                                                            .addOnSuccessListener(itemDocuments -> {
                                                                for (QueryDocumentSnapshot itemDocument : itemDocuments) {
                                                                    itemDocument.getReference().delete();
                                                                }
                                                            }).addOnFailureListener(e -> {
                                                        resultMessage.postValue(e.getMessage());
                                                        Log.i(TAG, "Error by deleting Home");
                                                    });
                                                    receiptDocument.getReference().delete();
                                                }

                                                // 5. Delete all reminders related to this home

                                                firestore.collection(HOME_COLLECTION)
                                                        .document(currentHome.getValue().getId())
                                                        .collection(REMINDER_COLLECTION)
                                                        .get()
                                                        .addOnSuccessListener(reminderDocuments -> {
                                                            for (QueryDocumentSnapshot reminderDocument : reminderDocuments) {
                                                                reminderDocument.getReference().delete();
                                                            }

                                                            // 5. Delete all notifications related to this home

                                                            firestore.collection(HOME_COLLECTION)
                                                                    .document(currentHome.getValue().getId())
                                                                    .collection(NOTIFICATION_COLLECTION)
                                                                    .get()
                                                                    .addOnSuccessListener(notificationDocuments -> {
                                                                        for (QueryDocumentSnapshot notificationDocument : notificationDocuments) {
                                                                            notificationDocument.getReference().delete();
                                                                        }
                                                                        resultMessage.postValue(application.getString(R.string.home_deleted_message));
                                                                        currentHome.postValue(null);
                                                                        Log.i(TAG, "Home was deleted successfully");
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        resultMessage.postValue(e.getMessage());
                                                                        Log.i(TAG, "Error by deleting Home");
                                                                    });

                                                        })
                                                        .addOnFailureListener(e -> {
                                                            resultMessage.postValue(e.getMessage());
                                                            Log.i(TAG, "Error by deleting Home");
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                resultMessage.postValue(e.getMessage());
                                                Log.i(TAG, "Error by deleting Home");
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    resultMessage.postValue(e.getMessage());
                                    Log.i(TAG, "Error by deleting Home");
                                });
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.i(TAG, "Error by deleting Home");
                    });
        }
    }

    /**
     * Update the currentHome with new data packed int the given newHome object
     *
     * @param newHome is an object that contain the new data to update the currentHome
     */

    public void updateHome(Home newHome) {

        firestore.collection(HOME_COLLECTION)
                .document(newHome.getId())
                .update(HOME_NAME, newHome.getName(),
                        HOME_VISIBILITY, newHome.getVisibility())
                .addOnSuccessListener(aVoid -> {

                    firestore.collection(HOME_USER_COLLECTION)
                            .whereEqualTo(HOME_ID, newHome.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    queryDocumentSnapshot.getReference().delete();
                                }

                                for (Member newMember : newHome.getMember_role().keySet()) {
                                    Boolean memberRole = newHome.getMember_role().get(newMember);

                                    Map<String, Object> data = new HashMap<>();
                                    data.put(HOME_ID, newHome.getId());
                                    data.put(HOME_NAME, newHome.getName());
                                    data.put(MEMBER_ID, newMember.getId());
                                    data.put(MEMBER_NAME, newMember.getName());
                                    data.put(MEMBER_ROLE, memberRole);

                                    firestore.collection(HOME_USER_COLLECTION)
                                            .add(data)
                                            .addOnFailureListener(e -> {
                                                resultMessage.postValue(e.getMessage());
                                                Log.i(TAG, "updateHome: ", e);
                                            });
                                }

                                currentHome.postValue(newHome);
                                resultMessage.postValue(application.getString(R.string.home_updated_message));

                            })
                            .addOnFailureListener(e -> {
                                resultMessage.postValue(e.getMessage());
                                Log.i(TAG, "updateHome: ", e);
                            });
                })
                .addOnFailureListener(e -> {
                    resultMessage.postValue(e.getMessage());
                    Log.i(TAG, "updateHome: ", e);
                });
    }

    //========================================= Member Functions

    /**
     * Get the currentMember out of the currentHome
     */

    public void leaveHome() {
        if (currentMember.getValue() != null && currentHome.getValue() != null) {

            // 1. Get the document of this user and this home and delete it

            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                    .whereEqualTo(MEMBER_ID, currentMember.getValue().getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            queryDocumentSnapshot.getReference().delete();
                        }

                        // 2. Check if this is the last member. If so delete the Home

                        firestore.collection(HOME_USER_COLLECTION)
                                .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                                .get()
                                .addOnSuccessListener(homeUserDocuments -> {
                                    boolean homeUserIsEmpty = true;
                                    for (QueryDocumentSnapshot q : homeUserDocuments) {
                                        homeUserIsEmpty = false;
                                        break;
                                    }
                                    if (homeUserIsEmpty) {
                                        deleteHome();
                                    } else {

                                        // 3. Check if the user left was the last admin. If so make new Admin

                                        boolean thereIsNoAdmin = true;

                                        for (QueryDocumentSnapshot homeUserDocument : homeUserDocuments) {
                                            Boolean memberRole = homeUserDocument.getBoolean(MEMBER_ROLE);
                                            if (memberRole == Member.ADMIN_ROLE) { // there is an admin
                                                thereIsNoAdmin = false;
                                                break;
                                            }
                                        }

                                        if (thereIsNoAdmin) {
                                            for (QueryDocumentSnapshot homeUserDocument : homeUserDocuments) {
                                                homeUserDocument
                                                        .getReference()
                                                        .update(MEMBER_ROLE, Member.ADMIN_ROLE)
                                                        .addOnSuccessListener(aVoid -> {
                                                            resultMessage.postValue(application.getString(R.string.member_left_home_message));
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            resultMessage.postValue(e.getMessage());
                                                            Log.w(TAG, "leaveHome: ", e);
                                                        });
                                                break;
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    resultMessage.postValue(e.getMessage());
                                    Log.w(TAG, "leaveHome: ", e);
                                });

                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "leaveHome: ", e);
                    });

        }
    }

    /**
     * Delete the notification linked with the given UserNotification object
     *
     * @param userNotification object containing the notification to be deleted
     */

    public void deleteUserNotification(UserNotification userNotification) {
        if (currentMember.getValue() != null) {
            firestore.collection(USER_NOTIFICATION_COLLECTION)
                    .whereEqualTo(HOME_ID, userNotification.getHomeId())
                    .whereEqualTo(USER_ID, currentMember.getValue().getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            queryDocumentSnapshot.getReference().delete();
                        }
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "deleteUserNotification: ", e);
                    });

        }
    }

    //========================================= User Functions

    /**
     * Send a join request to the home linked with the given Id
     *
     * @param homeId of the home to be send a join request to
     */

    public void sendJoinRequest(String homeId) {
        if (currentMember.getValue() != null) {
            String userId = currentMember.getValue().getId();
            String userName = currentMember.getValue().getName();
            Map<String, Object> data = new HashMap<>();
            data.put(USER_ID, userId);
            data.put(USER_NAME, userName);

            // check if there is a unanswered request first
            // if yes don't add new request

            firestore.collection(HOME_COLLECTION)
                    .document(homeId)
                    .collection(NOTIFICATION_COLLECTION)
                    .whereEqualTo(USER_ID, userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean thereIsARequest = false;
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            thereIsARequest = true;
                        }
                        if (!thereIsARequest) {
                            firestore.collection(HOME_COLLECTION)
                                    .document(homeId)
                                    .collection(NOTIFICATION_COLLECTION)
                                    .add(data)
                                    .addOnSuccessListener(documentReference -> {
                                        resultMessage.postValue(application.getString(R.string.request_sent_successfully));
                                    })
                                    .addOnFailureListener(e -> {
                                        resultMessage.postValue(e.getMessage());
                                        Log.w(TAG, "sendJoinRequest: ", e);
                                    });
                        } else {
                            resultMessage.postValue("You sent a join request to this Home before");
                        }

                    }).addOnFailureListener(e -> {
                resultMessage.postValue(e.getMessage());
                Log.w(TAG, "sendJoinRequest: ", e);
            });
        }
    }

    /**
     * Try to update the information of the currently logged in user.
     * If successful, information of the user are updated including the names in Receipts collections.
     * If unsuccessful, information are not updated and resultMessage is updated.
     *
     * @param newMember object containing the data of the Member to be updated
     */

    public void updateMember(Member newMember, Uri localUri) {

        /*
        To update profile image:
        1. Delete any images if there is any
        2. Upload new image and get the URI
        3. Update the user with the new URI
         */

        if (localUri != null) { // new Photo >> delete old photo (if there is one) + update new one + update member
            StorageReference photoRef = imageStorageRef.child(newMember.getId());
            UploadTask uploadTask = photoRef.putFile(localUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            updateMemberWithNewUri(newMember, uri);
                        })
                        .addOnFailureListener(e -> {
                            resultMessage.postValue(e.getMessage());
                        });
            }).addOnFailureListener(e -> {
                resultMessage.postValue(e.getMessage());
            });
        } else {
            updateMemberWithNewUri(newMember, null);
        }

    }

    private void updateMemberWithNewUri(Member newMember, Uri newUri) {

        UserProfileChangeRequest profileUpdates;

        if (newUri != null) {
            profileUpdates = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(newMember.getName())
                    .setPhotoUri(newUri)
                    .build();
        } else {
            profileUpdates = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(newMember.getName())
                    .build();
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseUser.updateEmail(newMember.getEmailAddress()).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {

                            firestore.collectionGroup(RECEIPT_COLLECTION)
                                    .whereEqualTo(MEMBER_ID, newMember.getId())
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        int counter = 1;
                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                            queryDocumentSnapshot
                                                    .getReference()
                                                    .update(MEMBER_NAME, newMember.getName())
                                                    .addOnFailureListener(e -> {
                                                        resultMessage.postValue(e.getMessage());
                                                        Log.w(TAG, "updateMemberWithNewUri: ", e);
                                                    });
                                        }

                                        firestore.collection(HOME_USER_COLLECTION)
                                                .whereEqualTo(MEMBER_ID, newMember.getId())
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot1 : queryDocumentSnapshots1) {
                                                        queryDocumentSnapshot1.getReference()
                                                                .update(MEMBER_NAME, newMember.getName())
                                                                .addOnFailureListener(e -> {
                                                                    resultMessage.postValue(e.getMessage());
                                                                    Log.w(TAG, "updateMemberWithNewUri: ", e);
                                                                });
                                                    }
                                                    if (newUri != null) {
                                                        newMember.setImageURI(newUri);
                                                    }
                                                    Log.i(TAG, "updateMemberWithNewUri: New Member posted");
                                                    currentMember.postValue(newMember);
                                                    resultMessage.postValue(application.getString(R.string.profile_updated_successfully));
                                                })
                                                .addOnFailureListener(e -> {
                                                    resultMessage.postValue(e.getMessage());
                                                    Log.w(TAG, "updateMemberWithNewUri: ", e);
                                                });

                                    })
                                    .addOnFailureListener(e -> {
                                        resultMessage.postValue(e.getMessage());
                                        Log.w(TAG, "updateMemberWithNewUri: ", e);
                                    });
                        } else {
                            resultMessage.postValue(task2.getException().getMessage());
                            Log.w(TAG, "updateMemberWithNewUri: ", task2.getException());
                        }

                    });
                } else {
                    resultMessage.postValue(task.getException().getLocalizedMessage());
                    Log.w(TAG, "updateMemberWithNewUri: ", task.getException());
                }
            });
        }
    }

    /**
     * Search for the home with the given homeCode
     * If successful, the searchResult will be updated with the home
     * If unsuccessful, the resultMessage will be updated with an appropriate message
     *
     * @param homeCode
     */

    public void searchHome(String homeCode) {
        if (currentMember.getValue() != null) {

            // 1. Search for all homes with this code
            ArrayList<Home> homes = new ArrayList<>();

            firestore.collection(HOME_COLLECTION)
                    .whereEqualTo(HOME_CODE, homeCode)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            Boolean homeVisibility = queryDocumentSnapshot.getBoolean(HOME_VISIBILITY);
                            // 2. Ignore private homes
                            if (homeVisibility == Home.VISIBILITY_PRIVATE) {
                                continue;
                            }
                            String homeName = queryDocumentSnapshot.getString(HOME_NAME);
                            String homeId = queryDocumentSnapshot.getId();
                            Home home = new Home(homeId, homeName);
                            // 3. if user is member in this home ignore it
                            if (isMember(home)) {
                                continue;
                            }
                            homes.add(home);
                        }

                        searchResult.postValue(homes);
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error by searching for Home", e);
                    });
        }
    }

    public void clearSearchResults() {
        searchResult.postValue(null);
    }

    /**
     * Try to create a new Home associated with the currentMember.
     * If successful, a new Home for the currentMember is created.
     * The currentMember will be automatically the admin of this Home.
     *
     * @param homeName  name of the home to be created.
     * @param isPrivate visibility: true for private, false for public.
     */

    public void createHome(String homeName, Boolean isPrivate) {
        if (currentMember.getValue() != null) {
            // create a unique code for the home
            String homeCode = UUID.randomUUID().toString().split("-")[0];

            Map<String, Object> data = new HashMap<>();
            data.put(HOME_NAME, homeName);

            if (isPrivate) {
                data.put(HOME_VISIBILITY, Home.VISIBILITY_PRIVATE);
            } else {
                data.put(HOME_VISIBILITY, Home.VISIBILITY_PUBLIC);
            }
            data.put(HOME_CODE, homeCode);

            firestore.collection(HOME_COLLECTION)
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
                        Map<String, Object> home_user_data = new HashMap<>();
                        home_user_data.put(HOME_ID, documentReference.getId());
                        home_user_data.put(HOME_NAME, homeName);
                        home_user_data.put(MEMBER_ROLE, true);
                        home_user_data.put(MEMBER_ID, currentMember.getValue().getId());
                        home_user_data.put(MEMBER_NAME, currentMember.getValue().getName());

                        firestore.collection(HOME_USER_COLLECTION)
                                .add(home_user_data)
                                .addOnSuccessListener(documentReference1 -> {
                                    resultMessage.postValue(application.getString(R.string.new_home_created_successfully));
                                })
                                .addOnFailureListener(e -> {
                                    resultMessage.postValue(e.getMessage());
                                    Log.w(TAG, "Error adding document", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error adding document", e);
                    });
        }
    }

    //========================================= Helper Functions

    private User makeUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
                return new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        firebaseUser.getPhotoUrl()
                );
            } else {
                return new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail(),
                        null
                );
            }
        }
        return null;
    }

    private boolean isMember(Home home) {
        boolean isMember = false;

        if (currentMember.getValue() != null) {
            for (Home memberHome : currentMember.getValue().getHome_role().keySet()) {
                if (memberHome.getId().equals(home.getId())) {
                    isMember = true;
                    break;
                }
            }
        }
        return isMember;
    }
}