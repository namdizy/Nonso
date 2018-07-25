package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nonso.android.nonso.ui.fragments.JourneyCommunityFragment;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;

public class JourneyProfilePagerAdapter extends FragmentPagerAdapter {

    private String mJourneyId;

    public JourneyProfilePagerAdapter(FragmentManager fm, String id) {
        super(fm);
        mJourneyId = id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new JourneyTimelineFragment().newInstance(mJourneyId);
            case 1:
                return new JourneyCommunityFragment();
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Steps";
            case 1:
                return "Community";
            default:
                return null;
        }
    }
}