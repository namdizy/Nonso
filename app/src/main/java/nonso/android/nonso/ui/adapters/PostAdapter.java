package nonso.android.nonso.ui.adapters;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.User;
import nonso.android.nonso.ui.activities.CreatePostReplyActivity;
import nonso.android.nonso.utils.DateUtils;
import nonso.android.nonso.viewModel.PostViewModel;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mPostList;
    private List<User> mUsersList;
    private PostAdapterOnclickHandler onclickHandler;
    private Context mContext;
    private Post currentPost;

    public interface PostAdapterOnclickHandler{
        void onCommentClick(Post post);
        void onMenuDeleteClick(Post post);
        void onMenuEditClick(Post post);
        void onLikeClick(Post post);
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

        final Post post = mPostList.get(position);
        currentPost = post;
        holder.mPostTitle.setText(post.getTitle());
        holder.mPostBody.setText(post.getBody());

        for(User user: mUsersList){
            if(user.getUserId().equals(post.getCreatorId())){
                holder.mPostCreatorName.setText(user.getUserName());
                Picasso.with(mContext).load(user.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                        .error(R.drawable.profile_image_placeholder).into(holder.mCreatorImage);
            }
        }

        if(post.getRepliesCount() > 0){
            String size;
            if(post.getRepliesCount() == 1){
                size = "1 Comment";
            }
            else{
                size =  String.valueOf(post.getRepliesCount()) + " Comments";
            }

           holder.mNumComments.setText(size);
        }else{
            holder.mNumComments.setVisibility(View.GONE);
        }

        if(post.getLikesCount() > 0){
            String size;
            if(post.getRepliesCount() == 1){
                size =  "1 Like";
            }
            else{
                size =  String.valueOf(post.getLikesCount()) + " Likes";
            }

            holder.mNumLikes.setText(size);
        }else{
            holder.mNumLikes.setVisibility(View.GONE);
        }


//        if(!post.getLikes().isEmpty()){
//            if(post.getLikes().get(mUserId)){
//                holder.mLikeImage.setImageResource(R.drawable.ic_like_filled);
//                holder.mLikeText.setText("Liked");
//            }
//        }

        Date date = post.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        holder.mPostCreatedTime.setText(dateUtils.getTimeAgo(date, mContext));

        holder.mCommentBtn.setOnClickListener(this::onCommentsClick);
        holder.mLikeBtn.setOnClickListener(this::onLikeClick);
        holder.mNumComments.setOnClickListener(this::onCommentsClick);
        holder.mNumLikes.setOnClickListener(this::onCommentsClick);

        holder.mMoreBtn.setOnClickListener(v -> {

            PopupMenu menu = new PopupMenu(mContext, holder.mMoreBtn);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.post_item_menu, menu.getMenu());
            menu.show();

            menu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()){
                    case R.id.post_item_menu_edit:
                        onclickHandler.onMenuEditClick(post);
                        return true;
                    case R.id.post_item_menu_delete:
                        onclickHandler.onMenuDeleteClick(post);
                        return true;
                    default:
                        return false;
                }
            });
        });

    }

    public void onLikeClick(View v){
        onclickHandler.onLikeClick(currentPost);
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

    public void setUser(List users){
        mUsersList = users;
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
        @BindView(R.id.post_item_replies) TextView mNumComments;
        @BindView(R.id.post_item_likes) TextView mNumLikes;
        @BindView(R.id.post_item_comment_container) LinearLayout mCommentBtn;
        @BindView(R.id.post_item_like_container) LinearLayout mLikeBtn;
        @BindView(R.id.post_item_like_image) ImageView mLikeImage;
        @BindView(R.id.post_item_like_text) TextView mLikeText;
        @BindView(R.id.post_item_more_btn) ImageButton mMoreBtn;


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
