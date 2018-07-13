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


import nonso.android.nonso.models.CreatorType;
import nonso.android.nonso.models.Journey;
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

    public StorageTask<UploadTask.TaskSnapshot> saveJourney(Journey journey){

        sJourney = journey;
        String prepend = DATABASE_STORAGE_IMAGE_BUCKET + journey.getCreatedBy().getId() +"_journey_profile_image"+ ".jpg";

        StorageTask task = uploadImage(Uri.parse(journey.getProfileImage()), prepend, CreatorType.JOURNEY);

        return uploadImage(Uri.parse(journey.getProfileImage()), prepend, CreatorType.JOURNEY);

    }

    public StorageTask<UploadTask.TaskSnapshot> uploadImage(Uri file, String prepend, final CreatorType flag){
        final StorageReference ref = mStorageRef.child( prepend );

        return ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        switch (flag){
                            case JOURNEY:
                                createJourney(uri);
                            case STEP:

                            case USER:
                        }
                        Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                    }
                });
            }
        });

    }


    public void createJourney(Uri downloadUri){

        sJourney.setProfileImage(downloadUri.toString());
        db.collection(DATABASE_COLLECTION_JOURNEYS).add(sJourney)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                    String journeyId = documentReference.getId();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding file to document", e);
                    }
                });
    }


}
