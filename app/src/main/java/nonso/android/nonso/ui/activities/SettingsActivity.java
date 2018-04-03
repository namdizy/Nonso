package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.tv_settings_logout) TextView mLogoutText;
    @BindView(R.id.btn_settings_back) ImageButton mBack;
    @BindView(R.id.settings_username_container) LinearLayout mUsernameContainer;
    @BindView(R.id.tv_settings_username) TextView mUsername;
    @BindView(R.id.settings_email_container) LinearLayout mEmailContainer;
    @BindView(R.id.tv_settings_email) TextView mEmail;
    @BindView(R.id.tv_settings_acknowledgement) TextView mAcknowledgment;
    @BindView(R.id.tv_settings_content_policy) TextView mContentPolicy;
    @BindView(R.id.tv_settings_privacy_policy) TextView mPrivacyPolicy;
    @BindView(R.id.tv_settings_user_agreement) TextView mUserAgreement;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsername.setText(mUser.getDisplayName());
        mEmail.setText(mUser.getEmail());
    }

    @OnClick(R.id.tv_settings_logout)
    public void logoutOnClick(View v){

        mAuth.getInstance().signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        getApplication().startActivity(intent);
    }

    @OnClick(R.id.btn_settings_back)
    public void backOnClick(View v){
        Intent intent = new Intent(this, MainActivity.class);
        getApplication().startActivity(intent);
    }
}
