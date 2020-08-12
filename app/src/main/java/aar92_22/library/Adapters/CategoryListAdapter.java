package aar92_22.library.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aar92_22.library.Database.CategoryEntry;
import aar92_22.library.Interfaces.CategoryListener;
import aar92_22.library.R;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    private List<CategoryEntry> categoryEntries;
    private Context mContext;
    private categoryOptionsListener mListener;

    public CategoryListAdapter(Context mContext, categoryOptionsListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.category_recycler_view_individual,
                parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        final CategoryEntry categoryEntry = categoryEntries.get(position);

        final String categoryName = categoryEntry.getCategory();

        holder.categoryDisplay.setText(categoryName);

        holder.editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEditClick(categoryEntry);
            }
        });

        holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteClick(categoryEntry.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        if(categoryEntries == null) {
            return 0;
        }
        return categoryEntries.size();
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder  {


        TextView categoryDisplay;
        ImageView editCategory;
        ImageView deleteCategory;


        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryDisplay = itemView.findViewById(R.id.category_name_display);
            editCategory = itemView.findViewById(R.id.edit_category);
            deleteCategory = itemView.findViewById(R.id.delete_category);

        }

    }


    public void setCategoryEntries (List<CategoryEntry> categoryEntries){
        this.categoryEntries = categoryEntries;
        notifyDataSetChanged();
    }


    public interface categoryOptionsListener {
        void onEditClick(CategoryEntry categoryEntry);
        void onDeleteClick(int id);
    }



}
