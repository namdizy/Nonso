package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import nonso.android.nonso.data.FirebaseDocumentLiveData;
import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;

public class JourneyViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = JourneyViewModel.class.getSimpleName();

    private Query journeyListRef;
    private FirebaseQueryLiveData jListLiveData;
    private LiveData<ArrayList<Journey>> journeyListLiveData;

    private DocumentReference journeyItemRef;
    private FirebaseDocumentLiveData jItemLiveData;
    private LiveData<Journey> journeyItemLiveData;

    private FirebaseUtils firebaseUtils;


    public JourneyViewModel(){
        firebaseUtils = new FirebaseUtils();
    }

    public void setJourneysList(String userId){

        journeyListRef = db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("createdBy.id", userId);
        jListLiveData = new FirebaseQueryLiveData(journeyListRef);
        journeyListLiveData = Transformations.map(jListLiveData, new Deserializer());
    }

    public void setUpJourneyItem(String journeyId){
        journeyItemRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        jItemLiveData = new FirebaseDocumentLiveData(journeyItemRef);
        journeyItemLiveData = Transformations.map(jItemLiveData, new ItemDeserializer());
    }

    public LiveData<Journey> getJourneyItemLiveData() {
        return journeyItemLiveData;
    }

    public void saveJourney(Journey journey, final Callback callback){
        firebaseUtils.saveJourney(journey, new Callback() {
            @Override
            public void result(Result result) {
                Log.v(TAG, "Journey Creation in callback saveJourney");
                callback.result(result);
            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }
        });
    }

    public void deleteJourney(Journey journey, final Callback callback){

        firebaseUtils.deleteJourney(journey, new Callback() {
            @Override
            public void result(Result result) {
                callback.result(result);
            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }
        });

    }

    public LiveData<ArrayList<Journey>> getJourneyListLiveData(){
        return journeyListLiveData;
    }

    private class Deserializer implements Function<QuerySnapshot, ArrayList<Journey>>{
        @Override
        public ArrayList<Journey> apply(QuerySnapshot input) {

            List<DocumentSnapshot> temp = input.getDocuments();
            ArrayList<Journey> returnJourney = new ArrayList<>();

            for (DocumentSnapshot snapshot: temp) {
                Journey t = snapshot.toObject(Journey.class);
                t.setJourneyId(snapshot.getId());
                returnJourney.add(t);
            }
            return returnJourney;
        }
        public Journey apply(DocumentSnapshot input) {
            return input.toObject(Journey.class);
        }
    }

    private class ItemDeserializer implements Function<DocumentSnapshot, Journey>{
        @Override
        public Journey apply(DocumentSnapshot input) {
            return input.toObject(Journey.class);
        }
    }
}
