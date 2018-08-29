package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

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
import nonso.android.nonso.ui.activities.StepDetailsActivity;
import nonso.android.nonso.ui.adapters.StepsArchiveAdapter;
import nonso.android.nonso.viewModel.StepsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyArchiveFragment.OnArchiveInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyArchiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyArchiveFragment extends Fragment implements StepsArchiveAdapter.StepsArchiveOnClickListener {

    @BindView(R.id.steps_archive_not_found_container) LinearLayout mStepsNotFound;
    @BindView(R.id.journey_archive_recycler_view_steps) RecyclerView mRecyclerView;

    private final String JOURNEY_ID_KEY = "journey_pref";
    private final String STEP_EXTRA = "step_extra";


    private String mJourneyId;
    private ArrayList<Step> mSteps = new ArrayList<>();
    private StepsViewModel viewModel;

    private OnArchiveInteractionListener mListener;
    private StepsArchiveAdapter stepsArchiveAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public JourneyArchiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param journeyId Parameter 1.
     * @return A new instance of fragment JourneyArchiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public JourneyArchiveFragment newInstance(String journeyId) {
        JourneyArchiveFragment fragment = new JourneyArchiveFragment();
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
        View view = inflater.inflate(R.layout.fragment_journey_archive, container, false);
        ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        stepsArchiveAdapter = new StepsArchiveAdapter(getContext(), this);
        mRecyclerView.setAdapter(stepsArchiveAdapter);

        viewModel = ViewModelProviders.of(this).get(StepsViewModel.class);
        viewModel.setStepsArchiveList(mJourneyId);
        viewModel.getStepsArchiveListLiveData().observe(this, new Observer<ArrayList<Step>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Step> steps) {
                stepsArchiveAdapter.setStep(steps);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArchiveInteractionListener) {
            mListener = (OnArchiveInteractionListener) context;
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
    public void onStepItemClick(Step step) {
        switch (step.getStepType()){
            case TEXT:
                Intent intent = new Intent(getContext(), StepDetailsActivity.class);
                intent.putExtra(STEP_EXTRA, step);
                startActivity(intent);
                break;
            case VIDEO:
                break;
            case IMAGES:
                break;
        }
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
    public void onMenuPublishClick(Step step) {
        step.setPublish(true);
        viewModel.saveStep(step, new Callback() {
            @Override
            public void userResult(User user) {

            }

            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        Toast.makeText(getContext(), "Oops looks like there was a problems saving this step!", Toast.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(), "Step is live!!", Toast.LENGTH_LONG).show();
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
        });
    }

    @Override
    public void onMenuDeleteClick(final Step step) {
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
    public interface OnArchiveInteractionListener {
        // TODO: Update argument type and name
        void onArchiveInteraction(Uri uri);
    }
}
