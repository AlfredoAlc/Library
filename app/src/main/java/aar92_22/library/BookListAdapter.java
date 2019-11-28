package aar92_22.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aar92_22.library.Database.BookEntry;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder>
        implements Filterable {


    private List<BookEntry> mBookEntry;
    private List<BookEntry> mBookEntryFull;
    private Context mContext;
    final private ListBookClickListener mOnClickListener ;
    final private BookLongClickListener mOnLongClickListener;



    private boolean listView;





    public BookListAdapter ( Context mContext, ListBookClickListener mOnClickListener,
                             BookLongClickListener mOnLongClickListener, boolean listView){
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
        this.mOnLongClickListener = mOnLongClickListener;
        this.listView = listView;
    }


    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForBookList = R.layout.individual_book_recycler_view;


        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(layoutIdForBookList,parent,false);
        BookViewHolder bookViewHolder  = new BookViewHolder(view);

        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        BookEntry book = mBookEntry.get(position);
        String title = book.getTitle();
        String author = book.getAuthor();

        holder.bookTitleList.setText(title);
        holder.bookAuthor.setText(author);
        holder.bookTitleModule.setText(title);


    }

    @Override
    public int getItemCount() {
        if (mBookEntry == null ){
            return 0;
        }
        return mBookEntry.size();
    }


    public void setBookEntry ( List<BookEntry> bookEntry){
        mBookEntry = bookEntry;
        mBookEntryFull = new ArrayList<>(mBookEntry);
        notifyDataSetChanged();

    }

    public List<BookEntry> getBooks ( ){
        return mBookEntry;
    }




    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnCreateContextMenuListener {

        ImageView bookImage;
        TextView bookTitleList;
        TextView bookAuthor;

        TextView bookTitleModule;

        FrameLayout listViewFrame;
        FrameLayout moduleViewFrame;

        public BookViewHolder(View itemView) {
            super(itemView);


            bookImage = itemView.findViewById(R.id.book_image_view);
            bookTitleList = itemView.findViewById(R.id.title_text_view_list);
            bookAuthor = itemView.findViewById(R.id.author_text_view);

            bookTitleModule = itemView.findViewById(R.id.title_text_view_module);

            listViewFrame = itemView.findViewById(R.id.layout_list_view);
            moduleViewFrame = itemView.findViewById(R.id.layout_module_view);


            if(listView){
                listViewFrame.setVisibility(View.VISIBLE);
                moduleViewFrame.setVisibility(View.GONE);

            }
            else {

                listViewFrame.setVisibility(View.GONE);
                moduleViewFrame.setVisibility(View.VISIBLE);
            }


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }




        @Override
        public void onClick(View v) {
            int clickedPosition = mBookEntry.get(getAdapterPosition()).getId();
            mOnClickListener.onListBookClick(clickedPosition);

        }

        @Override
        public boolean onLongClick(View v) {
            int clickedPosition = mBookEntry.get(getAdapterPosition()).getId();
            mOnLongClickListener.onLongBookClick(clickedPosition);
            v.showContextMenu();
            return true;
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(0, 1,0,R.string.update_string);
            menu.add(0,2,1,R.string.delete_string);

        }




    }




    public interface ListBookClickListener {

        void onListBookClick (int id);

    }

    public interface BookLongClickListener {

        void onLongBookClick (int id);

    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter =  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BookEntry> filteredList = new ArrayList<>();


            if(constraint != null || constraint.length() != 0){
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(BookEntry item : mBookEntryFull){
                    if(item.getTitle().toLowerCase().contains(filterPattern) ||
                        item.getAuthor().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

            }else{
                filteredList.addAll(mBookEntryFull);
            }


            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mBookEntry.clear();
            mBookEntry.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
