package com.mabem.homebook.Database;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.Model.Reminder;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.Model.UserNotification;
import com.mabem.homebook.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private static final String USER_NAME = "user_name";
    //========================================= User Notification Collection
    public static final String USER_NOTIFICATION_COLLECTION = "user_notification";

    //========================================= Storage
    public static final String PROFILE_IMAGES = "profile_images";





    private static final String TAG = "Database";



    private static Database instance;

    private final Application application;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference(PROFILE_IMAGES);

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> resultMessage = new MutableLiveData<>();
    private final MutableLiveData<Member> currentMember = new MutableLiveData<>();
    private final MutableLiveData<Home> currentHome = new MutableLiveData<>();
    private final MutableLiveData<Receipt> currentReceipt = new MutableLiveData<>();
    private final MutableLiveData<Reminder> currentReminder = new MutableLiveData<>();
    private final MutableLiveData<Home> resultHomeSearch = new MutableLiveData<>();


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
     * without being invoked by explicit action.
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
        if (currentUser.getValue() != null) {
            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(MEMBER_ID, currentUser.getValue().getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            HashMap<Home, Boolean> home_role = new HashMap<>();
                            ArrayList<AdminNotification> adminNotifications = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String homeName = document.getString(HOME_NAME);
                                String homeId = document.getString(HOME_ID); // different then HOME_CODE
                                Boolean role = document.getBoolean(MEMBER_ROLE);
                                Home home = new Home(homeId, homeName);
                                home_role.put(home, role);
                                if(role == Member.ADMIN_ROLE){
                                    firestore.collection(HOME_COLLECTION)
                                            .document(homeId)
                                            .collection(NOTIFICATION_COLLECTION)
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                                                    AdminNotification an = new AdminNotification(
                                                            queryDocumentSnapshot.getString(USER_ID),
                                                            homeId,
                                                            homeName,
                                                            queryDocumentSnapshot.getString(USER_NAME)
                                                    );
                                                    adminNotifications.add(an);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                resultMessage.postValue(e.getMessage());
                                                Log.w(TAG, "updateCurrentMember: ", e);
                                            });
                                }
                            }

                            ArrayList<UserNotification> userNotifications = new ArrayList<>();

                            firestore.collection(USER_NOTIFICATION_COLLECTION)
                                    .whereEqualTo(USER_ID, currentUser.getValue().getId())
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {

                                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                            UserNotification un = new UserNotification(
                                                    queryDocumentSnapshot.getString(HOME_NAME),
                                                    queryDocumentSnapshot.getBoolean(NOTIFICATION_TYPE)
                                            );
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        resultMessage.postValue(e.getMessage());
                                        Log.w(TAG, "updateCurrentMember: ", e);
                                    });
                            Member member = new Member(currentUser.getValue(), home_role, adminNotifications, userNotifications);
                            currentMember.postValue(member);
                        } else {
                            resultMessage.postValue(task.getException().getLocalizedMessage());
                        }
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
            firestore.collection(HOME_COLLECTION).document(homeId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                firestore.collection(HOME_COLLECTION)
                                        .document(document.getId())
                                        .collection(RECEIPT_COLLECTION)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                ArrayList<Receipt> receipts = new ArrayList<>();
                                                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                    Receipt receipt = new Receipt(
                                                            document1.getId(),
                                                            document1.getString(RECEIPT_NAME),
                                                            document1.getDate(RECEIPT_DATE),
                                                            document1.getDouble(RECEIPT_TOTAL),
                                                            document1.getString(MEMBER_NAME),
                                                            document1.getString(MEMBER_ID)
                                                    );
                                                    receipts.add(receipt);
                                                }
                                                Home home = new Home(
                                                        document.getId(),
                                                        document.getString(HOME_NAME),
                                                        document.getString(HOME_CODE),
                                                        document.getBoolean(HOME_VISIBILITY),
                                                        receipts
                                                );
                                                currentHome.postValue(home);
                                            } else {
                                                resultMessage.postValue(task1.getException().getMessage());
                                            }
                                        });
                            }
                        } else {
                            resultMessage.postValue(task.getException().getMessage());
                        }
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
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(receiptId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

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

                                        Receipt receipt = new Receipt(
                                                document.getId(),
                                                document.getString(RECEIPT_NAME),
                                                document.getDate(RECEIPT_DATE),
                                                document.getDouble(RECEIPT_TOTAL),
                                                document.getString(MEMBER_NAME),
                                                document.getString(MEMBER_ID)
                                        );
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
     * Try to get the reminder associated with @param reminderId.
     * If successful, currentReminder will be updated with reminder data.
     * If not, currentReminder will be null.
     *
     * @param reminderId reminderId to get from the database.
     */

    public void updateCurrentReminder(String reminderId) {
        if (currentHome.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .document(reminderId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Reminder reminder = new Reminder(
                                    document.getId(),
                                    document.getString(REMINDER_NAME),
                                    document.getString(REMINDER_FREQUENCY),
                                    document.getDate(REMINDER_DATE)
                            );
                            currentReminder.postValue(reminder);
                        } else {
                            resultMessage.postValue(task.getException().getMessage());
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
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(REMINDER_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
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
                            Home home = currentHome.getValue();
                            home.setReminders(reminders);
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
            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            HashMap<Member, Boolean> memberRole = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String memberName = document.getString(MEMBER_NAME);
                                String memberId = document.getString(MEMBER_ID); // different then HOME_CODE
                                boolean role = document.getBoolean(MEMBER_ROLE);
                                Member member = new Member(memberName, memberId);
                                memberRole.put(member, role);
                            }
                            Home home = currentHome.getValue();
                            home.setMember_role(memberRole);
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
                        if (firebaseUser != null) {
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

    public void setReminder(Reminder newReminder) {
        if (currentHome.getValue() != null) {

            Map<String, Object> data = new HashMap<>();
            data.put(REMINDER_DATE, newReminder.getDate());
            data.put(REMINDER_FREQUENCY, newReminder.getFrequency());
            data.put(REMINDER_NAME, newReminder.getName());

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
                        currentReminder.postValue(null);
                    })

                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error deleting document", e);
                    });

        }
    }

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

    public void addReceipt(Receipt receipt) {
        if (currentHome.getValue() != null && currentMember.getValue() != null) {

            Map<String, Object> data = new HashMap<>();
            data.put(RECEIPT_DATE, receipt.getDate());
            data.put(MEMBER_ID, currentMember.getValue().getId());
            data.put(MEMBER_NAME, currentMember.getValue().getName());
            data.put(RECEIPT_NAME, receipt.getName());

            double total = 0.0;

            for (Item item : receipt.getItems()) {
                total += item.getPrice();
            }

            data.put(RECEIPT_TOTAL, total);

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
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

    public void deleteReceipt(String receiptId) {
        if (currentHome.getValue() != null) {
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(receiptId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Receipt successfully deleted!");
                        resultMessage.postValue("Receipt successfully deleted!");
                        currentReceipt.postValue(null);
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error deleting document", e);
                    });

        }
    }

    public void updateReceipt(Receipt updatedReceipt) {
        if (currentHome.getValue() != null) {
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

            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .collection(RECEIPT_COLLECTION)
                    .document(updatedReceipt.getId())
                    .update(data)
                    .addOnSuccessListener(aVoid -> {

                        for (Item item : updatedReceipt.getItems()) {
                            Map<String, Object> newItem = new HashMap<>();
                            newItem.put(ITEM_NAME, item.getName());
                            newItem.put(ITEM_PRICE, item.getPrice());

                            firestore.collection(HOME_COLLECTION)
                                    .document(currentHome.getValue().getId())
                                    .collection(RECEIPT_COLLECTION)
                                    .document(updatedReceipt.getId())
                                    .collection(ITEM_COLLECTION)
                                    .document(item.getId())
                                    .update(newItem)
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
                        Log.w(TAG, "Error updating receipt Collection", e);
                        resultMessage.postValue(e.getMessage());
                    });
        }
    }

    //========================================= Admin Functions

    public void declineRequestToJoin(Home home, User user) {
    }

    public void acceptRequestToJoin(Home home, User user) {
    }

    public void removeMemberFromHome(String memberId) {
        if (currentHome.getValue() != null) {
            firestore.collection(HOME_USER_COLLECTION)
                    .whereEqualTo(MEMBER_ID, memberId)
                    .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                            firestore.collection(HOME_USER_COLLECTION)
                                    .document(queryDocumentSnapshot.getId())
                                    .delete();
                        }
                        Log.w(TAG, "Error while deleting member");
                    })
                    .addOnFailureListener(e -> {
                        resultMessage.postValue(e.getMessage());
                        Log.w(TAG, "Error while deleting member");
                    });
        }
    }

    public void inviteUser(Home home, String userEmail) {
    }

    public void deleteHome() {
        if (currentHome.getValue() != null){
            firestore.collection(HOME_COLLECTION)
                    .document(currentHome.getValue().getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        firestore.collection(HOME_USER_COLLECTION)
                                .whereEqualTo(HOME_ID, currentHome.getValue().getId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                                        firestore.collection(HOME_USER_COLLECTION)
                                                .document(queryDocumentSnapshot.getId())
                                                .delete();
                                    }
                                    resultMessage.postValue(application.getString(R.string.home_deleted_message));
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
        }
    }

    public void updateHome(Home home) {

    }

    //========================================= Member Functions

    public void leaveHome() {
        /*
         Todo: check first if this member is the last member in the home
         Todo: if so the home must be deleted. If not but this member is the last admin a new admin must be declared automatically
         */

    }

    public void getStatistics(Home home, Date date) {
    }

    //========================================= User Functions

    public void sendRequestToJoinHome(String homeId, User user) {
    }

    /**
     * Try to update the information of the currently logged in user.
     * If successful, information of the user are updated including the names in Receipts collections.
     * If unsuccessful, information are not updated and resultMessage is updated.
     *
     * @param newMember
     */

    public void updateMember(Member newMember, Uri localUri) {

        /*
        To update profile image:
        1. Delete any images if there is any
        2. Upload new image and get the URI
        3. Update the user with the new URI
         */

        if(localUri != null){ // new Photo >> delete old photo (if there is one) + update new one + update member
//            Uri image = Uri.fromFile(new File(String.valueOf(localUri)));
            StorageReference photoRef = imageStorageRef.child(newMember.getId());
            UploadTask uploadTask =  photoRef.putFile(localUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            updateMemberWithNewUri(newMember, uri);
                        })
                        .addOnFailureListener(e -> {
                            resultMessage.postValue(e.getMessage());
//                            Log.w(TAG, "updateMember: ",e);
                        });
            }).addOnFailureListener(e -> {
                resultMessage.postValue(e.getMessage());
//                Log.w(TAG, "updateMember: ",e);
            });
        }else {
            updateMemberWithNewUri(newMember, null);
        }

    }

    private void updateMemberWithNewUri(Member newMember, Uri newUri) {

        UserProfileChangeRequest profileUpdates;

        if(newUri != null){
             profileUpdates = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(newMember.getName())
                    .setPhotoUri(newUri)
                    .build();
        }else{
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
                                                    for(QueryDocumentSnapshot queryDocumentSnapshot1 : queryDocumentSnapshots1 ){
                                                        queryDocumentSnapshot1.getReference()
                                                                .update(MEMBER_NAME, newMember.getName())
                                                                .addOnFailureListener(e -> {
                                                                    resultMessage.postValue(e.getMessage());
                                                                    Log.w(TAG, "updateMemberWithNewUri: ", e);
                                                                });
                                                    }
                                                    if(newUri != null){
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

    public void searchHome(String homeCode) {
        firestore.collection(HOME_COLLECTION)
                .whereEqualTo(HOME_CODE, homeCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    QueryDocumentSnapshot result = null;
                    for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                        result = queryDocumentSnapshot;
                        String homeName = result.getString(HOME_NAME);
                        String homeId = result.getId();
                        Home home = new Home(homeId, homeName);
                        resultHomeSearch.postValue(home);
                    }
                    if(result == null){
                        resultHomeSearch.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    resultMessage.postValue(e.getMessage());
                    Log.w(TAG, "Error by searching for Home", e);
                });
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
            data.put(HOME_VISIBILITY, isPrivate);
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

    public User makeUser(FirebaseUser firebaseUser) {
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


}