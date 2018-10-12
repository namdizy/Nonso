package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.nonso.summernote.Summernote;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;
import nonso.android.nonso.models.User;
import nonso.android.nonso.viewModel.StepsViewModel;

public class CreateStepTextActivity extends AppCompatActivity {

    @BindView(R.id.create_step_text_title) EditText mStepTitle;
    @BindView(R.id.create_step_text_description) EditText mStepDescription;
    @BindView(R.id.create_step_summernote)
    Summernote mSummernote;


    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private final String STEP_EXTRA_DATA = "step_extra";

    private StepsViewModel viewModel;

    //private KRichEditorFragment editorFragment;

    private Step mStep;
    private final  String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_step_text);

        ButterKnife.bind(this);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        mStep = intent.getParcelableExtra(STEP_EXTRA_DATA);

        viewModel = ViewModelProviders.of(this).get(StepsViewModel.class);
        if (mStep.getDescription() != null){
            mStepDescription.setText(mStep.getDescription());
        }
        if(mStep.getTitle() != null){
            mStepTitle.setText(mStep.getTitle());
        }

        if(mStep.getBodyText() != null){
            mSummernote.setText(mStep.getBodyText());
        }
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
                delete();
                return true;
            case R.id.action_publish:
                mStep.setPublish(true);
                save();
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
    public void delete(){
        final Context context = this;

        viewModel.deleteStep(mStep, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        finish();
                        break;
                    case FAILED:
                        Toast.makeText(context, "Oops looks like something went wrong", Toast.LENGTH_LONG).show();
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

    public void save(){
        final Context context = this;

        String htmlText = mSummernote.getText();
        String description = mStepDescription.getText().toString();
        String title = mStepTitle.getText().toString();

        if(htmlText.isEmpty()){
           Toast.makeText(this, "Please Enter Some content!", Toast.LENGTH_LONG).show();
           return;
        }
        if(description.isEmpty()){
            Toast.makeText(this, "Please enter a description!", Toast.LENGTH_LONG).show();
            return;
        }

        if(title.isEmpty()){
            Toast.makeText(this, "Please enter a title!", Toast.LENGTH_LONG).show();
            return;
        }
        mStep.setTitle(title);
        mStep.setDescription(description);
        mStep.setBodyText(htmlText);
        mStep.setStepType(StepType.TEXT);

        viewModel.saveStep(mStep, new Callback() {
            @Override
            public void userResult(User user) {

            }

            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        Toast.makeText(context, "Oops looks like there was a problems saving this step!", Toast.LENGTH_LONG).show();
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
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, JourneyProfileActivity.class);
        intent.putExtra(JOURNEY_EXTRA_ID_KEY, mStep.getCreatedBy().getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
