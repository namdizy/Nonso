package nonso.android.nonso.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import nonso.android.nonso.models.User;

@Singleton
public class UserRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mUserRef;

    private static final String DATABASE_COLLECTION_USERS = "users";
    private static final String TAG = "UserRepository";

    public LiveData<User> getUser(String userId){

        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        final MutableLiveData<User> data = new MutableLiveData<>();

        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    Log.v(TAG, "Success got users: " + task.getResult().getData());
                    data.setValue(doc.toObject(User.class));
                }else{
                    Log.w(TAG, "Document get failed: ", task.getException());
                }
            }
        });
        return data;
    }
}
