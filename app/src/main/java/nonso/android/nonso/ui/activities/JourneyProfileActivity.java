package nonso.android.nonso.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import nonso.android.nonso.R;
import nonso.android.nonso.models.CreatedBy;
import nonso.android.nonso.models.CreatorType;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.ui.adapters.JourneyProfilePagerAdapter;
import nonso.android.nonso.ui.fragments.JourneyArchiveFragment;
import nonso.android.nonso.ui.fragments.JourneyCommunityFragment;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;
import nonso.android.nonso.viewModel.JourneyViewModel;


public class JourneyProfileActivity extends AppCompatActivity implements JourneyTimelineFragment.OnJourneyTimelineListener,
        JourneyArchiveFragment.OnArchiveInteractionListener, JourneyCommunityFragment.OnFragmentInteractionListener{

    @BindView(R.id.journey_page_image) ImageView mImageView;
    @BindView(R.id.journey_page_appbar) AppBarLayout appBarLayout;
    @BindView(R.id.journey_page_toolbar) Toolbar toolbar;
    @BindView(R.id.journey_page_description) TextView mJourneyDescription;
    @BindView(R.id.journey_creator_image) ImageView mJourneyCreatorImage;
    @BindView(R.id.journey_creator_name) TextView mJourneyCreatorName;
    @BindView(R.id.journey_profile_edit_btn) ImageButton mEditDescription;
    @BindView(R.id.journey_profile_tabs) TabLayout mTabLayout;
    @BindView(R.id.journey_profile_viewPager) ViewPager mViewPager;
    @BindView(R.id.journey_profile_fab) FabSpeedDial mFab;


    private Journey mJourney;
    private JourneyViewModel viewModel;
    private int mTabPosition;

    private final String STEP_EXTRA_DATA = "step_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private final String TAB_POSITION_EXTRA = "tab_position_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String journeyId = intent.getStringExtra(JOURNEY_EXTRA_ID_KEY);
        mTabPosition = intent.getIntExtra(TAB_POSITION_EXTRA, 0);

        viewModel = ViewModelProviders.of(this).get(JourneyViewModel.class);
        viewModel.setUpJourneyItem(journeyId);

        viewModel.getJourneyItemLiveData().observe(this,this::updateUI);

    }

    public void updateUI(Journey journey){

        mJourney = journey;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mJourney.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mJourneyDescription.setText(mJourney.getDescription());
        mJourneyCreatorName.setText(mJourney.getCreatedBy().getName());

        Picasso.with(this).load(mJourney.getImage().getImageUrl()).into(mImageView);
        Picasso.with(this).load(mJourney.getCreatedBy().getImageUrl()).placeholder(R.drawable.profile_image_placeholder).
                error(R.drawable.profile_image_placeholder).into(mJourneyCreatorImage);

        mViewPager.setAdapter(new JourneyProfilePagerAdapter(getSupportFragmentManager(), mJourney.getJourneyId(), mJourney.getCreatedBy().getId()));
        mViewPager.setCurrentItem(mTabPosition);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                int cx = mFab.getWidth()/2;
                int cy = mFab.getHeight()/2;
                float radius = (float)Math.hypot(cx, cy);
                switch (position){
                    case 0:
                        showFab(cx, cy, radius);
                        break;
                    case 1:
                        hideFab(cx, cy, radius);
                        break;
                    case 2:
                        showFab(cx, cy, radius);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFab.setMenuListener(menuListener);
    }


    public void showFab(int cx, int cy, float finalRadius){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Animator anim = ViewAnimationUtils.createCircularReveal(mFab, cx, cy, 0, finalRadius);
            mFab.setVisibility(View.VISIBLE);
            anim.setDuration(400);
            anim.start();
        }else{
            mFab.setVisibility(View.VISIBLE);
        }
    }
    public void hideFab(int cx, int cy, float endRadius){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(mFab, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFab.setVisibility(View.INVISIBLE);
                }
            });

            anim.setDuration(400);
            anim.start();
        }else{
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    FabSpeedDial.MenuListener menuListener = new FabSpeedDial.MenuListener() {
        @Override
        public boolean onPrepareMenu(NavigationMenu navigationMenu) {
            return true;
        }

        @Override
        public boolean onMenuItemSelected(MenuItem menuItem) {

            Step step = new Step();

            CreatedBy createdBy = new CreatedBy();
            createdBy.setCreatorType(CreatorType.JOURNEY);
            createdBy.setId(mJourney.getJourneyId());
            createdBy.setName(mJourney.getName());
            createdBy.setImageUrl(mJourney.getImage().getImageUrl());

            step.setCreatedBy(createdBy);
            step.setPublish(false);

            switch (menuItem.getItemId()){
                case R.id.fab_menu_add_text:
                    Intent intent = new Intent(getBaseContext(), CreateStepTextActivity.class);
                    intent.putExtra(STEP_EXTRA_DATA, step);
                    startActivity(intent);
                    break;
                case R.id.fab_menu_add_photo:
                    Intent intentImage = new Intent(getBaseContext(), CreateStepImageActivity.class);
                    intentImage.putExtra(STEP_EXTRA_DATA, step);
                    startActivity(intentImage);
                    break;
                case R.id.fab_menu_add_video:
                    Intent intentVideo = new Intent(getBaseContext(), CreateStepVideoActivity.class);
                    intentVideo.putExtra(STEP_EXTRA_DATA, step);
                    startActivity(intentVideo);
                    break;

            }
            return true;
        }

        @Override
        public void onMenuClosed() {

        }
    };


    @OnClick(R.id.journey_profile_edit_btn)
    public void onEditClick(View view){
        Intent intent = new Intent(this, DialogEditJourneyDescription.class);
        intent.putExtra(JOURNEY_EXTRA_ID_KEY, mJourney.getJourneyId());
        startActivity(intent);
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

    }

    @Override
    public void onArchiveInteraction(Uri uri) {

    }
}
