package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.CreatedBy;
import nonso.android.nonso.models.CreatorType;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.viewModel.PostViewModel;

public class CreatePostReplyActivity extends AppCompatActivity {

    @BindView(R.id.create_post_reply_title)
    TextView mPostTitle;
    @BindView(R.id.create_post_reply_body)
    EditText mPostBody;



    private Post mParentPost;
    private String PARENT_POST = "parent_post";
    private final String TAB_POSITION_EXTRA = "tab_position_extra";
    private final String JOURNEY_EXTRA_ID_KEY = "journey_extra";

    private User mUser;
    private PostViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_reply);

        ButterKnife.bind(this);

        mParentPost = getIntent().getParcelableExtra(PARENT_POST);
        mViewModel = ViewModelProviders.of(this).get(PostViewModel.class);

        mPostTitle.setText(mParentPost.getTitle());

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post_reply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void getUser(){
        mViewModel.getCurrentUser(new Callback() {
            @Override
            public void result(Result result) {
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
                mUser = user;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_post_reply_menu_post:
                publish();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, JourneyProfileActivity.class);
                intent.putExtra(JOURNEY_EXTRA_ID_KEY, mParentPost.getJourneyId());
                intent.putExtra(TAB_POSITION_EXTRA, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void publish(){
        Post post = new Post();
        post.setBody(mPostBody.getText().toString());
        post.setJourneyId(mParentPost.getJourneyId());
        post.setCreatorId(mUser.getUserId());

        final Context context = this;
        mViewModel.savePostReply(mParentPost, post, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        Intent intent = new Intent(context, JourneyProfileActivity.class);
                        intent.putExtra(JOURNEY_EXTRA_ID_KEY, mParentPost.getJourneyId());
                        intent.putExtra(TAB_POSITION_EXTRA, 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case FAILED:
                        Toast.makeText(context, "Failed to save post.. ", Toast.LENGTH_LONG).show();
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
