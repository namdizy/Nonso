package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private ArrayList mSteps;
    private Context mContext;
    private StepsAdapterOnClickListener mAdapterOnClickListener;

    public interface StepsAdapterOnClickListener{
        void onStepItemClick();
    }

    public StepsAdapter(Context context, StepsAdapterOnClickListener listener){
        mContext = context;
        mAdapterOnClickListener = listener;
    }


    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(mSteps == null) return 0;
        else return mSteps.size();
    }

    public void setStepsData(ArrayList steps){
        mSteps = steps;
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public StepsViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
