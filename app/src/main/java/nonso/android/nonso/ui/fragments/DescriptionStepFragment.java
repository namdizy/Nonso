package nonso.android.nonso.ui.fragments;

import android.content.Context;
import android.nfc.Tag;
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
import android.widget.ImageView;

import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

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

    private static String TAG = DescriptionStepFragment.class.getName();

    private int mStepPosition;
    private Journey mJourney;
    private Map<String, Object> mCategories;
    private String[] mCategoriesList;
    private Context mContext;
    DocumentReference mCategoriesRef;

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

        mCategoriesRef  = db.collection(DATABASE_COLLECTION_CATEGORIES).document(DATABASE_DOCUMENT_CATEGORIES);

        getCategories();
        mMultiSelectionSpinner.setListener(this);
        return view;
    }

    public void getCategories(){

        Log.v(TAG, "===========  getCategories: ===============");

        mCategoriesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mCategories =  documentSnapshot.getData();
                Log.v(TAG, "===========  retrieved categories: ===============");
                Log.v(TAG, "retrieved categories: " + documentSnapshot.getData());

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
