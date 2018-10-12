package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;

public class StepDetailsActivity extends AppCompatActivity {

    @BindView(R.id.step_details_webView)
    WebView mWebViewView;
    @BindView(R.id.step_details_title) TextView mStepTitle;
    @BindView(R.id.step_details_close_btn) ImageButton mCloseBtn;
    @BindView(R.id.step_details_cheers_container) LinearLayout mCheersContainer;
    @BindView(R.id.step_details_constraint_container) ConstraintLayout mConstriantContainer;

    private final String STEP_EXTRA = "step_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private Step mStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mStep = intent.getParcelableExtra(STEP_EXTRA);
        mStepTitle.setText(mStep.getTitle());



        if(!mStep.getPublish()){
            mCheersContainer.setVisibility(View.GONE);
            ConstraintSet constraintSet =  new ConstraintSet();
            constraintSet.clone(mConstriantContainer);
            constraintSet.connect(R.id.step_details_title,ConstraintSet.RIGHT,ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,0);
            constraintSet.applyTo(mConstriantContainer);
        }

        mWebViewView.loadDataWithBaseURL(null, mStep.getBodyText(), "test/html", "UTF=8", null);

    }

    @OnClick(R.id.step_details_close_btn)
    public void close(View view){
        Intent intent = new Intent(this, JourneyProfileActivity.class);
        intent.putExtra(JOURNEY_EXTRA_ID_KEY, mStep.getCreatedBy().getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
