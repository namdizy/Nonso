package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.ui.activities.CreatePostActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyCommunityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyCommunityFragment extends Fragment {


    @BindView(R.id.journey_profile_community_post) TextView mAddPost;

    private static final String JOURNEY_ID = "journey_id";

    private String mJourneyId;

    private OnFragmentInteractionListener mListener;

    public JourneyCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param journeyId Parameter 1.
     * @return A new instance of fragment JourneyCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JourneyCommunityFragment newInstance(String journeyId) {
        JourneyCommunityFragment fragment = new JourneyCommunityFragment();
        Bundle args = new Bundle();
        args.putString(JOURNEY_ID, journeyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJourneyId = getArguments().getString(JOURNEY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journey_community, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @OnClick(R.id.journey_profile_community_post)
    public void onCreatePostClick(View view){
        Intent intent = new Intent(getContext(), CreatePostActivity.class );
        intent.putExtra(JOURNEY_ID, mJourneyId);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
