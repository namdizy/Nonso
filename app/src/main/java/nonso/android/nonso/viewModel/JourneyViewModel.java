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
import com.google.firebase.firestore.DocumentReference;
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


import nonso.android.nonso.data.FirebaseDocumentLiveData;
import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public class JourneyViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String DATABASE_COLLECTION_USERS = "users/";

    private static final String DATABASE_SUBCOLLECTION_JOURNEYS = "journeyIds/";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private String TAG = JourneyViewModel.class.getSimpleName();

    private Query journeyListRef;
    private FirebaseQueryLiveData jListLiveData;
    private LiveData<Task<ArrayList<Journey>>> journeyListLiveData;

    private DocumentReference journeyItemRef;
    private FirebaseDocumentLiveData jItemLiveData;
    private LiveData<Journey> journeyItemLiveData;

    private FirebaseUtils firebaseUtils;


    public JourneyViewModel(){
        firebaseUtils = new FirebaseUtils();
    }

    public void setJourneysList(String userId){

        journeyListRef = db.collection(DATABASE_COLLECTION_USERS).document(userId)
                .collection(DATABASE_SUBCOLLECTION_JOURNEYS);
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

    public void createJourney(Journey journey, final Callback callback){
        firebaseUtils.saveJourney(journey, new Callback() {
            @Override
            public void result(Result result) {
                Log.v(TAG, "Journey Creation in callback saveJourney");
                callback.result(result);
            }

            @Override
            public void userResult(User user) {

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

    public void deleteJourney(Journey journey, final Callback callback){

        firebaseUtils.deleteJourney(journey, new Callback() {
            @Override
            public void userResult(User user) {

            }

            @Override
            public void result(Result result) {
                callback.result(result);
            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }
        });

    }

    public LiveData<Task<ArrayList<Journey>>> getJourneyListLiveData(){
        return journeyListLiveData;
    }


    private class Deserializer implements Function<QuerySnapshot, Task<ArrayList<Journey>>>{
        @Override
        public Task<ArrayList<Journey>> apply(QuerySnapshot input) {

            List<DocumentSnapshot> temp = input.getDocuments();
            final ArrayList<String> journeyIds = new ArrayList<>();

            if(temp.isEmpty()){
                return null;
            }

            for (DocumentSnapshot snapshot: temp) {
                if(snapshot.get("journeyId") != null ){
                    String journeyId = snapshot.get("journeyId").toString();
                    if(journeyId != null && !journeyId.isEmpty()){
                        journeyIds.add(journeyId);
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("ids", journeyIds);
            return mFunctions.getHttpsCallable("getJourneys")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, ArrayList<Journey>>() {
                        @Override
                        public ArrayList<Journey> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            Log.e(TAG, "Exception has occurred getting journeys: ", e.getCause());
                            return null;
                        }
                        else{
                            Object fromdb =  task.getResult().getData();
                            ArrayList<Journey> journeys = new ArrayList<>();
                            Gson gson = new Gson();

                            for(Object d: (ArrayList)fromdb){
                                String jsonStr = gson.toJson(d);
                                JSONObject jsonObject = new JSONObject(jsonStr);

                                String createdAt = jsonObject.get("createdAt").toString();
                                try {
                                    DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
                                    Date result =  df.parse(createdAt);
                                    jsonObject.put("createdAt", null);

                                    jsonStr = jsonObject.toString();

                                    Journey journey = gson.fromJson(jsonStr, Journey.class);
                                    journey.setCreatedAt(result);
                                    journeys.add(journey);
                                } catch (Exception e) {
                                    Log.w(TAG, "Error serializing Journey Object: ", e.getCause());
                                    return null;
                                }
                            }
                            return journeys;
                        }
                        }
                    });
        }
    }

    private class ItemDeserializer implements Function<DocumentSnapshot, Journey>{
        @Override
        public Journey apply(DocumentSnapshot input) {
            return input.toObject(Journey.class);
        }
    }
}
