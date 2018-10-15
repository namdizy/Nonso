package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.LikeType;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.ui.fragments.LikesFragment;
import nonso.android.nonso.ui.fragments.PostFragment;
import nonso.android.nonso.viewModel.PostViewModel;

public class PostDetailsActivity extends AppCompatActivity implements PostFragment.OnPostInteractionListener, LikesFragment.OnLikesFragmentInteractionListener {

    private Post mPost;
    private User mPostCreator;
    private User mCurrentUser;
    private PostViewModel mViewModel;


    private String PARENT_POST = "parent_post";

    private final String TAG = this.getClass().getSimpleName();

    private FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {

            String POST_CREATOR = "post_creator";

            getSupportActionBar().setTitle("Comments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            mPost = getIntent().getParcelableExtra(PARENT_POST);
            mPostCreator = getIntent().getParcelableExtra(POST_CREATOR);
            mViewModel = ViewModelProviders.of(this).get(PostViewModel.class);


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
                    mCurrentUser = user;

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.post_container, new PostFragment().newInstance(mPost, mPostCreator, mCurrentUser)).commitNow();
                }
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String JOURNEY_EXTRA_ID_KEY = "journey_extra";
        String TAB_POSITION_EXTRA = "tab_position_extra";

        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    if(fragmentManager.getBackStackEntryCount() == 1 ){
                        getSupportActionBar().setTitle("Comments");
                    }
                    fragmentManager.popBackStack();
                } else {
                    Intent intent = new Intent(this, JourneyProfileActivity.class);
                    intent.putExtra(JOURNEY_EXTRA_ID_KEY, mPost.getJourneyId());
                    intent.putExtra(TAB_POSITION_EXTRA, 1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReplyClick(Post post, User postCreator) {

        if(post.getTitle() == null){
            getSupportActionBar().setTitle("Replies to " + postCreator.getUserName());
        }else{
            getSupportActionBar().setTitle("Comments");
        }

        PostFragment fragment = new PostFragment().newInstance(post, postCreator, mCurrentUser);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.post_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onLikedClicked(Post post) {
        Like like = new Like();
        like.setCreatorId(mCurrentUser.getUserId());
        like.setLikeType(LikeType.LIKE);

        Context context = this;
        mViewModel.likePost(post, like, mCurrentUser, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        break;
                    case FAILED:
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

    @Override
    public void onUnLikedClicked(Post post) {


        mViewModel.unLikePost(post, mCurrentUser, new Callback() {
            @Override
            public void result(Result result) {
                switch (result){
                    case SUCCESS:
                        break;
                    case FAILED:
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

    @Override
    public void onLikeImageItemClick(ArrayList users) {

        if(users.size() > 1){
            getSupportActionBar().setTitle(users.size() + " Likes");
        }else{
            getSupportActionBar().setTitle(users.size() + " Like");
        }


        LikesFragment fragment = new LikesFragment().newInstance(users);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.post_container, fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    @Override
    public void onLikesFragmentUserPressed(User user) {

    }
}
