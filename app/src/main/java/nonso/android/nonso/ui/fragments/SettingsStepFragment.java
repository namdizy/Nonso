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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
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


    @BindView(R.id.switch_create_journeys_settings_permissions) Switch mPermissionsSwitch;
    @BindView(R.id.switch_create_journeys_settings_subscriptions) Switch mSubscriptionsSwitch;
    @BindView(R.id.cbx_create_journey_subscription_tier1) CheckBox mTier1Cbx;
    @BindView(R.id.cbx_create_journey_subscription_tier2) CheckBox mTier2Cbx;
    @BindView(R.id.cbx_create_journey_subscription_tier3) CheckBox mTier3Cbx;
    @BindView(R.id.tv_create_journeys_description_tier1_title) TextView mTier1TitleTV;
    @BindView(R.id.tv_create_journeys_description_tier2_title) TextView mTier2TitleTv;
    @BindView(R.id.tv_create_journeys_description_tier3_title) TextView mTier3TitleTV;


    private int mStepPosition;
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
            mStepPosition = getArguments().getInt(ARG_STEP_POSITION_KEY);
            mJourney = getArguments().getParcelable(ARG_JOURNEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_step, container, false);

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
        if(checked){
            mJourney.setSubscriptions(true);
            mTier1Cbx.setVisibility(View.VISIBLE);
            mTier2Cbx.setVisibility(View.VISIBLE);
            mTier3Cbx.setVisibility(View.VISIBLE);
            mTier1TitleTV.setVisibility(View.VISIBLE);
            mTier2TitleTv.setVisibility(View.VISIBLE);
            mTier3TitleTV.setVisibility(View.VISIBLE);
        }else{
            mJourney.setSubscriptions(true);

            mJourney.setTier3(false);
            mJourney.setTier1(false);
            mJourney.setTier2(false);

            mTier1Cbx.setChecked(false);
            mTier2Cbx.setChecked(false);
            mTier3Cbx.setChecked(false);

            mTier1Cbx.setVisibility(View.GONE);
            mTier2Cbx.setVisibility(View.GONE);
            mTier3Cbx.setVisibility(View.GONE);
            mTier1TitleTV.setVisibility(View.GONE);
            mTier2TitleTv.setVisibility(View.GONE);
            mTier3TitleTV.setVisibility(View.GONE);
        }

        mListener.OnSettingsStepListener(mJourney);
    }

    @OnCheckedChanged({R.id.cbx_create_journey_subscription_tier1, R.id.cbx_create_journey_subscription_tier2,
                        R.id.cbx_create_journey_subscription_tier3})
    public void onTierCheckChanged(CompoundButton button, boolean checked){
        switch (button.getId()){
            case R.id.cbx_create_journey_subscription_tier1:
                mJourney.setTier1(checked);
            case R.id.cbx_create_journey_subscription_tier2:
                mJourney.setTier2(checked);
            case R.id.cbx_create_journey_subscription_tier3:
                mJourney.setTier3(checked);
        }
        mListener.OnSettingsStepListener(mJourney);
    }

    @OnCheckedChanged(R.id.switch_create_journeys_settings_discoverers)
    public void onDiscoverersCheck(CompoundButton compoundButton, boolean checked){
        mJourney.setDisplayFollowers(checked);
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
