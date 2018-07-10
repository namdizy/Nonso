package nonso.android.nonso.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import nonso.android.nonso.models.User;

public class FirebaseUtils {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String TAG= FirebaseUtils.class.getSimpleName();



    public String getCurrentUserId(){

        return mUser.getEmail();
    }

}
