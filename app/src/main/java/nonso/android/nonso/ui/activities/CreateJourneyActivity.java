package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.StepperAdapter;
import nonso.android.nonso.ui.fragments.DescriptionStepFragment;
import nonso.android.nonso.ui.fragments.SettingsStepFragment;

public class CreateJourneyActivity extends AppCompatActivity implements DescriptionStepFragment.OnDescriptionStepListener,
        SettingsStepFragment.OnSettingsStepListener, StepperLayout.StepperListener  {

//    @BindView(R.id.btn_create_journeys_back) ImageButton mBack;
//    @BindView(R.id.btn_create_journey_pick_video) Button mPickVideo;
//    @BindView(R.id.create_journey_player_view) SimpleExoPlayerView mPlayerView;


    @BindView(R.id.stepperLayout) StepperLayout mStepperLayout;
    

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    private StorageReference mVideoDescriptionRef;
    private DocumentReference mUserRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private SimpleExoPlayer mExoPlayer;
    private long playerPosition;
    private String mSelectedVideoPath;
    private Journey mJourney;

    private static final String STORAGE_VIDEO_BUCKET = "videos/";
    private static final String STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_JOURNEYS = "journeys";
    private static final String DATABASE_COLLECTION_USERS = "users/";
    private final String TAG = CreateJourneyActivity.this.getClass().getSimpleName();


    private static final int PICK_VIDEO_REQUEST_CODE = 111;
    private static final int STORAGE_REQUEST_CODE = 113;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));
        mStepperLayout.setListener(this);

        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mJourney = new Journey();
    }


    public void backOnClick(View view){

        Intent intent = new Intent(this, MainActivity.class);
        getApplication().startActivity(intent);
    }


    public void pickVideoOnClick(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
            Uri selectedVideoUri = data.getData();
            String userId = mUser.getUid();

//            mVideoDescriptionRef = mStorageRef.child(STORAGE_VIDEO_BUCKET + userId);
            String selectedImagePath = getPath(selectedVideoUri);
            mSelectedVideoPath = selectedImagePath;

            if (Build.VERSION.SDK_INT >= 23) {
                int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                }
                else{
                    initializePlayer(Uri.parse(selectedImagePath));
                }
            }


//            uploadVideo(selectedVideoUri);
        }
    }

    public void initializePlayer(Uri mediaUri){
//
//        Log.v(TAG, "This is the video url: "+ mediaUri);
//
//        if(mExoPlayer == null){
//
//            mPlayerView.setVisibility(View.VISIBLE);
//
//            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mediaUri.getPath(),
//                    MediaStore.Images.Thumbnails.MINI_KIND);
//            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
//
//            mPlayerView.setBackgroundDrawable(bitmapDrawable);
//
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//            TrackSelection.Factory videoTrackSelectionFactory =
//                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
//            TrackSelector trackSelector =
//                    new DefaultTrackSelector(videoTrackSelectionFactory);
//
//            mExoPlayer =
//                    ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//            mPlayerView.setPlayer(mExoPlayer);
//
//            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
//            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                    Util.getUserAgent(this, "BakingApp"), defaultBandwidthMeter);
//            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaUri);
//
//            mExoPlayer.seekTo(playerPosition);
//            mExoPlayer.prepare(videoSource);
//            mExoPlayer.setPlayWhenReady(true);
//        }
    }

    public String getPath(Uri uri) {
        Toast.makeText(this, "In get path url: "+ uri.toString(), Toast.LENGTH_LONG).show();

        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }


    private void uploadVideo(Uri videoUri){
        if(videoUri != null){
            UploadTask uploadTask = mVideoDescriptionRef.putFile(videoUri);

//            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    if(task.isSuccessful())
//                        Toast.makeText(getBaseContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
//                }
//            });
        }else {

        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mExoPlayer != null){
            playerPosition = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if(mExoPlayer == null){
            //initializePlayer(Uri.parse(mStepItem.getVideoURL()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initializePlayer(Uri.parse(mSelectedVideoPath));
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    @Override
    public void OnDescriptionStepListener(Journey journey) {
        if(journey.getName() != null){
            mJourney.setName(journey.getName());
        }else if(journey.getDescription() != null){
            mJourney.setDescription(journey.getDescription());
        }

        Log.v(TAG, "this is a journey: name: " + journey.getName() + "| description: " +journey.getDescription());
    }


    @Override
    public void OnSettingsStepListener(Journey journey) {
        mJourney.setPermissions(journey.isPermissions());
        mJourney.setSubscriptions(journey.isSubscriptions());
        mJourney.setTier1(journey.isTier1());
        mJourney.setTier2(journey.isTier2());
        mJourney.setTier3(journey.isTier3());


        Log.v(TAG, "this is a journey: name: " + journey.getName() + "| description: " +journey.getDescription() +
                "| permissions: " + journey.isPermissions() + "| subscriptions: " +journey.isSubscriptions() +"| Tier 1: " +journey.isTier1());
    }

    @Override
    public void onCompleted(View completeButton) {
        final LinearLayout mProgressbar = (LinearLayout) findViewById(R.id.create_journey_progress_bar_container);
        mProgressbar.setVisibility(View.VISIBLE);

        mJourney.setUserId(mUserRef.getId());
        db.collection(DATABASE_JOURNEYS)
                .add(mJourney)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mProgressbar.setVisibility(View.GONE);
                        String journeyid = documentReference.getId();

                        updateUser(journeyid);

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void updateUser(final String journeyId){
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                ArrayList<String> userJourneys = user.getCreatedJourneys();
                if(userJourneys == null){
                    userJourneys = new ArrayList<>();
                    userJourneys.add(journeyId);
                }else{
                    userJourneys.add(journeyId);
                }

                mUserRef.update("createdJourneys", userJourneys);
            }
        });

    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {

    }

    @Override
    public void onReturn() {
        finish();
    }
}
