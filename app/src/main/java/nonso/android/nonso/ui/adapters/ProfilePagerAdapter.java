package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import nonso.android.nonso.ui.fragments.ProfileFollowingJourneysListFragment;
import nonso.android.nonso.ui.fragments.ProfileJourneysListFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public ProfilePagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileJourneysListFragment();
            case 1:
                return new ProfileFollowingJourneysListFragment();
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
                return "Journeys";
            case 1:
                return "Following";
            default:
                return null;
        }
    }
}
