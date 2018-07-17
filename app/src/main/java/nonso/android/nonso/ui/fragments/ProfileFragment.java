package nonso.android.nonso.ui.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.activities.CreateJourneyActivity;
import nonso.android.nonso.ui.activities.DialogEditGoalsActivity;
import nonso.android.nonso.ui.activities.ImageViewActivity;
import nonso.android.nonso.ui.activities.SettingsActivity;
import nonso.android.nonso.ui.adapters.ProfilePagerAdapter;
import nonso.android.nonso.utils.ImageUtils;
import nonso.android.nonso.viewModel.UserViewModel;


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

    private static final int REQUEST_PROFILE_IMAGE_CODE = 101;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =201;
    private static final String EXTRA_CREATOR = "user";

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
        UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(mUserId);

        viewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                updateUI(user);
            }
        });

        mViewPager.setAdapter(new ProfilePagerAdapter(getFragmentManager(), mUserId));
        mTabLayout.setupWithViewPager(mViewPager);

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

    @OnClick(R.id.btn_profile_settings)
    public void onSettingsClick(View view){

        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fab_profile_create)
    public void fabOnClick(View view){

        Intent intent = new Intent(getContext(), CreateJourneyActivity.class);
        intent.putExtra(EXTRA_CREATOR, mUser);
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

        if(checkAndRequestPermissions()){
            startPicker();
        }

    }

    private void startPicker(){
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_PROFILE_IMAGE_CODE);
    }

    private boolean checkAndRequestPermissions(){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startPicker();
            }
        }
    }

        @OnClick(R.id.profile_edit_btn)
    public void onEditGoalsClick(View view){

        Intent intent = new Intent(getContext(), DialogEditGoalsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PROFILE_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            setProfileImage(Matisse.obtainPathResult(data).get(0));
        }
    }

    public void setProfileImage(String imagePath){
        ImageUtils imageUtils = new ImageUtils();

        Bitmap bitmap = imageUtils.decodeFile(imagePath);

        mUserProfileImage.setImageBitmap(bitmap);
        uploadToFirebase(Uri.parse(imagePath));
    }

    private void uploadToFirebase(Uri uri){

        Uri file = Uri.fromFile(new File(uri.getPath()));


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
