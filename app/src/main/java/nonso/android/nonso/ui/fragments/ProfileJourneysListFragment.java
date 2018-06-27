package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.JourneysAdapter;

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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration registration;

    private ArrayList<Journey> mJourneys = new ArrayList<>();
    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;

    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";
    private static final String JOURNEYS_SAVED_STATE = "journeys_saved_state";
    public static final String ARG_USER_KEY = "user_data";
    public static final String USER_ID_STATE = "user_id_state";

    private User mUser;


    public DescriptionStepperFragment newInstance(User user) {
        DescriptionStepperFragment fragment = new DescriptionStepperFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_KEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileJourneysListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_journeys_list, container, false);
        ButterKnife.bind(this, view);


        journeysLayoutManager = new LinearLayoutManager(getContext());
        mJourneysRecyclerView.setLayoutManager(journeysLayoutManager);
        mJourneysRecyclerView.setHasFixedSize(true);

        journeysAdapter = new JourneysAdapter(getContext(), this);
        mJourneysRecyclerView.setAdapter(journeysAdapter);

        if(savedInstanceState != null){
            mJourneys =  savedInstanceState.getParcelableArrayList(JOURNEYS_SAVED_STATE);
            mUser = savedInstanceState.getParcelable(USER_ID_STATE);
            journeysAdapter.setJourneysData(mJourneys);
        }else{
            getUserJourneys();
        }

        return view;
    }


    private void getUserJourneys(){
         registration = db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("userId",mUser.getUserId())
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if(e !=null){
                        Log.w(TAG, "Error getting Journeys, ", e );
                    }

                    for(DocumentChange change: queryDocumentSnapshots.getDocumentChanges()){
                        if(change.getType() == DocumentChange.Type.ADDED){
                            Journey temp = change.getDocument().toObject(Journey.class);
                            temp.setJourneyId(change.getDocument().getId());
                            mJourneys.add(temp);
                        }
                    }
                    journeysAdapter.setJourneysData(mJourneys);
                }
            });
    }

    private ListenerRegistration journeyChangeListener(){


        return null;
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
        registration.remove();
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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(JOURNEYS_SAVED_STATE, mJourneys);
        outState.putParcelable(USER_ID_STATE, mUser);
    }
}
