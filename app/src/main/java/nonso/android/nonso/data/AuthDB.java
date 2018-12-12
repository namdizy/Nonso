package nonso.android.nonso.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.elasticSearch.ElasticSearch;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.interfaces.ElasticSearchCallback;

public class AuthDB {


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_ELASTIC_SEARCH = "elasticsearch/";
    private final String TAG = this.getClass().getSimpleName();

    public AuthDB(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public String getCurrentUserId(){

        return mUser.getUid();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseUser getmUser() {
        return mUser;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
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


    public void getElasticSearchAuthorization(ElasticSearchCallback callback){
        db.collection(DATABASE_ELASTIC_SEARCH).document("authorization").get()
                .addOnSuccessListener(documentSnapshot ->
                    callback.authorization(documentSnapshot.toObject(ElasticSearch.class).getAuthorization())
                ).addOnFailureListener(e -> callback.result(Result.FAILED));
    }

}
