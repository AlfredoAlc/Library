package aar92_22.library.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;

import java.io.ByteArrayOutputStream;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.R;
import aar92_22.library.Utilities.Navigator;


public class Editar extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    boolean seleccionFoto = false;

    private Navigator navigator;
    private Agregar.BookConsumer consumer;


    private Libro libro;

    public static PermissionGranted permissionGranted;


    byte [] fotoByte;
    String foto;
    int RESIZE_PHOTO_PIXELS_PERCENTAGE = 10;


    EditText txtNombreLibro, txtNombreAutor, txtEditorial, txtSinopsis;
    ImageView mostrarImagen;

    Button btnGuardar;
    ImageButton btnAgregarImagen;

    MagicalCamera magicalCamera;


    public Editar() {
    }

    public static Editar newInstance(Libro libro, String param2) {
        Editar fragment = new Editar();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, libro);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionGranted = new PermissionGranted(getActivity());

        if (android.os.Build.VERSION.SDK_INT >= 24) {
            permissionGranted.checkAllMagicalCameraPermission();
        }else{
            permissionGranted.checkCameraPermission();
            permissionGranted.checkReadExternalPermission();
            permissionGranted.checkWriteExternalPermission();
            permissionGranted.checkLocationPermission();
        }
        if (getArguments() != null) {
            libro = (Libro) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        magicalCamera = new MagicalCamera(getActivity(), RESIZE_PHOTO_PIXELS_PERCENTAGE,permissionGranted);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_editar, container, false);

        txtNombreLibro = (EditText) view.findViewById(R.id.nombreLibro);
        txtNombreAutor = (EditText) view.findViewById(R.id.nombreAutor);
        txtEditorial = (EditText) view.findViewById(R.id.editorial);
        txtSinopsis = (EditText) view.findViewById(R.id.sinopsis);
        mostrarImagen = (ImageView) view.findViewById(R.id.imageView);

        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);
        btnAgregarImagen = (ImageButton) view.findViewById(R.id.imageButton);

        mostrarLibro(libro);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable name;
                Editable aut;
                Editable sinop;
                Editable edito;
                Bitmap bit;


                try {

                    name = txtNombreLibro.getText();
                    aut = txtNombreAutor.getText();
                    sinop = txtSinopsis.getText();
                    edito = txtEditorial.getText();

                    if(seleccionFoto){
                        bit = magicalCamera.getPhoto();
                        fotoByte = bitmapToBytes(bit, Bitmap.CompressFormat.PNG);
                        foto = bytesToStringBase64(fotoByte);
                    }


                    Toast.makeText(getContext(), "saving...", Toast.LENGTH_SHORT).show();
                    //Libro lib = new Libro();
                    libro.setNombre(name.toString());
                    libro.setAutor(aut.toString());
                    libro.setSinopsis(sinop.toString());
                    libro.setEditorial(edito.toString());
                    libro.setFoto(foto);

                    libro.update();

                    //consumer.consumeBook(lib);
                    navigator.navigate("dasdas");
                    seleccionFoto = false;


                } catch (Exception ex) {
                    AlertDialog alert = new AlertDialog.Builder(getContext())
                            .setMessage("Please, write all data and select a picture.").setPositiveButton("ok", null).create();
                    alert.show();
                    alert.setCanceledOnTouchOutside(false);


                }


            }

        });

        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                seleccionFoto = true;

            }



        });

        return view;
    }


    public void mostrarLibro(Libro libro){
        txtNombreLibro.setText(libro.getNombre());
        txtNombreAutor.setText(libro.getAutor());
        txtEditorial.setText(libro.getEditorial());
        txtSinopsis.setText(libro.getSinopsis());

        foto = libro.getFoto();
        fotoByte = stringBase64ToBytes(foto);

        mostrarImagen.setImageBitmap(bytesToBitmap(fotoByte, Bitmap.CompressFormat.PNG));

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

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if(magicalCamera.takeFragmentPhoto()){
                        startActivityForResult(magicalCamera.getIntentFragment(), MagicalCamera.TAKE_PHOTO);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    if(magicalCamera.selectedFragmentPicture()){
                        startActivityForResult(Intent.createChooser(magicalCamera.getIntentFragment(),  "My Header Example"),
                                MagicalCamera.SELECT_PHOTO);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        magicalCamera.permissionGrant(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        mostrarImagen.setImageBitmap(magicalCamera.getPhoto());

        foto = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),
                "BookCover","MyLibrary", MagicalCamera.PNG, true);

    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, stream);
        return stream.toByteArray();
    }

    public static String bytesToStringBase64(byte[] byteArray) {
        StringBuilder base64 = new StringBuilder(Base64.encodeToString(byteArray, Base64.DEFAULT));
        return base64.toString();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Agregar.BookConsumer) {
            consumer = (Agregar.BookConsumer) context;
            navigator = (Navigator) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        consumer = null;

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
