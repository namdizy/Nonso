package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.fxn.pix.Pix;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class CreateStepImageActivity extends AppCompatActivity {

    @BindView(R.id.create_step_image_library) LinearLayout mGalleryContainer;

    private final int GALLERY_REQUEST_CODE = 111;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_image);

        getSupportActionBar().setTitle("Image Step");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ButterKnife.bind(this);

        mContext= this;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_step_text, menu);
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
        Pix.start(this, GALLERY_REQUEST_CODE, 6);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            ArrayList<String> imgs = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
        }
    }
}
