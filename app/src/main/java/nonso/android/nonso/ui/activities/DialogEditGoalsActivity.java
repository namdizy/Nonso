package nonso.android.nonso.ui.activities;

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

public class DialogEditGoalsActivity extends AppCompatActivity {

    @BindView(R.id.dialog_goals) EditText mGoalsText;
    @BindView(R.id.dialog_discard) Button mDiscardBtn;
    @BindView(R.id.dialog_update) Button mUpdateBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference mUserRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String DATABASE_COLLECTION_USERS = "users/";
    private final String TAG = this.getLocalClassName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_goals);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());
    }

    @OnClick(R.id.dialog_discard)
    public void onDiscardClick(View view){
        finish();
    }

    @OnClick(R.id.dialog_update)
    public void onUpdateClick(View view){

        String goals = mGoalsText.getText().toString();

        mUserRef.update("goal", goals)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.v(TAG, "Success: Updated user goal");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed: Update user goal failed", e);
                    }
                });

    }
}
