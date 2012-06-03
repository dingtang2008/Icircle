package com.ile.icircle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


	public String picPath;
	public static final int PHOTO_WITH_CAMERA = 1010;
	public static final int PHOTO_WITH_DATA = 1020;
	public static final File PHOTO_DIR = new File(  
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	public File capturefile;
	public Context mContext;
	public static final int GET_POSTER_DIALOG = 100;

	PictureGet(Context context){
		mContext = context;
	}

	public String getPhotoFileName() {  
		Date date = new Date(System.currentTimeMillis());  
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");  
		return dateFormat.format(date) + ".jpg";  
	} 

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

			float scaleWidth  = (float) sWidth / bmpWidth;
			float scaleHeight = (float) sHeight / bmpHeight;  
			Matrix matrix = new Matrix(); 
			matrix.postScale(scaleWidth, scaleHeight);
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
		choices[0] = mContext.getString(R.string.take_photo); 
		choices[1] = mContext.getString(R.string.local_gallery); 
		//final ListAdapter adapter = new ArrayAdapter<String>(mContext,  
				//		android.R.layout.simple_list_item_1, choices);  

		final AlertDialog.Builder mbuilder = new AlertDialog.Builder(  
				mContext);  
		mbuilder.setTitle(mContext.getString(R.string.get_pic));  
		mbuilder.setSingleChoiceItems(choices, 0, getPicOnClick);
		return mbuilder.create();
	}  

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap Bitmap2Bytes(byte[] byt) {
		Bitmap mBitmap = BitmapFactory.decodeByteArray(byt, 0, byt.length);
		return mBitmap;
	}

	private boolean isSdCardExsit(){ 
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); 
	}

	public static String saveBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return "";
		}
		Date date = new Date(System.currentTimeMillis());  
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		String bitmapDir = Environment.getExternalStorageDirectory() + "/iCircle/Picture/";
		File destDir = new File(bitmapDir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String bitmapUrl = bitmapDir + dateFormat.format(date) + ".png";
		File file = new File(bitmapUrl);
		try {
			file.createNewFile();  
			FileOutputStream out=new FileOutputStream(file);
			if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
				out.flush();
				out.close();
			} else {
				bitmapUrl = "";
			}
		} catch (FileNotFoundException e) {
			bitmapUrl = "";
			e.printStackTrace();
		} catch (IOException e) {
			bitmapUrl = "";
			e.printStackTrace();
		}
		return bitmapUrl;
	}
}
