package com.ile.icircle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LiveActActivity extends Activity implements OnClickListener{

	private PictureGet mPictureGet;
	private RelativeLayout mTitle;
	private LinearLayout mBottom;
	private TextView mtitle;
	private ListView livelist;
	private int actId;

	private Bitmap mSendBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_live_layout);
		mPictureGet = new PictureGet(this);
		init();
	}

	private void init() {
		Intent mIntent = getIntent();
		actId = mIntent.getIntExtra(UtilString.ACTID, -1);
		Log.i("test", "actId = "+actId);

		mTitle = (RelativeLayout) findViewById(R.id.title);
		Button extend_detail = (Button) mTitle.findViewById(R.id.act_extend);
		//mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		mTitle.findViewById(R.id.act_back).setVisibility(View.GONE);
		extend_detail.setVisibility(View.VISIBLE);
		extend_detail.setText(R.string.btn_detail);
		extend_detail.setBackgroundResource(R.drawable.live_act_selector);
		extend_detail.setOnClickListener(this);

		mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(R.string.act_live);
		//mtitle.setOnClickListener(this);

		livelist = (ListView) findViewById(R.id.livelist);

		allliveuser = new ArrayList<HashMap<String, Object>>();
		int id = 0;
		for (int i = 0; i < 8; i++) {
			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
			Log.i("test", "id = "+ id);
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("userportrait", R.drawable.portrait_default);
			user.put("username", "测试用户名" + (i + 1));
			user.put("useracttext", "测试内容，哦合法破坏骗人fdafjrjago发票" +
					"挖掘分配及股票撒啊撒大声大声大大大声大声大声的撒的撒的撒打算的撒旦撒旦撒大师的撒旦" + i + 1);
			if (i == 2 | i == 5)
				id = 0;
			user.put("useractpic", getLiveBitmap(id));
			user.put("time", "2012-03-26" + (i + 1));
			allliveuser.add(user);
		}
		livelist.setAdapter(new LiveListAdapter(this, 0));
		

		mBottom = (LinearLayout) findViewById(R.id.live_bottom);
		
		EditText userCommit = (EditText) mBottom.findViewById(R.id.user_commit);
		userCommit.setHint("校园十佳歌手");
		 mBottom.findViewById(R.id.camera).setOnClickListener(this);
		 mBottom.findViewById(R.id.send).setOnClickListener(this);
		
	}


	private Bitmap getLiveBitmap(int id) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
		bitmap = mPictureGet.resizeBitmap(bitmap, 400, 230);
		return bitmap;
	}


	static class ViewHolder {
		ImageView userPortrait;
		TextView usreName;
		TextView time;
		TextView usreActText;
		ImageView usreActPic;
		int position;
	}
	ArrayList<HashMap<String, Object>> lstImageItem;
	ArrayList<HashMap<String, Object>> allliveuser;
	private class LiveListAdapter extends BaseAdapter {
		Context mContext;
		private LayoutInflater mInflater;
		private int mActId;

		public LiveListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		public LiveListAdapter(Context context, int actid) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
			mActId = actid;
		}
		public int getmActId() {
			return mActId;
		}

		public void setmActId(int mActId) {
			this.mActId = mActId;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return allliveuser.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.act_live_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.userPortrait = (ImageView) convertView
						.findViewById(R.id.user_portrait);
				holder.usreName = (TextView) convertView
						.findViewById(R.id.user_name);
				holder.time = (TextView) convertView
						.findViewById(R.id.time);
				holder.usreActText = (TextView) convertView
						.findViewById(R.id.user_act_text);
				holder.usreActPic = (ImageView) convertView
						.findViewById(R.id.user_act_pic);
				holder.usreActPic.setOnClickListener(LiveActActivity.this);

				holder.position = position;
				/*holder.actTag = (ImageView) convertView
						.findViewById(R.id.activity_tag);*/

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			Integer portraitId = (Integer) allliveuser.get(position).get("userportrait");
			String username = (String) allliveuser.get(position).get("username");
			String useracttext = (String) allliveuser.get(position).get("useracttext");
			String time = (String) allliveuser.get(position).get("time");
			//Integer useractpicId = (Integer) allliveuser.get(position).get("useractpic");

			Bitmap resizeBitmap = (Bitmap) allliveuser.get(position).get("useractpic");

			holder.userPortrait.setImageResource(portraitId);
			holder.usreName.setText(username);
			holder.usreActText.setText(useracttext);
			holder.time.setText(time);
			//holder.usreActPic.setImageResource(useractpicId);
			holder.usreActPic.setImageBitmap(resizeBitmap);

			/*
			File file=new File("/sdcard/live" + position + ".png");
	        try {
	            FileOutputStream out=new FileOutputStream(file);
	            if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
	                out.flush();
	                out.close();
	            }
	        } catch (FileNotFoundException e) {
	           // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	           // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
			 */

			return convertView;
		}
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.act_back:
			finish();
			break;
		case R.id.act_extend:
			finish();
			overridePendingTransition(R.anim.finish_enter_anim, R.anim.finish_exit_anim);
			break;
		case R.id.camera:
			showDialog(PictureGet.GET_POSTER_DIALOG);
			break;
		case R.id.send:
			//sendMyWord
			break;
		default:
			break;
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
					Toast.makeText(LiveActActivity.this, getString(R.string.err_no_sdcard), Toast.LENGTH_LONG);  
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
				mSendBitmap = mPictureGet.getPic(mPictureGet.capturefile.getAbsolutePath());
				break;  

			case PictureGet.PHOTO_WITH_DATA://获取从图库选择的文件  
				Uri uri = data.getData();  
				String scheme = uri.getScheme();  
				if (scheme.equalsIgnoreCase("file")) {
					mSendBitmap = mPictureGet.getPic(uri.getPath());
				} else if (scheme.equalsIgnoreCase("content")) {
					mSendBitmap = mPictureGet.getPicFromDatabase(uri);
				}  
				break;  
			}  
		}
		if (mSendBitmap != null){
			mSendBitmap = mPictureGet.resizeBitmap(mSendBitmap, 400, 230);
			Log.i("test", "actPoster = " + mSendBitmap);
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}  
}
