package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;


public class JourneyProfileActivity extends AppCompatActivity implements JourneyTimelineFragment.OnFragmentInteractionListener{

    @BindView(R.id.journey_profile_viewPager) ViewPager mViewPager;
    @BindView(R.id.tv_journey_profile_name) TextView mJourneyName;
    @BindView(R.id.tv_journey_profile_short_description) TextView mJourneyShortDescription;
    @BindView(R.id.journey_profile_tabs) TabLayout mTabLayout;

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

        mViewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new JourneyTimelineFragment();
                case 1:
                    return new JourneyTimelineFragment();
                default:
                    return new JourneyTimelineFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "First Tab";
                case 1:
                    return "Second Tab";
                default:
                    return "Third Tab";
            }
        }
    }

}
