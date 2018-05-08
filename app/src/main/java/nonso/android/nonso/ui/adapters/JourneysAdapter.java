package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.JourneysViewHolder> {

    private ArrayList<Journey> mJourneys;
    private final JourneysAdapterOnClickHandler mOnClickListener;
    private Context mContext;

    private final String TAG = JourneysAdapter.class.getName();

    public interface JourneysAdapterOnClickHandler{
        void onJourneyItemClick(Journey journey);
    }


    public JourneysAdapter(Context context, JourneysAdapterOnClickHandler clickHandler){
        mContext = context;
        mOnClickListener = clickHandler;
    }

    @NonNull
    @Override
    public JourneysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.journeys_list_item, parent, false);

        return new JourneysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JourneysViewHolder holder, int position) {


        Journey journey = mJourneys.get(position);
        holder.mJourneyTitle.setText(journey.getName());
        holder.mJourneyDescription.setText(journey.getDescription());


        Target target = new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.mJourneyImage.setImageBitmap(bitmap);

                Palette.Builder builder = Palette.from(bitmap);
                Palette palette = builder.generate();
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();

                if(vibrantSwatch != null){
                    holder.mCard.setCardBackgroundColor(vibrantSwatch.getRgb());
                    holder.mCard.getBackground().setAlpha(128);
                    holder.mJourneyTitle.setTextColor(vibrantSwatch.getTitleTextColor());
                    holder.mJourneyDescription.setTextColor(vibrantSwatch.getTitleTextColor());
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                holder.mJourneyImage.setImageResource(R.drawable.image_view_placeholder);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        holder.mJourneyImage.setTag(target);
        Picasso.with(mContext).load(journey.getProfileImage()).placeholder(R.drawable.image_view_placeholder)
                .error(R.drawable.image_view_placeholder).into(target);
//        Picasso.with(mContext).load(journey.getProfileImage()).placeholder(R.drawable.image_view_placeholder)
//                .error(R.drawable.image_view_placeholder).into(holder.mJourneyImage);
    }

    @Override
    public int getItemCount() {
        if(mJourneys == null) return 0;
        else return mJourneys.size();
    }

    public class JourneysViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.profile_journey_item_title) TextView mJourneyTitle;
        @BindView(R.id.profile_journey_item_image) ImageView mJourneyImage;
        @BindView(R.id.profile_journey_item_description) TextView mJourneyDescription;
        @BindView(R.id.profile_journey_item_container)
        CardView mCard;

        public JourneysViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Journey journey = mJourneys.get(position);
            mOnClickListener.onJourneyItemClick(journey);
        }
    }

    public void setJourneysData(ArrayList<Journey> data){
        mJourneys = data;
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
