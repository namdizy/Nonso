package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageMetadata;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.viewModel.AuthorizationViewModel;

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

    private AuthorizationViewModel mViewModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String _username;
    private String _email;
    private String _password;
    private String _retypePassword;

    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(AuthorizationViewModel.class);

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
        _email = mEmailText.getText().toString();
        _password = mPasswordText.getText().toString();

        if(_email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            mEmailText.setError("Enter a valid email address");
            valid = false;
        }else{
            mEmailText.setError(null);
        }

        if(_password.isEmpty() || _password.length() < 4 || _password.length() >10){
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
        _username = mUsernameText.getText().toString().trim();
        _email = mEmailText.getText().toString().trim();
        _password = mPasswordText.getText().toString().trim();
        _retypePassword = mRetypePasswordText.getText().toString().trim();

        boolean validated;

        if( _username.length()> 1 && _email.length() > 4 && _password.length() >4 && _retypePassword.length() > 4 ){
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

        mViewModel.createUser(_email, _password, _username, new Callback() {
            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void userResult(User user) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        onSignUpSuccess();
                        break;
                    case FAILED:
                        onSignUpFailed();
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

    public void onSignUpSuccess(){
        mProgressBarContainer.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        getApplication().startActivity(intent);
    }
    public void onSignUpFailed(){
        mProgressBarContainer.setVisibility(View.INVISIBLE);
        Toast.makeText(SignUpActivity.this, "Oops looks like Sign up failed. Please Try again",
                Toast.LENGTH_SHORT).show();
    }


}
