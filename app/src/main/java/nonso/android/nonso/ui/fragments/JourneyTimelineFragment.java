package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.ui.activities.CreateStepTextActivity;
import nonso.android.nonso.ui.activities.TextStepActivity;
import nonso.android.nonso.ui.adapters.StepsAdapter;
import nonso.android.nonso.viewModel.StepsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyTimelineFragment.OnJourneyTimelineListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class JourneyTimelineFragment extends Fragment implements StepsAdapter.StepsAdapterOnClickListener{

    @BindView(R.id.journey_profile_recycler_view_steps) RecyclerView stepsRecyclerView;
    @BindView(R.id.steps_list_container)
    LinearLayout mStepsContainer;
    @BindView(R.id.steps_not_found_container) LinearLayout mStepsNotFound;

    private final String JOURNEY_ID_KEY = "journey_pref";
    private final String STEP_EXTRA = "step_extra";
    private StepsViewModel viewModel;

    private final String TAG = JourneyTimelineFragment.class.getSimpleName();

    private OnJourneyTimelineListener mListener;

    private ArrayList<Step> mStepsList = new ArrayList<Step>();
    private StepsAdapter stepsAdapter;
    private RecyclerView.LayoutManager stepsLayoutManager;
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

        stepsLayoutManager = new LinearLayoutManager(getContext());
        stepsRecyclerView.setLayoutManager(stepsLayoutManager);
        stepsRecyclerView.setHasFixedSize(true);

        stepsAdapter = new StepsAdapter(getContext(),this);
        stepsRecyclerView.setAdapter(stepsAdapter);

        viewModel = ViewModelProviders.of(this).get(StepsViewModel.class);
        viewModel.setStepsList(mJourneyId);
        viewModel.getStepsListLiveData().observe(this, new Observer<ArrayList<Step>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Step> steps) {
                setSteps(steps);
            }
        });
        return view;
    }

    @Override
    public void onDeleteClick(final Step step) {
        viewModel.deleteStep(step, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        Toast.makeText(getContext(), "Oops looks like there was an issue deleting "+ step.getTitle(), Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(),  step.getTitle() + " Step has been deleted!", Toast.LENGTH_LONG).show();
                        break;
                }
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

            @Override
            public void userResult(User user) {

            }
        });
    }

    public void setSteps(ArrayList steps){

        if(steps != null){
            mStepsContainer.setVisibility(View.VISIBLE);
            mStepsNotFound.setVisibility(View.GONE);
            mStepsList = steps;
            stepsAdapter.setStepsData(mStepsList);
        }else{
            mStepsNotFound.setVisibility(View.VISIBLE);
            mStepsContainer.setVisibility(View.GONE);
        }

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
    public void onMenuEditClick(Step step) {
        switch (step.getStepType()){
            case TEXT:
                Intent intent = new Intent(getContext(), CreateStepTextActivity.class);
                intent.putExtra(STEP_EXTRA, step);
                startActivity(intent);
            case IMAGES:
                break;
            case VIDEO:
                break;
        }
    }

    @Override
    public void onStepItemClick(Step step) {
        switch (step.getStepType()){
            case TEXT:
                Intent intent = new Intent(getContext(), TextStepActivity.class);
                intent.putExtra(STEP_EXTRA, step);
                startActivity(intent);
                break;
            case VIDEO:
                break;
            case IMAGES:
                break;
        }
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
