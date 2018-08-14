package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.JourneysAdapter;
import nonso.android.nonso.viewModel.JourneyViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileJourneysListFragment.OnProfileJourneysListInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileJourneysListFragment extends Fragment implements JourneysAdapter.JourneysAdapterOnClickHandler{

    @BindView(R.id.profile_recycler_view_journeys) RecyclerView mJourneysRecyclerView;
    @BindView(R.id.profile_journeys_not_found_container)
    LinearLayout mJourneysNotFound;

    private final String TAG = ProfileJourneysListFragment.class.getSimpleName();
    private OnProfileJourneysListInteractionListener mListener;

    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;
    private String mUserId;
    private static final String UID_KEY = "user_id";
    private JourneyViewModel mViewModel;

    public ProfileJourneysListFragment() { }

    public ProfileJourneysListFragment newInstance(String uid) {
        ProfileJourneysListFragment fragment = new ProfileJourneysListFragment();
        Bundle args = new Bundle();
        args.putString(UID_KEY, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getString(UID_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile_journeys_list, container, false);
        ButterKnife.bind(this, view);

        mViewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);
        mViewModel.setJourneysList(mUserId);
        mViewModel.getJourneyListLiveData().observe(this, new Observer<ArrayList<Journey>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Journey> journeys) {

            }
        });
        return view;
    }

    private void updateUI(ArrayList<Journey> journeys){

        if(journeys != null && journeys.size() > 0){

            mJourneysNotFound.setVisibility(View.GONE);
            journeysLayoutManager = new LinearLayoutManager(getContext());
            mJourneysRecyclerView.setLayoutManager(journeysLayoutManager);
            mJourneysRecyclerView.setHasFixedSize(true);

            journeysAdapter = new JourneysAdapter(getContext(), this);
            mJourneysRecyclerView.setAdapter(journeysAdapter);

            journeysAdapter.setJourneysData(journeys);
        } else{
            mJourneysNotFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileJourneysListInteractionListener) {
            mListener = (OnProfileJourneysListInteractionListener) context;
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
    public void onJourneyItemClick(String journeyId) {
        if (mListener != null) {
            mListener.onProfileJourneysListItemInteractionListener(journeyId);
        }
    }

    @Override
    public void onMenuDeleteClick(final Journey journey) {
        mViewModel.deleteJourney(journey,  new Callback(){
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

            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        Toast.makeText(getContext(), journey.getName() + " deleted.", Toast.LENGTH_LONG).show();
                        break;
                    case FAILED:
                        Toast.makeText(getContext(), "Looks like there was a problem deleting " + journey.getName(), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }
        });
    }

    @Override
    public void onMenuSettingClick(Journey journey) {

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
    public interface OnProfileJourneysListInteractionListener {
        // TODO: Update argument type and name
        void onProfileJourneysListItemInteractionListener(String journeyId );
    }

}
