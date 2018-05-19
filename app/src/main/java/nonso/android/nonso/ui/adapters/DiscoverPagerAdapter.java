package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nonso.android.nonso.ui.fragments.DiscoverNewFragment;
import nonso.android.nonso.ui.fragments.DiscoveryHotFragment;


public class DiscoverPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public DiscoverPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DiscoveryHotFragment();
            case 1:
                return new DiscoverNewFragment();
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
                return "Hot";
            case 1:
                return "New";
            default:
                return null;
        }
    }

}