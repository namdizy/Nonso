package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.ui.adapters.PostAdapter;
import nonso.android.nonso.ui.adapters.RepliesAdapter;
import nonso.android.nonso.utils.DateUtils;
import nonso.android.nonso.viewModel.PostViewModel;

public class PostDetailsActivity extends AppCompatActivity implements RepliesAdapter.RepliesAdapterOnClickHandler {


    @BindView(R.id.post_details_title) TextView mTitle;
    @BindView(R.id.post_details_creator_image) CircleImageView mCreatorImage;
    @BindView(R.id.post_details_creator_name) TextView mCreatorName;
    @BindView(R.id.post_details_body) TextView mBody;
    @BindView(R.id.post_details_created_time) TextView mTimeCreated;
    @BindView(R.id.post_details_comments_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.post_details_replies_editor) EditText mReplyComment;
    @BindView(R.id.post_details_replies_post) ImageButton mPostReply;

    private Post mPost;
    private PostViewModel mViewModel;
    private String mUserId;

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
        mViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        mUserId = mViewModel.getCurrentUserId();

        updateUI();

    }

    public void updateUI(){

        //mCreatorName.setText(mPost.getCreatedBy().getName());
        mBody.setText(mPost.getBody());
        mTitle.setText(mPost.getTitle());


        Date date = mPost.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        mTimeCreated.setText(dateUtils.getTimeAgo(date, this));

//        if(!mPost.getComments().isEmpty()){
//            String size =  String.valueOf(mPost.getComments().size()) + " comments";
//            mReplies.setText(size);
//        }else{
//            mReplies.setVisibility(View.GONE);
//        }


        mViewModel.setRepliesList(mPost.getDocumentReference());
        mViewModel.getReplies().observe(this, this::updateRecyclerView);



//        Picasso.with(this).load(mPost.getCreatedBy().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
//                .error(R.drawable.profile_image_placeholder).into(mCreatorImage);
    }

    public void updateRecyclerView(ArrayList<Post> replies){
        RecyclerView.LayoutManager repliesLayoutManager;
        RepliesAdapter mRepliesAdapter;

        repliesLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(repliesLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRepliesAdapter = new RepliesAdapter(this, this);
        mRecyclerView.setAdapter(mRepliesAdapter);

        mRepliesAdapter.setReplies(replies);

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


    @OnClick(R.id.post_details_comment_container)
    public void onCommentClick(View view){
        mReplyComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mReplyComment, InputMethodManager.SHOW_IMPLICIT);
    }

    @OnClick(R.id.post_details_replies_post)
    public void onReplyClick(View view){
        String reply = mReplyComment.getText().toString();
        Post post = new Post();
        post.setCreatorId(mUserId);
        post.setJourneyId(mPost.getJourneyId());
        post.setBody(reply);

        mViewModel.savePostReply(mPost, post, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case FAILED:
                        break;
                    case SUCCESS:
                        break;
                }
            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }

            @Override
            public void journeyResult(Journey journey) {

            }

            @Override
            public void stepResult(Step step) {

            }

            @Override
            public void userResult(User user) {

            }
        });

    }


}
