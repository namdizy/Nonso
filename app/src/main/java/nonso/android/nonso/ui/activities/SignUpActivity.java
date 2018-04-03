package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_input_username) EditText mUsernameText;
    @BindView(R.id.sign_up_input_email) EditText mEmailText;
    @BindView(R.id.sign_up_input_password) TextInputEditText mPasswordText;
    @BindView(R.id.sign_up_input_retype_password) TextInputEditText mRetypePasswordText;
    @BindView(R.id.cbx_disclaimer) CheckBox mDisclaimerCbx;
    @BindView(R.id.btn_signup) Button mSignupBtn;
    @BindView(R.id.btn_link_login) Button mLoginBtn;
    @BindView(R.id.progressBarSignUpContainer) LinearLayout mProgressBarContainer;

    private final String TAG = SignUpActivity.this.getClass().getSimpleName();

    private FirebaseAuth mAuth;

    private String username;
    private String email;
    private String password;
    private String retypePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @OnTextChanged(value = {
            R.id.sign_up_input_username,
            R.id.sign_up_input_email,
            R.id.sign_up_input_password,
            R.id.sign_up_input_retype_password},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void textChangeListener(Editable editable){
        enableSubmitBtn();
        if(editable == mRetypePasswordText.getEditableText()){
            validatePasswordMatch();
        }
    }

    public boolean validateForm(){
        boolean valid = true;
        email = mEmailText.getText().toString();
        password = mPasswordText.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailText.setError("Enter a valid email address");
            valid = false;
        }else{
            mEmailText.setError(null);
        }

        if(password.isEmpty() || password.length() < 4 || password.length() >10){
            mPasswordText.setError("Password should be between 4 and 10 characters");
            valid = false;
        }else{
            mPasswordText.setError(null);
        }

        if(!mDisclaimerCbx.isChecked()){
            valid = false;
            mDisclaimerCbx.setError("");
        }
        else{
            mDisclaimerCbx.setError(null);
        }

        return valid;
    }
    public void enableSubmitBtn(){
        username = mUsernameText.getText().toString().trim();
        email = mEmailText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();
        retypePassword = mRetypePasswordText.getText().toString().trim();

        boolean validated;

        if( username.length()> 1 && email.length() > 4 && password.length() >4 && retypePassword.length() > 4 ){
            validated = true;
        }else{
            validated = false;
        }

        mSignupBtn.setEnabled(validated);
    }

    public void validatePasswordMatch(){
        if(!mRetypePasswordText.getText().toString().trim().equals(mPasswordText.getText().toString().trim())){
            mRetypePasswordText.setError("Passwords do not match");
        }
    }

    @OnClick(R.id.btn_signup)
    public void signUp(View view){

        if(!validateForm()){
            return;
        }

        mProgressBarContainer.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");
                        mProgressBarContainer.setVisibility(View.INVISIBLE);
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();

                        user.updateProfile(profileUpdates);

                        onSignUpSuccess(user);
                    }else{
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        mProgressBarContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @OnClick(R.id.btn_link_login)
    public void login(View view){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnCheckedChanged(R.id.cbx_disclaimer)
    public void onChecked(CompoundButton button, boolean isChecked){

        if(isChecked){
            mDisclaimerCbx.setError(null);
        }

    }

    public void onSignUpSuccess(FirebaseUser user){
        if (user != null){
            user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Log.v(TAG, "Email sent.");

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        getApplication().startActivity(intent);
                    }
                }
            });
        }else{
            return;
        }
    }

}
