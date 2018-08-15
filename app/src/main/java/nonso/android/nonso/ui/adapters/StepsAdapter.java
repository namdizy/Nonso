package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private ArrayList<Step> mSteps;
    private Context mContext;
    private StepsAdapterOnClickListener mAdapterOnClickListener;

    public interface StepsAdapterOnClickListener{
        void onStepItemClick(Step step);
        void onMenuEditClick(Step step);
        void onDeleteClick(Step step);
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


        holder.mStepMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(mContext, holder.mStepMoreBtn);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.step_item_menu, menu.getMenu());
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.step_item_menu_delete:
                               mAdapterOnClickListener.onDeleteClick(step);
                               return true;
                           case R.id.step_item_menu_edit:
                               mAdapterOnClickListener.onMenuEditClick(step);
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
