package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.viewModel.UserViewModel;

public class DialogEditGoalsActivity extends AppCompatActivity {

    @BindView(R.id.dialog_goals) EditText mGoalsText;
    @BindView(R.id.dialog_discard) Button mDiscardBtn;
    @BindView(R.id.dialog_update) Button mUpdateBtn;

    private static final String UID_KEY = "user_id";
    private UserViewModel viewModel;
    private String mUserId;

    private final String TAG = DialogEditGoalsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_goals);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(UID_KEY);

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);

    }

    @OnClick(R.id.dialog_discard)
    public void onDiscardClick(View view){
        finish();
    }

    @OnClick(R.id.dialog_update)
    public void onUpdateClick(View view){

        String goals = mGoalsText.getText().toString();
        viewModel.updateUserGoals(mUserId, goals, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        finish();
                        break;
                    case FAILED:
                        break;

                }
            }

            @Override
            public void journey(Uri downloadUrl) {

            }

            @Override
            public void authorization(FirebaseUser user) {

            }
        });


    }
}
