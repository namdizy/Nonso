package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.ui.adapters.ImagesAdapter;

public class CreateStepImageActivity extends AppCompatActivity {

    @BindView(R.id.create_step_image_library) LinearLayout mGalleryContainer;
    @BindView(R.id.create_step_recyclerView_images) RecyclerView mRecyclerView;

    private final String TAG = CreateStepImageActivity.class.getSimpleName();

    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_DATA = "journey_extra";

    private final int GALLERY_REQUEST_CODE = 111;
    private Activity mContext;
    private ImagesAdapter mImagesAdapter;
    StaggeredGridLayoutManager mGridLayoutManager;
    private Step mStep;
    private Journey mJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_image);

        getSupportActionBar().setTitle("Image Step");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mStep = intent.getParcelableExtra(STEP_EXTRA_DATA);
        mJourney = intent.getParcelableExtra(JOURNEY_EXTRA_DATA);

        mContext= this;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_step_image_video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_as_draft:
                return true;
            case  R.id.action_discard:
                return true;
            case R.id.action_publish:
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.create_step_image_library)
    public void onLibraryClick(){

//        GalleryConfig config = new GalleryConfig.Build()
//                .limitPickPhoto(6)
//                .singlePhoto(false)
//                .hintOfPick("Choose Image")
//                .filterMimeTypes(new String[]{"image/*" })
//                .build();
//        GalleryActivity.openActivity(this, GALLERY_REQUEST_CODE, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
//            ArrayList<String> imgs = (ArrayList<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
//
//            Log.v(TAG, "URLS: "+ imgs);
//
//            if(imgs.size() == 1){
//                mGridLayoutManager= new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
//            }else{
//                mGridLayoutManager= new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
//            }
//            mRecyclerView.setLayoutManager(mGridLayoutManager);
//
//            mImagesAdapter = new ImagesAdapter();
//            mRecyclerView.setAdapter(mImagesAdapter);
//
//            mImagesAdapter.setImagesUrls(imgs);
//            mRecyclerView.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
