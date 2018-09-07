package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.telecom.Call;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.Object.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Video;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.utils.ImageUtils;

public class FirebaseUtils {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_STORAGE_VIDEO_BUCKET = "videos/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String DATABASE_COLLECTION_STEPS = "steps/";
    private static final String DATABASE_COLLECTION_POST = "post/";
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private Journey sJourney;


    public FirebaseUtils(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    /*
     * AUTH SECTION
     */
    public String getCurrentUserId(){

        return mUser.getUid();
    }

    public FirebaseUser getAuthUser(){
        return mAuth.getCurrentUser();
    }

    public void getCurrentUser(final Callback callback){
        db.collection(DATABASE_COLLECTION_USERS).document(mUser.getUid()).get()
                .addOnSuccessListener(snapshot ->
                        callback.userResult(snapshot.toObject(User.class))
                ).addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    public void signOut(){
        mAuth.signOut();
    }

    public void login(String email, String password, final Callback callback){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->{
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            callback.authorizationResult(mAuth.getCurrentUser());

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.authorizationResult(mAuth.getCurrentUser());
                        }
                });
    }

    /*
     * USER SECTION
     */

    public void createUser(String email, String password, final String username, final Callback callback){

        final User userPojo = new User();
        userPojo.setUserName(username);
        userPojo.setEmail(email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");

                        final FirebaseUser authUser = mAuth.getCurrentUser();

                        userPojo.setUserId(authUser.getUid());
                        authUser.sendEmailVerification();
                        db.collection(DATABASE_COLLECTION_USERS).document(authUser.getUid())
                                .set(userPojo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Created new user");
                                    callback.result(Result.SUCCESS);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "Creating new user false");
                                    callback.result(Result.FAILED);

                                });

                    }else {
                        callback.result(Result.FAILED);
                    }
                });
    }

    public void updateUserGoals(String userId, String goals, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("goal", goals)
                .addOnSuccessListener(aVoid -> {
                    Log.v(TAG, "Success: Updated user goal");
                    callback.result(Result.SUCCESS);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed: Update user goal failed", e);
                    callback.result(Result.FAILED);
                });
    }

    private void updateUserImage(String userId, Image image, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("image.imageReference", image.getImageReference(), "image.imageUrl", image.getImageUrl() )
                .addOnSuccessListener(Void ->
                        callback.result(Result.SUCCESS)
                )
                .addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    public void saveUserImage(final String userId, Image image, final Callback callback){

        Bitmap bitmap = new ImageUtils().StringToBitMap(image.getImageUrl());


        uploadImage(bitmap, image.getImageReference(), new Callback() {
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
                image.setImageUrl(downloadUrl.toString());
                updateUserImage(userId, image, callback);
            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }
        });
    }


    /*
     * JOURNEY SECTION
     */

    public void getJourney(String journeyId, final Callback callback){

        DocumentReference docRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        docRef.get().addOnSuccessListener( documentSnapshot ->
                callback.journeyResult(documentSnapshot.toObject(Journey.class))
        );
    }

    public void saveJourney(Journey journey, final Callback callback){

        sJourney = journey;

        if(sJourney.getImage() != null){

            ImageUtils imageUtils = new ImageUtils();
            Bitmap imageBitmap = imageUtils.StringToBitMap(sJourney.getImage().getImageUrl());

            uploadImage(imageBitmap, sJourney.getImage().getImageReference(), new Callback() {
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
                    createJourney(downloadUrl, callback);
                }
            });
        }else{
            createJourney(null, callback);
        }
    }

    public void deleteJourney(Journey journey, final Callback callback){
        db.collection(DATABASE_COLLECTION_JOURNEYS).
                document(journey.getJourneyId())
                .delete()
                .addOnSuccessListener( Void ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e -> {
                        callback.result(Result.FAILED);
                        Log.v(TAG, "Failed to delete journey");
                });
    }

    private void createJourney(Uri downloadUri, final Callback callback){

        if(downloadUri != null){
            Image image = sJourney.getImage();
            image.setImageUrl(downloadUri.toString());
            sJourney.setImage(image);
        }
        db.collection(DATABASE_COLLECTION_JOURNEYS).add(sJourney)
            .addOnSuccessListener(documentReference -> {
                    documentReference.update("journeyId", documentReference.getId());
                    callback.result(Result.SUCCESS);}
            )
            .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding uploading document", e);
                    callback.result(Result.FAILED);
                }
            );
    }

    public void updateJourneyDescription(String journeyId, String description, final Callback callback){

        DocumentReference journeyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        journeyRef.update("description", description)
                .addOnSuccessListener(Void ->
                        callback.result(Result.SUCCESS)

                ).addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    /*
     * UPLOAD VIDEO AND IMAGE
     */
    private void uploadImage(final Bitmap bitmap, String prepend, final Callback callback ){
        final StorageReference ref = mStorageRef.child( prepend );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        bitmap.recycle();

        uploadTask.continueWithTask(task ->{
            if (!task.isSuccessful()) {
                callback.result(Result.FAILED);
            }
            return ref.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                bitmap.recycle();
                callback.imageResult(downloadUri);
                Log.d(TAG, "onSuccess: uri= "+ downloadUri.toString());
            } else {
                callback.result(Result.FAILED);
            }

        });
    }
    private void uploadVideo(final String uri, String ref, final Callback callback ){
        final StorageReference videoRef = mStorageRef.child( ref );

        Uri file = Uri.fromFile(new File(uri));

        UploadTask uploadTask = videoRef.putFile(file);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                callback.result(Result.FAILED);
            }
            return videoRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.imageResult(downloadUri);
                Log.d(TAG, "onSuccess: uri= "+ downloadUri.toString());
            } else {
                callback.result(Result.FAILED);
            }
        });
    }


    /*
     * STEP SECTION
     */

    public void saveStep(Step step, final Callback callback){
        switch (step.getStepType()){
            case IMAGES:
                saveStepWithImages(step, callback);
                break;
            case TEXT:
                saveStepWithText(step, callback);
                break;
            case VIDEO:
                saveStepWithVideo(step, callback);
                break;
        }
    }

    private void saveStepWithImages(@NonNull Step step, Callback callback){

        if(step.getStepId() == null){
           Collection<Image> images =  step.getImages().values();
            List<Image> imageList = new ArrayList<>(images);

            List<Task<Uri>> taskArrayList= new ArrayList<>();
            for (Image i: imageList) {

                final StorageReference ref = mStorageRef.child( i.getImageReference() );
                taskArrayList.add(uploadImageTask(new ImageUtils().StringToBitMap(i.getImageUrl()), ref, new Callback() {
                    @Override
                    public void result(Result result) {

                    }

                    @Override
                    public void imageResult(Uri downloadUrl) {
                        i.setImageUrl(downloadUrl.toString());
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

                    @Override
                    public void userResult(User user) {

                    }
                }));
            }

           Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(task -> {
               Map<String, Image> imageMap = new HashMap<>();
               for(Image im: imageList){
                   imageMap.put(String.valueOf(imageList.indexOf(im)), im);
               }
               step.setImages(imageMap);
               saveNewStepToFirebase(step, callback);
           });

        }else{
            updateStepInFirebase(step, callback);
        }

    }


    private Task<Uri> uploadImageTask(final Bitmap bitmap, StorageReference ref, Callback callback){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        bitmap.recycle();

        return uploadTask.continueWithTask(task -> {
            bitmap.recycle();
            ref.getDownloadUrl().addOnCompleteListener(result->
                callback.imageResult(result.getResult())
            );
            return ref.getDownloadUrl();
        });
    }

    private void saveStepWithVideo(@NonNull final Step step, final Callback callback){

        if(step.getStepId() == null){
            uploadVideo(step.getVideo().getVideoUrl(), DATABASE_STORAGE_VIDEO_BUCKET + step.getVideo().getVideoReference(), new Callback() {
                @Override
                public void result(Result result) {
                    callback.result(result);
                }

                @Override
                public void imageResult(Uri downloadUrl) {
                    Video v = step.getVideo();
                    v.setVideoUrl(downloadUrl.toString());
                    step.setVideo(v);
                    saveNewStepToFirebase(step, callback);
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

                @Override
                public void userResult(User user) {

                }
            });


        }else{
            updateStepInFirebase(step, callback);
        }
    }

    private void saveStepWithText(Step step, final Callback callback){
        if(step.getStepId() == null){
            saveNewStepToFirebase(step, callback);
        }else{
            updateStepInFirebase(step, callback);
        }
    }

    private void saveNewStepToFirebase(Step step, final Callback callback){
        db.collection(DATABASE_COLLECTION_STEPS).add(step)
                .addOnSuccessListener(documentReference -> {
                        documentReference.update("stepId", documentReference.getId());
                        callback.result(Result.SUCCESS);
                    })
                .addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    private void updateStepInFirebase(Step step, final Callback callback){
        db.collection(DATABASE_COLLECTION_STEPS).document(step.getStepId()).update("title", step.getTitle(),
                "bodyText", step.getBodyText(),
                "description", step.getDescription(),
                "publish", step.getPublish())
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                    callback.result(Result.FAILED)
                );
    }

    public void deleteStep(Step step, final Callback callback){

        db.collection(DATABASE_COLLECTION_STEPS).document(step.getStepId()).delete()
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }


    /*
     * POST SECTION
     */

    public void savePost(Post post, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).add(post).addOnSuccessListener(documentReference -> {
            documentReference.update("postId", documentReference.getId());
            callback.result(Result.SUCCESS);
        }).addOnFailureListener(e ->
            callback.result(Result.FAILED
        ));
    }

    public void savePostReply(Post parent, Post child, final Callback callback){

        Map<String, Boolean> map = parent.getComments();

        db.collection(DATABASE_COLLECTION_POST).add(child).addOnSuccessListener(documentReference -> {
            documentReference.update("postId", documentReference.getId());

            map.put(documentReference.getId(), true);

            db.collection(DATABASE_COLLECTION_POST).document(parent.getPostId())
                    .update("comments", map).addOnSuccessListener(aVoid -> {
                        Log.v(TAG, "Success: Updated user goal");
                        callback.result(Result.SUCCESS);
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Failed: Update user goal failed", e);
                        callback.result(Result.FAILED);
                    });
        }).addOnFailureListener(e ->
            callback.result(Result.FAILED
        ));
    }


    public void updatePostLikes(Map<String, Boolean> likes, String postId, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).document(postId).update("likes", likes)
                .addOnSuccessListener(aVoid ->
                    callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                    callback.result(Result.FAILED)
                );
    }

}
