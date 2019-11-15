package aar92_22.library.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.R;
import aar92_22.library.Utilities.FragmentLink;
import aar92_22.library.Utilities.Holder;


public class LibrosAdapter extends RecyclerView.Adapter<LibrosAdapter.ViewHolder> {

    private final List<Libro> bookList;
    private final Context context;
    private final FragmentLink link;


    String fotoString;
    byte[] fotoByte;


    public LibrosAdapter(List<Libro> bookList, Context context, FragmentLink link) {
        this.bookList = bookList;
        this.context = context;
        this.link = link;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final FragmentLink link;
        public TextView nombreLibro;
        public TextView nombreAutor;
        public ImageView foto;

        private Context context;

        public ViewHolder(LinearLayout v, Context context, FragmentLink link) {
            super(v);
            this.context = context;
            this.link = link;
            v.setOnClickListener(this);
            nombreLibro = (TextView) v.findViewById(R.id.titulo_ver);
            nombreAutor = (TextView) v.findViewById(R.id.autor_ver);
            foto = (ImageView) v.findViewById(R.id.foto);
        }

        @Override
        public void onClick(View v) {
            link.linkPosition(getLayoutPosition());
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.libro_item, parent, false);

        ViewHolder vh = new ViewHolder(v, context, link);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Libro libro = bookList.get(position);
        holder.nombreLibro.setText(libro.getNombre());
        holder.nombreAutor.setText(libro.getAutor());

        fotoString = libro.getFoto();
        fotoByte = stringBase64ToBytes(fotoString);
        holder.foto.setImageBitmap(bytesToBitmap(fotoByte, Bitmap.CompressFormat.PNG));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    public static byte[] stringBase64ToBytes(String stringBase64) {
        byte[] byteArray = Base64.decode(stringBase64, Base64.DEFAULT);
        return byteArray;
    }

    public static Bitmap bytesToBitmap(byte[] byteArray, Bitmap.CompressFormat format) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, stream);
        return bitmap;
    }

}