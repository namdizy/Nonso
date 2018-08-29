package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public class StepsViewModel extends ViewModel {


    private static final String DATABASE_COLLECTION_STEPS = "steps";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = JourneyViewModel.class.getSimpleName();

    private Query stepsListRef;
    private FirebaseQueryLiveData sListLiveData;
    private LiveData<ArrayList<Step>> stepsListLiveData;

    private Query stepsArchiveListRef;
    private FirebaseQueryLiveData sArchiveListLiveData;
    private LiveData<ArrayList<Step>> stepsArchiveListLiveData;

    private FirebaseUtils firebaseUtils;

    public StepsViewModel(){
        firebaseUtils = new FirebaseUtils();
    }

    public void setStepsList(String journeyId){

        stepsListRef = db.collection(DATABASE_COLLECTION_STEPS).whereEqualTo("createdBy.id", journeyId)
                .whereEqualTo("publish", true);
        sListLiveData = new FirebaseQueryLiveData(stepsListRef);
        stepsListLiveData = Transformations.map(sListLiveData, new Deserializer());
    }

    public void setStepsArchiveList(String journeyId){
        stepsArchiveListRef = db.collection(DATABASE_COLLECTION_STEPS).whereEqualTo("createdBy.id", journeyId)
                .whereEqualTo("publish", false);
        sArchiveListLiveData = new FirebaseQueryLiveData(stepsArchiveListRef);
        stepsArchiveListLiveData = Transformations.map(sArchiveListLiveData, new Deserializer());
    }


    public LiveData<ArrayList<Step>> getStepsListLiveData() {
        return stepsListLiveData;
    }

    public LiveData<ArrayList<Step>> getStepsArchiveListLiveData() {
        return stepsArchiveListLiveData;
    }

    public void saveStep(Step step, final Callback callback){
        firebaseUtils.saveStep(step, callback);
    }

    public void deleteStep(Step step, final Callback callback){
        firebaseUtils.deleteStep(step, callback);
    }


    private class Deserializer implements Function<QuerySnapshot, ArrayList<Step>>{

        @Override
        public ArrayList<Step> apply(QuerySnapshot input) {

            ArrayList<Step> steps = new ArrayList<>();

            for (DocumentSnapshot snapshot : input) {
                steps.add(snapshot.toObject(Step.class));
            }

            return steps;
        }
    }
}
