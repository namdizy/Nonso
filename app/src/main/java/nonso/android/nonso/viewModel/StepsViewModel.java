package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;

public class StepsViewModel extends ViewModel {


    private static final String DATABASE_COLLECTION_STEPS = "steps";
    private static final String DATABASE_COLLECTIONS_JOURNEYS = "journeys";
    private static final String DATABASE_SUBCOLLECTION_STEPS =  "stepIds";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private String TAG = JourneyViewModel.class.getSimpleName();

    private Query stepsListRef;
    private FirebaseQueryLiveData sListLiveData;
    private LiveData<Task<ArrayList<Step>>> stepsListLiveData;

    private FirebaseUtils firebaseUtils;

    public StepsViewModel(){
        firebaseUtils = new FirebaseUtils();
    }

    public void setStepsList(String journeyId){

        stepsListRef = db.collection(DATABASE_COLLECTIONS_JOURNEYS).document(journeyId)
                .collection(DATABASE_SUBCOLLECTION_STEPS);
        sListLiveData = new FirebaseQueryLiveData(stepsListRef);
        stepsListLiveData = Transformations.map(sListLiveData, new Deserializer());
    }


    public LiveData<Task<ArrayList<Step>>> getStepsListLiveData() {
        return stepsListLiveData;
    }


    public void saveStep(Step step, final Callback callback){
        firebaseUtils.saveStep(step, new Callback() {
            @Override
            public void result(Result result) {
                callback.result(result);
            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }
        });
    }


    private class Deserializer implements Function<QuerySnapshot, Task<ArrayList<Step>>>{

        @Override
        public Task<ArrayList<Step>> apply(QuerySnapshot input) {

            List<DocumentSnapshot> temp = input.getDocuments();
            final ArrayList<String> stepIds = new ArrayList<>();

            if(temp.isEmpty()){
                return null;
            }

            for(DocumentSnapshot snapshot: temp){
                if(snapshot.get("stepId") != null){
                    String stepId = snapshot.get("stepId").toString();
                    if(stepId != null && !stepId.isEmpty()){
                        stepIds.add(stepId);
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("ids",stepIds);

            return mFunctions.getHttpsCallable("getSteps")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, ArrayList<Step>>() {
                        @Override
                        public ArrayList<Step> then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                            if(!task.isSuccessful()){
                                Exception e = task.getException();
                                Log.e(TAG, "Exception has occurred getting journeys: ", e.getCause());
                                return null;
                            }else{

                                Object fromdb = task.getResult().getData();
                                ArrayList<Step> steps = new ArrayList<>();
                                Gson gson = new Gson();

                                for(Object s: (ArrayList)fromdb){
                                    String jsonStr = gson.toJson(s);
                                    JSONObject jsonObject = new JSONObject(jsonStr);

                                    String createdAt = jsonObject.get("createdAt").toString();

                                    try{

                                        DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
                                        Date result =  df.parse(createdAt);
                                        jsonObject.put("createdAt", null);

                                        jsonStr = jsonObject.toString();

                                        Step step = gson.fromJson(jsonStr, Step.class);
                                        step.setCreatedAt(result);
                                        steps.add(step);

                                    }catch (Exception e){
                                        Log.w(TAG, "Error serializing Step Object: ", e.getCause());
                                        return null;
                                    }
                                }
                                return steps;
                            }
                        }
                    });
        }
    }
}
