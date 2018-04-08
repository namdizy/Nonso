package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.BitSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import nonso.android.nonso.R;
import nonso.android.nonso.ui.adapters.StepperAdapter;
import nonso.android.nonso.ui.fragments.DescriptionStepFragment;
import nonso.android.nonso.ui.fragments.MediaStepFragment;
import nonso.android.nonso.ui.fragments.SettingsStepFragment;

public class CreateJourneyActivity extends AppCompatActivity implements DescriptionStepFragment.OnDescriptionStepListener,
        MediaStepFragment.OnMediaStepListener, SettingsStepFragment.OnSettingsStepListener, StepperLayout.StepperListener  {

//    @BindView(R.id.btn_create_journeys_back) ImageButton mBack;
//    @BindView(R.id.btn_create_journey_pick_video) Button mPickVideo;
//    @BindView(R.id.create_journey_player_view) SimpleExoPlayerView mPlayerView;


    @BindView(R.id.stepperLayout) StepperLayout mStepperLayout;
    

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    private StorageReference mVideoDescriptionRef;


    private SimpleExoPlayer mExoPlayer;
    private long playerPosition;
    private String mSelectedVideoPath;

    private static final String STORAGE_VIDEO_BUCKET = "videos/";
    private static final String STORAGE_IMAGE_BUCKET = "images/";
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

        mStorageRef = FirebaseStorage.getInstance().getReference();
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
    public void OnDescriptionStepListener(Uri uri) {

    }

    @Override
    public void OnMediaStepListener(Uri uri) {

    }

    @Override
    public void OnSettingsStepListener(Uri uri) {

    }

    @Override
    public void onCompleted(View completeButton) {
        Toast.makeText(this, "onCompleted!", Toast.LENGTH_SHORT).show();
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
