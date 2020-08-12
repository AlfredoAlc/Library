package aar92_22.library.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;

import aar92_22.library.Interfaces.CategoryListener;
import aar92_22.library.Interfaces.LibraryListener;
import aar92_22.library.Interfaces.SelectingCoverListener;
import aar92_22.library.R;


public class Dialogs {


    public static void deleteBookConfirmationDialog(Context context, final LibraryListener libraryListener) {
        AlertDialog.Builder builder = createBuilderWithMessage(context, R.layout.dialog_warning,
                context.getString(R.string.delete_string), context.getString(R.string.delete_message));

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        libraryListener.deleteBookListener();
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public static void deleteLibraryConfirmationDialog(Context context, final LibraryListener libraryListener) {
        AlertDialog.Builder builder = createBuilderWithMessage(context, R.layout.dialog_warning_red,
                context.getString(R.string.delete_string), context.getString(R.string.delete_library_message));

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        libraryListener.deleteLibraryListener();
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public static void deleteCategoryConfirmationDialog(Context context, final CategoryListener categoryListener,
                                                        final int categoryId) {
        AlertDialog.Builder builder = createBuilderWithMessage(context, R.layout.dialog_warning_red,
                context.getString(R.string.delete_string), context.getString(R.string.delete_category_message));

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        categoryListener.deleteCategoryListener(categoryId);
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public static void renameCategoryDialog(Context context, final CategoryListener categoryListener,
                                            final int categoryId, String currentName){
        View renameCategoryView = View.inflate(context, R.layout.dialog_text_input, null);
        TextView titleTv = renameCategoryView.findViewById(R.id.title);
         titleTv.setText(context.getString(R.string.rename_title));
        final EditText categoryName = renameCategoryView.findViewById(R.id.input);
        categoryName.setHint(currentName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setCancelable(true);
        builder.setView(renameCategoryView);

        builder.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        categoryListener.renameCategoryListener(categoryId, categoryName.getText().toString());
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public static void addNewCategoryDialog(Context context, final CategoryListener categoryListener){
        View renameCategoryView = View.inflate(context, R.layout.dialog_text_input, null);
        TextView titleTv = renameCategoryView.findViewById(R.id.title);
        titleTv.setText(context.getString(R.string.add_new_category));
        final EditText categoryName = renameCategoryView.findViewById(R.id.input);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setCancelable(true);
        builder.setView(renameCategoryView);

        builder.setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        categoryListener.addNewCategoryListener(categoryName.getText().toString());
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }


    public static void selectImageOptions(Context context, final SelectingCoverListener selectingCoverListener){
        View view =  View.inflate(context, R.layout.dialog_image_options, null);
        TextView selectImage = view.findViewById(R.id.select_image);
        TextView takePhoto = view.findViewById(R.id.take_photo);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setCancelable(true);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectingCoverListener.selectImageListener();
                dialog.dismiss();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectingCoverListener.takePhotoListener();
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private static AlertDialog.Builder createBuilderWithMessage(Context context, @LayoutRes int layout,
                                                                String title, String message){
        View view = View.inflate(context, layout, null);
        TextView titleTv = view.findViewById(R.id.title);
        titleTv.setText(title);
        TextView messageTv = view.findViewById(R.id.message);
        messageTv.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
        builder.setCancelable(true);
        builder.setView(view);
        return builder;
    }



}
