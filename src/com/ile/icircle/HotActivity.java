package com.ile.icircle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ile.icircle.ScrollLayout;
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

public class HotActivity extends Activity implements OnClickListener{

	private int mViewCount;
	private int mCurSel;
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
	private ContentValues mCulValue;

	private int[] hot_act_img_url = new int[7];

	private String[] mInterestCountTest = new String[7];
	private String[] mAttendCountTest = new String[7];
	private String[] actTimeStrings = new String[7];
	private String[] actLocationStrings = new String[7];
	private String[] actClassifyIntroduceStrings = new String[7];
	private String[] actClassifyTitleStrings = new String[7];
	private String[] actStateStrings = new String[7];
	
	private String mSchool;
	private boolean isDataLoader = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_hot_layout);
		mCulValue = new ContentValues();
		SharedPreferences sharedata = getSharedPreferences(UtilString.SCHOOLNAME, 0);
		mSchool = sharedata.getString(UtilString.SCHOOLNAME, "");
		init();
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
		TextView mCurHotClassifyContent = (TextView)mCurView.findViewById(R.id.classify_content);
		TextView mCurHotClassifyTitle = (TextView)mCurView.findViewById(R.id.classify_title);
		//Bitmap bitmap = getHttpBitmap(hot_act_img_url[mCurSel]);
		//mCurHotImg.setBackgroundResource(hot_act_img_test_id[mCurSel]);

		String[] actTimeStrings = getResources().getStringArray(R.array.hot_act_time_test);
		String[] actLocationStrings = getResources().getStringArray(R.array.hot_act_location_test);
		String[] actClassifyIntroduceStrings = getResources().getStringArray(R.array.hot_act_classify_introduce_test);
		String[] actClassifyTitleStrings = getResources().getStringArray(R.array.hot_act_classify_title_test);
		String[] actStateStrings = getResources().getStringArray(R.array.hot_act_state_test);

		mCurHotImg.setBackgroundResource(hot_act_img_url[mCurSel]);
		mCurHotTime.setText(actTimeStrings[mCurSel]);
		mCurHotLocation.setText(actLocationStrings[mCurSel]);
		mCurHotClassifyTitle.setText(actClassifyTitleStrings[mCurSel]);
		mCurHotClassifyContent.setText(actClassifyIntroduceStrings[mCurSel]);
		mCulValue.put("classifyintroduce", actClassifyIntroduceStrings[mCurSel]);
		mCulValue.put("classifytitle", actClassifyTitleStrings[mCurSel]);
		mCulValue.put("location", actLocationStrings[mCurSel]);
		mCulValue.put("time", actTimeStrings[mCurSel]);
		mCulValue.put("state", actStateStrings[mCurSel]);

		mState.setText(actStateStrings[mCurSel]);
		mInterestPeople.setText(mInterestCountTest[mCurSel].toString());
		mAttendPeople.setText(mAttendCountTest[mCurSel].toString());
//		if (cursor != null && cursor.moveToPosition(mCurSel)) {
//			Log.i("test", "cursor getCount = " + cursor.getCount());
//			Log.i("test", "cursor.getInt(UtilString.hotActIdIndex) = " + cursor.getInt(UtilString.hotActIdIndex));
//			Log.i("test", "cursor.getString(UtilString.hotActLocationIndex) = " + cursor.getString(UtilString.hotActLocationIndex));
//			Log.i("test", "cursor.getInt(UtilString.hotActHotTagIndex) = " + cursor.getInt(UtilString.hotActHotTagIndex));
//			if (cursor.getInt(UtilString.hotActHotTagIndex) == 1) {
//				mCurHotTime.setText(cursor.getString(UtilString.hotActStartTimeIndex) + "-" + cursor.getString(UtilString.hotActEndTimeIndex));
//				mCurHotLocation.setText(cursor.getString(UtilString.hotActLocationIndex));
//				mCurHotClassifyTitle.setText(cursor.getString(UtilString.hotActClassidyTitleIndex));
//				mCurHotClassifyContent.setText(cursor.getString(UtilString.hotActClassifyIntroduceIndex));
//				mCurHotImg.setBackgroundResource(cursor.getInt(UtilString.hotActPosterURLIndex));
//				mState.setText(cursor.getString(UtilString.hotActStateIndex));
//				mInterestPeople.setText(cursor.getString(UtilString.hotInterestPeopleIndex));
//				mAttendPeople.setText(cursor.getString(UtilString.hotAttendPeopleIndex));
//			}
//		}

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
			intent.putExtra(UtilString.ACTTITLE, ((Button)v).getText());
			intent.putExtra(UtilString.ACTID, mCurSel);
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

//	public void testdata(){
//		if (firsttime) {
//			return;
//		}
//		Log.i("test", "testdata");
//		String[] actTimeStrings = getResources().getStringArray(R.array.hot_act_time_test);
//		String[] actLocationStrings = getResources().getStringArray(R.array.hot_act_location_test);
//		String[] actClassifyIntroduceStrings = getResources().getStringArray(R.array.hot_act_classify_introduce_test);
//		String[] actClassifyTitleStrings = getResources().getStringArray(R.array.hot_act_classify_title_test);
//		String[] actStateStrings = getResources().getStringArray(R.array.hot_act_state_test);
//		Integer[] mInterestCountTest = { 154,233, 444,111,123,72,29};
//		Integer[] mAttendCountTest = { 123,22, 33,12,5,44,65};
//		for (int i = 0; i < hot_act_view_id.length;i ++){
//			mCulValue.put(CircleContract.Activity.CLASSIFY_TITLE, actClassifyTitleStrings[i]);
//			mCulValue.put(CircleContract.Activity.CLASSIFY_INTRODUCE, actClassifyIntroduceStrings[i]);
//			mCulValue.put(CircleContract.Activity.LOCATION, actLocationStrings[i]);
//			mCulValue.put(CircleContract.Activity.START_TIME, actTimeStrings[i]);
//			mCulValue.put(CircleContract.Activity.END_TIME, actTimeStrings[i]);
//			mCulValue.put(CircleContract.Activity.STATE, actStateStrings[i]);
//			mCulValue.put(CircleContract.Activity.HOTTAG, 1);
//			mCulValue.put(CircleContract.Activity.INTEREST_PEOPLE, mInterestCountTest[i]);
//			mCulValue.put(CircleContract.Activity.ATTEND_PEOPLE, mAttendCountTest[i]);
//			mCulValue.put(CircleContract.Activity.POSTERURL, hot_act_img_test_id[i]);
//			Uri count = getContentResolver().insert(CircleContract.Activity.CONTENT_URI, mCulValue);
//			Log.i("test", "count = "+count);
//		}
//		firsttime = true;
//	}

	ProgressDialog loaderpd;
	ProgressDialog refreshpd;
	
	class HotActTask extends AsyncTask<Cursor, Integer, Cursor> {

		public HotActTask(Context context){
			if (!isDataLoader) {
				loaderpd = new ProgressDialog(context); 
				loaderpd.show();
			}
		}

		@Override
		protected Cursor doInBackground(Cursor... cursors) {
			String selection = CircleContract.Activity.HOTTAG + "=1";
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
			for(int i = 0; i < 7; i++) {
				if (cursor.moveToPosition(i)) {
					actTimeStrings[i] = cursor.getString(UtilString.hotActStartTimeIndex) + "-" + cursor.getString(UtilString.hotActEndTimeIndex);
					actLocationStrings[i] = cursor.getString(UtilString.hotActLocationIndex);
					actClassifyIntroduceStrings[i] = cursor.getString(UtilString.hotActClassifyIntroduceIndex);
					actClassifyTitleStrings[i] = cursor.getString(UtilString.hotActClassidyTitleIndex);
					actStateStrings[i] = cursor.getString(UtilString.hotActStateIndex);
					mInterestCountTest[i] = cursor.getString(UtilString.hotInterestPeopleIndex);
					mAttendCountTest[i] = cursor.getString(UtilString.hotAttendPeopleIndex);
					hot_act_img_url[i] = cursor.getInt(UtilString.hotActPosterURLIndex);
				}
			}
			isDataLoader = true;
			if (loaderpd != null) {
				loaderpd.dismiss();
				loaderpd = null;
			}
			refreshView();
		} else {
			isDataLoader = false;
			if (loaderpd != null) {
				loaderpd.dismiss();
				loaderpd = null;
			}
			refreshpd = new ProgressDialog(this); 
			refreshpd.show();
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFLESH_HOTACT);
		}
	}
	
	CircleHandle mCircleHandle = new CircleHandle(this){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case CircleHandle.MSG_REFLESH_HOTACT:
				mCircleHandle.refreshHotAct();
				if (refreshpd != null) {
					refreshpd.dismiss();
					refreshpd = null;
				}
				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
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
