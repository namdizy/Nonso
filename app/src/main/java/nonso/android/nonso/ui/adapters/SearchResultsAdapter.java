package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nonso.android.nonso.ui.fragments.search.SearchResultsJourneys;
import nonso.android.nonso.ui.fragments.search.SearchResultsTop;
import nonso.android.nonso.ui.fragments.search.SearchResultsUsers;

public class SearchResultsAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SearchResultsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchResultsTop();
            case 1:
                return new SearchResultsUsers();
            case 2:
                return new SearchResultsJourneys();
            default:
                return null;
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
                return "Top";
            case 1:
                return "Users";
            case 2:
                return "Journeys";
            default:
                return null;
        }
    }
}
