package nonso.android.nonso.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.activities.CreateJourneyActivity;
import nonso.android.nonso.ui.activities.ImageViewActivity;
import nonso.android.nonso.ui.activities.SettingsActivity;
import nonso.android.nonso.ui.adapters.ProfilePagerAdapter;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.viewModel.ProfileViewModel;


public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_collapsing_container) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.btn_profile_settings) ImageButton mProfileSettings;
    @BindView(R.id.fab_profile_create) FloatingActionButton mCreateFab;
    @BindView(R.id.profile_edit_profile_image) FrameLayout mEditProfileImage;
    @BindView(R.id.profile_tabs) TabLayout mTabLayout;
    @BindView(R.id.profile_viewPager) ViewPager mViewPager;
    @BindView(R.id.profile_image) ImageView mUserProfileImage;
    @BindView(R.id.profile_username) TextView mUsername;
    @BindView(R.id.profile_goals) TextView mUserGoals;

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String PROFILE_IMAGE_EXTRA = "profile_image_url";
    private static final String UID_KEY = "user_id";

    private final int PROFILE_IMAGE_REQUEST_CODE = 101;

    private String mUserId;
    private User mUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(UID_KEY, uid);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getString(UID_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        setUp();
        setRetainInstance(true);
        return view;
    }

    private void setUp(){
        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModel.init(mUserId);

        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                updateUI(user);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                int cx = mCreateFab.getWidth()/2;
                int cy = mCreateFab.getHeight()/2;
                float radius = (float)Math.hypot(cx, cy);
                switch (position){
                    case 0:
                        showFab(cx, cy, radius);
                        break;
                    case 1:
                        hideFab(cx, cy, radius);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void updateUI(User user){
        mUser = user;
        mUsername.setText(user.getUserName());
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        //mProfileImageRef = mStorageRef.child(STORAGE_IMAGE_BUCKET + user.getUserId() + "_user_profile_image"+ ".jpg");
        //mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(user.getEmail());

        mViewPager.setAdapter(new ProfilePagerAdapter(getFragmentManager(), getContext()));
        mTabLayout.setupWithViewPager(mViewPager);

        mUserGoals.setText(user.getGoal());

        Picasso.with(getContext()).load(user.getImageUri()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(mUserProfileImage);
    }

    public void showFab(int cx, int cy, float finalRadius){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Animator anim = ViewAnimationUtils.createCircularReveal(mCreateFab, cx, cy, 0, finalRadius);
            mCreateFab.setVisibility(View.VISIBLE);
            anim.setDuration(400);
            anim.start();
        }else{
            mCreateFab.setVisibility(View.VISIBLE);
        }


    }
    public void hideFab(int cx, int cy, float endRadius){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(mCreateFab, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mCreateFab.setVisibility(View.INVISIBLE);
                }
            });

            anim.setDuration(400);
            anim.start();
        }else{
            mCreateFab.setVisibility(View.INVISIBLE);
        }

    }


//    private ListenerRegistration addListenerUserListener(){
//        return mUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//                if(e != null){
//                    Log.w(TAG, "Listen failed,", e);
//                }
//
//                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";
//                if(snapshot != null && snapshot.exists()){
//                    Log.d(TAG, source + " data: " + snapshot.getData());
//                    mUserData = snapshot.toObject(User.class);
//
//                    mUserGoals.setText(mUserData.getGoal());
//                }else{
//
//                }
//            }
//        });
//    }

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
    public void onProfileImageClick(){
        Intent intent = new Intent(getContext(), ImageViewActivity.class);

        if(mUser.getImageUri() != null){
            intent.putExtra(PROFILE_IMAGE_EXTRA, mUser.getImageUri().toString());
        }
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.profile_edit_profile_image)
    public void onEditImageClick(){

        Pix.start(this,
                PROFILE_IMAGE_REQUEST_CODE);
    }

//    @OnClick(R.id.profile_edit_btn)
//    public void onEditGoalsClick(View view){
//
//        Intent intent = new Intent(getContext(), DialogEditGoalsActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PROFILE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
            ArrayList<String> selectionResult = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            String imgUri = selectionResult.get(0);
            setProfileImage(imgUri);

        }
    }

    public void setProfileImage(String imageString){
        Uri imageUri = Uri.parse(imageString);
        mUserProfileImage.setImageURI(imageUri);
        uploadToFirebase(imageUri);
    }

    private void uploadToFirebase(Uri uri){

        Uri file = Uri.fromFile(new File(uri.getPath()));

        //upLoadImage(file);

    }

//    private void upLoadImage(Uri file){
//        mProfileImageRef.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                return mProfileImageRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    updateUserAuth(downloadUri);
//                } else {
//                    Log.w(TAG, "Error updating document");
//                }
//            }
//        });
//    }

//    private void updateUserAuth(final Uri uri){
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//            .setPhotoUri(uri)
//            .build();
//        mUser.updateProfile(profileUpdates)
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        updateUser(uri);
//                    }
//                }
//            });
//    }
//    private void updateUser(Uri uri){
//
//        mUserRef.update("imageUri", uri.toString())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        updateMetaData();
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
//                    }
//                });
//    }

//    private void updateMetaData(){
//
//        StorageMetadata metadata = new StorageMetadata.Builder()
//            .setCustomMetadata(METADATA_KEY, mUser.getUid())
//            .build();
//        mProfileImageRef.updateMetadata(metadata)
//            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                @Override
//                public void onSuccess(StorageMetadata storageMetadata) {
//                    Log.d(TAG, "User profile metadata updated.");
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "Metadata update failed.");
//                }
//            });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
