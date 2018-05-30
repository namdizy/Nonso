package nonso.android.nonso.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.activities.CreateJourneyActivity;
import nonso.android.nonso.ui.activities.SettingsActivity;


public class ProfileFragment extends Fragment {

    @BindView(R.id.btn_profile_settings) ImageButton mProfileSettings;
    @BindView(R.id.fab_profile_create) FloatingActionButton mCreateFab;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    private DocumentReference mUserRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String STORAGE_IMAGE_BUCKET = "images/";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String METADATA_KEY = "creator_id";
    private static final String DATABASE_COLLECTION_USERS = "users/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";

    private ListenerRegistration registration;
    private ArrayList<Journey> mJourneys;
    private JourneysListFragment mJourneyListFragment;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProfileImageRef = mStorageRef.child(STORAGE_IMAGE_BUCKET + mUser.getUid() + "_user_profile_image"+ ".jpg");


        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());
        mJourneys = new ArrayList<>();

        Log.w(TAG, "OnCreateView called: called!");
        getUserJourneys();

        registration = addListenerUserListener();

        //setRetainInstance(true);
        return view;
    }

    private ListenerRegistration addListenerUserListener(){
        return mUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                if(e != null){
                    Log.w(TAG, "Listen failed,", e);
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if(snapshot != null && snapshot.exists()){
                    Log.d(TAG, source + " data: " + snapshot.getData());

                }else{

                }
            }
        });
    }


    private void getUserJourneys(){

        db.collection(DATABASE_COLLECTION_JOURNEYS).whereEqualTo("userId", mUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Journey temp = document.toObject(Journey.class);
                        temp.setJourneyId(document.getId());
                        mJourneys.add(temp);
                    }

                    if (!isAdded()) return;
                    mJourneyListFragment = new JourneysListFragment().newInstance(mJourneys);
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.profile_journeys_container, mJourneyListFragment).commitAllowingStateLoss();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                }
            });
    }

    @OnClick(R.id.btn_profile_settings)
    public void onSettingsClick(View view){

        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fab_profile_create)
    public void fabOnClick(View view){

        Intent intent = new Intent(getContext(), CreateJourneyActivity.class);
        getActivity().startActivity(intent);

    }

    public void startPickImageActivity(){
//        PickImageDialog.build(new PickSetup()
//                .setSystemDialog(true)
//                .setButtonOrientation(LinearLayoutCompat.HORIZONTAL))
//                .setOnPickResult(new IPickResult() {
//                    @Override
//                    public void onPickResult(PickResult r) {
//                        Uri uri = r.getUri();
//                        startCropImageActivity(uri);
//                    }
//                })
//                .setOnPickCancel(new IPickCancel() {
//                    @Override
//                    public void onCancelClick() {
//                        //TODO: Handle cancel: most likely do nothing
//                    }
//                }).show(getFragmentManager());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            Uri resultUri = result.getUri();
//            uploadToFirebase(resultUri);
//            //mUserProfileImage.setImageURI(resultUri);
//
//        }
    }


    private void startCropImageActivity(Uri imageUri) {
//        CropImage.activity(imageUri)
//                .start(getContext(), this);
    }

    private void uploadToFirebase(Uri uri){

        Uri file = Uri.fromFile(new File(uri.getPath()));

        upLoadImage(file);

    }

    private void upLoadImage(Uri file){
        mProfileImageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUrl)
                    .build();
                mUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateMetaData();
                            }
                        }
                    });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: Handle failure
                // ...
            }
        });
    }

    private void updateUser(Uri uri){

        mUserRef.update("imageUri", uri.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void updateMetaData(){

        StorageMetadata metadata = new StorageMetadata.Builder()
            .setCustomMetadata(METADATA_KEY, mUser.getUid())
            .build();
        mProfileImageRef.updateMetadata(metadata)
            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    Log.d(TAG, "User profile metadata updated.");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Metadata update failed.");
                }
            });
    }

    @Override
    public void onDestroy() {

        //registration.remove();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
