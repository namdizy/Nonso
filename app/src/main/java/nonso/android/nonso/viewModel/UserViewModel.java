package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import nonso.android.nonso.data.FirebaseDocumentLiveData;
import nonso.android.nonso.models.User;

public class UserViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userRef;
    private FirebaseDocumentLiveData uLiveData;
    private LiveData<User> userLiveData;

    public UserViewModel(){ }

    public void init(String userId){

        userRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        uLiveData  = new FirebaseDocumentLiveData(userRef);
        userLiveData = Transformations.map(uLiveData, new Deserializer());
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
