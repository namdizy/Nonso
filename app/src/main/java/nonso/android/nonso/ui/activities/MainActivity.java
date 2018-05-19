package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.DiscoverFragment;
import nonso.android.nonso.ui.fragments.JourneysFragment;
import nonso.android.nonso.ui.fragments.JourneysListFragment;
import nonso.android.nonso.ui.fragments.NotificationsFragment;
import nonso.android.nonso.ui.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements DiscoverFragment.OnFragmentInteractionListener,
        JourneysFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener,
        JourneysListFragment.OnJourneysListFragmentListener{

    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private final String ITEM_PREFERENCE_KEY = "menu_item_key";
    private final String JOURNEY_EXTRA_DATA = "journey_extra";

    @BindView(R.id.bottom_navigation_view) BottomNavigationViewEx mBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mBottomNavigationView.enableShiftingMode(false);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int menuItem  =  pref.getInt(ITEM_PREFERENCE_KEY, -1);

                if(menuItem > -1){
                    fragmentSelect(item);
                }
                Log.v(TAG, "=============  I am not sure  ==========");
                return true;

            }


        });

        MenuItem selectedItem;

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int menuItem  =  pref.getInt(ITEM_PREFERENCE_KEY, -1);


        //TODO: this caused the fragments to be created twice. Need to fix
//        if (savedInstanceState == null) {
//
//            if(menuItem==-1){
//                selectedItem = mBottomNavigationView.getMenu().getItem(0);
//                fragmentSelect(selectedItem);
//                Log.v(TAG, "=============this is the main activity==========");
//
//            }else{
//                mBottomNavigationView.setCurrentItem(menuItem);
//                selectedItem = mBottomNavigationView.getMenu().getItem(menuItem);
//                fragmentSelect(selectedItem);
//                Log.v(TAG, "=============  woooooot  ==========");
//
//            }
//
//        }

    }

    private void fragmentSelect(MenuItem item){


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;

        SharedPreferences.Editor editor = pref.edit();
        int index = mBottomNavigationView.getMenuItemPosition(item);
        editor.putInt(ITEM_PREFERENCE_KEY, index);
        editor.apply();

        switch (item.getItemId()){
            case R.id.menu_home:
                fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(index));
                if(fragment == null){
                    fragment = new JourneysFragment();
                }
                break;
            case R.id.menu_notifications:
                fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(index));
                if(fragment == null){
                    fragment = new NotificationsFragment();
                }
                break;
            case R.id.menu_profile:
                fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(index));
                if(fragment == null){
                    fragment = new ProfileFragment();
                }
                break;
            case R.id.menu_search:
                fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(index));
                if(fragment == null){
                    fragment = new DiscoverFragment();
                }
                break;

        }
        if (fragment != null) {
            fragmentTransaction.replace(R.id.fragments_container, fragment,
                    String.valueOf(index)).commitAllowingStateLoss();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onJourneysListInteraction(Journey journey) {
        Intent intent = new Intent(MainActivity.this, JourneyProfileActivity.class);
        intent.putExtra(JOURNEY_EXTRA_DATA, journey);
        startActivity(intent);
    }
}
