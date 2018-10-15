package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.viewModel.JourneyViewModel;

public class DialogEditJourneyDescription extends AppCompatActivity {


    @BindView(R.id.journey_profile_dialog_discard) Button mDiscardBtn;
    @BindView(R.id.journey_profile_dialog_goals) EditText mJourneyDescription;
    @BindView(R.id.journey_profile_dialog_update) Button mUpdateBtn;
    @BindView(R.id.journey_profile_dialog_char_count)
    TextView mCharCount;

    private Journey mJourney;
    private final String JOURNEY_EXTRA_KEY = "journey_extra";
    private JourneyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_journey_description);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        mJourney = getIntent().getParcelableExtra(JOURNEY_EXTRA_KEY);
        viewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);

        mJourneyDescription.setText(mJourney.getDescription());
        mJourneyDescription.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mJourneyDescription, InputMethodManager.SHOW_IMPLICIT);
    }

    @OnTextChanged(value = R.id.journey_profile_dialog_goals,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onGoalsTextChange(Editable editable){
        int charLeft =160 - editable.toString().length();
        mCharCount.setText(String.valueOf(charLeft) );
    }

    @OnClick(R.id.journey_profile_dialog_discard)
    public void onDiscardClick(View view){
        finish();
    }

    @OnClick(R.id.journey_profile_dialog_update)
    public void onUpdateClick(View view){

        String description = mJourneyDescription.getText().toString();
        final Context context = this;

        viewModel.updateJourneyDescription(mJourney.getJourneyId(), description, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        finish();
                        break;
                    case FAILED:
                        Toast.makeText(context, "Oops looks like something went wrong", Toast.LENGTH_LONG ).show();
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
}
