package nonso.android.nonso.ui.fragments;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private FirebaseStorage mStorage;

    private Uri mCropImageUri;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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


        return view;
    }


    public BottomSheetBehavior.BottomSheetCallback bottomSheetCallback(){
        return new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        };
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
        CropImage.startPickImageActivity(getContext(), this);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

            if(CropImage.isReadExternalStoragePermissionsRequired(getContext(), imageUri)){
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE );
            }else{
                Log.v("Profile Fragment pick", "imageurl"+ imageUri);
                startCropImageActivity(mCropImageUri);
            }

        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK ){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                mUserProfileImage.setImageURI(resultUri);

                String userPicture = mUser.getDisplayName()+".jpg";

                // Create a storage reference from our app
                StorageReference storageRef = mStorage.getReference();

                // Create a reference to profile image
                StorageReference mountainsRef = storageRef.child(userPicture);

                // Create a reference to 'images/profile image.jpg'
                StorageReference mountainImagesRef = storageRef.child("images/"+userPicture);

                // While the file names are the same, the references point to different files
                mountainsRef.getName().equals(mountainImagesRef.getName());    // true
                mountainsRef.getPath().equals(mountainImagesRef.getPath());
                try{
                    InputStream stream = new FileInputStream(new File(resultUri.toString()));

                    UploadTask uploadTask = mountainsRef.putStream(stream);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(downloadUrl.toString()))
                                    .build();

                            mUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("FRAGMENT PROFILE", "User profile updated.");
                                            }
                                        }
                                    });
                        }
                    });

                }catch (FileNotFoundException e){

                }




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(getContext(), this);
    }
}
