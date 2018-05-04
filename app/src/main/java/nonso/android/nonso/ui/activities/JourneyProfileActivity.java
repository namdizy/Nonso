package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.adapters.SectionPagerAdapter;
import nonso.android.nonso.ui.fragments.JourneyCommunityFragment;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;
import nonso.android.nonso.ui.fragments.JourneyAboutFragment;


public class JourneyProfileActivity extends AppCompatActivity implements JourneyTimelineFragment.OnFragmentInteractionListener,
        JourneyAboutFragment.OnFragmentInteractionListener, JourneyCommunityFragment.OnFragmentInteractionListener{

    @BindView(R.id.journey_profile_viewPager) ViewPager mViewPager;
    @BindView(R.id.tv_journey_profile_name) TextView mJourneyName;
    @BindView(R.id.tv_journey_profile_short_description) TextView mJourneyShortDescription;
    @BindView(R.id.journey_profile_tabs) TabLayout mTabLayout;
    @BindView(R.id.journey_profile_image) ImageView mJourneyProfile;

    private Journey mJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mJourney = intent.getParcelableExtra("journey");

        mJourneyName.setText(mJourney.getName());
        mJourneyShortDescription.setText(mJourney.getDescription());

        mViewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager(), this));
        mTabLayout.setupWithViewPager(mViewPager);

        Picasso.with(this).load(mJourney.getProfileImage()).placeholder(R.drawable.image_view_placeholder)
                .error(R.drawable.image_view_placeholder).into(mJourneyProfile);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




}
