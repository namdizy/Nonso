package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;


public class JourneyActivity extends AppCompatActivity {

    private Journey mJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        Intent intent = getIntent();
        mJourney = intent.getParcelableExtra("journey");

    }
}
