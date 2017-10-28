package com.liftindia.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;
import com.liftindia.app.R;
import com.liftindia.app.helper.Const;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//@ContentView(R.layout.activity_cropper)
public class CropperActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_cancel;
    Button btn_rotate;
    Button btn_save;

    String currentPhotoPath;
    Bitmap currentImage;
    CropImageView cropImageView;
    //commented
  //  CropImageView.CropMode cropMode = CropImageView.CropMode.RATIO_1_1;
  CropImageView.CropMode cropMode = CropImageView.CropMode.RATIO_FREE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        cropImageView = (CropImageView)findViewById(R.id.cropImageView);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_rotate = (Button)findViewById(R.id.btn_rotate);
        btn_save = (Button)findViewById(R.id.btn_save);

        btn_cancel.setOnClickListener(this);
        btn_rotate.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        if (getIntent().hasExtra(Const.IMAGE)) {
            currentPhotoPath = getIntent().getStringExtra(Const.IMAGE);
            new DecodeBitmap().execute();
            //setImage();
//            File file = new File(getIntent().getStringExtra(Const.IMAGE));
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
////                    bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
//            cropImageView.setImageBitmap(bitmap);
        }
        if(getIntent().hasExtra("cropMode")){
            String mode = getIntent().getStringExtra("cropMode");
            //Commented
           // cropMode = CropImageView.CropMode.RATIO_16_9;
        }
        cropImageView.setCropMode(cropMode);
    }
   /* private void setImage() {
        try {
            int targetSize = getResources().getDisplayMetrics().widthPixels;
            currentImage = decodeSampledBitmap(targetSize);
            // currentImage=Bitmap.createScaledBitmap(currentImage,
            // targetSize,targetSize,false);
            // currentImage=getOrientedBitmap();
            cropImageView.setImageBitmap(currentImage);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e("Setting bitmap", " OutOfMemoryError");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Setting bitmap", " Error");
        }
    }*/
    class DecodeBitmap extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int targetSize = getResources().getDisplayMetrics().widthPixels;
                currentImage = decodeSampledBitmap(targetSize);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                Log.e("Setting bitmap", " OutOfMemoryError");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Setting bitmap", " Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(currentImage!=null){
                cropImageView.setImageBitmap(currentImage);
            }
        }
    }

    private Bitmap decodeSampledBitmap(int size) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, options);
        options.inSampleSize = calculateInSampleSize(options, size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(currentPhotoPath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqWidth || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqWidth && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                CropperActivity.this.finish();
                break;
            case R.id.btn_rotate:
                try {
                    cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btn_save:
                try {
                    Const.bitmap = cropImageView.getCroppedBitmap();
                    setResult(RESULT_OK);
                    CropperActivity.this.finish();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
