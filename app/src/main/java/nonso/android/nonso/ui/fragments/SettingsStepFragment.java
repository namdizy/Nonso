package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsStepFragment.OnSettingsStepListener} interface
 * to handle interaction events.
 * Use the {@link SettingsStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsStepFragment extends Fragment implements Step {

    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";

    private String mStepPosition;
    private Journey mJourney;

    private OnSettingsStepListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Current step position.
     * @param journey Parameter 2.
     * @return A new instance of fragment SettingsStepFragment.
     */
    public SettingsStepFragment newInstance(int position, Journey journey) {
        SettingsStepFragment fragment = new SettingsStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_POSITION_KEY, position);
        args.putParcelable(ARG_JOURNEY, journey);
        fragment.setArguments(args);
        return fragment;
    }
    public SettingsStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStepPosition = getArguments().getString(ARG_STEP_POSITION_KEY);
            mJourney = getArguments().getParcelable(ARG_JOURNEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_step, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnSettingsStepListener(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsStepListener) {
            mListener = (OnSettingsStepListener) context;
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

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

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
    public interface OnSettingsStepListener {
        // TODO: Update argument type and name
        void OnSettingsStepListener(Uri uri);
    }
}
