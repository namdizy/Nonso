package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.StepType;
import nonso.android.nonso.models.User;
import nonso.android.nonso.utils.DateUtils;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private ArrayList<Step> mSteps;
    private Context mContext;
    private StepsAdapterOnClickListener mAdapterOnClickListener;

    private static ViewPager mPager;
    private static int currentPage = 0;

    public interface StepsAdapterOnClickListener{
        void onStepItemClick(Step step);
        void onMenuEditClick(Step step);
        void onMenuDeleteClick(Step step);
        void onAddCheerClick(Step step);
    }

    public StepsAdapter(Context context, StepsAdapterOnClickListener listener){
        mContext = context;
        mAdapterOnClickListener = listener;
    }


    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.steps_list_item, parent, false);

        return new StepsAdapter.StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsViewHolder holder, int position) {

        final Step step = mSteps.get(position);

        holder.mStepTitle.setText(step.getTitle());
        holder.mStepDescription.setText(step.getDescription());

        Date date = step.getCreatedAt();
        DateUtils dateUtils = new DateUtils();
        if(step.getStepType()== StepType.IMAGES){
            holder.mImageViewContainer.setVisibility(View.VISIBLE);

            Collection<Image> imageCollection = step.getImages().values();
            ArrayList<String> imageUris = new ArrayList<>();

            for(Image im: imageCollection){
                imageUris.add(im.getImageUrl());
            }

            holder.mViewPager.setAdapter(new ImageSlideAdapter(mContext, imageUris));
            holder.mCircleIndicator.setViewPager(holder.mViewPager);

            RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.step_item_images_container);
            params.setMargins(50, 10, 0, 0);
            holder.mStepTitle.setLayoutParams(params);
        }

        if(step.getStepType() == StepType.VIDEO){
            initializePlayer(Uri.parse(step.getVideo().getVideoUrl()), holder.mExoplayerView, holder.mExoPlayer);
        }


        holder.mStepCreatedTime.setText(dateUtils.getTimeAgo(date, mContext));
        holder.mAddCheer.setOnClickListener(v ->
                mAdapterOnClickListener.onAddCheerClick(step)
        );

        holder.mStepMoreBtn.setOnClickListener(v -> {

                PopupMenu menu = new PopupMenu(mContext, holder.mStepMoreBtn);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.step_item_menu, menu.getMenu());
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.step_item_menu_delete:
                               mAdapterOnClickListener.onMenuDeleteClick(step);
                               return true;
                           case R.id.step_item_menu_edit:
                               mAdapterOnClickListener.onMenuEditClick(step);
                               return true;
                           case R.id.step_item_add_cheer_text:
                               return true;
                           default:
                               return false;
                       }
                    }
                });
        });

    }

    public void initializePlayer(Uri mediaUri, SimpleExoPlayerView playerView, SimpleExoPlayer mExoPlayer){
        if(mExoPlayer == null){
            playerView.setVisibility(View.VISIBLE);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            mExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            playerView.setPlayer(mExoPlayer);

            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, "BakingApp"), defaultBandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);


            //mExoPlayer.seekTo(playerPosition);
            mExoPlayer.prepare(videoSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public int getItemCount() {
        if(mSteps == null) return 0;
        else return mSteps.size();
    }

    public void setStepsData(ArrayList<Step> steps){
        mSteps = steps;
        notifyDataSetChanged();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.step_item_title) TextView mStepTitle;
        @BindView(R.id.step_item_description) TextView mStepDescription;
        @BindView(R.id.step_item_more_btn) ImageButton mStepMoreBtn;
        @BindView(R.id.step_item_created_time) TextView mStepCreatedTime;
        @BindView(R.id.step_item_add_cheer_container) LinearLayout mAddCheer;
        SimpleExoPlayerView mExoplayerView;
        SimpleExoPlayer mExoPlayer;

        @BindView(R.id.step_item_pager) ViewPager mViewPager;
        @BindView(R.id.step_item_indicator) CircleIndicator mCircleIndicator;
        @BindView(R.id.step_item_images_container) RelativeLayout mImageViewContainer;

        public StepsViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Step step = mSteps.get(position);
            mAdapterOnClickListener.onStepItemClick(step);
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
