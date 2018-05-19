package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.ui.adapters.JourneyProfilePagerAdapter;
import nonso.android.nonso.ui.fragments.JourneyCommunityFragment;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;
import nonso.android.nonso.ui.fragments.JourneyAboutFragment;


public class JourneyProfileActivity extends AppCompatActivity implements JourneyTimelineFragment.OnJourneyTimelineListener,
        JourneyAboutFragment.OnFragmentInteractionListener, JourneyCommunityFragment.OnFragmentInteractionListener{

    @BindView(R.id.journey_page_image) ImageView mImageView;
    @BindView(R.id.journey_page_appbar) AppBarLayout appBarLayout;
    @BindView(R.id.journey_page_toolbar) Toolbar toolbar;
    @BindView(R.id.journey_page_description) TextView mJourneyDescription;
    @BindView(R.id.journey_creator_image) ImageView mJourneyCreatorImage;
    @BindView(R.id.journey_creator_name) TextView mJourneyCreatorName;
    @BindView(R.id.journey_profile_tabs) TabLayout mTabLayout;
    @BindView(R.id.journey_profile_viewPager) ViewPager mViewPager;

    private Journey mJourney;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    private final String JOURNEY_EXTRA_DATA = "journey_extra";
    private final String STEP_EXTRA_DATA = "step_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mJourney = intent.getParcelableExtra(JOURNEY_EXTRA_DATA);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mJourney.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mJourneyDescription.setText(mJourney.getDescription());
        mJourneyCreatorName.setText(mUser.getDisplayName());

        Picasso.with(this).load(mJourney.getProfileImage()).into(mImageView);
        Picasso.with(this).load(mUser.getPhotoUrl()).into(mJourneyCreatorImage);

        mViewPager.setAdapter(new JourneyProfilePagerAdapter(getSupportFragmentManager(), this));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onDestroy() {
        //Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }

    @Override
    public void onJourneyTimelineInteraction(Step step) {

        switch (step.getStepType()){

            case TEXT:
                Intent intent = new Intent(this, CreateStepTextActivity.class);
                intent.putExtra(STEP_EXTRA_DATA, step);
                intent.putExtra(JOURNEY_EXTRA_DATA, mJourney);
                startActivity(intent);
                break;
            case IMAGES:
            case VIDEO:
        }
    }
}
