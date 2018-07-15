package nonso.android.nonso.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.CreatorType;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.User;

public class FirebaseUtils {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    private Journey sJourney;


    public String getCurrentUserId(){

        return mUser.getEmail();
    }

    public void saveJourney(Journey journey, final Callback callback){

        sJourney = journey;
        String prepend = DATABASE_STORAGE_IMAGE_BUCKET + journey.getCreatedBy().getId() +"_journey_profile_image"+ ".jpg";

        if(sJourney.getProfileImage() != null){
            uploadImage(Uri.parse(journey.getProfileImage()), prepend, new Callback() {
                @Override
                public void result(Result result) {}

                @Override
                public void journey(Uri downloadUrl) {
                    createJourney(downloadUrl, new Callback() {
                        @Override
                        public void result(Result result) {
                            Log.v(TAG, "Journey Creation in callback result of save Journey");
                            callback.result(result);
                        }

                        @Override
                        public void journey(Uri downloadUrl) { }
                    });
                }
            });
        }else{
            createJourney(null, new Callback() {
                @Override
                public void result(Result result) {
                    Log.v(TAG, "Journey Creation in callback result of save Journey");
                    callback.result(result);
                }

                @Override
                public void journey(Uri downloadUrl) {

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

    public void uploadImage(Uri file, String prepend, final Callback callback ){
        final StorageReference ref = mStorageRef.child( prepend );

       ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.journey(uri);
                        Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                    }
                });
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


}
