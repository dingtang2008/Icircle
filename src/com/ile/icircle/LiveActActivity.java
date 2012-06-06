package com.ile.icircle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
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
	private long actTagId;

	private Bitmap mSendBitmap;
	private String mSendBitmapUrl;
	private String mSendContent = "";

	public int tryloadtimes = 0;
	private LiveCursorAdapter mAdapter;
	private QueryHandler mQueryHandler;
	private final static int DIALOG_REFRESH_DATA = 0;

	private static final int ACT_LIVE_QUERY_TOKEN = 101;

	private static final int MSG_INSERT_DATA = 201;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_live_layout);
		mPictureGet = new PictureGet(this);
		newIntent(getIntent());
		pd = new ProgressDialog(this);
		pd.setTitle(R.string.loading_data);
		mQueryHandler = new QueryHandler(this);
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter.getCursor() != null) {
			mAdapter.getCursor().close();
		}
		mQueryHandler.removeCallbacksAndMessages(ACT_LIVE_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_ACTLIVE);
		mCircleHandle.removeMessages(CircleHandle.MSG_INSERT_DATA);
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		if (mSendBitmap != null) {
			mSendBitmap.recycle();
			mSendBitmap = null;
		}
	}

	private void newIntent(Intent mIntent) {
		actTagId = mIntent.getLongExtra(UtilString.ACTID, -1);
		Log.i("test", "actId = "+actTagId);

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

		//		allliveuser = new ArrayList<HashMap<String, Object>>();
		//		int id = 0;
		//		for (int i = 0; i < 8; i++) {
		//			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
		//			Log.i("test", "id = "+ id);
		//			HashMap<String, Object> user = new HashMap<String, Object>();
		//			user.put("userportrait", R.drawable.portrait_default);
		//			user.put("username", "测试用户名" + (i + 1));
		//			user.put("useracttext", "测试内容，哦合法破坏骗人fdafjrjago发票" +
		//					"挖掘分配及股票撒啊撒大声大声大大大声大声大声的撒的撒的撒打算的撒旦撒旦撒大师的撒旦" + i + 1);
		//			if (i == 2 | i == 5)
		//				id = 0;
		//			user.put("useractpic", getLiveBitmap(id));
		//			user.put("time", "2012-03-26" + (i + 1));
		//			allliveuser.add(user);
		//		}

		mAdapter = new LiveCursorAdapter(this);
		livelist.setAdapter(mAdapter);


		mBottom = (LinearLayout) findViewById(R.id.live_bottom);

		EditText userCommit = (EditText) mBottom.findViewById(R.id.user_commit);
		userCommit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mSendContent = s.toString();
			}
		});
		userCommit.setHint("校园十佳歌手");
		mBottom.findViewById(R.id.camera).setOnClickListener(this);
		mBottom.findViewById(R.id.send).setOnClickListener(this);

	}

	void refreshViews(){

	}

	private Bitmap getLiveBitmap(int id) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
		bitmap = mPictureGet.resizeBitmap(bitmap, 400, 230);
		return bitmap;
	}
	
	private void showProgress(String title){
		pd = new ProgressDialog(this);
		pd.setTitle(R.string.refresh_data);
		pd.show();
	}
	private void dismissProgress(){
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFRESH_ACTLIVE:
				Log.i("test", this.toString() + "MSG_REFRESH_ACTLIVE");
//				mCircleHandle.refreshActLive();
//				if (pd != null) {
//					pd.dismiss();
//				}
				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_ACTLIVE);
				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
					@Override
					public void onRefreshFinish() {
						dismissProgress();
						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
					}
				});
//				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
				dismissProgress();
				showProgress(getResources().getString(R.string.loading_data));
//				if (pd != null) {
//					pd.setTitle(R.string.loading_data);
//					pd.show();
//				}
				String[] selectionArgs = {String.valueOf(actTagId)};
				String sortOrder = null;
				String selection = null;
				//choose query cursor
				selection = CircleContract.ActLive.ACTLIVE_ACT_ID + "= ?";
				mQueryHandler.startQuery(ACT_LIVE_QUERY_TOKEN, null, CircleContract.ActLive.CONTENT_URI, UtilString.commentProjection, selection, selectionArgs, sortOrder);
				break;
			case CircleHandle.MSG_INSERT_DATA:
				Log.i("test", this.toString() + "MSG_INSERT_DATA");
//				if (mSendBitmap == null && mSendContent.isEmpty()) {
				if (mSendBitmap == null && mSendContent.isEmpty()) {
					Toast.makeText(LiveActActivity.this, R.string.error_send, 0);
				} else {
					mSendBitmapUrl = PictureGet.saveBitmap(mSendBitmap);
					mCircleHandle.insertActLive(1, actTagId, mSendBitmapUrl, mSendContent);
					mCircleHandle.uploadPicture(mSendBitmap);
					//need reflesh data from network
					mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
				}
				break;
			default:
				break;
			}
		}
	};

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<LiveActActivity> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<LiveActActivity>((LiveActActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.i("test", "token = "+token);
			if (mActivity == null) {
				return;
			}
			if(token == ACT_LIVE_QUERY_TOKEN) {
				mActivity.get().loadActLiveFromDB(cursor);
			}

		}
	}

	public void loadActLiveFromDB(Cursor cursor) {
		if (cursor != null && cursor.getCount() != 0) {
			mAdapter.changeCursor(cursor);
			dismissProgress();
		} else if (tryloadtimes == 0){
			tryloadtimes ++;
			showDialog(DIALOG_REFRESH_DATA);
		}  else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
		dismissProgress();
	}

	static class ViewHolder {
		ImageView userPortrait;
		TextView usreName;
		TextView commentTime;
		TextView commentContent;
		ImageView commentPic;
		int position;
	}

	private class LiveCursorAdapter extends CursorAdapter {
		private LayoutInflater mInflater;

		public LiveCursorAdapter(Context context) {
			super(context, null, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			setView(view, context , cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.act_live_item, null); 
			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();
			holder.userPortrait = (ImageView) view.findViewById(R.id.user_portrait);
			holder.usreName = (TextView) view.findViewById(R.id.user_name);
			holder.commentTime = (TextView) view.findViewById(R.id.time);
			holder.commentContent = (TextView) view.findViewById(R.id.user_act_text);
			holder.commentPic = (ImageView) view.findViewById(R.id.user_act_pic);
			holder.commentPic.setOnClickListener(LiveActActivity.this);
			view.setTag(holder);
			return view;
		}

		private void setView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder)view.getTag();
			int portraitImg;
			String commentImgUrl = "";
			Bitmap mPortraitBitmap = null;
			Bitmap mCommentBitmap = null;
			String username = "";
			String commentContent = "";
			String commentTime = "";
			if (cursor != null) {
				int actPeopleId = cursor.getInt(UtilString.commentPeopleIdIndex);
				if (actPeopleId != 0) {
					String[] selectionArgs = {String.valueOf(actPeopleId)};
					String selection = CircleContract.People.PEOPLE_ID + "= ?";
					String sortOrder = null;
					Cursor peopleCursor = context.getContentResolver().query(CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, sortOrder);
					if (peopleCursor != null && peopleCursor.moveToFirst()) {
						portraitImg = peopleCursor.getInt(UtilString.peopleProtraitIndex);
						username = peopleCursor.getString(UtilString.peopleNameIndex);
						mPortraitBitmap = BitmapFactory.decodeResource(getResources(), portraitImg);
						mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
						if (mPortraitBitmap != null) {
							holder.userPortrait.setImageBitmap(mPortraitBitmap);
						} else {
							holder.userPortrait.setBackgroundResource(R.drawable.portrait_default);
						}
						holder.usreName.setText(username);
					}
					peopleCursor.close();

					commentContent = cursor.getString(UtilString.commentContentIndex);
					commentTime = cursor.getString(UtilString.commentTimeIndex);
					holder.commentContent.setText(commentContent);
					holder.commentTime.setText(commentTime);

					commentImgUrl = cursor.getString(UtilString.commentImgIndex);
					if(commentImgUrl.startsWith("http://")) {
						
					} else if(commentImgUrl.startsWith("/mnt/sdcard/")) {
						mCommentBitmap = mPictureGet.getPic(commentImgUrl);
					}
					if (mCommentBitmap != null) {
						mCommentBitmap = mPictureGet.resizeBitmap(mCommentBitmap, 400, 230);
					}
					holder.commentPic.setImageBitmap(mCommentBitmap);
				} else {
					view.setVisibility(View.GONE);
					//need remove this data because the people is null
				}
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			super.changeCursor(cursor);
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
			//Need check user login
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_INSERT_DATA);
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
		case DIALOG_REFRESH_DATA:
			AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);  
			mbuilder.setTitle(getString(R.string.dialog_data_empty_title));  
			mbuilder.setMessage(R.string.dialog_data_empty);
			mbuilder.setPositiveButton(R.string.act_confirm_editor, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null) {
						dialog.dismiss();
						if (pd != null) {
							showProgress(getResources().getString(R.string.refresh_data));
						}
						mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_ACTLIVE);
					}
				}
			});
			mbuilder.setNegativeButton(R.string.act_cancel_editor, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null) {
						dialog.dismiss();
						dismissProgress();
					}
				}
			});
			dialog = mbuilder.create();
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
				mSendBitmapUrl = uri.toString();
				String scheme = uri.getScheme();
				if (scheme.equalsIgnoreCase("file")) {
					mSendBitmap = mPictureGet.getPic(uri.getPath());
				} else if (scheme.equalsIgnoreCase("content")) {
					mSendBitmap = mPictureGet.getPicFromDatabase(uri);
				}  
				break;  
			}  
		}
		Log.i("test", "mSendBitmapUrl = "+mSendBitmapUrl);
//		if (mSendBitmap != null){
//			mSendBitmap = mPictureGet.resizeBitmap(mSendBitmap, 400, 230);
//			Log.i("test", "actPoster = " + mSendBitmap);
//		}
		super.onActivityResult(requestCode, resultCode, data);  
	}  
}
