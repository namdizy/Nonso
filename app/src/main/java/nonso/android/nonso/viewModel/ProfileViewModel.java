package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.models.User;
import nonso.android.nonso.data.FirebaseUtils;

public class ProfileViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userRef;
    private FirebaseQueryLiveData liveData;
    private LiveData<User> userLiveData;

    public ProfileViewModel(){ }

    public void init(String userId){

         userRef = db.collection(DATABASE_COLLECTION_USERS)
                .document(userId);
         liveData  = new FirebaseQueryLiveData(userRef);
         userLiveData = Transformations.map(liveData, new Deserializer());
    }

    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }

    private class Deserializer implements Function<DocumentSnapshot, User>{
        @Override
        public User apply(DocumentSnapshot input) {
            return input.toObject(User.class);
        }
    }
}
