package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.models.Journey;

public class JourneyViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Query journeyRef;
    private FirebaseQueryLiveData jLiveData;
    private LiveData<ArrayList<Journey>> journeyLiveData;

    public JourneyViewModel(){

    }

    public void setJourneysList(String userId){

        journeyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("userId", userId);
        jLiveData = new FirebaseQueryLiveData(journeyRef);
        journeyLiveData = Transformations.map(jLiveData, new Deserializer());
    }

    public void setUpJourney(String journeyId){

    }

    public LiveData<ArrayList<Journey>> getJourneyListLiveData(){
        return journeyLiveData;
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
}
