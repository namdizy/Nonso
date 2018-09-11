package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nonso.android.nonso.data.AuthDB;
import nonso.android.nonso.data.FirebaseDocumentLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.data.UsersDB;
import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public class UserViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userRef;
    private FirebaseDocumentLiveData uLiveData;
    private LiveData<User> userLiveData;
    private UsersDB usersDB;
    private AuthDB authDB;

    public UserViewModel(){
        usersDB = new UsersDB();
        authDB = new AuthDB();
    }

    public void init(String userId){

        userRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        uLiveData  = new FirebaseDocumentLiveData(userRef);
        userLiveData = Transformations.map(uLiveData, new Deserializer());
    }


    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }

    public void updateUserGoals(String userId, String goals, final Callback callback){

        usersDB.updateUserGoals(userId, goals,callback);
    }

    public void saveUserImage(String userId, Image image, Callback callback){
        usersDB.saveUserImage(userId, image, callback);
    }

    private class Deserializer implements Function<DocumentSnapshot, User>{
        @Override
        public User apply(DocumentSnapshot input) {
            return input.toObject(User.class);
        }
    }

    public String getCurrentUserId(){
       return authDB.getCurrentUserId();
    }
}
