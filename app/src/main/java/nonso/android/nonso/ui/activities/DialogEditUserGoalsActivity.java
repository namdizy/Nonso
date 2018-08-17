package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    private static final String UID_KEY = "user_id";
    private UserViewModel viewModel;
    private String mUserId;

    private final String TAG = DialogEditUserGoalsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_goals);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        mUserId = getIntent().getStringExtra(UID_KEY);

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);

    }

    @OnClick(R.id.dialog_discard)
    public void onDiscardClick(View view){
        finish();
    }

    @OnClick(R.id.dialog_update)
    public void onUpdateClick(View view){
        final Context context = this;

        String goals = mGoalsText.getText().toString();
        viewModel.updateUserGoals(mUserId, goals, new Callback() {
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
