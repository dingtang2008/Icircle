package com.ile.icircle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ile.icircle.ScrollLayout;
import com.ile.icircle.CircleHandle.RefreshFinishListener;
import com.ile.icircle.ScrollLayout.OnViewChangeListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HotActivity extends Activity implements OnClickListener{

	private int mViewCount;
	private int mCurSel;
	private int tryloadtimes = 0;
	private static ScrollLayout mScrollLayout;
	private int[] hot_act_view_id = {
			R.id.hot_act1,
			R.id.hot_act2,
			R.id.hot_act3,
			R.id.hot_act4,
			R.id.hot_act5,
			R.id.hot_act6,
			R.id.hot_act7
	};
	private int[] hot_act_img_test_id = {
			R.drawable.test1,
			R.drawable.test2,
			R.drawable.test3,
			R.drawable.test4,
			R.drawable.test5,
			R.drawable.test6,
			R.drawable.test7
	};

	private LinearLayout mCurView;
	private TextView mtitle;
	private ImageView[] mImageViews;
	private LinearLayout linearLayout;
	private RelativeLayout mTitle;
	private TextView mState;
	private TextView mInterestPeople;
	private TextView mAttendPeople;
	//private ContentValues mCulValue;

	private String[] hot_act_img_url = new String[7];

	private int[] mInterestCountTest = new int[7];
	private int[] mAttendCountTest = new int[7];
	private String[] actTimeStrings = new String[7];
	private String[] actLocationStrings = new String[7];
	private String[] actTitleStrings = new String[7];
	private String[] actClassifyStrings = new String[7];
	private String[] actStateStrings = new String[7];
	private long[] actTagIds = new long[7];

	private String mSchool;
	private boolean isDataLoader = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_hot_layout);
		//mCulValue = new ContentValues();
		SharedPreferences sharedata = getSharedPreferences(UtilString.SCHOOLNAME, 0);
		mSchool = sharedata.getString(UtilString.SCHOOLNAME, "");
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_HOTACT);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		if (loaderpd != null) {
			loaderpd.dismiss();
			loaderpd = null;
		}
		if (refreshpd != null) {
			refreshpd.dismiss();
			refreshpd = null;
		}
	}

	public static ScrollLayout getscroll(){
		return mScrollLayout;
	}

	private void init() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
		mViewCount = mScrollLayout.getChildCount();
		mCurSel = 0;

		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new ImageView[mViewCount];

		mScrollLayout.SetOnViewChangeListener(new OnViewChangeListener(){
			public void OnViewChange(int view) {
				setCurPoint(view);
			}
		});

		mTitle = (RelativeLayout) findViewById(R.id.title);
		ImageView extend_add = (ImageView) mTitle.findViewById(R.id.act_extend);
		extend_add.setBackgroundResource(R.drawable.add_act_selector);
		extend_add.setOnClickListener(this);

		mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(mSchool);
		mtitle.setBackgroundResource(R.drawable.title_text_bg);
		mtitle.setOnClickListener(this);

		mState =  (TextView) findViewById(R.id.act_state);
		mInterestPeople =  (TextView) findViewById(R.id.interest_count);
		mAttendPeople =  (TextView) findViewById(R.id.attend_count);

		Button btn_act_detail = (Button) findViewById(R.id.btn_act_detail);
		btn_act_detail.setOnClickListener(this);

		linearLayout = (LinearLayout) findViewById(R.id.guide);
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(false);
			mImageViews[i].setTag(i);
		}
		//refreshView();
		//mHandler.sendEmptyMessage(REFRESH_DATA);
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	private void refreshView() {
		mCurView = (LinearLayout)findViewById(hot_act_view_id[mCurSel]);

		ImageView mCurHotImg = (ImageView)mCurView.findViewById(R.id.act_img);
		TextView mCurHotTime = (TextView)mCurView.findViewById(R.id.time_content);
		TextView mCurHotLocation = (TextView)mCurView.findViewById(R.id.location_content);
		TextView mCurHotTitle = (TextView)mCurView.findViewById(R.id.act_title);
		TextView mCurHotClassify = (TextView)mCurView.findViewById(R.id.act_classify);

		Bitmap mPoster = null;
		PictureGet mPictureGet = new PictureGet(this);
		if(hot_act_img_url[mCurSel].startsWith("http://")) {
		} else if(hot_act_img_url[mCurSel].startsWith("/mnt/sdcard/")) {
			mPoster = mPictureGet.getPic(hot_act_img_url[mCurSel]);
		}
		//		mCurHotImg.setBackgroundResource(hot_act_img_url[mCurSel]);
		mCurHotImg.setImageBitmap(mPoster);
		mCurHotTime.setText(actTimeStrings[mCurSel]);
		mCurHotLocation.setText(actLocationStrings[mCurSel]);
		mCurHotClassify.setText(actClassifyStrings[mCurSel]);
		mCurHotTitle.setText(actTitleStrings[mCurSel]);

		mState.setText(actStateStrings[mCurSel]);
		mInterestPeople.setText(String.valueOf(mInterestCountTest[mCurSel]));
		mAttendPeople.setText(String.valueOf(mAttendCountTest[mCurSel]));

		mImageViews[mCurSel].setEnabled(true);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(false);
		mCurSel = index;
		if (isDataLoader) {
			refreshView();
		} else {
			mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
		}
	}

	/*
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url){
		URL myFileURL;
		Bitmap bitmap=null;
		try{
			myFileURL = new URL(url);
			HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			//conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_act_detail:
			intent = new Intent(this, DetailActActivity.class);
			intent.putExtra(UtilString.ACTID, actTagIds[mCurSel]);
			//intent.putExtra(UtilString.ACVALUES, mCulValue);
			startActivity(intent);
			break;
		case R.id.act_extend:
			intent = new Intent(this, EditActActivity.class);
			//intent.putExtra(UtilString.ACTID, mCurSel);
			startActivity(intent);
			break;
		case R.id.act_title:
			intent = new Intent(this, MyLocation.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	ProgressDialog loaderpd;
	ProgressDialog refreshpd;

	class HotActTask extends AsyncTask<Cursor, Integer, Cursor> {

		public HotActTask(Context context){
			//			if (!isDataLoader) {
			loaderpd = new ProgressDialog(context); 
			loaderpd.setTitle(R.string.loading_data);
			loaderpd.show();
			//			}
		}

		@Override
		protected Cursor doInBackground(Cursor... cursors) {
			String selection = CircleContract.Activity.HOTTAG + "=1 AND " + CircleContract.Activity.ACT_INVITER_PERSONAL + "=0";
			Cursor cursor = getContentResolver().query(CircleContract.Activity.CONTENT_URI, UtilString.hotActProjection, 
					selection, null, null);
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor cursor) {
			// TODO Auto-generated method stub
			loadDataFromDB(cursor);
		}

	}

	private void loadDataFromDB(Cursor cursor) {
		if (cursor != null && cursor.getCount() != 0) {
			tryloadtimes ++;
			for(int i = 0; i < 7; i++) {
				if (cursor.moveToPosition(i)) {
					actTimeStrings[i] = cursor.getString(UtilString.actStartTimeIndex) + "-" + cursor.getString(UtilString.actEndTimeIndex);
					actLocationStrings[i] = cursor.getString(UtilString.actLocationIndex);
					actTitleStrings[i] = cursor.getString(UtilString.actTitleIndex);
					actClassifyStrings[i] = cursor.getString(UtilString.actClassidyIndex);
					actStateStrings[i] = cursor.getString(UtilString.actStateIndex);
					mInterestCountTest[i] = cursor.getInt(UtilString.actInterestPeopleIndex);
					mAttendCountTest[i] = cursor.getInt(UtilString.actAttendPeopleIndex);
					hot_act_img_url[i] = cursor.getString(UtilString.actPosterIndex);
					actTagIds[i] = cursor.getLong(UtilString.actTagIdIndex);
				}
			}
			isDataLoader = true;
			if (loaderpd != null) {
				loaderpd.dismiss();
				loaderpd = null;
			}
			cursor.close();
			refreshView();
		} else if (tryloadtimes == 0){
			tryloadtimes ++;
			isDataLoader = false;
			if (loaderpd != null) {
				loaderpd.dismiss();
				loaderpd = null;
			}
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_HOTACT);
		} else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
	}

	CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFRESH_HOTACT:
				refreshpd = new ProgressDialog(HotActivity.this); 
				refreshpd.setTitle(R.string.refresh_data);
				refreshpd.show();
//				mCircleHandle.refreshAct();
				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_HOTACT);
				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
					@Override
					public void onRefreshFinish() {
						if (refreshpd != null) {
							refreshpd.dismiss();
							refreshpd = null;
						}
						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
					}
				});
//				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
				break;
			case CircleHandle.LOADER_DATA:
				HotActTask mHotActTask = new HotActTask(HotActivity.this);
				mHotActTask.execute(null);
				break;
			default:
				break;
			}
		}
	};
}
