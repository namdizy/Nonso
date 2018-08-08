package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ebolo.krichtexteditor.RichEditor;
import com.ebolo.krichtexteditor.fragments.KRichEditorFragment;
import com.ebolo.krichtexteditor.fragments.Options;
import com.ebolo.krichtexteditor.ui.widgets.EditorButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;
import nonso.android.nonso.viewModel.StepsViewModel;

public class CreateStepTextActivity extends AppCompatActivity {

    @BindView(R.id.create_step_text_title)
    EditText mStepTitle;
    @BindView(R.id.create_step_text_description) EditText mStepDescription;


    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private final String STEP_EXTRA_DATA = "step_extra";

    private StepsViewModel viewModel;

    private KRichEditorFragment editorFragment;

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


        editorFragment = (KRichEditorFragment) getSupportFragmentManager().findFragmentByTag("EDITOR");

        if (editorFragment == null)
            editorFragment = KRichEditorFragment.getInstance(
                    new Options()
                            .placeHolder("Write something cool...")
                            .onImageButtonClicked(new Runnable() {
                                @Override
                                public void run() {
                                    //ImagePicker.create(JavaActivity.this).start();
                                }
                            })
                            // Un-comment this line and comment out the layout below to
                            // disable the toolbar
                            // .showToolbar(false)
                            .buttonLayout( Arrays.asList(
                                    EditorButton.UNDO,
                                    EditorButton.REDO,
                                    EditorButton.IMAGE,
                                    EditorButton.LINK,
                                    EditorButton.BOLD,
                                    EditorButton.ITALIC,
                                    EditorButton.UNDERLINE,
                                    EditorButton.SUBSCRIPT,
                                    EditorButton.SUPERSCRIPT,
                                    EditorButton.STRIKETHROUGH,
                                    EditorButton.JUSTIFY_LEFT,
                                    EditorButton.JUSTIFY_CENTER,
                                    EditorButton.JUSTIFY_RIGHT,
                                    EditorButton.JUSTIFY_FULL,
                                    EditorButton.ORDERED,
                                    EditorButton.UNORDERED,
                                    EditorButton.CHECK,
                                    EditorButton.NORMAL,
                                    EditorButton.H1,
                                    EditorButton.H2,
                                    EditorButton.H3,
                                    EditorButton.H4,
                                    EditorButton.H5,
                                    EditorButton.H6,
                                    EditorButton.INDENT,
                                    EditorButton.OUTDENT,
                                    EditorButton.BLOCK_QUOTE,
                                    EditorButton.BLOCK_CODE,
                                    EditorButton.CODE_VIEW
                            ) )
                            .onInitialized(new Runnable() {
                                @Override
                                public void run() {

                                    if(mStep.getBodyText() != null || mStep.getBodyText() != ""){
                                        editorFragment.getEditor().setContents(mStep.getBodyText());
                                    }

                                }
                            })
            );


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.create_step_text_fragment_container, editorFragment, "EDITOR")
                .commit();
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
                saveAsDraft();
                return true;
            case  R.id.action_discard:
                return true;
            case R.id.action_publish:
                publish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save(){
        final Context context = this;
        editorFragment.getEditor().getContents(new RichEditor.OnContentsReturned() {
            @Override
            public void process(@NonNull final String text) {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mStep.setTitle(mStepTitle.getText().toString());
                        mStep.setDescription(mStepDescription.getText().toString());
                        mStep.setBodyText(text);
                        mStep.setStepType(StepType.TEXT);

                        viewModel.saveStep(mStep, new Callback() {
                            @Override
                            public void result(Result result) {
                                switch (result){
                                    case FAILED:
                                        Toast.makeText(context, "Oops looks like there was a problems saving this step!", Toast.LENGTH_LONG).show();
                                        finish();
                                        break;
                                    case SUCCESS:
                                        if(mStep.getPublish()){
                                            finish();
                                        }
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
                } );
            }
        });
    }

    public void saveAsDraft(){
        mStep.setPublish(false);
        save();
    }

    public void publish(){
        mStep.setPublish(true);
        save();
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
