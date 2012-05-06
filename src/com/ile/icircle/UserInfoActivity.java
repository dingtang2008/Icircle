package com.ile.icircle;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {

	private Bitmap mUserBitmap;
	private PictureGet mPictureGet;
	private RelativeLayout mTitle;
	public ImageButton btsetting, btaccountmanagement, btbrowsmode,
			btofficeweibo, btfreeback, bttestversion, btabout;

	private TableRow more_page_row0, more_page_row1,more_page_row2,more_page_row3, more_page_row5,
			more_page_row6, more_page_row7;
	public RelativeLayout rLayout1;

	ImageView touxiangimg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_page);
		mPictureGet = new PictureGet(this);
		initview();
	}

	public void initview() {
		mTitle = (RelativeLayout) findViewById(R.id.title);
		mTitle.findViewById(R.id.act_extend).setVisibility(View.GONE);
		//mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		mTitle.findViewById(R.id.act_back).setOnClickListener(new btclick());
		TextView mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(R.string.user_info);
		
		more_page_row0 = (TableRow) this.findViewById(R.id.more_page_row0);// 设置头像
		more_page_row0.setOnClickListener(new btclick());
		
		more_page_row1 = (TableRow) this.findViewById(R.id.more_page_row1);// 账号管理
		more_page_row1.setOnClickListener(new btclick());

		more_page_row2 = (TableRow) this.findViewById(R.id.more_page_row2);// 我的微薄
		more_page_row2.setOnClickListener(new btclick());
		
		more_page_row3 = (TableRow) this.findViewById(R.id.more_page_row3);// 我的微薄
		more_page_row3.setOnClickListener(new btclick());
		
		more_page_row5 = (TableRow) this.findViewById(R.id.more_page_row5);// 意见反馈
		more_page_row5.setOnClickListener(new btclick());
		
		touxiangimg = (ImageView) findViewById(R.id.touxiang_img);
	}
	
	public void refleshView(){
		touxiangimg.setImageBitmap(mUserBitmap);
	}

	public class btclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.act_back:
				finish();
				break;
			case R.id.more_page_row0:
				showDialog(PictureGet.GET_POSTER_DIALOG);
				break;
			case R.id.more_page_row1:
				break;
			case R.id.more_page_row5:
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Exit.btexit(MoreSetting.this);//当我们按下返回键的时候要执行的动�?
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog dialog = null;  
		switch(id) {  
		case PictureGet.GET_POSTER_DIALOG:  
			dialog = mPictureGet.creatDialog(getPicOnClick);
			break;
		}
		return dialog; 
	}
	
	DialogInterface.OnClickListener  getPicOnClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) { 
			dialog.dismiss();  
			switch (which) {  
			case 0: {  
				String status = Environment.getExternalStorageState();  
				if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡  
					Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					mPictureGet.capturefile = new File(PictureGet.PHOTO_DIR, mPictureGet.getPhotoFileName());  
					try {  
						mPictureGet.capturefile.createNewFile();  
						i.putExtra(MediaStore.EXTRA_OUTPUT,  
								Uri.fromFile(mPictureGet.capturefile));//将拍摄的照片信息存到capturefile中  
					} catch (IOException e) {  
						// TODO Auto-generated catch block  
						e.printStackTrace();  
					}  

					startActivityForResult(i, PictureGet.PHOTO_WITH_CAMERA);// 用户点击了从照相机获取  
				} else {  
					Toast.makeText(UserInfoActivity.this, getString(R.string.err_no_sdcard), Toast.LENGTH_LONG);  
				}  
				break;  
			}  
			case 1:// 从相册中去获取  
				Intent intent = new Intent();  
				/* 开启Pictures画面Type设定为image */  
				intent.setType("image/*");  
				/* 使用Intent.ACTION_GET_CONTENT这个Action */  
				intent.setAction(Intent.ACTION_GET_CONTENT);  
				/* 取得相片后返回本画面 */  
				startActivityForResult(intent, PictureGet.PHOTO_WITH_DATA);  
				break;  
			}  
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {  
			switch (requestCode) {  
			case PictureGet.PHOTO_WITH_CAMERA://获取拍摄的文件  
				mUserBitmap = mPictureGet.getPic(mPictureGet.capturefile.getAbsolutePath());
				break;  

			case PictureGet.PHOTO_WITH_DATA://获取从图库选择的文件  
				Uri uri = data.getData();  
				String scheme = uri.getScheme();  
				if (scheme.equalsIgnoreCase("file")) {
					mUserBitmap = mPictureGet.getPic(uri.getPath());
				} else if (scheme.equalsIgnoreCase("content")) {
					mUserBitmap = mPictureGet.getPicFromDatabase(uri);
				}  
				break;  
			}  
		}
		if (mUserBitmap != null){
			mUserBitmap = mPictureGet.resizeBitmap(mUserBitmap, 122, 122);
			Log.i("test", "actPoster = " + mUserBitmap);
		}
		refleshView();
		super.onActivityResult(requestCode, resultCode, data);  
	}  

}
