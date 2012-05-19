package com.ile.icircle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ile.icircle.ScrollLayout;
import com.ile.icircle.ScrollLayout.OnViewChangeListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;
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

	private String[] hot_act_img_url = {
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

	private Integer[] mInterestCountTest = { 154,233, 444,111,123,72,29};
	private Integer[] mAttendCountTest = { 123,22, 33,12,5,44,65};
	private String mSchool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_hot_layout);
		SharedPreferences sharedata = getSharedPreferences(UtilString.SCHOOLNAME, 0);
		mSchool = sharedata.getString(UtilString.SCHOOLNAME, ""); 
		init();
	}

	public static ScrollLayout getscroll(){
		return mScrollLayout;
	}

	private void init() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.scrollLayout);
		mCulValue = new ContentValues();
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
		refreshView();
	}

	private void refreshView() {
		mCurView = (LinearLayout)findViewById(hot_act_view_id[mCurSel]);

		ImageView mCurHotImg = (ImageView)mCurView.findViewById(R.id.act_img);
		TextView mCurHotTime = (TextView)mCurView.findViewById(R.id.time_content);
		TextView mCurHotLocation = (TextView)mCurView.findViewById(R.id.location_content);
		TextView mCurHotClassifyContent = (TextView)mCurView.findViewById(R.id.classify_content);
		TextView mCurHotClassifyTitle = (TextView)mCurView.findViewById(R.id.classify_title);
		mCurHotImg.setBackgroundResource(hot_act_img_test_id[mCurSel]);
		//Bitmap bitmap = getHttpBitmap(hot_act_img_url[mCurSel]);
		//mCurHotImg.setImageBitmap(hot_act_img_test_id[mCurSel]);

		String[] actTimeStrings = getResources().getStringArray(R.array.hot_act_time_test);
		String[] actLocationStrings = getResources().getStringArray(R.array.hot_act_location_test);
		String[] actClassifyContentStrings = getResources().getStringArray(R.array.hot_act_classify_test);
		String[] actClassifyTitleStrings = getResources().getStringArray(R.array.hot_act_classify_title_test);
		String[] actStateStrings = getResources().getStringArray(R.array.hot_act_state_test);

		mCurHotTime.setText(actTimeStrings[mCurSel]);
		mCurHotLocation.setText(actLocationStrings[mCurSel]);
		mCurHotClassifyContent.setText(actClassifyContentStrings[mCurSel]);
		mCurHotClassifyTitle.setText(actClassifyTitleStrings[mCurSel]);
		mCulValue.put("classifytitle", actClassifyTitleStrings[mCurSel]);
		mCulValue.put("classify", actClassifyContentStrings[mCurSel]);
		mCulValue.put("location", actLocationStrings[mCurSel]);
		mCulValue.put("time", actTimeStrings[mCurSel]);
		mCulValue.put("state", actStateStrings[mCurSel]);

		mState.setText(actStateStrings[mCurSel]);
		mInterestPeople.setText(mInterestCountTest[mCurSel].toString());
		mAttendPeople.setText(mAttendCountTest[mCurSel].toString());

		mImageViews[mCurSel].setEnabled(true);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(false);
		mCurSel = index;
		refreshView();
	}

	/*
	 * ��ȡ����ͼƬ��Դ
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
			intent.putExtra(UtilString.ACVALUES, mCulValue);
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

}
