package aar92_22.library.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;

import java.io.ByteArrayOutputStream;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.R;
import aar92_22.library.Utilities.Navigator;

import static android.view.View.INVISIBLE;


public class Agregar extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private BookConsumer consumer;
    private Navigator navigator;

    Button btnGuardar;
    ImageButton btnAgregarImagen;
    ImageView mostrarImagen;


    public static PermissionGranted permissionGranted;
    MagicalCamera magicalCamera;

    int RESIZE_PHOTO_PIXELS_PERCENTAGE = 10;
    public String foto;
    byte [] fotoByte;



    public Agregar() {
    }

    public static Agregar newInstance(String param1, String param2) {
        Agregar fragment = new Agregar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        magicalCamera = new MagicalCamera(getActivity(), RESIZE_PHOTO_PIXELS_PERCENTAGE,permissionGranted);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agregar, container, false);

        final EditText nombreLibro = (EditText) view.findViewById(R.id.nombreLibro);
        final EditText nombreAutor = (EditText) view.findViewById(R.id.nombreAutor);
        final EditText sinopsis = (EditText) view.findViewById(R.id.sinopsis);
        final EditText editorial = (EditText) view.findViewById(R.id.editorial);


        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);
        btnAgregarImagen = (ImageButton) view.findViewById(R.id.imageButton);
        mostrarImagen = (ImageView) view.findViewById(R.id.imageView);





        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable name;
                Editable aut;
                Editable sinop;
                Editable edito;
                Bitmap bit;


                try {

                    name = nombreLibro.getText();
                    aut = nombreAutor.getText();
                    sinop = sinopsis.getText();
                    edito = editorial.getText();

                    bit = magicalCamera.getPhoto();
                    fotoByte = bitmapToBytes(bit, Bitmap.CompressFormat.PNG);
                    foto = bytesToStringBase64(fotoByte);

                    Toast.makeText(getContext(), "saving...", Toast.LENGTH_SHORT).show();
                    Libro lib = new Libro();
                    lib.setNombre(name.toString());
                    lib.setAutor(aut.toString());
                    lib.setSinopsis(sinop.toString());
                    lib.setEditorial(edito.toString());
                    lib.setFoto(foto);


                    consumer.consumeBook(lib);
                    navigator.navigate("dasdas");


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

            }



        });





        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookConsumer) {
            consumer = (BookConsumer) context;
            navigator = (Navigator) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        consumer = null;
    }



    public interface BookConsumer {
        public void consumeBook(Libro libro);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        magicalCamera.permissionGrant(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        magicalCamera.resultPhoto(requestCode, resultCode, data);

        mostrarImagen.setImageBitmap(magicalCamera.getPhoto());

        /*foto = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),
                "BookCover","MyLibrary", MagicalCamera.PNG, true);
*/
    }


    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               if (options[item].equals("Take Photo")) {
                    if(magicalCamera.takeFragmentPhoto()){
                        startActivityForResult(magicalCamera.getIntentFragment(),MagicalCamera.TAKE_PHOTO);
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


    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, stream);
        return stream.toByteArray();
    }

    public static String bytesToStringBase64(byte[] byteArray) {
        StringBuilder base64 = new StringBuilder(Base64.encodeToString(byteArray, Base64.DEFAULT));
        return base64.toString();
    }




}
