package aar92_22.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import aar92_22.library.Database.BookEntry;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {


    private List<BookEntry> mBookEntry;
    private Context mContext;
    final private ListBookClickListener mOnClickListener ;
    final private BookLongClickListener mOnLongClickListener;



    public BookListAdapter ( Context mContext, ListBookClickListener mOnClickListener,
                             BookLongClickListener mOnLongClickListener ){
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
        this.mOnLongClickListener = mOnLongClickListener;
    }


    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForBookList = R.layout.individual_book_recycler_view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(layoutIdForBookList,parent,false);
        BookViewHolder bookViewHolder = new BookViewHolder(view);

        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        BookEntry book = mBookEntry.get(position);
        String title = book.getTitle();
        String author = book.getAuthor();

        holder.bookTitle.setText(title);
        holder.bookAuthor.setText(author);


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
        notifyDataSetChanged();

    }

    public List<BookEntry> getBooks ( ){
        return mBookEntry;
    }


    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnCreateContextMenuListener {

        ImageView bookImage;
        TextView bookTitle;
        TextView bookAuthor;

        public BookViewHolder(View itemView) {
            super(itemView);

            bookImage = (ImageView) itemView.findViewById(R.id.book_image_view);
            bookTitle = (TextView) itemView.findViewById(R.id.title_text_view);
            bookAuthor = (TextView) itemView.findViewById(R.id.author_text_view);

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
}
