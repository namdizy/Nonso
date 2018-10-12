package nonso.android.nonso.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.interfaces.UserListCallback;
import nonso.android.nonso.ui.adapters.LikesImageAdapter;
import nonso.android.nonso.ui.adapters.RepliesAdapter;
import nonso.android.nonso.utils.DateUtils;
import nonso.android.nonso.viewModel.PostViewModel;

public class PostFragment extends Fragment implements LikesImageAdapter.LikesAdapterClickListener, RepliesAdapter.RepliesAdapterOnClickHandler {

    @BindView(R.id.post_creator_image) CircleImageView mCreatorImage;
    @BindView(R.id.post_body) TextView mBody;
    @BindView(R.id.post_title) TextView mTitle;
    @BindView(R.id.post_creator_name) TextView mCreatorName;
    @BindView(R.id.post_created_time) TextView mTimeCreated;
    @BindView(R.id.post_comment_container) LinearLayout mCommentsContainerBtn;
    @BindView(R.id.post_comment_text) TextView mCommentsBtnText;
    @BindView(R.id.post_like_container) LinearLayout mLikeContainerBtn;
    @BindView(R.id.post_liked_container) LinearLayout mLikedContainerBtn;
    @BindView(R.id.post_likes) TextView mPostLikes;
    @BindView(R.id.post_comments) TextView mReplies;
    @BindView(R.id.post_comments_recyclerview) RecyclerView mRepliesRecyclerView;
    @BindView(R.id.post_comments_recyclerview_container) LinearLayout mRepliesContainer;
    @BindView(R.id.post_likes_recyclerview) RecyclerView mLikesRecyclerView;
    @BindView(R.id.post_likes_recyclerview_container) LinearLayout mLikesContainer;
    @BindView(R.id.post_replies_editor) EditText mReplyComment;
    @BindView(R.id.post_replies_post) ImageButton mPostReply;

    private Post mReply;
    private PostViewModel mViewModel;
    private User mCurrentUser;
    private User mCreator;
    private OnPostInteractionListener mOnClickListener;

    private static final String  REPLY_PARAM = "reply_param";
    private static final String USER_CREATOR_PARAM = "creator_user_param";
    private static final String USER_CURRENT_PARAM = "current_user_param";

    public PostFragment newInstance(Post reply, User user, User currentUser) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putParcelable(REPLY_PARAM, reply);
        args.putParcelable(USER_CREATOR_PARAM, user);
        args.putParcelable(USER_CURRENT_PARAM, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReply = getArguments().getParcelable(REPLY_PARAM);
            mCreator = getArguments().getParcelable(USER_CREATOR_PARAM);
            mCurrentUser = getArguments().getParcelable(USER_CURRENT_PARAM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.post_item, container, false);
        ButterKnife.bind(this, view);


        if(savedInstanceState == null){
            mViewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
            updateUI();
        }
        return view;
    }

    void updateUI(){

        Date date = mReply.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        mTimeCreated.setText(dateUtils.getTimeAgo(date, getActivity()));
        mBody.setText(mReply.getBody());
        mCreatorName.setText(mCreator.getUserName());

        Map<String, Boolean> likedList =  mCurrentUser.getLikedPost();
        if(!likedList.containsKey(mReply.getPostId())) {
            mLikedContainerBtn.setVisibility(View.GONE);
            mLikeContainerBtn.setVisibility(View.VISIBLE);
        }else{
            mLikedContainerBtn.setVisibility(View.VISIBLE);
            mLikeContainerBtn.setVisibility(View.GONE);
        }

        if(mReply.getTitle() == null){
            mTitle.setVisibility(View.GONE);
            mCommentsBtnText.setText("Reply");
        }else {
            mTitle.setText(mReply.getTitle());
        }

        mViewModel.setRepliesList(mReply.getDocumentReference());
        mViewModel.setLikesList(mReply.getDocumentReference());
        mViewModel.getReplies().observe(this, this::updateCommentsRecyclerView);
        mViewModel.getLikesData().observe(this, this::getUsersFromLikes);

        Picasso.with(getContext()).load(mCreator.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(mCreatorImage);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.post_comment_container)
    public void onCommentClick(View view){
        mReplyComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mReplyComment, InputMethodManager.SHOW_IMPLICIT);
    }

    @OnClick(R.id.post_replies_post)
    public void saveReply(View view){
        String reply = mReplyComment.getText().toString();
        Post post = new Post();
        post.setCreatorId(mCurrentUser.getUserId());
        post.setJourneyId(mReply.getJourneyId());
        post.setBody(reply);

        if(reply.isEmpty()){
            Toast.makeText(getContext(), "Response cannot be empty!", Toast.LENGTH_LONG).show();
        }else{
            reply(post);
        }
    }

    public void reply(Post post){

        mReplyComment.setText("");
        mReply.setRepliesCount(mReply.getRepliesCount() +1);
        mReplyComment.clearFocus();
        updateCount();

        mViewModel.savePostReply(mReply, post, new Callback() {
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

    private void updateCount(){
        if(mReply.getLikesCount() > 0){
            String size ;
            if(mReply.getLikesCount() == 1){
                size = "1 Like";
            }
            else{
                size =  String.valueOf(mReply.getLikesCount()) + " Likes";
            }
            mPostLikes.setText(size);
            mPostLikes.setVisibility(View.VISIBLE);
        }else{
            mPostLikes.setVisibility(View.GONE);
        }
        if(mReply.getRepliesCount() > 0){
            String size;
            if(mReply.getRepliesCount() == 1){
                size = "1 Reply";
            }
            else{
                size =  String.valueOf(mReply.getRepliesCount()) + " Replies";
            }
            mReplies.setText(size);
            mReplies.setVisibility(View.VISIBLE);
        }else{
            mReplies.setVisibility(View.GONE);
        }
    }
    private void getUsersFromLikes(ArrayList<Like> likes){
        ArrayList<String> userIds = new ArrayList<>();
        for(Like l: likes){
            userIds.add(l.getCreatorId());
        }

        mViewModel.getUsers(userIds, new UserListCallback() {
            @Override
            public void result(Result result) {
            }

            @Override
            public void userList(ArrayList<User> users) {
                updateLikesRecyclerView(users);
            }
        });
    }



    private void updateLikesRecyclerView(ArrayList<User> users){

        if(users.size() > 0){
            mLikesContainer.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager likesLayoutManager;
            LikesImageAdapter mLikesImageAdapter;

            likesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mLikesRecyclerView.setLayoutManager(likesLayoutManager);
            mLikesRecyclerView.setHasFixedSize(true);

            mLikesImageAdapter = new LikesImageAdapter(getContext(), this);
            mLikesRecyclerView.setAdapter(mLikesImageAdapter);
            mLikesImageAdapter.setUsers(users);
        }else{
            mLikesContainer.setVisibility(View.GONE);
        }


    }

    public void updateCommentsRecyclerView(ArrayList<Post> replies){

        if(replies.size() >0 ){
            mRepliesContainer.setVisibility(View.VISIBLE);
            updateCommentRecycler(replies);
        }else{
            mRepliesContainer.setVisibility(View.GONE);
        }
    }
    private void updateCommentRecycler(ArrayList<Post> replies){
        RecyclerView.LayoutManager repliesLayoutManager;
        RepliesAdapter mRepliesAdapter;

        repliesLayoutManager = new LinearLayoutManager(getContext());
        mRepliesRecyclerView.setLayoutManager(repliesLayoutManager);
        mRepliesRecyclerView.setHasFixedSize(true);

        mRepliesAdapter = new RepliesAdapter(getContext(), this);
        mRepliesRecyclerView.setAdapter(mRepliesAdapter);

        ArrayList<String> userIds = new ArrayList<>();
        for(Post p: replies){
            userIds.add(p.getCreatorId());
        }

        mViewModel.getUsers(userIds, new UserListCallback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void userList(ArrayList<User> users) {
                mRepliesAdapter.setCurrentUser(mCurrentUser);
                mRepliesAdapter.setReplies(replies);
                mRepliesAdapter.setUsers(users);
            }
        });
    }

    @Override
    public void onMenuDeleteClicked(Post reply) {

    }

    @Override
    public void onMenuSettingClicked(Post reply) {

    }

    @Override
    public void onReplyLikeClicked(Post reply) {
        mOnClickListener.onLikedClicked(reply);
    }

    @Override
    public void onReplyUnLikeClicked(Post reply) {
        mOnClickListener.onUnLikedClicked(reply);
    }

    @Override
    public void onReplyReplyClicked(Post reply, User creator) {
        mRepliesContainer.setVisibility(View.GONE);
        mLikesContainer.setVisibility(View.GONE);
        mOnClickListener.onReplyClick(reply, creator);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostInteractionListener) {
            mOnClickListener = (OnPostInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostInteractionListener");
        }
    }

    public interface OnPostInteractionListener {
        void onReplyClick(Post post, User postCreator);
        void onLikedClicked(Post post);
        void onUnLikedClicked(Post post);
    }

}
