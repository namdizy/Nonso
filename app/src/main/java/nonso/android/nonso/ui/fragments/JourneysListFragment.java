package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
 * {@link JourneysListFragment.OnJourneysListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link JourneysListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneysListFragment extends Fragment implements JourneysAdapter.JourneysAdapterOnClickHandler {

    private FirebaseAuth mAuth;
    private StorageReference mProfileImageRef;
    private DocumentReference mUserRef;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @BindView(R.id.profile_recycler_view_journeys) RecyclerView journeysRecyclerView;
    @BindView(R.id.tv_profile_username) TextView mUsernameText;
    @BindView(R.id.profile_image) ImageView mUserProfileImage;

    private final String TAG = JourneysListFragment.class.getName();

    private static final String ARG_JOURNEYS_LIST = "journeys_list";
    private static final String DATABASE_COLLECTION_USERS = "users/";

    private ArrayList<Journey> mJourneysData;
    private OnJourneysListFragmentListener mListener;
    private JourneysAdapter journeysAdapter;
    private RecyclerView.LayoutManager journeysLayoutManager;
    private User mUserData;

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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());

        if(mJourneysData != null ){
            journeysLayoutManager = new LinearLayoutManager(getContext());
            journeysRecyclerView.setLayoutManager(journeysLayoutManager);
            journeysRecyclerView.setHasFixedSize(true);

            journeysAdapter = new JourneysAdapter(getContext(), this);
            journeysRecyclerView.setAdapter(journeysAdapter);
            journeysAdapter.setJourneysData(mJourneysData);

            mUsernameText.setText(mUser.getDisplayName());

            Picasso.with(getContext()).load(mUser.getPhotoUrl()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(mUserProfileImage);
        }

        return view;
    }

    private void getUserData(){
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mUserData = documentSnapshot.toObject(User.class);
            }
        });

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

    public interface OnJourneysListFragmentListener {
        void onJourneysListInteraction(Journey journey);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
