package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.User;
import nonso.android.nonso.utils.DateUtils;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.RepliesViewHolder> {

    private ArrayList<Post> mReplies;
    private Context mContext;
    private RepliesAdapterOnClickHandler mOnclickHandler;
    private List<User> mUsersList;
    private User mReplyCreator;


    public interface RepliesAdapterOnClickHandler{
        void onReplyLikeClicked(Post reply);
        void onReplyReplyClicked(Post reply, User replyCreator);
        void onReplyUnLikeClicked(Post reply);
        void onMenuDeleteClicked(Post reply);
        void onMenuSettingClicked(Post reply);
    }


    public RepliesAdapter(Context context, RepliesAdapterOnClickHandler onClickHandler){
        mContext = context;
        mOnclickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.replies_list_item, parent, false);
        return new RepliesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepliesViewHolder holder, int position) {

        Post reply = mReplies.get(position);
        holder.mReplyBody.setText(reply.getBody());

        Date date = reply.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        if(date != null){
            holder.mReplyDate.setText(dateUtils.getTimeAgoShort(date));
        }

        for(User user: mUsersList){
            if(user.getUserId().equals(reply.getCreatorId())){
                mReplyCreator = user;
                holder.mReplyCreatorName.setText(user.getUserName());
                Picasso.with(mContext).load(user.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                        .error(R.drawable.profile_image_placeholder).into(holder.mReplyCreatorImage);
            }
        }


        updateReplies(holder, reply);
        updateLikes(holder, reply);

        holder.mReplyLikedBtn.setOnClickListener(listener -> {
                holder.mReplyLikedBtn.setVisibility(View.GONE);
                holder.mReplyLikeBtn.setVisibility(View.VISIBLE);
                reply.setLikesCount(reply.getRepliesCount() - 1);
                updateLikes(holder, reply);
                mOnclickHandler.onReplyUnLikeClicked(reply);
            }
        );

        holder.mReplyLikeBtn.setOnClickListener(listener -> {
                holder.mReplyLikedBtn.setVisibility(View.VISIBLE);
                holder.mReplyLikeBtn.setVisibility(View.GONE);
                reply.setLikesCount(reply.getRepliesCount() + 1);
                updateLikes(holder, reply);
                mOnclickHandler.onReplyLikeClicked(reply);
            }
        );

        holder.mReplyReplyBtn.setOnClickListener(listener ->
            mOnclickHandler.onReplyReplyClicked(reply, mReplyCreator)
        );

        holder.mReplyContainer.setOnClickListener(listener ->
            mOnclickHandler.onReplyReplyClicked(reply, mReplyCreator)
        );


        holder.mReplyMenuBtn.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(mContext, holder.mReplyMenuBtn);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.journey_item_menu, menu.getMenu());
            menu.show();

            menu.setOnMenuItemClickListener(item -> {
                switch(item.getItemId()){
                    case R.id.journey_item_menu_delete:
                        mOnclickHandler.onMenuDeleteClicked(reply);
                        return true;
                    case R.id.journey_item_menu_settings:
                        mOnclickHandler.onMenuSettingClicked(reply);
                        return true;
                    default:
                        return false;

                }
            });
        });
    }

    private void updateLikes(RepliesViewHolder holder, Post reply){
        if(reply.getLikesCount() > 0){
            String size;
            if(reply.getRepliesCount() == 1){
                size =  "1 Like";
            }
            else{
                size =  String.valueOf(reply.getLikesCount()) + " Likes";
            }

            holder.mReplyLikes.setText(size);
        }else{
            holder.mReplyLikes.setVisibility(View.GONE);
        }
    }

    private void updateReplies(RepliesViewHolder holder, Post reply){
        if(reply.getRepliesCount() > 0){
            String size;
            if(reply.getRepliesCount() == 1){
                size = "1 Comment";
            }
            else{
                size =  String.valueOf(reply.getRepliesCount()) + " Comments";
            }

            holder.mReplyReplies.setText(size);
        }else{
            holder.mReplyReplies.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(mReplies == null) return 0;
        else return mReplies.size();
    }

    public void setReplies(ArrayList<Post> replies){
        mReplies = replies;
        notifyDataSetChanged();
    }

    public void setUsers(List users){
        mUsersList = users;
        notifyDataSetChanged();
    }


    public class RepliesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.reply_body) TextView mReplyBody;
        @BindView(R.id.reply_creator_name) TextView mReplyCreatorName;
        @BindView(R.id.reply_date) TextView mReplyDate;
        @BindView(R.id.reply_likes) TextView mReplyLikes;
        @BindView(R.id.reply_replies) TextView mReplyReplies;
        @BindView(R.id.reply_creator_image) CircleImageView mReplyCreatorImage;
        @BindView(R.id.reply_item_like_image) ImageView mReplyLikeBtn;
        @BindView(R.id.reply_item_reply_image) ImageView mReplyReplyBtn;
        @BindView(R.id.reply_item_liked_image) ImageView mReplyLikedBtn;
        @BindView(R.id.reply_container) LinearLayout mReplyContainer;
        @BindView(R.id.reply_menu_btn) ImageButton mReplyMenuBtn;



        public RepliesViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
