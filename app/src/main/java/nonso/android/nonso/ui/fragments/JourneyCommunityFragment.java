package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.ui.activities.CreatePostActivity;
import nonso.android.nonso.ui.activities.CreatePostReplyActivity;
import nonso.android.nonso.ui.activities.PostDetailsActivity;
import nonso.android.nonso.ui.adapters.PostAdapter;
import nonso.android.nonso.viewModel.PostViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyCommunityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyCommunityFragment extends Fragment implements PostAdapter.PostAdapterOnclickHandler {


    @BindView(R.id.journey_profile_community_post) TextView mAddPost;
    @BindView(R.id.journey_profile_community_post_recyclerview) RecyclerView mRecyclerView;

    private static final String JOURNEY_ID = "journey_id";
    private static final String POST_ID = "post_id";
    private String PARENT_POST = "parent_post";

    private String mJourneyId;

    private PostAdapter mPostAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PostViewModel viewModel;

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

        viewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        viewModel.setPostList(mJourneyId);
        viewModel.getPost().observe(this, this::updateUI);

        return view;
    }


    public void updateUI(ArrayList<Post> posts){

        if(posts != null && posts.size() > 0){
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);

            mPostAdapter = new PostAdapter(getContext(), this);
            mRecyclerView.setAdapter(mPostAdapter);
            mPostAdapter.setPostList(posts);
        }else{

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
    public void onReplyClick(Post post) {

        Intent intent = new Intent(getContext(), CreatePostReplyActivity.class);
        intent.putExtra(PARENT_POST, post);
        startActivity(intent);
    }

    @Override
    public void onCommentClick(Post post) {
        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        intent.putExtra(PARENT_POST, post);
        startActivity(intent);
    }

    @Override
    public void onMenuDeleteClick(Post post) {

    }

    @Override
    public void onMenuEditClick(Post post) {

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
