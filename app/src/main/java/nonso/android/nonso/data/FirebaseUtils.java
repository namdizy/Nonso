package nonso.android.nonso.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


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


    public String getCurrentUserId(){

        return mUser.getEmail();
    }

    public void saveJourney(Journey journey){

        String prepend = DATABASE_STORAGE_IMAGE_BUCKET +"_user_profile_image"+ ".jpg";

    }

    public Uri uploadImage(Uri file, String prepend, final String flag){

        final StorageReference ref = mStorageRef.child( prepend );
        UploadTask uploadTask = ref.putFile(file);
        Uri downloadUri;

        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //downloadUri = uri;
                        Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                    }
                });
            }
        });
        return null;
    }



}
