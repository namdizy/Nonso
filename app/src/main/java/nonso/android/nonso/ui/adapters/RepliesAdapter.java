package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        //holder.mReplyCreatorName.setText(reply.getCreatedBy().getName());

        Date date = reply.getCreatedAt();
        DateUtils dateUtils = new DateUtils();

        holder.mReplyDate.setText(dateUtils.getTimeAgoShort(date));


        for(User user: mUsersList){
            if(user.getUserId().equals(reply.getCreatorId())){
                mReplyCreator = user;
                holder.mReplyCreatorName.setText(user.getUserName());
                Picasso.with(mContext).load(user.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                        .error(R.drawable.profile_image_placeholder).into(holder.mReplyCreatorImage);
            }
        }

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


//        Picasso.with(mContext).load(reply.getCreatedBy().getImageUrl()).placeholder(R.drawable.image_view_placeholder)
//                .error(R.drawable.image_view_placeholder).into(holder.mReplyCreatorImage);
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
