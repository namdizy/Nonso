package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.ui.activities.CreatePostReplyActivity;
import nonso.android.nonso.utils.DateUtils;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mPostList;
    private PostAdapterOnclickHandler onclickHandler;
    private Context mContext;

    private Post currentPost;

    private String JOURNEY_ID = "journey_id";
    private String POST_ID = "post_id";


    public interface PostAdapterOnclickHandler{
        void onReplyClick(Post post);
        void onCommentClick(Post post);
    }

    public PostAdapter(Context context, PostAdapterOnclickHandler handler){
        mContext = context;
        onclickHandler = handler;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.post_list_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = mPostList.get(position);
        currentPost = post;
        holder.mPostTitle.setText(post.getTitle());
        holder.mPostBody.setText(post.getBody());
        holder.mPostCreatorName.setText(post.getCreatedBy().getName());

        String size = "0 " + "comments";
        if(!post.getComments().isEmpty()){
           size =  String.valueOf(post.getComments().size()) + "comments";
        }

        holder.mComments.setText(size);

        Date date = post.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        holder.mPostCreatedTime.setText(dateUtils.getTimeAgo(date, mContext));

        holder.mReplyBtn.setOnClickListener(this::replyOnclick);
        holder.mComments.setOnClickListener(this::onCommentsClick);

    }

    public void replyOnclick(View v){
        onclickHandler.onReplyClick(currentPost);
    }

    public void onCommentsClick(View v){
        onclickHandler.onCommentClick(currentPost);
    }
    @Override
    public int getItemCount() {
        if(mPostList == null) return 0;
        else return mPostList.size();
    }

    public void setPostList(List<Post> posts ){
        mPostList = posts;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.post_item_title) TextView mPostTitle;
        @BindView(R.id.post_item_body) TextView mPostBody;
        @BindView(R.id.post_created_time) TextView mPostCreatedTime;
        @BindView(R.id.post_creator_name) TextView mPostCreatorName;
        @BindView(R.id.post_creator_image) CircleImageView mCreatorImage;
        @BindView(R.id.post_item_reply_container) LinearLayout mReplyBtn;
        @BindView(R.id.post_item_replies) TextView mComments;


        public PostViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
