package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class CreateJourneyActivity extends AppCompatActivity {

    @BindView(R.id.btn_create_journeys_back) ImageButton mBack;
    @BindView(R.id.btn_create_journey_pick_video) Button mPickVideo;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    private StorageReference mVideoDescriptionRef;

    private static final String STORAGE_VIDEO_BUCKET = "videos/";
    private static final String STORAGE_IMAGE_BUCKET = "images/";
    private final String TAG = CreateJourneyActivity.this.getClass().getSimpleName();


    private static final int PICK_VIDEO_REQUEST_CODE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }


    @OnClick(R.id.btn_create_journeys_back)
    public void backOnClick(View view){

        Intent intent = new Intent(this, MainActivity.class);
        getApplication().startActivity(intent);
    }


    @OnClick(R.id.btn_create_journey_pick_video)
    public void pickVideoOnClick(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == Activity.RESULT_OK && resultCode == PICK_VIDEO_REQUEST_CODE){
            Uri selectedVideoUri = data.getData();
            String userId = mUser.getUid();

            mVideoDescriptionRef = mStorageRef.child(STORAGE_VIDEO_BUCKET + userId);

        }
    }

    private void uploadVideo(Uri videoUri){
        if(videoUri != null){
            UploadTask uploadTask = mVideoDescriptionRef.putFile(videoUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                        Toast.makeText(getBaseContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

        }
    }
}
