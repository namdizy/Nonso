package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.adapters.JourneysAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneysListFragment.OnJourneysListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link JourneysListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneysListFragment extends Fragment implements JourneysAdapter.JourneysAdapterOnClickHandler {

    @BindView(R.id.profile_recycler_view_journeys)
    RecyclerView journeysRecyclerView;

    private final String TAG = JourneysListFragment.class.getName();

    private static final String ARG_JOURNEYS_LIST = "journeys_list";

    private ArrayList<Journey> mJourneysData;

    private OnJourneysListFragmentListener mListener;
    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;

    public JourneysListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param journeysList Journeys data.
     * @return A new instance of fragment JourneysListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public JourneysListFragment newInstance(ArrayList<Journey> journeysList) {
        JourneysListFragment fragment = new JourneysListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_JOURNEYS_LIST, journeysList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJourneysData = getArguments().getParcelableArrayList(ARG_JOURNEYS_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_journeys_list, container, false);
        ButterKnife.bind(this, view);

        Log.v(TAG, "starting list fragment: " +mJourneysData);

        if(mJourneysData != null){
            journeysLayoutManager = new LinearLayoutManager(getContext());
            journeysRecyclerView.setLayoutManager(journeysLayoutManager);
            journeysRecyclerView.setHasFixedSize(true);

            journeysAdapter = new JourneysAdapter(this);
            journeysRecyclerView.setAdapter(journeysAdapter);
            journeysAdapter.setJourneysData(mJourneysData);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnJourneysListFragmentListener) {
            mListener = (OnJourneysListFragmentListener) context;
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
            mListener.onJourneysListInteraction(journey);
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
    public interface OnJourneysListFragmentListener {
        // TODO: Update argument type and name
        void onJourneysListInteraction(Journey journey);
    }
}
