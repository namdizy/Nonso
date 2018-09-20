package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.User;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.LikesViewHolder>{

    private ArrayList<User> mUsersList;
    private Context mContext;
    private LikesAdapterClickListener mClickListener;

    public LikesAdapter(Context context, LikesAdapterClickListener onClickListener){
        mContext = context;
        mClickListener = onClickListener;
    }

    public interface LikesAdapterClickListener{

    }

    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.likes_image_list_item, parent, false);

        return new LikesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        User user = mUsersList.get(position);
        Picasso.with(mContext).load(user.getImage().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(holder.mLikesImage);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public void setUsers(ArrayList users){
        mUsersList = users;
    }

    public class LikesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.likes_image_item) ImageView mLikesImage;

        @Override
        public void onClick(View view) {

        }

        public LikesViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }
    }
}