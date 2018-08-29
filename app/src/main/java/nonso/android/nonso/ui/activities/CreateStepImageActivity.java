package nonso.android.nonso.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseUser;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.ui.adapters.ImagesAdapter;
import nonso.android.nonso.utils.ImageUtils;
import nonso.android.nonso.utils.StringGenerator;
import nonso.android.nonso.viewModel.StepsViewModel;

public class CreateStepImageActivity extends AppCompatActivity {

    @BindView(R.id.create_step_image_select_container) LinearLayout mGalleryContainer;
    @BindView(R.id.create_step_recyclerView_images) RecyclerView mRecyclerView;
    @BindView(R.id.create_step_image_progress_bar_container) LinearLayout mProgressbar;
    @BindView(R.id.create_step_image_title) EditText mStepTitle;
    @BindView(R.id.create_step_image_description) EditText mStepDescription;

    private final String TAG = CreateStepImageActivity.class.getSimpleName();

    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private static final int REQUEST_IMAGE_CODE = 102;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS =202;

    private ImagesAdapter mImagesAdapter;
    GridLayoutManager mGridLayoutManager;
    private Step mStep;
    private StepsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_image);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewModel = ViewModelProviders.of(this).get(StepsViewModel.class);

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
                save();
                return true;
            case  R.id.action_discard:
                deleteStep();
                return true;
            case R.id.action_publish:
                mStep.setPublish(true);
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


    private void save(){
        mProgressbar.setVisibility(View.VISIBLE);
        mStep.setStepType(StepType.IMAGES);
        mStep.setTitle(mStepTitle.getText().toString());
        mStep.setDescription(mStepDescription.getText().toString());
        Context context = this;
        viewModel.saveStep(mStep, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        mProgressbar.setVisibility(View.GONE);
                        Toast.makeText(context, "Oops something went wrong saving!", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case SUCCESS:
                        mProgressbar.setVisibility(View.GONE);
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
    private void deleteStep(){

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

    @OnClick(R.id.create_step_image_select_container)
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
                mGridLayoutManager= new GridLayoutManager(this, 1);
            }else{
                mGridLayoutManager= new GridLayoutManager(this, 2);
            }
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mImagesAdapter = new ImagesAdapter();
            mRecyclerView.setAdapter(mImagesAdapter);

            ArrayList<Bitmap> bitmap = new ArrayList<>();

            for(String i: imgs){
                Bitmap b = new ImageUtils().decodeFile(i);
                bitmap.add(b);
            }

            Map<String, Image> imageMap = new HashMap<>();
            int count = 0;
            for(Bitmap b: bitmap){

                Image im = new Image();
                im.setImageUrl(new ImageUtils().BitMapToString(b));
                im.setImageReference("images/"+new StringGenerator().getRandomString() + ".png");

                imageMap.put(String.valueOf(count), im);
                count++;
            }

            mStep.setImages(imageMap);
            mImagesAdapter.setImagesUrls(bitmap);
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
