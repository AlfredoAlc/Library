package aar92_22.library.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.TransactionTooLargeException;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frosquivel.magicalcamera.MagicalCamera;

import java.io.ByteArrayOutputStream;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.MainActivity;
import aar92_22.library.R;
import aar92_22.library.Utilities.Navigator;


public class VerCompleto extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private Libro libro;

    private Navigator navigator;

    String fotoString;
    byte [] fotoByte;

    TextView txtNombreLibro, txtNombreAutor, txtEditorial, txtSinopsis;
    ImageView foto;



    public VerCompleto() {


    }

    public static VerCompleto newInstance(Libro libro, String param2) {
        VerCompleto fragment = new VerCompleto();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, libro);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void menuVisible(MenuItem btnEliminar, MenuItem btnEditar){
        btnEliminar.setVisible(true);
        btnEditar.setVisible(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            libro = (Libro) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_completo, container, false);

        txtNombreLibro = (TextView) view.findViewById(R.id.nombreLibro);
        txtNombreAutor = (TextView) view.findViewById(R.id.nombreAutor);
        txtEditorial = (TextView) view.findViewById(R.id.editorial);
        txtSinopsis = (TextView) view.findViewById(R.id.sinopsis);
        foto = (ImageView) view.findViewById(R.id.foto);

        mostrarLibro(libro);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Navigator) {
            navigator = (Navigator) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        navigator = null;
        super.onDetach();
    }









    public void mostrarLibro(Libro libro){
        txtNombreLibro.setText(libro.getNombre());
        txtNombreAutor.setText("Autor: " + libro.getAutor());
        txtEditorial.setText("Editorial: " + libro.getEditorial());
        txtSinopsis.setText(libro.getSinopsis());

        fotoString = libro.getFoto();
        fotoByte = stringBase64ToBytes(fotoString);

        foto.setImageBitmap(bytesToBitmap(fotoByte, Bitmap.CompressFormat.PNG));


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


