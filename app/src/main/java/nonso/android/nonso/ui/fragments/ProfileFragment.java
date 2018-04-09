package nonso.android.nonso.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.ui.activities.CreateJourneyActivity;
import nonso.android.nonso.ui.activities.SettingsActivity;


public class ProfileFragment extends Fragment {

    @BindView(R.id.btn_profile_settings) ImageButton mProfileSettings;
    @BindView(R.id.tv_profile_username) TextView mUsernameText;
    @BindView(R.id.fab_profile_create) FloatingActionButton mCreateFab;
    @BindView(R.id.profile_image) ImageView mUserProfileImage;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;

    private static final String STORAGE_IMAGE_BUCKET = "images/";
    private static final String TAG = "ProfileFragment";
    private static final String METADATA_KEY = "userID";

    private Uri mCropImageUri;



    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUsernameText.setText(mUser.getDisplayName());

        if(mUser.getPhotoUrl() != null){
            Picasso.with(getContext()).load(mUser.getPhotoUrl()).into(mUserProfileImage);
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProfileImageRef = mStorageRef.child(STORAGE_IMAGE_BUCKET + mUser.getUid()+ ".jpg");

        setRetainInstance(true);
        return view;
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

    @OnClick(R.id.profile_image)
    public void profileImageOnclick(View view){
        //TODO: Make this a task so when lifecycle events occur this fragment can be recreated with this process still running
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Uri resultUri = result.getUri();
            uploadToFirebase(resultUri);
            mUserProfileImage.setImageURI(resultUri);

        }
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(getContext(), this);
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
                                Log.d("FragmentProfile", "User profile updated.");
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
}
