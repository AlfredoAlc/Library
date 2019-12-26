package aar92_22.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

    Drawable drawable;

    private boolean listView;




    public BookListAdapter ( Context mContext, ListBookClickListener mOnClickListener,
                             BookLongClickListener mOnLongClickListener, boolean listView){
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
        this.mOnLongClickListener = mOnLongClickListener;
        this.listView = listView;
    }


    public void setBookEntry ( List<BookEntry> bookEntry){
        mBookEntry = bookEntry;
        mBookEntryFull = new ArrayList<>(mBookEntry);
        notifyDataSetChanged();

    }

    /*public List<BookEntry> getBooks ( ){
        return mBookEntry;
    }*/



    @Override
    @NonNull
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutIdForBookList = R.layout.individual_book_recycler_view;

        drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_add_book);


        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(layoutIdForBookList,parent,false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookEntry book = mBookEntry.get(position);

        String title = "";
        String lastName = "";
        String firstName = "";
        int numberPages = 0;

        byte[] imageInBytes = null;
        Bitmap mResultsBitmap;

        if(!book.getTitle().equals("")){
            title = book.getTitle();
        }
        if(!book.getLastName().equals("")){
            lastName = book.getLastName();
        }
        if(!book.getFirstName().equals("")){
            firstName = book.getFirstName();
        }


        numberPages = book.getNumberPages();
        if(numberPages > 0){
            holder.mNumberPages.setVisibility(View.VISIBLE);
            String pages_string = mContext.getString(R.string.pages_string);
            String textDisplayed = numberPages + " " + pages_string;
            holder.mNumberPages.setText(textDisplayed);
        } else {
            holder.mNumberPages.setVisibility(View.INVISIBLE);
        }


        holder.mTitle.setText(title);
        holder.mLastName.setText(lastName);
        holder.mFirstName.setText(firstName);


        holder.mTitleModule.setText(title);

        if(book.getBookCover() != null) {
            imageInBytes = book.getBookCover();
            mResultsBitmap = BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length);
            holder.bookImageList.setImageBitmap(mResultsBitmap);
            holder.bookImageModule.setImageBitmap(mResultsBitmap);
        }else {
            holder.bookImageList.setImageDrawable(drawable);
            holder.bookImageModule.setImageDrawable(drawable);
        }



    }

    public interface ListBookClickListener {

        void onListBookClick (int id);

    }

    public interface BookLongClickListener {

        void onLongBookClick (int id);

    }

    @Override
    public int getItemCount() {
        if (mBookEntry == null ){
            return 0;
        }
        return mBookEntry.size();
    }


    @Override
    public Filter getFilter() {
        return searchFilter;
    }



    private Filter searchFilter =  new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BookEntry> filteredList = new ArrayList<>();

            if (constraint != null || constraint.length() != 0) {

                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BookEntry item : mBookEntryFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern) ||
                            item.getLastName().toLowerCase().contains(filterPattern) ||
                            item.getFirstName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

            } else {
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







    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnCreateContextMenuListener {

        ImageView bookImageList;
        ImageView bookImageModule;
        TextView mTitle;
        TextView mLastName;
        TextView mFirstName;
        TextView mNumberPages;

        TextView mTitleModule;

        FrameLayout listViewFrame;
        FrameLayout moduleViewFrame;

        private BookViewHolder(View itemView) {
            super(itemView);


            bookImageList = itemView.findViewById(R.id.book_image_view_list);
            bookImageModule = itemView.findViewById(R.id.book_image_view_module);
            mTitle = itemView.findViewById(R.id.title_tv);
            mLastName = itemView.findViewById(R.id.last_name_tv);
            mFirstName = itemView.findViewById(R.id.first_name_tv);
            mNumberPages = itemView.findViewById(R.id.number_pages_tv);


            mTitleModule = itemView.findViewById(R.id.title_tv_module);

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

            menu.add(0, 1,0,R.string.edit_string);
            menu.add(0,2,1,R.string.delete_string);

        }




    }




}
