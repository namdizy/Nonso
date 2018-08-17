package nonso.android.nonso.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.ui.adapters.ImagesAdapter;

public class CreateStepImageActivity extends AppCompatActivity {

    @BindView(R.id.create_step_image_library) LinearLayout mGalleryContainer;
    @BindView(R.id.create_step_recyclerView_images) RecyclerView mRecyclerView;

    private final String TAG = CreateStepImageActivity.class.getSimpleName();

    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private static final int REQUEST_IMAGE_CODE = 102;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =202;

    private ImagesAdapter mImagesAdapter;
    StaggeredGridLayoutManager mGridLayoutManager;
    private Step mStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_image);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

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
                return true;
            case  R.id.action_discard:
                return true;
            case R.id.action_publish:
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, JourneyProfileActivity.class);
                intent.putExtra(JOURNEY_EXTRA_ID_KEY, mStep.getCreatedBy().getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.create_step_image_library)
    public void onLibraryClick(){

        if(checkAndRequestPermissions()){
            startPicker();
        }
    }
    private void startPicker(){
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .maxSelectable(4)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_IMAGE_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CODE){
            List<String> imgs = Matisse.obtainPathResult(data);

            if(imgs.size() == 1){
                mGridLayoutManager= new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL);
            }else{
                mGridLayoutManager= new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
            }
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mImagesAdapter = new ImagesAdapter();
            mRecyclerView.setAdapter(mImagesAdapter);
            mImagesAdapter.setImagesUrls((ArrayList) imgs);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
