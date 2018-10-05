package nonso.android.nonso.ui.activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import java.util.HashMap;

import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.ui.fragments.CategoriesFragment;

public class CategoriesActivity extends AppCompatActivity implements CategoriesFragment.OnCategoriesFragmentInteraction {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.categories_container, CategoriesFragment.newInstance())
                    .commitNow();
        }

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void OnChipSelect(String st) {

    }
}
