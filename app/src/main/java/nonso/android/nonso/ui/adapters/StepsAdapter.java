package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String DATABASE_COLLECTION_USERS = "users/";
    private DocumentReference mUserRef;
    private User mCreator;
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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.steps_list_item, parent, false);

        return new StepsAdapter.StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsViewHolder holder, int position) {

        Step step = mSteps.get(position);

        String creatorId = step.getCreatorId();
        mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(creatorId);

        holder.mStepTitle.setText(step.getTitle());
        holder.mStepDescription.setText(step.getDescription());

        mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    mCreator = snapshot.toObject(User.class);

                    //holder.mCreatorName.setText(mCreator.getUserName());
                }else{

                }
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

        public StepsViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

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
