package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ebolo.krichtexteditor.RichEditor;
import com.ebolo.krichtexteditor.fragments.KRichEditorFragment;
import com.ebolo.krichtexteditor.fragments.Options;
import com.ebolo.krichtexteditor.ui.widgets.EditorButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;

public class CreateStepTextActivity extends AppCompatActivity {

    @BindView(R.id.create_step_text_title)
    EditText mStepTitle;
    @BindView(R.id.create_step_text_description) EditText mStepDescription;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference mUserRef;
    //private StorageReference mStorageRef;
    private DocumentReference mJourneyRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //private static final String STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys";
    private static final String DATABASE_COLLECTION_STEPS = "steps";
    private static final String DATABASE_COLLECTION_USERS = "users";

    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_DATA = "journey_extra";

    private KRichEditorFragment editorFragment;

    private Step mStep;
    private Journey mJourney;
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
        mJourney = intent.getParcelableExtra(JOURNEY_EXTRA_DATA);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());

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
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveAsDraft(){

        editorFragment.getEditor().getContents(new RichEditor.OnContentsReturned() {
            @Override
            public void process(@NonNull final String text) {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, "this is the content: " + text);

                        mStep.setTitle(mStepTitle.getText().toString());
                        mStep.setDescription(mStepDescription.getText().toString());
                        mStep.setBodyText(text);
                        mStep.setStepType(StepType.TEXT);
                        mStep.setPublish(false);
                        mStep.setCreatorId(mUserRef.getId());
                        mStep.setJourneyId(mJourney.getJourneyId());

                        db.collection(DATABASE_COLLECTION_STEPS)
                            .add(mStep)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    String stepId = documentReference.getId();
                                    updateJourney(stepId);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Failure Listener: Failed to create step text");
                                }
                            });
                    }
                } );
            }
        });
    }

    public void updateJourney(String stepId){
        mJourneyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(mJourney.getJourneyId());
        Map<String, Boolean> s = mJourney.getSteps();
        s.put(stepId, true);

        mJourneyRef.update("steps", s).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
