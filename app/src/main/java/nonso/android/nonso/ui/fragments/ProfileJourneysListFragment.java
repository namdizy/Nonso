package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.adapters.JourneysAdapter;
import nonso.android.nonso.viewModel.JourneyViewModel;
import nonso.android.nonso.viewModel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileJourneysListFragment.OnProfileJourneysListInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileJourneysListFragment extends Fragment implements JourneysAdapter.JourneysAdapterOnClickHandler{

    @BindView(R.id.profile_recycler_view_journeys) RecyclerView mJourneysRecyclerView;

    private final String TAG = ProfileJourneysListFragment.class.getSimpleName();
    private OnProfileJourneysListInteractionListener mListener;

    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;
    private String mUserId;
    private static final String UID_KEY = "user_id";

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

        JourneyViewModel viewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);
        viewModel.setJourneysList(mUserId);
        viewModel.getJourneyListLiveData().observe(this, new Observer<ArrayList<Journey>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Journey> journeys) {
                updateUI(journeys);
            }
        });
        return view;
    }

    private void updateUI(ArrayList<Journey> journeys){
        journeysLayoutManager = new LinearLayoutManager(getContext());
        mJourneysRecyclerView.setLayoutManager(journeysLayoutManager);
        mJourneysRecyclerView.setHasFixedSize(true);

        journeysAdapter = new JourneysAdapter(getContext(), this);
        mJourneysRecyclerView.setAdapter(journeysAdapter);

        journeysAdapter.setJourneysData(journeys);
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
    public void onJourneyItemClick(Journey journey) {
        if (mListener != null) {
            mListener.journeysListInteractionListener(journey);
        }
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
        void journeysListInteractionListener(Journey journey);
    }

}
