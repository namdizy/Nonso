package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.square1.richtextlib.ui.RichContentView;
import io.square1.richtextlib.v2.RichTextV2;
import io.square1.richtextlib.v2.content.RichTextDocumentElement;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;

public class StepDetailsActivity extends AppCompatActivity {

    @BindView(R.id.step_details_text_content) RichContentView mContentView;
    @BindView(R.id.step_details_title)
    TextView mStepTitle;
    @BindView(R.id.step_details_close_btn) ImageButton mCloseBtn;

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

        RichTextDocumentElement element = RichTextV2.textFromHtml(this, mStep.getBodyText());
        mContentView.setText(element);

    }

    @OnClick(R.id.step_details_close_btn)
    public void close(View view){
        Intent intent = new Intent(this, JourneyProfileActivity.class);
        intent.putExtra(JOURNEY_EXTRA_ID_KEY, mStep.getCreatedBy().getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
