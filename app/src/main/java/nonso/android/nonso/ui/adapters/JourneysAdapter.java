package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Journey;

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.JourneysViewHolder> {

    private ArrayList<Journey> mJourneys;
    private final JourneysAdapterOnClickHandler mOnClickListener;

    private final String TAG = JourneysAdapter.class.getName();

    public interface JourneysAdapterOnClickHandler{
        void onJourneyItemClick(Journey journey);
    }


    public JourneysAdapter(JourneysAdapterOnClickHandler clickHandler){
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
    public void onBindViewHolder(@NonNull JourneysViewHolder holder, int position) {
        Journey journey = mJourneys.get(position);
        holder.mJourneyTitle.setText(journey.getName());
    }

    @Override
    public int getItemCount() {
        if(mJourneys == null) return 0;
        else return mJourneys.size();
    }

    public class JourneysViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.profile_journey_item_title) TextView mJourneyTitle;

        public JourneysViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);

            Log.v(TAG, "starting the view holder: "+ mJourneys);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Journey journey = mJourneys.get(position);

        }
    }

    public void setJourneysData(ArrayList<Journey> data){
        mJourneys = data;
    }
}
