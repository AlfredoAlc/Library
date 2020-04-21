package aar92_22.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder> {


    Context context;


    public SearchResultsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SearchResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SearchResultsHolder extends RecyclerView.ViewHolder {


        public SearchResultsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}




