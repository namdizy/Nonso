package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
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

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesAdapterViewHolder>{


    private ArrayList<String> mImagesUrls;

    public ImagesAdapter(){

    }

    @NonNull
    @Override
    public ImagesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.images_grid, parent, false);

        return new ImagesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapterViewHolder holder, int position) {

        String imageUrl = mImagesUrls.get(position);

        Context context = holder.mImageView.getContext();

//        Picasso.with(context).load(imageUrl).into(holder.mImageView);

        holder.mImageView.setImageURI(Uri.parse(imageUrl));
    }

    @Override
    public int getItemCount() {
        if(mImagesUrls == null) return 0;
        return mImagesUrls.size();
    }

    public class ImagesAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imv_image_item) ImageView mImageView;

        public ImagesAdapterViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public void setImagesUrls(ArrayList images){
        mImagesUrls = images;
        notifyDataSetChanged();
    }
}
