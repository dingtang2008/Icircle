package com.ile.icircle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CircleHandle extends Handler {

	public static final int LOADER_DATA = 0;
	public static final int MSG_START_QUERY = 1;
	public static final int MSG_REFLESH_HOTACT = 1001;
	public static final int MSG_REFLESH_ACTPEOPLE = 1002;
	public static final int MSG_REFLESH_PEOPLE = 1003;
	public static final int MSG_REFLESH_PERSONAL = 1004;
	public static final int MSG_LOGIN = 1005;
	
	Context mContext;
	
	CircleHandle(Context context){
		mContext = context;
	}
	
	public void refreshAct(){
		testActdata();
	}
	
	public void refreshActPeople(){
		testActPeopledata();
	}
	
	public void refreshPeople(){
		testPeopledata();
	}
	
	private void testActPeopledata() {
		Log.i("test", "testActPeopledata");
		ContentValues mCulValue = new ContentValues();
		for (int i = 0; i < 50; i ++){
			mCulValue.put(CircleContract.ActPeople.PEOPLE_ID, 1000 + i);
			if ((i % 3) == 0) {
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10007);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10001);
			} else if ((i % 5) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10001);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10002);
			} else if ((i % 7) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10002);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10003);
			} else if ((i % 9) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10003);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10004);
			} else if ((i % 11) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10004);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10005);
			} else if ((i % 13) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10005);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10006);
			} else if ((i % 17) == 0){
				mCulValue.put(CircleContract.ActPeople.ATTEND_ACT_TAGID, 10006);
				mCulValue.put(CircleContract.ActPeople.INTREST_ACT_TAGID, 10007);
			}
			Uri count = mContext.getContentResolver().insert(CircleContract.ActPeople.CONTENT_URI, mCulValue);
			Log.i("test", "count = "+count);
		}
	}
	
	private void testPeopledata() {
		Log.i("test", "testPeopledata");
		ContentValues mCulValue = new ContentValues();
		for (int i = 0; i < 50; i ++){
			mCulValue.put(CircleContract.People.PEOPLE_ID, 1000 + i);
			mCulValue.put(CircleContract.People.PROTRAIT, R.drawable.portrait_default);
			mCulValue.put(CircleContract.People.NAME, "test" + i);
			Uri count = mContext.getContentResolver().insert(CircleContract.People.CONTENT_URI, mCulValue);
			Log.i("test", "count = "+count);
		}
	}

	public void refreshPersonal(){
		testPersonaldata();
	}

	private void testPersonaldata() {
		// TODO Auto-generated method stub
		
	}

	public void testActdata(){
		Log.i("test", "testdata");
		String[] actTimeStrings = mContext.getResources().getStringArray(R.array.hot_act_time_test);
		String[] actLocationStrings = mContext.getResources().getStringArray(R.array.hot_act_location_test);
		String[] actClassifyIntroduceStrings = mContext.getResources().getStringArray(R.array.hot_act_classify_introduce_test);
		String[] actClassifyTitleStrings = mContext.getResources().getStringArray(R.array.hot_act_classify_title_test);
		String[] actStateStrings = mContext.getResources().getStringArray(R.array.hot_act_state_test);
		Integer[] mInterestCountTest = { 154,233, 444,111,123,72,29};
		Integer[] mAttendCountTest = { 123,22, 33,12,5,44,65};
		Integer[] TagIdTest = { 10001,10002, 10003,10004,10005,10006,10007};
		String[] actIntroduceTest = { "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试", 
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试"};
		int[] hot_act_img_test_id = {
				R.drawable.test1,
				R.drawable.test2,
				R.drawable.test3,
				R.drawable.test4,
				R.drawable.test5,
				R.drawable.test6,
				R.drawable.test7
		};

		Calendar mCalendar = Calendar.getInstance();
		Date myDate = mCalendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

		Log.i("test", "myDate = "+myDate.toString());
		String date = null;
		date = format.format(new Date());
		Log.i("test", "date = "+date.toString());
		
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
			mCulValue.put(CircleContract.Activity.POSTER, hot_act_img_test_id[i]);
			mCulValue.put(CircleContract.Activity.TAG_ID, TagIdTest[i]);
			mCulValue.put(CircleContract.Activity.ACT_INTRODUCE, actIntroduceTest[i]);
			mCulValue.put(CircleContract.Activity.PUBLISH_TIME, date);
			Uri count = mContext.getContentResolver().insert(CircleContract.Activity.CONTENT_URI, mCulValue);
			Log.i("test", "count = "+count);
		}
	}
}
