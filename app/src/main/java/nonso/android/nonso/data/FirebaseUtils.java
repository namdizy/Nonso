package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;

import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.utils.ImageUtils;
import nonso.android.nonso.utils.MultiSelectionSpinner;

public class FirebaseUtils {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String DATABASE_COLLECTION_STEPS = "steps/";
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private Journey sJourney;


    public FirebaseUtils(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public String getCurrentUserId(){

        return mUser.getUid();
    }
    public FirebaseUser getAuthUser(){
        return mAuth.getCurrentUser();
    }

    public void getCurrentUser(final Callback callback){
        db.collection(DATABASE_COLLECTION_USERS).document(mUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        callback.userResult(snapshot.toObject(User.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.result(Result.FAILED);
                    }
                });
    }

    public void signout(){
        mAuth.signOut();
    }

    public void getJourney(String journeyId, final Callback callback){

        DocumentReference docRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                callback.journeyResult(documentSnapshot.toObject(Journey.class));
            }
        });
    }

    public void saveJourney(Journey journey, final Callback callback){

        sJourney = journey;
        String prepend = DATABASE_STORAGE_IMAGE_BUCKET + journey.getCreatedBy().getId() +"_journey_profile_image"+ ".png";

        if(sJourney.getProfileImage() != null){

            ImageUtils imageUtils = new ImageUtils();
            Bitmap imageBitmap = imageUtils.StringToBitMap(sJourney.getProfileImage());

            uploadImage(imageBitmap, prepend, new Callback() {
                @Override
                public void result(Result result) {}

                @Override
                public void authorizationResult(FirebaseUser user) {

                }

                @Override
                public void userResult(User user) {

                }

                @Override
                public void journeyResult(Journey journey) {

                }

                @Override
                public void stepResult(Step step) {

                }

                @Override
                public void imageResult(Uri downloadUrl) {
                    createJourney(downloadUrl, new Callback() {
                        @Override
                        public void result(Result result) {
                            Log.v(TAG, "Journey Creation in callback result of save Journey");
                            callback.result(result);
                        }

                        @Override
                        public void userResult(User user) {

                        }

                        @Override
                        public void imageResult(Uri downloadUrl) { }

                        @Override
                        public void authorizationResult(FirebaseUser user) {

                        }

                        @Override
                        public void journeyResult(Journey journey) {

                        }

                        @Override
                        public void stepResult(Step step) {

                        }
                    });
                }
            });
        }else{
            createJourney(null, new Callback() {
                @Override
                public void userResult(User user) {

                }

                @Override
                public void result(Result result) {
                    Log.v(TAG, "Journey Creation in callback result of save Journey");
                    callback.result(result);
                }

                @Override
                public void journeyResult(Journey journey) {

                }

                @Override
                public void authorizationResult(FirebaseUser user) {

                }

                @Override
                public void imageResult(Uri downloadUrl) {

                }

                @Override
                public void stepResult(Step step) {

                }
            });
        }
    }

    public void deleteJourney(Journey journey, final Callback callback){
        db.collection(DATABASE_COLLECTION_JOURNEYS).
                document(journey.getJourneyId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.result(Result.SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.result(Result.FAILED);
                        Log.v(TAG, "Failed to delete journey");
                    }
                });
    }

    public void uploadImage(final Bitmap bitmap, String prepend, final Callback callback ){
        final StorageReference ref = mStorageRef.child( prepend );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        bitmap.recycle();

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    callback.result(Result.FAILED);
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    bitmap.recycle();
                    callback.imageResult(downloadUri);
                    Log.d(TAG, "onSuccess: uri= "+ downloadUri.toString());
                } else {
                   callback.result(Result.FAILED);
                }
            }
        });
    }

    public void createJourney(Uri downloadUri, final Callback callback){

        if(downloadUri != null){
            sJourney.setProfileImage(downloadUri.toString());
        }
        db.collection(DATABASE_COLLECTION_JOURNEYS).add(sJourney)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.w(TAG, "Document upload complete");

                    documentReference.update("journeyId", documentReference.getId());
                    callback.result(Result.SUCCESS);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding uploading document", e);
                    callback.result(Result.FAILED);
                }
            });
    }

    public void saveStep(Step step, final Callback callback){

        db.collection(DATABASE_COLLECTION_STEPS).add(step)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.update("stepId", documentReference.getId());
                    callback.result(Result.SUCCESS);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   callback.result(Result.FAILED);
                }
            });
    }
    public void login(String email, String password, final Callback callback){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            callback.authorizationResult(mAuth.getCurrentUser());

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.authorizationResult(mAuth.getCurrentUser());
                        }
                    }
                });
    }

    public void createUser(String email, String password, final String username, final Callback callback){

        final User userPojo = new User();
        userPojo.setUserName(username);
        userPojo.setEmail(email);
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");

                        final FirebaseUser authUser = mAuth.getCurrentUser();

                        userPojo.setUserId(authUser.getUid());
                        authUser.sendEmailVerification();
                        db.collection(DATABASE_COLLECTION_USERS).document(authUser.getUid())
                                .set(userPojo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Created new user");
                                        callback.result(Result.SUCCESS);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Creating new user false");
                                        callback.result(Result.FAILED);
                                    }
                                });

                    }else {
                        callback.result(Result.FAILED);
                    }
                }
            });
    }

    public void updateUserGoals(String userId, String goals, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("goal", goals)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v(TAG, "Success: Updated user goal");
                        callback.result(Result.SUCCESS);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed: Update user goal failed", e);
                        callback.result(Result.FAILED);
                    }
                });
    }

    public void updateUserImage(String userId, Uri userImage, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("imageUri", userImage.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.result(Result.SUCCESS);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.result(Result.FAILED);
                    }
                });
    }

    public void saveUserImage(final String userId, Bitmap userImage, final Callback callback){
        String prepend = DATABASE_STORAGE_IMAGE_BUCKET + userId +"_user_profile_image"+ ".png";


        uploadImage(userImage, prepend, new Callback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void userResult(User user) {

            }

            @Override
            public void imageResult(Uri downloadUrl) {
                updateUserImage(userId, downloadUrl, new Callback() {
                    @Override
                    public void result(Result result) {
                        callback.result(result);
                    }

                    @Override
                    public void imageResult(Uri downloadUrl) {

                    }

                    @Override
                    public void userResult(User user) {

                    }

                    @Override
                    public void authorizationResult(FirebaseUser user) {

                    }

                    @Override
                    public void journeyResult(Journey journey) {

                    }

                    @Override
                    public void stepResult(Step step) {

                    }
                });
            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }
        });
    }

}
