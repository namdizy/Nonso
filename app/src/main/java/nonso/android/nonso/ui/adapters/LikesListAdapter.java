package nonso.android.nonso.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class LikesListAdapter extends RecyclerView.Adapter<LikesListAdapter.LikesListViewHolder> {


    @NonNull
    @Override
    public LikesListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesListViewHolder likesListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LikesListViewHolder extends RecyclerView.ViewHolder{


        public LikesListViewHolder(View view){
            super(view);
        }
    }
}
