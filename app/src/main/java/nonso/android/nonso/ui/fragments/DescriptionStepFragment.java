package nonso.android.nonso.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.utils.MultiSelectionSpinner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DescriptionStepFragment.OnDescriptionStepListener} interface
 * to handle interaction events.
 * Use the {@link DescriptionStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionStepFragment extends Fragment implements Step, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    @BindView(R.id.edit_create_journeys_input_description) EditText mJourneysDescription;
    @BindView(R.id.edit_create_journeys_input_name) EditText mJourneysName;
    @BindView(R.id.create_journey_description_image) ImageView mJourneysImage;
    @BindView(R.id.create_journey_description_spinner) MultiSelectionSpinner mMultiSelectionSpinner;


    private static final String ARG_STEP_POSITION_KEY = "messageResourceId";
    private static final String ARG_JOURNEY = "journey_object";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_CATEGORIES = "categories";
    private static final String DATABASE_DOCUMENT_CATEGORIES = "categories";
    private static final String DATABASE_IMAGE_BUCKET = "images/";

    private static String TAG = DescriptionStepFragment.class.getName();

    private int mStepPosition;
    private Journey mJourney;
    private Map<String, Object> mCategories;
    private String[] mCategoriesList;
    DocumentReference mCategoriesRef;
    StorageReference mJourneyProfileImageRef;
    private StorageReference mStorageRef;

    private OnDescriptionStepListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position of this step.
     * @param journey Journey being created.
     * @return A new instance of fragment DescriptionStepFragment.
     */
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

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCategoriesRef  = db.collection(DATABASE_COLLECTION_CATEGORIES).document(DATABASE_DOCUMENT_CATEGORIES);
        mJourneyProfileImageRef = mStorageRef.child(DATABASE_IMAGE_BUCKET  + "_journey_profile_image"+ ".jpg");

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

    @OnClick(R.id.create_journey_description_image)
    public void onJourneyImageClick(View view){
        startPickImageActivity();
    }

    public void startPickImageActivity(){
        PickImageDialog.build(new PickSetup()
                .setSystemDialog(true)
                .setButtonOrientation(LinearLayoutCompat.HORIZONTAL))
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        Uri uri = r.getUri();
                        startCropImageActivity(uri);
                    }
                })
                .setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        //TODO: Handle cancel: most likely do nothing
                    }
                }).show(getFragmentManager());
    }

    public void startCropImageActivity(Uri uri){
        CropImage.activity(uri)
                .start(getContext(), this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri resultUri = result.getUri();
            uploadToFirebase(resultUri);
            mJourneysImage.setImageURI(resultUri);

        }
    }

    public void uploadToFirebase(Uri uri){
        Uri file = Uri.fromFile(new File(uri.getPath()));
        upLoadImage(file);
    }

    public void upLoadImage(Uri uri){
        mJourneyProfileImageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            //mJourney.set

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: Handle failure
                // ...
            }
        });
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
}
