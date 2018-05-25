package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.activities.MainActivity;
import nonso.android.nonso.utils.MultiSelectionSpinner;

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
    @BindView(R.id.create_journey_description_spinner) MultiSelectionSpinner mMultiSelectionSpinner;
    @BindView(R.id.create_journey_description_close) ImageButton mCloseButton;
    @BindView(R.id.create_journey_description_profile_image) ImageView mJourneysImage;
    @BindView(R.id.create_journey_description_image_btn) LinearLayout mImageButton;


    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";
    private static final int GALLERY_REQUEST_CODE = 111;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_CATEGORIES = "categories";
    private static final String DATABASE_DOCUMENT_CATEGORIES = "categories";
    private static final int PERMISSIONS_REQUEST_STORAGE = 13;

    private static String TAG = DescriptionStepperFragment.class.getName();

    private int mStepPosition;
    private Journey mJourney;
    private Map<String, Object> mCategories;
    private String[] mCategoriesList;
    DocumentReference mCategoriesRef;

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
        View view = inflater.inflate(R.layout.fragment_description_step, container, false);
        ButterKnife.bind(this, view);

        mCategoriesRef  = db.collection(DATABASE_COLLECTION_CATEGORIES).document(DATABASE_DOCUMENT_CATEGORIES);
        getCategories();
        mMultiSelectionSpinner.setListener(this);
        return view;
    }

    public void getCategories(){
        mCategoriesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mCategories =  documentSnapshot.getData();
                mCategoriesList = mCategories.keySet().toArray(new String[mCategories.keySet().size()]);
                mMultiSelectionSpinner.setItems(mCategoriesList);
            }
        });

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

        Log.v(TAG, "the strings: " + strings);

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

        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .hintOfPick("Choose Image")
                .filterMimeTypes(new String[]{"image/*" })
                .build();
        GalleryActivity.openActivity(getActivity(), GALLERY_REQUEST_CODE, config);
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

    public void setProfileImage(String imageString){
        //startCropImageActivity(Uri.parse(imageString));

        mImageButton.setVisibility(View.GONE);

        //Uri resultUri = result.getUri();
        mJourneysImage.setImageURI(Uri.parse(imageString));
        //mJourney.setProfileImage(Ur);
        mJourneysImage.setVisibility(View.VISIBLE);
    }
}
