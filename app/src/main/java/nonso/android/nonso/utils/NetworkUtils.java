package nonso.android.nonso.utils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nonso.android.nonso.models.User;

public class NetworkUtils {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String DATABASE_COLLECTION_USERS = "users/";

    private DocumentReference mUserRef;
    private User mUser;

    public User getUserFromId(String userId){
        db.collection(DATABASE_COLLECTION_USERS).document(userId)
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);
                }
            }).getResult();


        return null;
    }
}
