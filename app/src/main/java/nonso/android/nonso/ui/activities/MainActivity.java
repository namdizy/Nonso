package nonso.android.nonso.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.fxn.pix.Pix;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.DiscoverFragment;
import nonso.android.nonso.ui.fragments.JourneysFragment;
import nonso.android.nonso.ui.fragments.NotificationsFragment;
import nonso.android.nonso.ui.fragments.ProfileFollowingJourneysListFragment;
import nonso.android.nonso.ui.fragments.ProfileFragment;
import nonso.android.nonso.ui.fragments.ProfileJourneysListFragment;
import nonso.android.nonso.utils.JourneyUtils;

public class MainActivity extends AppCompatActivity implements DiscoverFragment.OnFragmentInteractionListener,
        JourneysFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener
        , ProfileFollowingJourneysListFragment.OnProfileFollowingJourneysInteractionListener,
        ProfileJourneysListFragment.OnProfileJourneysListInteractionListener{

    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private final String ITEM_PREFERENCE_KEY = "menu_item_key";
    private final String JOURNEY_PREFERENCE_KEY = "journey_pref";
    private final String TAG_JOURNEY = "journey_tag";
    private final String TAG_PROFILE = "profile_tag";
    private final String TAG_NOTIFICATIONS = "notifications_tag";
    private final String TAG_SEARCH = "search_tag";

    private final int PROFILE_IMAGE_REQUEST_CODE = 101;



    @BindView(R.id.bottom_navigation_view) BottomNavigationViewEx mBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomNavigationView.enableShiftingMode(false);

        mBottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationViewClickListener);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int menuItem  =  pref.getInt(ITEM_PREFERENCE_KEY, 0);

        mBottomNavigationView.setCurrentItem(menuItem);
    }

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationViewClickListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int menuItem  =  pref.getInt(ITEM_PREFERENCE_KEY, -1);

            if(menuItem > -1){
                fragmentSelect(item);
            }
            return true;

        }
    };


    private void fragmentSelect(MenuItem item){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        Fragment fragment = null;
        SharedPreferences.Editor editor = pref.edit();
        int index = mBottomNavigationView.getMenuItemPosition(item);
        editor.putInt(ITEM_PREFERENCE_KEY, index);
        editor.apply();

        //Fragment frJourneys = manager.findFragmentByTag(String.valueOf(index));

        switch (item.getItemId()){
            case R.id.menu_home:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_JOURNEY);
                if(fragment == null){
                    fragment = new JourneysFragment();
                }
                break;
            case R.id.menu_notifications:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_NOTIFICATIONS);
                if(fragment == null){
                    fragment = new NotificationsFragment();
                }
                break;
            case R.id.menu_profile:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_PROFILE);
                if(fragment == null){
                    fragment = new ProfileFragment();
                }
                break;
            case R.id.menu_search:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_SEARCH);
                if(fragment == null){
                    fragment = new DiscoverFragment();
                }
                break;

        }
        if (fragment != null) {
            fragmentTransaction.replace(R.id.fragments_container, fragment,
                    String.valueOf(index)).commit();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void journeysListInteractionListener(Journey journey) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(JOURNEY_PREFERENCE_KEY, new JourneyUtils().loadStringFromJourney(journey));
        editor.apply();
        Intent intent = new Intent(MainActivity.this, JourneyProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public void OnProfileFollowingJourneysInteractionListener(Uri uri) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        getSupportFragmentManager().putFragment(outState, ProfileFragment.class.getSimpleName(), new ProfileFragment());
    }
}
