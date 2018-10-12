package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikesFragment.OnLikesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikesFragment extends Fragment {
    private static final String ARG_POST = "post_param";
    private String mPost;

    private OnLikesFragmentInteractionListener mListener;

    public LikesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post POST.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LikesFragment newInstance(Post post, String param2) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = getArguments().getString(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_likes, container, false);
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
