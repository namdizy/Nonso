package nonso.android.nonso.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nonso.android.nonso.data.AuthDB;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.ui.fragments.JourneyArchiveFragment;
import nonso.android.nonso.ui.fragments.JourneyCommunityFragment;
import nonso.android.nonso.ui.fragments.JourneyTimelineFragment;

public class JourneyProfilePagerAdapter extends FragmentPagerAdapter {

    private String mJourneyId;
    private String mCreatorId;
    AuthDB authDB;

    public JourneyProfilePagerAdapter(FragmentManager fm, String journeyId, String creatorId) {
        super(fm);
        mJourneyId = journeyId;
        mCreatorId = creatorId;
        authDB = new AuthDB();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new JourneyTimelineFragment().newInstance(mJourneyId);
            case 1:
                return new JourneyCommunityFragment().newInstance(mJourneyId);
            case 2:
                return new JourneyArchiveFragment().newInstance(mJourneyId);
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        String id = authDB.getCurrentUserId();
        if(mCreatorId.equals(id)){
            return 3;
        }
        else{
            return 2;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Steps";
            case 1:
                return "Community";
            case 2:
                return "Archive";
            default:
                return null;
        }
    }
}