package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.viewModel.UserViewModel;

public class DialogEditUserGoalsActivity extends AppCompatActivity {

    @BindView(R.id.dialog_goals) EditText mGoalsText;
    @BindView(R.id.dialog_discard) Button mDiscardBtn;
    @BindView(R.id.dialog_update) Button mUpdateBtn;
    @BindView(R.id.dialog_goals_char_count) TextView mCharCount;

    private static final String USER_EXTRA = "user_extra";
    private UserViewModel viewModel;
    private User mUser;

    private final String TAG = DialogEditUserGoalsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_goals);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        mUser = getIntent().getParcelableExtra(USER_EXTRA);

        if(mUser.getGoal() != null ){
            mGoalsText.setText(mUser.getGoal());
        }
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mGoalsText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mGoalsText, InputMethodManager.SHOW_IMPLICIT);

    }

    @OnTextChanged(value = R.id.dialog_goals,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onGoalsTextChange(Editable editable){
       int charLeft =160 - editable.toString().length();
       mCharCount.setText(String.valueOf(charLeft) );
    }

    @OnClick(R.id.dialog_discard)
    public void onDiscardClick(View view){
        finish();
    }

    @OnClick(R.id.dialog_update)
    public void onUpdateClick(View view){
        final Context context = this;

        String goals = mGoalsText.getText().toString();
        viewModel.updateUserGoals(mUser.getUserId(), goals, new Callback() {
            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void userResult(User user) {

            }

            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        finish();
                        break;
                    case FAILED:
                        Toast.makeText(context, "Oops something went wrong!", Toast.LENGTH_LONG).show();
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
        });


    }
}
