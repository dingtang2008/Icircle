package com.ile.icircle;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CircleHandle extends Handler {

	public static final int LOADER_DATA = 0;
	public static final int MSG_REFLESH_HOTACT = 1;
	public static final int MSG_REFLESH_PEOPLE = 2;
	public static final int MSG_LOGIN = 3;
	
	Context mContext;
	
	CircleHandle(Context context){
		mContext = context;
	}
	
	public void refreshHotAct(){
		testdata();
	}

	public void testdata(){
		Log.i("test", "testdata");
		String[] actTimeStrings = mContext.getResources().getStringArray(R.array.hot_act_time_test);
		String[] actLocationStrings = mContext.getResources().getStringArray(R.array.hot_act_location_test);
		String[] actClassifyIntroduceStrings = mContext.getResources().getStringArray(R.array.hot_act_classify_introduce_test);
		String[] actClassifyTitleStrings = mContext.getResources().getStringArray(R.array.hot_act_classify_title_test);
		String[] actStateStrings = mContext.getResources().getStringArray(R.array.hot_act_state_test);
		Integer[] mInterestCountTest = { 154,233, 444,111,123,72,29};
		Integer[] mAttendCountTest = { 123,22, 33,12,5,44,65};
		int[] hot_act_img_test_id = {
				R.drawable.test1,
				R.drawable.test2,
				R.drawable.test3,
				R.drawable.test4,
				R.drawable.test5,
				R.drawable.test6,
				R.drawable.test7
		};

		ContentValues mCulValue = new ContentValues();
		for (int i = 0; i < 7;i ++){
			mCulValue.put(CircleContract.Activity.CLASSIFY_TITLE, actClassifyTitleStrings[i]);
			mCulValue.put(CircleContract.Activity.CLASSIFY_INTRODUCE, actClassifyIntroduceStrings[i]);
			mCulValue.put(CircleContract.Activity.LOCATION, actLocationStrings[i]);
			mCulValue.put(CircleContract.Activity.START_TIME, actTimeStrings[i]);
			mCulValue.put(CircleContract.Activity.END_TIME, actTimeStrings[i]);
			mCulValue.put(CircleContract.Activity.STATE, actStateStrings[i]);
			mCulValue.put(CircleContract.Activity.HOTTAG, 1);
			mCulValue.put(CircleContract.Activity.INTEREST_PEOPLE, mInterestCountTest[i]);
			mCulValue.put(CircleContract.Activity.ATTEND_PEOPLE, mAttendCountTest[i]);
			mCulValue.put(CircleContract.Activity.POSTERURL, hot_act_img_test_id[i]);
			Uri count = mContext.getContentResolver().insert(CircleContract.Activity.CONTENT_URI, mCulValue);
			Log.i("test", "count = "+count);
		}
	}
}
