package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.forgot_password_input_email) EditText mEmailText;
    @BindView(R.id.btn_forgot_password_submit) Button mSubmit;
    @BindView(R.id.btn_forgot_password_back) ImageButton mBack;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_forgot_password_back)
    public void backOnClick(View view){
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_forgot_password_submit)
    public void submitOnclick(View view){
        mAuth = FirebaseAuth.getInstance();

        String email = mEmailText.getText().toString();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                getApplication().startActivity(intent);
            }
        });
    }

    @OnTextChanged(value = {
            R.id.forgot_password_input_email},
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void textListener(Editable editable){
        enableSubmitIfReady();
    }


    public void enableSubmitIfReady(){
        boolean isReady = mEmailText.getText().toString().length() > 3;

        mSubmit.setEnabled(isReady);
    }
}
