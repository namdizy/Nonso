package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import nonso.android.nonso.data.FirebaseDocumentLiveData;
import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.User;

public class ProfileViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userRef;
    private Query journeyRef;
    private FirebaseDocumentLiveData uLiveData;
    private LiveData<User> userLiveData;
    private FirebaseQueryLiveData jLiveData;
    private LiveData<Journey> journeyLiveData;
    private String mUserId;

    public ProfileViewModel(){ }

    public void init(String userId){

        userRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        uLiveData  = new FirebaseDocumentLiveData(userRef);
        userLiveData = Transformations.map(uLiveData, new Deserializer());

        //TODO: make a new viewmodel and add this to it. it shoul dbe JourneyViewModel
//        journeyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("userId", userId);
//        jLiveData = new FirebaseQueryLiveData(journeyRef);
//        journeyLiveData = Transformations.map(jLiveData, new)
//        this.mUserId = userId;
    }

    public LiveData<User> getUserLiveData(){
        return userLiveData;
    }

    public LiveData<Journey> getJourneysLiveData(){


        return null;
    }

    private class Deserializer implements Function<DocumentSnapshot, User>{
        @Override
        public User apply(DocumentSnapshot input) {
            return input.toObject(User.class);
        }
    }
}
