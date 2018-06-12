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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    private FirebaseAuth mAuth;
    private DocumentReference mUserRef;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Journey> mJourneys = new ArrayList<>();
    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";

    public ProfileJourneysListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_journeys_list, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        journeysLayoutManager = new LinearLayoutManager(getContext());
        mJourneysRecyclerView.setLayoutManager(journeysLayoutManager);
        mJourneysRecyclerView.setHasFixedSize(true);

        journeysAdapter = new JourneysAdapter(getContext(), this);
        mJourneysRecyclerView.setAdapter(journeysAdapter);

        getUserJourneys();
        return view;
    }


    private void getUserJourneys(){

        db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("userId", mUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Journey temp = document.toObject(Journey.class);
                        temp.setJourneyId(document.getId());
                        mJourneys.add(temp);
                    }
                    journeysAdapter.setJourneysData(mJourneys);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                }
            });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnProfileJourneysListInteractionListener(uri);
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
    public void onJourneyItemClick(Journey journey) {

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
        void OnProfileJourneysListInteractionListener(Uri uri);
    }
}
