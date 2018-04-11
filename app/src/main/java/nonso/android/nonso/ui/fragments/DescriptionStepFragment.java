package nonso.android.nonso.ui.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DescriptionStepFragment.OnDescriptionStepListener} interface
 * to handle interaction events.
 * Use the {@link DescriptionStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionStepFragment extends Fragment implements Step {

    @BindView(R.id.edit_create_journeys_input_description) EditText mJourneysDescription;
    @BindView(R.id.edit_create_journeys_input_name) EditText mJourneysName;
    @BindView(R.id.create_journey_description_profile_image) ImageView mProfileView;
    @BindView(R.id.create_journey_description_banner_image) ImageView mBannerView;

    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";
    private static final int CROP_IMAGE_BANNER_REQUEST_CODE = 111;

    private int mStepPosition;
    private Journey mJourney;

    private OnDescriptionStepListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position of this step.
     * @param journey Journey being created.
     * @return A new instance of fragment DescriptionStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public DescriptionStepFragment newInstance(int position, Journey journey) {
        DescriptionStepFragment fragment = new DescriptionStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_POSITION_KEY, position);
        args.putParcelable(ARG_JOURNEY, journey);
        fragment.setArguments(args);
        return fragment;
    }
    public DescriptionStepFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mStepPosition = getArguments().getInt(ARG_STEP_POSITION_KEY);
            mJourney = getArguments().getParcelable(ARG_JOURNEY);
            if(mJourney == null){
                mJourney = new Journey();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description_step, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.create_journey_description_banner_image)
    public void onBannerImageClicked(View view){
        PickImageDialog.build(new PickSetup()
                .setSystemDialog(true)
                .setButtonOrientation(LinearLayoutCompat.HORIZONTAL))
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        Uri uri = r.getUri();
                        performCrop(uri, CROP_IMAGE_BANNER_REQUEST_CODE);
                    }
                })
                .setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        //TODO: Handle cancel: most likely do nothing
                    }
                }).show(getFragmentManager());
    }

    @OnClick(R.id.create_journey_description_profile_image)
    public void onProfileImageClicked(View view){

    }

    private void performCrop(Uri uri, int code ){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, code);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CROP_IMAGE_BANNER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Toast.makeText(getContext(), "banner crop image result", Toast.LENGTH_SHORT).show();
        }
    }

    @OnTextChanged(value = R.id.edit_create_journeys_input_name,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNameTextChange(Editable editable){

        mJourney.setName(editable.toString());
        if(mListener != null){
            mListener.OnDescriptionStepListener(mJourney);
        }
    }

    @OnTextChanged(value = R.id.edit_create_journeys_input_description,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDescriptionTextChange(Editable editable){
        mJourney.setDescription(editable.toString());
        if(mListener != null){
            mListener.OnDescriptionStepListener(mJourney);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDescriptionStepListener) {
            mListener = (OnDescriptionStepListener) context;
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
    public interface OnDescriptionStepListener {
        // TODO: Update argument type and name
        void OnDescriptionStepListener(Journey journey);
    }
}
