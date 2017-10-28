package com.liftindia.app.helper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.liftindia.app.R;

public class ImageIntent {
    private Activity activity;
    private OnImageChosenListener listener;
    private String currentPath = "";

    public ImageIntent(Activity activity, OnImageChosenListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public String getImages() {
        String currentPhotoPath = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File imagefile = createImageFile();
            if (imagefile != null) {
                currentPhotoPath = imagefile.getAbsolutePath();
                // imageCaptureUri = Uri.fromFile(imagefile);
                Intent galleryPick = new Intent();
                /*if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
                }else{
					galleryPick.setAction(Intent.ACTION_GET_CONTENT);
				}*/
                galleryPick.setAction(Intent.ACTION_PICK);
                galleryPick.setType("image/*");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagefile));
                takePictureIntent.putExtra("return-data", false);
                Intent chooserIntent = Intent.createChooser(galleryPick, "Select or take a new Picture");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
                try {
                    activity.startActivityForResult(chooserIntent, Const.IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentPhotoPath;
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(column_index);
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

	
	/*public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File mFileTemp = null;
		//ContextWrapper contextWrapper=new ContextWrapper(activity);
		//String root=contextWrapper.getFilesDir().getAbsolutePath();
		String root=activity.getDir("my_sub_dir",Context.MODE_PRIVATE).getAbsolutePath();
		File myDir = new File(root + "/Img");
		if(!myDir.exists()){
			myDir.mkdirs();
		}
		try {
			mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
			Log.e("path",mFileTemp.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return mFileTemp;
	}*/

    public File createImageFile() {
        String state = Environment.getExternalStorageState();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                File directory = new File(Environment.getExternalStorageDirectory() + "/tempImg");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                mFileTemp = File.createTempFile(imageFileName, ".jpg", directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File directory = new File(activity.getFilesDir() + "/tempImg");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                mFileTemp = File.createTempFile(imageFileName, ".jpg", directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFileTemp;
    }

    public void showImageChooser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.dialog_image_chooser, null);
        TextView camera = (TextView) view.findViewById(R.id.camera);
        TextView gallery = (TextView) view.findViewById(R.id.gallery);

        alertDialogBuilder.setView(view).setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.camera) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = null;
                    file = createImageFile();
                    if (file != null) {
                        currentPath = file.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        intent.putExtra("return-data", false);
                        try {
                            activity.startActivityForResult(intent, Const.PICK_CAMERA);
                            listener.onImageFileCreated(currentPath);
                        } catch (Exception e) {
                        }
                    }
                } else {
                    Intent in = new Intent();
                    in.setType("image/*");
                    in.setAction(Intent.ACTION_PICK);
                    try {
                        activity.startActivityForResult(in, Const.PICK_GALLERY);
                        ((OnImageChosenListener) activity).onImageFileCreated(currentPath);
                    } catch (Exception e) {
                    }
                }
                dialog.dismiss();
            }
        };

        camera.setOnClickListener(onClickListener);
        gallery.setOnClickListener(onClickListener);

/*
        final String[] items = new String[]{"Camera", "Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppTheme));
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = null;
                    file = createImageFile();
                    if (file != null) {
                        currentPath = file.getAbsolutePath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        intent.putExtra("return-data", false);
                        try {
                            activity.startActivityForResult(intent, Const.PICK_CAMERA);
                            listener.onImageFileCreated(currentPath);
                        } catch (Exception e) {
                        }
                    }
                    dialog.dismiss();
                } else {
                    Intent in = new Intent();
                    in.setType("image*//*");
                    in.setAction(Intent.ACTION_PICK);
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                        in.setAction(Intent.ACTION_PICK);
//                    } else {
//                        in.setAction(Intent.ACTION_GET_CONTENT);
//                        in.addCategory(Intent.CATEGORY_OPENABLE);
//                    }
                    //activity.startActivityForResult(Intent.createChooser(in, "Complete action using"), PICK_FROM_FILE);
                    try {
                        activity.startActivityForResult(in, Const.PICK_GALLERY);
                        ((OnImageChosenListener) activity).onImageFileCreated(currentPath);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    dialog.dismiss();
                }
            }
        });*/
        // final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface OnImageChosenListener {
        void onImageFileCreated(String path);
    }

}
