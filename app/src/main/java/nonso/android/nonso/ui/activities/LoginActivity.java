package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import nonso.android.nonso.viewModel.AuthorizationViewModel;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.this.getClass().getSimpleName();

    private AuthorizationViewModel mViewModel;

    private String email;
    private String password;

    @BindView(R.id.login_input_email) EditText mEmailText;
    @BindView(R.id.login_input_password) TextInputEditText mPasswordText;
    @BindView(R.id.btn_link_signup) Button mSignUpBtn;
    @BindView(R.id.btn_login) Button mLoginBtn;
    @BindView(R.id.progressBarContainer) LinearLayout mProgressBarContainer;
    @BindView(R.id.tv_forgot_password) TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(AuthorizationViewModel.class);

    }


    @OnTextChanged(value = {
            R.id.login_input_password,
            R.id.login_input_email},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void textListener(Editable editable){
        enableSubmitIfReady();
    }

    public void enableSubmitIfReady(){

        boolean isReady = mEmailText.getText().toString().length() > 3
                && mPasswordText.getText().toString().length() > 4;

        mLoginBtn.setEnabled(isReady);

    }

    @OnClick(R.id.btn_login)
    public void login(View view){

        email = mEmailText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();

        if(!validate()){
            onLoginSuccess(null);
            return;
        }

        mLoginBtn.setEnabled(false);

        mProgressBarContainer.setVisibility(View.VISIBLE);

        mViewModel.signIn(email, password, new Callback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void userResult(User user) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {
                onLoginSuccess(user);
            }
        });

    }

    @OnClick(R.id.btn_link_signup)
    public void signUp(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        getApplication().startActivity(intent);
    }

    @OnClick(R.id.tv_forgot_password)
    public void forgotPasswordClick(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class );
        getApplication().startActivity(intent);
    }

    public boolean validate(){
        boolean valid = true;

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailText.setError("Enter a valid email address");
            valid = false;
        }else{
            mEmailText.setError(null);
        }

        if(password.isEmpty() || password.length() < 4 || password.length() >15){
            mPasswordText.setError("Password should be between 4 and 10 characters");
            valid = false;
        }else{
            mPasswordText.setError(null);
        }
        return valid;
    }

    public void onLoginSuccess(FirebaseUser firebaseUser) {

        mLoginBtn.setEnabled(true);
        mProgressBarContainer.setVisibility(View.GONE);
        if(firebaseUser == null){
            Toast.makeText(getBaseContext(), "Login failed: Email or Password is incorrect", Toast.LENGTH_LONG).show();
        }
        else if (firebaseUser.isEmailVerified()){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(!firebaseUser.isEmailVerified()){
            Toast.makeText(getBaseContext(), "Login failed: Please check your email, and verify.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBarContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mViewModel.getCurrentUser();

        if(currentUser != null){
            onLoginSuccess(currentUser);
        }
    }
}
