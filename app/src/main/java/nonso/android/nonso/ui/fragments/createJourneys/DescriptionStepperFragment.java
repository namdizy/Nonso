package nonso.android.nonso.ui.fragments.createJourneys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.activities.CategoriesActivity;
import nonso.android.nonso.ui.activities.MainActivity;
import nonso.android.nonso.utils.ImageUtils;
import nonso.android.nonso.utils.MultiSelectionSpinner;
import nonso.android.nonso.utils.StringGenerator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DescriptionStepperFragment.OnDescriptionStepListener} interface
 * to handle interaction events.
 * Use the {@link DescriptionStepperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionStepperFragment extends Fragment implements Step, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    @BindView(R.id.edit_create_journeys_input_description) EditText mJourneysDescription;
    @BindView(R.id.edit_create_journeys_input_name) EditText mJourneysName;
    @BindView(R.id.create_journey_description_select_category) Button mSelectCategory;
    @BindView(R.id.create_journey_description_close) ImageButton mCloseButton;
    @BindView(R.id.create_journey_description_profile_image) ImageView mJourneysImage;
    @BindView(R.id.create_journey_description_image_btn) LinearLayout mImageButton;


    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";
    private static final int REQUEST_PROFILE_IMAGE_CODE = 111;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =201;


    private static String TAG = DescriptionStepperFragment.class.getName();

    private int mStepPosition;
    private Journey mJourney;

    private OnDescriptionStepListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position of this step.
     * @param journey Journey being created.
     * @return A new instance of fragment DescriptionStepperFragment.
     */
    public DescriptionStepperFragment newInstance(int position, Journey journey) {
        DescriptionStepperFragment fragment = new DescriptionStepperFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_POSITION_KEY, position);
        args.putParcelable(ARG_JOURNEY, journey);
        fragment.setArguments(args);
        return fragment;
    }
    public DescriptionStepperFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_journey_description_step, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    /**
     * Implements function from MultiSelectionSpinner.OnMultipleItemsSelectedListener
     * @param indices the indexes of selected items
     */
    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    /**
     *  Implements function from MultiSelectionSpinner.OnMultipleItemsSelectedListener
     * @param strings Strings object of selected items
     */

    @Override
    public void selectedStrings(List<String> strings) {

        Map<String, Boolean> map= new HashMap<>();
        for(String st: strings){
            map.put(st, true);
        }
        mJourney.setCategories(map);
        if(mListener != null){
            mListener.OnDescriptionStepListener(mJourney);
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

    @OnClick(R.id.create_journey_description_close)
    public void onCloseClick(View view){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.create_journey_description_image_btn)
    public void onJourneyImageClick(View view){

        if(checkAndRequestPermissions()){
            startPicker();
        }
    }

    @OnClick(R.id.create_journey_description_select_category)
    public void onSelectCategoriesClick(View view){
        Intent intent = new Intent(getContext(), CategoriesActivity.class);
        startActivity(intent);
    }

    public boolean checkAndRequestPermissions(){
        int camera = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        int storage1 = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void startPicker(){
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PROFILE_IMAGE_CODE);
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
        void OnDescriptionStepListener(Journey journey);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PROFILE_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            setProfileImage(Matisse.obtainPathResult(data).get(0));
        }
    }

    public void setProfileImage(String imageString){
        ImageUtils imageUtils = new ImageUtils();

        Bitmap bitmap = imageUtils.decodeFile(imageString);

        mImageButton.setVisibility(View.GONE);
        mJourneysImage.setImageBitmap(bitmap);

        String bitmapString = imageUtils.BitMapToString(bitmap);
        Image image = new Image();
        image.setImageReference("images/" + new StringGenerator().getRandomString() +".jpg");
        image.setImageUrl(bitmapString);
        mJourney.setImage(image);

        mJourneysImage.setVisibility(View.VISIBLE);
        if(mListener != null){
            mListener.OnDescriptionStepListener(mJourney);
        }
    }
}
