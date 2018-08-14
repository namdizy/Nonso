package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.utils.DateUtils;

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.JourneysViewHolder> {

    private ArrayList<Journey> mJourneys;
    private final JourneysAdapterOnClickHandler mOnClickListener;
    private Context mContext;

    private final String TAG = JourneysAdapter.class.getName();

    public interface JourneysAdapterOnClickHandler{
        void onJourneyItemClick(String journeyId);
        void onMenuDeleteClick(Journey journey);
        void onMenuSettingClick(Journey journey);
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
        final Journey journey = mJourneys.get(position);
        holder.mJourneyTitle.setText(journey.getName());
        holder.mJourneyDescription.setText(journey.getDescription());

        holder.mJourneyCreatorName.setText(journey.getCreatedBy().getName());
        Date date = journey.getCreatedAt();

        DateUtils dateUtils = new DateUtils();
        holder.mJourneyCreatedTime.setText(dateUtils.getTimeAgo(date, mContext));

        holder.mMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext, holder.mMoreBtn);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.journey_item_menu, menu.getMenu());
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.journey_item_menu_delete:
                                mOnClickListener.onMenuDeleteClick(journey);
                                return true;
                            case R.id.journey_item_menu_settings:
                                mOnClickListener.onMenuSettingClick(journey);
                                return true;
                            default:
                                return false;

                        }
                    }
                });

            }
        });

        Picasso.with(mContext).load(journey.getProfileImage()).placeholder(R.drawable.image_view_placeholder)
                .error(R.drawable.image_view_placeholder).into(holder.mJourneyImage);
        Picasso.with(mContext).load(journey.getCreatedBy().getImageUrl()).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(holder.mJourneyCreatorImage);
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
        @BindView(R.id.profile_journey_item_container) CardView mCard;
        @BindView(R.id.profile_journey_item_more_btn) ImageButton mMoreBtn;
        @BindView(R.id.profile_journey_creator_name) TextView mJourneyCreatorName;
        @BindView(R.id.profile_journey_created_time) TextView mJourneyCreatedTime;
        @BindView(R.id.profile_journey_creator_image) ImageView mJourneyCreatorImage;

        public JourneysViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Journey journey = mJourneys.get(position);
            mOnClickListener.onJourneyItemClick(journey.getJourneyId());
        }
    }

    public void setJourneysData(ArrayList<Journey> data){
        mJourneys = data;
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

}
