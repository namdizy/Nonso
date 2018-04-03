package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class CreateJourneyActivity extends AppCompatActivity {

    @BindView(R.id.btn_create_journeys_back) ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journey);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_create_journeys_back)
    public void backOnClick(View view){

        Intent intent = new Intent(this, MainActivity.class);
        getApplication().startActivity(intent);
    }
}
