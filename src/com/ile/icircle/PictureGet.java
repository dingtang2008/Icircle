package com.ile.icircle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class PictureGet {


	public String picPath;//文件路径  
	public static final int PHOTO_WITH_CAMERA = 1010;// 拍摄照片  
	public static final int PHOTO_WITH_DATA = 1020;// 从SD中得到照片  
	public static final File PHOTO_DIR = new File(  
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");//拍摄照片存储的文件夹路劲  
	public File capturefile;//拍摄的照片文件
	public Context mContext;
	public static final int GET_POSTER_DIALOG = 100;
	
	PictureGet(Context context){
		mContext = context;
	}

	/* 
     * 通过相机回传图片的文件名 
     */  
	public String getPhotoFileName() {  
        Date date = new Date(System.currentTimeMillis());  
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");  
        return dateFormat.format(date) + ".jpg";  
    } 
    /* 
     * 压缩图片，避免内存不足报错 
     */  
	public Bitmap decodeFile(File f) {  
        Bitmap b = null;  
        try {  
            // Decode image size  
            BitmapFactory.Options o = new BitmapFactory.Options();  
            o.inJustDecodeBounds = true;  
  
            FileInputStream fis = new FileInputStream(f);  
            BitmapFactory.decodeStream(fis, null, o);  
            fis.close();  
  
            int scale = 1;  
            if (o.outHeight > 100 || o.outWidth > 100) {  
                scale = (int) Math.pow(  
                        2,  
                        (int) Math.round(Math.log(100 / (double) Math.max(  
                                o.outHeight, o.outWidth)) / Math.log(0.5)));  
            }  
  
            // Decode with inSampleSize  
            BitmapFactory.Options o2 = new BitmapFactory.Options();  
            o2.inSampleSize = scale;  
            fis = new FileInputStream(f);  
            b = BitmapFactory.decodeStream(fis, null, o2);  
            fis.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return b;  
    }  

	public Bitmap resizeBitmap(Bitmap bitmap, int sWidth, int sHeight) {
		if (bitmap != null) {
			int bmpWidth  = bitmap.getWidth(); 
			int bmpHeight  = bitmap.getHeight();

			//缩放图片的尺寸 

			float scaleWidth  = (float) sWidth / bmpWidth;     //按固定大小缩放  sWidth 写多大就多大
			float scaleHeight = (float) sHeight / bmpHeight;  
			Matrix matrix = new Matrix(); 
			matrix.postScale(scaleWidth, scaleHeight);//产生缩放后的Bitmap对象 
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
		}
        return bitmap;  
    }  
	
    public Bitmap getPic(String picPath) {  
        Bitmap decodeBitmap = null;  
        File file = new File(picPath);  
        decodeBitmap = decodeFile(file);
        return decodeBitmap;  
    }

    public Bitmap getPicFromDatabase(Uri uri) {  
        Bitmap decodeBitmap = null;  
        Cursor cursor = mContext.getContentResolver().query(uri, null, null,  
                null, null);  
        cursor.moveToFirst();  
        File file = new File(cursor.getString(1));  
        decodeBitmap = decodeFile(file);
        return decodeBitmap;  
    }

	public AlertDialog creatDialog(DialogInterface.OnClickListener getPicOnClick) {
		String[] choices;  
		choices = new String[2];  
		choices[0] = mContext.getString(R.string.take_photo); // 拍照  
		choices[1] = mContext.getString(R.string.local_gallery); // 从相册中选择  
		//final ListAdapter adapter = new ArrayAdapter<String>(mContext,  
		//		android.R.layout.simple_list_item_1, choices);  

		final AlertDialog.Builder mbuilder = new AlertDialog.Builder(  
				mContext);  
		mbuilder.setTitle(mContext.getString(R.string.get_pic));  
		mbuilder.setSingleChoiceItems(choices, 0, getPicOnClick);
		return mbuilder.create();
	}  
}
