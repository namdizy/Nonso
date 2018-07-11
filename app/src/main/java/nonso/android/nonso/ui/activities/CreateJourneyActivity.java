package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import nonso.android.nonso.R;
import nonso.android.nonso.models.CreatedBy;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.StepperAdapter;
import nonso.android.nonso.ui.fragments.createJourneys.DescriptionStepperFragment;
import nonso.android.nonso.ui.fragments.createJourneys.SettingsStepperFragment;

public class CreateJourneyActivity extends AppCompatActivity implements DescriptionStepperFragment.OnDescriptionStepListener,
        SettingsStepperFragment.OnSettingsStepListener, StepperLayout.StepperListener  {

    @BindView(R.id.stepperLayout) StepperLayout mStepperLayout;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private StorageReference mProfileImageRef;
    private DocumentReference mUserRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Journey mJourney;

    private static final int GALLERY_REQUEST_CODE = 111;
    private static final String EXTRA_CREATOR = "user";
    private User mCreator;

    private static final String STORAGE_IMAGE_BUCKET = "images/";
    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys";
    private static final String DATABASE_COLLECTION_USERS = "users";
    private final String TAG = CreateJourneyActivity.this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        intent.getParcelableExtra(EXTRA_CREATOR);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));
        mStepperLayout.setListener(this);

        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(mUser.getEmail());

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mJourney = new Journey();
    }

    @Override
    public void OnDescriptionStepListener(Journey journey) {
        mJourney.setName(journey.getName());
        mJourney.setDescription(journey.getDescription());
        mJourney.setCategories(journey.getCategories());
        mJourney.setProfileImage(journey.getProfileImage());
    }

    @Override
    public void OnSettingsStepListener(Journey journey) {
        mJourney.setPermissions(journey.isPermissions());
        mJourney.setSubscriptions(journey.isSubscriptions());
        mJourney.setTier1(journey.isTier1());
        mJourney.setTier2(journey.isTier2());
        mJourney.setTier3(journey.isTier3());
    }

    @Override
    public void onCompleted(View completeButton) {
        final LinearLayout mProgressbar = (LinearLayout) findViewById(R.id.create_journey_progress_bar_container);
        mProgressbar.setVisibility(View.VISIBLE);

        mJourney.setUserId(mUserRef.getId());

        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(mCreator.getUserId());
        createdBy.setImageUrl(mCreator.getImageUri());
        createdBy.setName(mCreator.getUserName());
        mJourney.setCreatedBy(createdBy);

        db.collection(DATABASE_COLLECTION_JOURNEYS)
                .add(mJourney)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String journeyId = documentReference.getId();

                        if(mJourney.getProfileImage() != null){
                            uploadImage(journeyId);
                        }
                        updateUser(journeyId, mProgressbar);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding file to document", e);
                    }
                });
    }

    private void uploadImage(final String journeyId){

        mProfileImageRef = mStorageRef.child(STORAGE_IMAGE_BUCKET  +journeyId+ "_journey_profile_image"+ ".jpg");
        Uri uri = Uri.fromFile(new File(mJourney.getProfileImage()));
        mProfileImageRef.putFile(uri)
            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return mProfileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    DocumentReference journeyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
                    journeyRef.update("profileImage", downloadUri.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
                } else {
                    Log.w(TAG, "Error updating document");
                }
            }
        });
    }
    private void updateUser(final String journeyId, final LinearLayout progressbar){
        final Context context = this;
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                Map<String, Boolean> userJourneys = user.getCreatedJourneys();
                userJourneys.put(journeyId, true);
                mUserRef.update("createdJourneys", userJourneys).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressbar.setVisibility(View.GONE);
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){

//            Fragment f = (Fragment) mStepperLayout.getAdapter().findStep(mStepperLayout.getCurrentStepPosition());
//            List<String> imgs = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
//            if (mStepperLayout.getCurrentStepPosition() == 0 && f != null) {
//                ((DescriptionStepperFragment)f).setProfileImage(imgs.get(0));
//            }
        }
    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {

    }

    @Override
    public void onReturn() {
        finish();
    }
}
