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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.this.getClass().getSimpleName();
    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();
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
        Log.v(TAG, "LoginActivity click");

        email = mEmailText.getText().toString().trim();
        password = mPasswordText.getText().toString().trim();

        if(!validate()){
            onLoginSuccess(null);
            return;
        }

        mLoginBtn.setEnabled(false);

        mProgressBarContainer.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onLoginSuccess(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            onLoginSuccess(null);
                        }
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
        if (firebaseUser != null && firebaseUser.isEmailVerified()){
            mProgressBarContainer.setVisibility(View.GONE);
            mLoginBtn.setEnabled(true);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            mProgressBarContainer.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), "Login failed: Incorrect username or password", Toast.LENGTH_LONG).show();
            mLoginBtn.setEnabled(true);
            return;
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

        FirebaseUser currentUser = mAuth.getCurrentUser();

        onLoginSuccess(currentUser);
    }
}
