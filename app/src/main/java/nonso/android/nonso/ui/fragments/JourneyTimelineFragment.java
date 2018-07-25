package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.ui.adapters.StepsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyTimelineFragment.OnJourneyTimelineListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class JourneyTimelineFragment extends Fragment implements StepsAdapter.StepsAdapterOnClickListener{

    @BindView(R.id.journey_profile_recycler_view_steps) RecyclerView stepsRecyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_STEPS = "steps/";
    private final String JOURNEY_ID_KEY = "journey_pref";

    private final String TAG = JourneyTimelineFragment.class.getSimpleName();
    private ListenerRegistration listenerRegistration;

    private OnJourneyTimelineListener mListener;

    private Step mStep;
    private Journey mJourney;
    private ArrayList<Step> mStepsList = new ArrayList<Step>();
    private StepsAdapter stepsAdapter;
    private RecyclerView.LayoutManager stepsLayoutManager;
    private SharedPreferences mPref;
    private String mJourneyId;

    public JourneyTimelineFragment() { }

    public JourneyTimelineFragment newInstance(String journeyId) {
        JourneyTimelineFragment fragment = new JourneyTimelineFragment();
        Bundle args = new Bundle();
        args.putString(JOURNEY_ID_KEY, journeyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mJourneyId = getArguments().getString(JOURNEY_ID_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_journey_timeline, container, false);
        ButterKnife.bind(this, view);

        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mJourney = new Journey();
        //stepsLayoutManager = new LinearLayoutManager(getContext());
        //stepsRecyclerView.setLayoutManager(stepsLayoutManager);
        //stepsRecyclerView.setHasFixedSize(true);

        //stepsAdapter = new StepsAdapter(getContext(),this);
        //stepsRecyclerView.setAdapter(stepsAdapter);

        //setStepsList();
        return view;
    }

    private void setStepsList(){

        db.collection(DATABASE_COLLECTION_STEPS).whereEqualTo("journeyId", mJourney.getJourneyId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                            mStepsList.add(snapshot.toObject(Step.class));
                        }
                        if(mStepsList != null){
                            stepsAdapter.setStepsData(mStepsList);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error retrieving steps"+ e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJourneyTimelineListener) {
            mListener = (OnJourneyTimelineListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStepItemClick() {

    }


//    private void createText(){
//        if (mListener != null) {
//            Step step = new Step();
//            step.setStepType(StepType.TEXT);
//            mListener.onJourneyTimelineInteraction(step);
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnJourneyTimelineListener {
        // TODO: Update argument type and name
        void onJourneyTimelineInteraction(Step step);
    }
}
