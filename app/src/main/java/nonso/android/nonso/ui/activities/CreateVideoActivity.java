package nonso.android.nonso.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.Video;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.utils.StringGenerator;
import nonso.android.nonso.viewModel.StepsViewModel;


public class CreateVideoActivity extends AppCompatActivity {

    @BindView(R.id.create_step_video_playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.create_step_video_select_container) LinearLayout mSelectVideo;
    @BindView(R.id.create_step_video_title) TextView mTitle;
    @BindView(R.id.create_step_video_description) TextView mDescription;

    private Step mStep;
    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private static final int REQUEST_VIDEO_CODE = 102;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =202;

    private StepsViewModel viewModel;
    private SimpleExoPlayer mExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_video);

        ButterKnife.bind(this);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewModel = ViewModelProviders.of(this).get(StepsViewModel.class);

        Intent intent = getIntent();
        mStep = intent.getParcelableExtra(STEP_EXTRA_DATA);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_step_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_as_draft:
                save();
                return true;
            case  R.id.action_discard:
                deleteStep();
                return true;
            case R.id.action_publish:
                mStep.setPublish(true);
                save();
                return true;
            case android.R.id.home:
                releasePlayer();
                Intent intent = new Intent(this, JourneyProfileActivity.class);
                intent.putExtra(JOURNEY_EXTRA_ID_KEY, mStep.getCreatedBy().getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save(){

        final Context context = this;

        mExoPlayer.setPlayWhenReady(false);
        mStep.setTitle(mTitle.getText().toString());
        mStep.setDescription(mDescription.getText().toString());
        mStep.setStepType(StepType.VIDEO);

        viewModel.saveStep(mStep, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        Toast.makeText(context, "Oops looks like there was a problems saving this step!", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case SUCCESS:
                        Toast.makeText(context, "Saved!", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void userResult(User user) {

            }
        });
    }

    public void deleteStep(){

        if(mStep.getStepId() != null){

            final Context context = this;
            viewModel.deleteStep(mStep, new Callback() {
                @Override
                public void result(Result result) {
                    switch (result){
                        case FAILED:
                            Toast.makeText(context, "Oops something went wrong!", Toast.LENGTH_LONG).show();
                            finish();
                            break;
                        case SUCCESS:
                            finish();
                            break;
                    }
                }

                @Override
                public void imageResult(Uri downloadUrl) {

                }

                @Override
                public void authorizationResult(FirebaseUser user) {

                }

                @Override
                public void journeyResult(Journey journey) {

                }

                @Override
                public void stepResult(Step step) {

                }

                @Override
                public void userResult(User user) {

                }
            });
        }
        finish();

    }


    @OnClick(R.id.create_step_video_select_container)
    public void onVideoSelectClick(){
        if(checkAndRequestPermissions()){
            startPicker();
        }
    }


    private void startPicker(){
        Matisse.from(this)
                .choose(MimeType.ofVideo())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(1)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_VIDEO_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_CODE){
            String videoUri = Matisse.obtainPathResult(data).get(0);
            initializePlayer(Uri.parse(videoUri));
        }
    }

    public void initializePlayer(Uri mediaUri){
        if(mExoPlayer == null){
            mPlayerView.setVisibility(View.VISIBLE);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            mExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "BakingApp"), defaultBandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);


            //mExoPlayer.seekTo(playerPosition);
            mExoPlayer.prepare(videoSource);
            mExoPlayer.setPlayWhenReady(true);

            Video v = new Video();
            v.setVideoUrl(mediaUri.toString());
            v.setVideoReference(new StringGenerator().getRandomString()+ ".mp4");

            mStep.setVideo(v);
        }
    }

    private boolean checkAndRequestPermissions(){
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

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
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
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

    @Override
    public void onPause() {
        super.onPause();
        //playerPosition = mExoPlayer.getCurrentPosition();
        releasePlayer();
    }

    private void releasePlayer() {
        if(mExoPlayer != null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


}
