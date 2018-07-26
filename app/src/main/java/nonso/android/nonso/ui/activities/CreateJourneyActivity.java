package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

import nonso.android.nonso.R;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.CreatedBy;
import nonso.android.nonso.models.CreatorType;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.adapters.StepperAdapter;
import nonso.android.nonso.ui.fragments.createJourneys.DescriptionStepperFragment;
import nonso.android.nonso.ui.fragments.createJourneys.SettingsStepperFragment;
import nonso.android.nonso.viewModel.JourneyViewModel;

public class CreateJourneyActivity extends AppCompatActivity implements DescriptionStepperFragment.OnDescriptionStepListener,
        SettingsStepperFragment.OnSettingsStepListener, StepperLayout.StepperListener  {

    @BindView(R.id.stepperLayout) StepperLayout mStepperLayout;

    private Journey mJourney;

    private static final String EXTRA_CREATOR = "user";
    private User mCreator;

    private final String TAG = CreateJourneyActivity.this.getClass().getSimpleName();

    private JourneyViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        mCreator = intent.getParcelableExtra(EXTRA_CREATOR);
        viewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);

        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));
        mStepperLayout.setListener(this);


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
    }

    @Override
    public void onCompleted(View completeButton) {
        final LinearLayout mProgressbar = findViewById(R.id.create_journey_progress_bar_container);
        mProgressbar.setVisibility(View.VISIBLE);

        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(mCreator.getUserId());
        createdBy.setImageUrl(mCreator.getImageUri());
        createdBy.setName(mCreator.getUserName());
        createdBy.setCreatorType(CreatorType.USER);
        mJourney.setCreatedBy(createdBy);

        viewModel.createJourney(mJourney, new Callback() {
            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        mProgressbar.setVisibility(View.GONE);
                        finish();
                        break;
                    case FAILED:
                        Log.v(TAG, "Journey Creation failed");
                        Toast.makeText(getApplicationContext(), "Oops looks like there was an issue creating the journey!", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
            }

            @Override
            public void imageResult(Uri downloadUrl) { }
        });

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
