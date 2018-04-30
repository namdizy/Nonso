package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.ui.fragments.JourneysFragment;
import nonso.android.nonso.ui.fragments.JourneysListFragment;
import nonso.android.nonso.ui.fragments.NotificationsFragment;
import nonso.android.nonso.ui.fragments.ProfileFragment;
import nonso.android.nonso.ui.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        JourneysFragment.OnFragmentInteractionListener, NotificationsFragment.OnFragmentInteractionListener,
        JourneysListFragment.OnJourneysListFragmentListener{

    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private final String ITEM_PREFERENCE_KEY = "menu_item_key";

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
                fragmentSelect(item);
                return true;
            }


        });

        MenuItem selectedItem;

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int menuItem  =  pref.getInt(ITEM_PREFERENCE_KEY, -1);

        if (savedInstanceState == null) {

            if(menuItem==-1){
                selectedItem = mBottomNavigationView.getMenu().getItem(0);
                fragmentSelect(selectedItem);
            }else{
                mBottomNavigationView.setCurrentItem(menuItem);
                selectedItem = mBottomNavigationView.getMenu().getItem(menuItem);
                fragmentSelect(selectedItem);
            }

        }

    }

    private void fragmentSelect(MenuItem item){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        SharedPreferences.Editor editor = pref.edit();
        int index = mBottomNavigationView.getMenuItemPosition(item);
        editor.putInt(ITEM_PREFERENCE_KEY, index);
        editor.apply();

        switch (item.getItemId()){
            case R.id.menu_home:
                JourneysFragment journeysFragment = new JourneysFragment();
                fragmentTransaction
                        .replace(R.id.fragments_container, journeysFragment)
                        .commit();
                break;
            case R.id.menu_notifications:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                fragmentTransaction
                        .replace(R.id.fragments_container, notificationsFragment)
                        .commit();
                break;
            case R.id.menu_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                fragmentTransaction
                        .replace(R.id.fragments_container, profileFragment)
                        .commit();
                break;
            case R.id.menu_search:
                SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction
                        .replace(R.id.fragments_container, searchFragment)
                        .commit();
                break;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onJourneysListInteraction(Journey journey) {
        Toast.makeText(MainActivity.this, "Journey clicked ", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, JourneyActivity.class);
        intent.putExtra("journey", journey);
        startActivity(intent);
    }
}
