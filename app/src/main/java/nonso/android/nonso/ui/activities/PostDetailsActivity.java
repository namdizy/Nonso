package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.utils.DateUtils;

public class PostDetailsActivity extends AppCompatActivity {


    @BindView(R.id.post_details_title) TextView mTitle;
    @BindView(R.id.post_details_creator_image) CircleImageView mCreatorImage;
    @BindView(R.id.post_details_creator_name) TextView mCreatorName;
    @BindView(R.id.post_details_body) TextView mBody;
    @BindView(R.id.post_details_created_time) TextView mTimeCreated;
    @BindView(R.id.post_details_replies) TextView mReplies;
    @BindView(R.id.post_details_comments_recyclerview) RecyclerView mRecyclerView;

    private Post mPost;

    private String PARENT_POST = "parent_post";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";
    private final String TAB_POSITION_EXTRA = "tab_position_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPost = getIntent().getParcelableExtra(PARENT_POST);

        updateUI();

    }

    public void updateUI(){

        mCreatorName.setText(mPost.getCreatedBy().getName());
        mBody.setText(mPost.getBody());
        mTitle.setText(mPost.getTitle());


        Date date = mPost.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        mTimeCreated.setText(dateUtils.getTimeAgo(date, this));

        if(!mPost.getComments().isEmpty()){
            String size =  String.valueOf(mPost.getComments().size()) + " comments";
            mReplies.setText(size);
        }else{
            mReplies.setVisibility(View.GONE);
        }


        Picasso.with(this).load(mPost.getCreatedBy().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(mCreatorImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, JourneyProfileActivity.class);
                intent.putExtra(JOURNEY_EXTRA_ID_KEY, mPost.getJourneyId());
                intent.putExtra(TAB_POSITION_EXTRA, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
