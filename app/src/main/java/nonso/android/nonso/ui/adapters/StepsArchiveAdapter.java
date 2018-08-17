package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.utils.DateUtils;

public class StepsArchiveAdapter extends RecyclerView.Adapter<StepsArchiveAdapter.StepsViewHolder> {

    private Context mContext;
    private StepsArchiveOnClickListener mClickListener;
    private ArrayList<Step> mSteps;

    public interface StepsArchiveOnClickListener{
        void onStepItemClick(Step step);
        void onMenuEditClick(Step step);
        void onMenuDeleteClick(Step step);
    }

    public StepsArchiveAdapter(Context context, StepsArchiveOnClickListener clickListener){

        mContext = context;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.steps_archive_item, parent, false);

        return new StepsArchiveAdapter.StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsViewHolder holder, int position) {
        final Step step = mSteps.get(position);

        holder.mStepTitle.setText(step.getTitle());
        holder.mStepDescription.setText(step.getDescription());

        Date date = step.getCreatedAt();
        holder.mStepCreatedAt.setText(new DateUtils().getTimeAgo(date, mContext));

        holder.mStepMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(mContext, holder.mStepMenuBtn);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.step_item_menu, menu.getMenu());
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.step_item_menu_delete:
                                mClickListener.onMenuDeleteClick(step);
                                return true;
                            case R.id.step_item_menu_edit:
                                mClickListener.onMenuEditClick(step);
                                return true;
                            case R.id.step_item_menu_make_last:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
    }

    public void setStep(ArrayList<Step> steps){
        mSteps = steps;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(mSteps == null) return 0;
        else return mSteps.size();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.step_archive_item_created_time) TextView mStepCreatedAt;
        @BindView(R.id.step_archive_item_description) TextView mStepDescription;
        @BindView(R.id.step_archive_item_title) TextView mStepTitle;
        @BindView(R.id.step_archive_item_more_btn) ImageButton mStepMenuBtn;

        public StepsViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Step step = mSteps.get(position);
            mClickListener.onStepItemClick(step);
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
