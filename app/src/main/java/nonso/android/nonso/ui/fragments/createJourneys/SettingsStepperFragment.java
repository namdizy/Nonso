package nonso.android.nonso.ui.fragments.createJourneys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsStepperFragment.OnSettingsStepListener} interface
 * to handle interaction events.
 * Use the {@link SettingsStepperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsStepperFragment extends Fragment implements Step {

    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";


    @BindView(R.id.switch_create_journeys_settings_permissions) Switch mPermissionsSwitch;
    @BindView(R.id.switch_create_journeys_settings_subscriptions) Switch mSubscriptionsSwitch;
    @BindView(R.id.switch_create_journeys_settings_mature_content) Switch mMatureContentSwitch;


    private int mStepPosition;
    private Journey mJourney;

    private OnSettingsStepListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Current step position.
     * @param journey Parameter 2.
     * @return A new instance of fragment SettingsStepperFragment.
     */
    public SettingsStepperFragment newInstance(int position, Journey journey) {
        SettingsStepperFragment fragment = new SettingsStepperFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_POSITION_KEY, position);
        args.putParcelable(ARG_JOURNEY, journey);
        fragment.setArguments(args);
        return fragment;
    }
    public SettingsStepperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStepPosition = getArguments().getInt(ARG_STEP_POSITION_KEY);
            mJourney = getArguments().getParcelable(ARG_JOURNEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_journey_settings_step, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnCheckedChanged(R.id.switch_create_journeys_settings_permissions)
    public void onPermissionChange(CompoundButton button, boolean checked){
        if(checked){
            mJourney.setPermissions(true);
        }else{
            mJourney.setPermissions(false);
        }

        mListener.OnSettingsStepListener(mJourney);
    }


    @OnCheckedChanged(R.id.switch_create_journeys_settings_subscriptions)
    public void onSubscriptionChange(CompoundButton button, boolean checked){

        mJourney.setSubscriptions(true);
        mListener.OnSettingsStepListener(mJourney);
    }


    @OnCheckedChanged(R.id.switch_create_journeys_settings_discoverers)
    public void onDiscoverersCheck(CompoundButton compoundButton, boolean checked){
        mJourney.setDisplayFollowers(checked);
        mListener.OnSettingsStepListener(mJourney);
    }

    public void onMatureContentCheck(CompoundButton compoundButton, boolean checked){
        mJourney.setMatureContent(checked);
        mListener.OnSettingsStepListener(mJourney);
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
        void OnSettingsStepListener(Journey journey);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
