package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.User;

public class LikesImageAdapter extends RecyclerView.Adapter<LikesImageAdapter.LikesViewHolder>{

    private ArrayList<User> mUsersList;
    private Context mContext;
    private LikesAdapterClickListener mClickListener;

    public LikesImageAdapter(Context context, LikesAdapterClickListener onClickListener){
        mContext = context;
        mClickListener = onClickListener;
    }

    public interface LikesAdapterClickListener{
        void imageItemClick(ArrayList usersList);
    }

    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == R.layout.likes_image_list_item){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_image_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_list_more_btn, parent, false);
        }

        return new LikesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {

        if(position == mUsersList.size()) {
            holder.mMoreBtn.setVisibility(View.VISIBLE);
            holder.mMoreBtn.setOnClickListener(view ->
                    Toast.makeText(mContext, "Button Clicked", Toast.LENGTH_LONG).show()
            );
        }
        else {
            User user = mUsersList.get(position);
            Picasso.with(mContext).load(user.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                    .error(R.drawable.profile_image_placeholder).into(holder.mLikesImage);
            holder.mLikesImage.setOnClickListener(listener -> mClickListener.imageItemClick(mUsersList));
        }



    }

    @Override
    public int getItemCount() {
        return mUsersList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mUsersList.size()) ?  R.layout.likes_list_more_btn : R.layout.likes_image_list_item;
    }

    public void setUsers(ArrayList users){
        mUsersList = users;
    }

    public class LikesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mLikesImage;
        ImageView mMoreBtn;

        @Override
        public void onClick(View view) {

        }

        public LikesViewHolder(View view){
            super(view);
            mLikesImage = view.findViewById(R.id.likes_image_item);
            mMoreBtn = view.findViewById(R.id.likes_image_list_more_btn);
            view.setOnClickListener(this);
        }
    }
}