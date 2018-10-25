package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.os.Bundle;
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
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.LikesListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikesFragment.OnLikesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikesFragment extends Fragment {
    private static final String ARG_LIKES_LIST = "likes_list";
    private ArrayList mLikes;

    @BindView(R.id.likes_list_recyclerView) RecyclerView mRecyclerView;

    private OnLikesFragmentInteractionListener mListener;

    public LikesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param users POST.
     * @return A new instance of fragment LikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public  LikesFragment newInstance(ArrayList users) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIKES_LIST, users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLikes = getArguments().getParcelableArrayList(ARG_LIKES_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

//        LikesListAdapter = new StepsArchiveAdapter(getContext(), this);
//        mRecyclerView.setAdapter(stepsArchiveAdapter);

        return view;
    }


    public void onUserItemPressed(User user) {
        if (mListener != null) {
            mListener.onLikesFragmentUserPressed(user);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLikesFragmentInteractionListener) {
            mListener = (OnLikesFragmentInteractionListener) context;
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

    public interface OnLikesFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLikesFragmentUserPressed(User user);
    }
}
