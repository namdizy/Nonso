package nonso.android.nonso.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_back_btn) ImageButton mBackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.search_back_btn)
    public void backBtnClicked(View view){

        finish();
    }




}
