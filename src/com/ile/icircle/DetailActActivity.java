package com.ile.icircle;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ile.icircle.HotActivity.HotActTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActActivity extends Activity implements OnClickListener{

	private RelativeLayout mTitle;
	private TextView mtitle;
	private TextView actTitle;
	private TextView timeContent;
	private TextView locationContent;
	private TextView actState;
	private TextView actClassify;
	private TextView actIntroduce;
	private TextView detailInterstate;
	private TextView detailAttend;
	private long actTagId;
	private ImageView mPoster;
	private ContentValues mValue;


	private TextView toAttend;
	private TextView toInterest;
	private LinearLayout toAttendLayout;
	private LinearLayout toInterestLayout;
	private ImageView toAttendImg;
	private ImageView toInterestImg;
	private int toAttendColor;
	private int toInterestColor;
	private int whiteColor;

	private LinearLayout interestView;
	private LinearLayout attendView;
	//	private Integer[] mImageIds = {
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default
	//	}; //should be 37*37dip
	//	private String[] mStrings = { "pjol","sss", "sdad", "sda", "sdad","adad"};

	QueryHandler mQueryHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_detail_layout);
		mQueryHandler = new QueryHandler(this);
		Intent mIntent = getIntent();
		actTagId = mIntent.getLongExtra(UtilString.ACTID, -1);
		Log.i("test", "actTagId = "+actTagId);
		mPictureGet = new PictureGet(this);

		init();
		initBottom();

		mCircleHandle.sendEmptyMessageDelayed(CircleHandle.MSG_START_QUERY, 100);
		//		startQuery();
	}

	private void init() {
		Log.i("test", "init");

		//		mValue = mIntent.getParcelableExtra(UtilString.ACVALUES);

		mTitle = (RelativeLayout) findViewById(R.id.title);
		Button extend_live = (Button) mTitle.findViewById(R.id.act_extend);
		mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		extend_live.setVisibility(View.VISIBLE);
		extend_live.setBackgroundResource(R.drawable.live_act_selector);
		extend_live.setText(R.string.btn_live);
		extend_live.setOnClickListener(this);

		mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(R.string.btn_detail);

		mPoster = (ImageView) findViewById(R.id.act_poster_img);//should be 135*81dip

		actClassify = (TextView) findViewById(R.id.act_classify);
		actTitle = (TextView) findViewById(R.id.act_detail_title);
		timeContent = (TextView) findViewById(R.id.time_content);
		locationContent = (TextView) findViewById(R.id.location_content);
		actState = (TextView) findViewById(R.id.act_state);
		actIntroduce = (TextView) findViewById(R.id.introduce);
		detailInterstate = (TextView) findViewById(R.id.detail_interest);
		detailAttend = (TextView) findViewById(R.id.detail_attend);
		//
		//		classifyTitle.setText("测试测试测试测试");
		//		classifyContent.setText("测试测试测试测试");
		//		timeContent.setText("测试测试测试测试");
		//		locationContent.setText(" 测试食堂1" + "\n 测试食堂2" + "\n 测试食堂3" + "\n 测试食堂4");
		//		actState.setText("测试测试");
		//
		//		actIntroduce.setText("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
		//				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");

		//		detailInterstate.setText(getString(R.string.detail_interest_people) + "   (45)");
		//		detailAttend.setText(getString(R.string.detail_attend_people) + "   (15)");

		interestLoadProgressbar = (ProgressBar) findViewById(R.id.interest_progressbar);
		attendLoadProgressbar = (ProgressBar) findViewById(R.id.attend_progressbar);

		interestView = (LinearLayout) findViewById(R.id.interest_list);


		//		for (int i = 0; i < 6; i++) {
		//			people = (LinearLayout) interestView.getChildAt(i);
		//			peopleIcon = (ImageView) people.findViewById(R.id.ItemImage);
		//			peopleName = (TextView) people.findViewById(R.id.ItemText);
		//
		//			peopleIcon.setBackgroundResource(mImageIds[i]);
		//			peopleName.setText(mStrings[i]);
		//		}

		attendView = (LinearLayout) findViewById(R.id.attend_list);
		//		for (int i = 0; i < 6; i++) {
		//			people = (LinearLayout) attendView.getChildAt(i);
		//			peopleIcon = (ImageView) people.findViewById(R.id.ItemImage);
		//			peopleName = (TextView) people.findViewById(R.id.ItemText);
		//
		//			peopleIcon.setBackgroundResource(mImageIds[i]);
		//			peopleName.setText(mStrings[i]);
		//		}

		findViewById(R.id.detail_attend_more).setOnClickListener(this);
		findViewById(R.id.detail_interset_more).setOnClickListener(this);
	}

	public void initBottom(){

		Log.i("test", "initBottom");
		toAttendColor = getResources().getColor(R.color.to_attend_color);
		toInterestColor  = getResources().getColor(R.color.to_intrest_color);
		whiteColor  = getResources().getColor(R.color.white);

		toAttendLayout = (LinearLayout) findViewById(R.id.to_attend_layout);
		toInterestLayout  = (LinearLayout) findViewById(R.id.to_interest_layout);
		toAttendImg = (ImageView) toAttendLayout.findViewById(R.id.to_attend_img);
		toInterestImg = (ImageView) toInterestLayout.findViewById(R.id.to_interest_img);
		toAttend = (TextView) toAttendLayout.findViewById(R.id.to_attend);
		toInterest = (TextView) toInterestLayout.findViewById(R.id.to_interest);
		toAttendLayout.setOnClickListener(this);
		toInterestLayout.setOnClickListener(this);
		toAttend.setTextSize(15);
		toInterest.setTextSize(15);
		if (isAttent) {
			toAttendLayout.setBackgroundResource(R.drawable.bottom_item_selected);
			toAttendImg.setBackgroundResource(R.drawable.ic_to_selected);
			toAttend.setTextColor(whiteColor);
		} else {
			toAttendLayout.setBackgroundDrawable(null);
			toAttendImg.setBackgroundResource(R.drawable.ic_to_attend);
			toAttend.setTextColor(toAttendColor);
		}
		if (isInterest) {
			toInterestLayout.setBackgroundResource(R.drawable.bottom_item_selected);
			toInterestImg.setBackgroundResource(R.drawable.ic_to_selected);
			toInterest.setTextColor(whiteColor);
		} else {
			toInterestLayout.setBackgroundDrawable(null);
			toInterestImg.setBackgroundResource(R.drawable.ic_to_intest);
			toInterest.setTextColor(toInterestColor);
		}
	}

	private void startQuery() {
		Log.i("test", "startQuery");
		String sortOrder = null;
		String[] selectionArgs = {String.valueOf(actTagId)};
		String selection = CircleContract.Activity.TAG_ID + "= ?";
		mQueryHandler.startQuery(ACT_DETAIL_QUERY_TOKEN, null, CircleContract.Activity.CONTENT_URI, UtilString.detailActProjection, selection, selectionArgs, sortOrder);
		selection = CircleContract.ActPeople.INTREST_ACT_TAGID + "= ?";
		mQueryHandler.startQuery(ACT_INTERESTPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
		selection = CircleContract.ActPeople.ATTEND_ACT_TAGID + "= ?";
		mQueryHandler.startQuery(ACT_ATTENDPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
	}

	private void refreshView() {
		actClassify.setText(actClassifyString);
		actTitle.setText(actTitleString);
		timeContent.setText(actTimeString);
		locationContent.setText(actLocationString);
		actState.setText(actStateString);
		detailInterstate.setText(getString(R.string.detail_interest_people) +  "(" + mInterestCount + ")");
		detailAttend.setText(getString(R.string.detail_attend_people) +  "(" + mAttendCount + ")");
		
		Bitmap mPosterBitmap = null;
		if(posterUrl.startsWith("http://")) {
		} else if(posterUrl.startsWith("/mnt/sdcard/")) {
			mPosterBitmap = mPictureGet.getPic(posterUrl);
		}
		//Bitmap mPosterBitmap = BitmapFactory.decodeResource(getResources(), poster_img);
		mPosterBitmap = mPictureGet.resizeBitmap(mPosterBitmap, 202, 121);
		if (mPosterBitmap != null) {
			mPoster.setImageBitmap(mPosterBitmap);
		} else {
			mPoster.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_default1));
		}

		actIntroduce.setText(actIntroduceString);
	}

	private void refreshInterestPeopleView() {
		Log.i("test", "refreshInterestPeopleView = ");
		Bitmap mPortraitBitmap;
		for (int i = 0; i < 6; i++) {
			LinearLayout interestpeople = (LinearLayout) interestView.getChildAt(i);
			ImageView interestpeopleIcon = (ImageView) interestpeople.findViewById(R.id.ItemImage);
			TextView interestpeopleName = (TextView) interestpeople.findViewById(R.id.ItemText);
			if (actInterestPeopleId[i] == 0) {
				interestpeople.setVisibility(View.INVISIBLE);
			} else {
				mPortraitBitmap = BitmapFactory.decodeResource(getResources(), interestPeoplePortrait[i]);
				mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
				if (mPortraitBitmap != null) {
					interestpeopleIcon.setImageBitmap(mPortraitBitmap);
				} else {
					interestpeopleIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.portrait_default));
				}
				//interestpeopleIcon.setBackgroundResource(interestPeoplePortrait[i]);
				interestpeopleName.setText(interestPeopleName[i]);
			}
			dismissProgress(interestLoadProgressbar);
		}
	}

	private void refreshAttendPeopleView() {
		Log.i("test", "refreshAttendPeopleView = ");
		Bitmap mPortraitBitmap;
		for (int i = 0; i < 6; i++) {

			LinearLayout attendpeople = (LinearLayout) attendView.getChildAt(i);
			ImageView attendpeopleIcon = (ImageView) attendpeople.findViewById(R.id.ItemImage);
			TextView attendpeopleName = (TextView) attendpeople.findViewById(R.id.ItemText);

			if (actAttendPeopleId[i] == 0) {
				attendpeople.setVisibility(View.INVISIBLE);
			} else {
				mPortraitBitmap = BitmapFactory.decodeResource(getResources(), interestPeoplePortrait[i]);
				mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
				if (mPortraitBitmap != null) {
					attendpeopleIcon.setImageBitmap(mPortraitBitmap);
				} else {
					attendpeopleIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.portrait_default));
				}
				//attendpeopleIcon.setBackgroundResource(attendPeoplePortrait[i]);
				attendpeopleName.setText(attendPeopleName[i]);
			}
			dismissProgress(attendLoadProgressbar);
//			attendLoadProgressbar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mQueryHandler.removeCallbacksAndMessages(ACT_DETAIL_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(ACT_INTERESTPEOPLE_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(ACT_ATTENDPEOPLE_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(INTERESTPEOPLE_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(ATTENDPEOPLE_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_ACTPEOPLE);
		mCircleHandle.removeMessages(CircleHandle.MSG_START_QUERY);
		Log.i("test", "onDestroy mCircleHandle.hasMessages(CircleHandle.MSG_REFRESH_ACTPEOPLE) = "+mCircleHandle.hasMessages(CircleHandle.MSG_REFRESH_ACTPEOPLE));

		interestLoadProgressbar.setVisibility(View.GONE);
		attendLoadProgressbar.setVisibility(View.GONE);
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.act_back:
			finish();
			break;
		case R.id.act_extend:
			intent = new Intent(this, LiveActActivity.class);
			intent.putExtra(UtilString.ACTID, actTagId);
			startActivity(intent);
			overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
			break;
		case R.id.to_attend_layout:
			//Need check user login
			toInterestLayout.setBackgroundDrawable(null);
			toInterestImg.setBackgroundResource(R.drawable.ic_to_intest);
			toInterest.setTextColor(toInterestColor);
			isInterest = false;
			mCircleHandle.deleteActAttendInterest(1, actTagId, UtilString.INTEREST);
			if (!isAttent) {
				toAttendLayout.setBackgroundResource(R.drawable.bottom_item_selected);
				toAttendImg.setBackgroundResource(R.drawable.ic_to_selected);
				toAttend.setTextColor(whiteColor);
				isAttent = true;
				mCircleHandle.insertActAttendInterest(1, actTagId, UtilString.ATTEND);
			} else {
				toAttendLayout.setBackgroundDrawable(null);
				toAttend.setTextColor(toAttendColor);
				toAttendImg.setBackgroundResource(R.drawable.ic_to_attend);
				isAttent = false;
				mCircleHandle.deleteActAttendInterest(1, actTagId, UtilString.ATTEND);
			}
			//mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
			break;
		case R.id.to_interest_layout:
			//Need check user login
			Log.i("test", "to_interest_layout");
			Log.i("test", "isInterest = "+isInterest);
			toAttendLayout.setBackgroundDrawable(null);
			toAttend.setTextColor(toAttendColor);
			toAttendImg.setBackgroundResource(R.drawable.ic_to_attend);
			isAttent = false;
			mCircleHandle.deleteActAttendInterest(1, actTagId, UtilString.ATTEND);
			if (!isInterest) {
				toInterestLayout.setBackgroundResource(R.drawable.bottom_item_selected);
				toInterestImg.setBackgroundResource(R.drawable.ic_to_selected);
				toInterest.setTextColor(whiteColor);
				isInterest = true;
				mCircleHandle.insertActAttendInterest(1, actTagId, UtilString.INTEREST);
			} else {
				toInterestLayout.setBackgroundDrawable(null);
				toInterestImg.setBackgroundResource(R.drawable.ic_to_intest);
				toInterest.setTextColor(toInterestColor);
				isInterest = false;
				mCircleHandle.deleteActAttendInterest(1, actTagId, UtilString.INTEREST);
			}
			//mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
			break;
		case R.id.detail_attend_more:
			intent = new Intent(this, DetailActExtendActivity.class);
			intent.putExtra(UtilString.DETAILEXTEND, UtilString.ATTEND);
			intent.putExtra(UtilString.ACTID, actTagId);
			intent.putExtra(UtilString.DETAILEXTENDTITLE, detailAttend.getText());
			startActivity(intent);
			break;
		case R.id.detail_interset_more:
			intent = new Intent(this, DetailActExtendActivity.class);
			intent.putExtra(UtilString.DETAILEXTEND, UtilString.INTEREST);
			intent.putExtra(UtilString.ACTID, actTagId);
			intent.putExtra(UtilString.DETAILEXTENDTITLE, detailInterstate.getText());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<DetailActActivity> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<DetailActActivity>((DetailActActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.i("test", "token = "+token);
			if (mActivity == null) {
				return;
			}
			if (token == ACT_DETAIL_QUERY_TOKEN) {
				mActivity.get().loadDetailDataFromDB(cursor);
			}
			if(token == ACT_INTERESTPEOPLE_QUERY_TOKEN || token == ACT_ATTENDPEOPLE_QUERY_TOKEN) {
				mActivity.get().loadActPeopleFromDB(cursor, token);
			} else if(token == INTERESTPEOPLE_QUERY_TOKEN || token == ATTENDPEOPLE_QUERY_TOKEN) {
				mActivity.get().loadPeopleFromDB(cursor, token);
			}

		}
	}

	public CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_START_QUERY:
				Log.i("test", "MSG_START_QUERY");
				startQuery();
				break;
			case CircleHandle.MSG_REFRESH_ACTPEOPLE:
				Log.i("test", "MSG_REFRESH_ACTPEOPLE");
				showProgress(interestLoadProgressbar);
				showProgress(attendLoadProgressbar);
//				refreshTask.execute(null);
				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_ACTPEOPLE);
				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
					@Override
					public void onRefreshFinish() {
						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
					}
				});
//				mCircleHandle.refreshActPeople();
//				mCircleHandle.refreshPeople();
//				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
				String[] selectionArgs = {String.valueOf(actTagId)};
				String sortOrder = null;
				String selection = CircleContract.ActPeople.INTREST_ACT_TAGID + "= ?";
				mQueryHandler.startQuery(ACT_INTERESTPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
				selection = CircleContract.ActPeople.ATTEND_ACT_TAGID + "= ?";
				mQueryHandler.startQuery(ACT_ATTENDPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
				break;
			default:
				break;
			}
		}
	};

	public int tryloadtimes = 0;
	public boolean isAttent = false;
	public boolean isInterest = false;
	private static final int ACT_DETAIL_QUERY_TOKEN = 100;
	private static final int ACT_INTERESTPEOPLE_QUERY_TOKEN = 101;
	private static final int ACT_ATTENDPEOPLE_QUERY_TOKEN = 102;
	private static final int INTERESTPEOPLE_QUERY_TOKEN = 103;
	private static final int ATTENDPEOPLE_QUERY_TOKEN = 104;


	public ProgressBar interestLoadProgressbar;
	public ProgressBar attendLoadProgressbar;

	private int[] interestPeoplePortrait = new int[6];

	private String[] interestPeopleName = new String[6];

	private int[] attendPeoplePortrait = new int[6];

	private String[] attendPeopleName = new String[6];

	private int[] actInterestPeopleId = new int[6];
	private int[] actAttendPeopleId = new int[6];

	private int poster_img;
	private String posterUrl;
	private int mInterestCount;
	private int mAttendCount;
	private String actTimeString;
	private String actLocationString;
	private String actTitleString;
	private String actClassifyString;
	private String actStateString;

	private String actIntroduceString;
	private Date actPublishTime;
	private long actTagIds;
	private PictureGet mPictureGet;

	public void loadDetailDataFromDB(Cursor cursor) {
		if (cursor == null) {
			return;
		}
		int length = cursor.getCount();

		Log.i("test", "cursor.getCount() = "+cursor.getCount());
		cursor.moveToFirst();
		if (length != 0) {
			actTimeString = cursor.getString(UtilString.actStartTimeIndex) + "-" + cursor.getString(UtilString.actEndTimeIndex);
			actLocationString = cursor.getString(UtilString.actLocationIndex);
			actTitleString = cursor.getString(UtilString.actTitleIndex);
			actClassifyString = cursor.getString(UtilString.actClassidyIndex);
			actStateString = cursor.getString(UtilString.actStateIndex);
			mInterestCount = cursor.getInt(UtilString.actInterestPeopleIndex);
			mAttendCount = cursor.getInt(UtilString.actAttendPeopleIndex);
//			poster_img = cursor.getInt(UtilString.actPosterIndex);
			posterUrl = cursor.getString(UtilString.actPosterIndex);
			actTagIds = cursor.getInt(UtilString.actTagIdIndex);

			actIntroduceString = cursor.getString(UtilString.actIntroduceIndex);
			String myDate =cursor.getString(UtilString.actPublishTimeIndex); 
			Log.i("test", "myDate = "+myDate);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
			try {
				Date date = format.parse(myDate);
				actPublishTime = date;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshView();
		}
		cursor.close();
	}

	public void loadActPeopleFromDB(Cursor cursor, int queryToken) {

		Log.i("test", "loadActPeopleFromDB queryToken = " + queryToken);
		if (cursor != null && cursor.getCount() != 0) {
			Log.i("test", "cursor.getCount() = "+cursor.getCount());
			for (int i = 0; i < 6; i++) {
				if (cursor.moveToPosition(i)) {
					if (queryToken == ACT_INTERESTPEOPLE_QUERY_TOKEN) {
						actInterestPeopleId[i] = cursor.getInt(UtilString.actPeoplePeopleIdIndex);
						Log.i("test", "actInterestPeopleId[i] = "+actInterestPeopleId[i]);
						if (actInterestPeopleId[i] != 0) {
							String[] selectionArgs = {String.valueOf(actInterestPeopleId[i])};
							String selection = CircleContract.People.PEOPLE_ID + "= ?";
							mQueryHandler.startQuery(INTERESTPEOPLE_QUERY_TOKEN, null, CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, null);
						}
					} else if (queryToken == ACT_ATTENDPEOPLE_QUERY_TOKEN) {
						actAttendPeopleId[i] = cursor.getInt(UtilString.actPeoplePeopleIdIndex);
						if (actAttendPeopleId[i] != 0) {
							String[] selectionArgs = {String.valueOf(actAttendPeopleId[i])};
							String selection = CircleContract.People.PEOPLE_ID + "= ?";
							mQueryHandler.startQuery(ATTENDPEOPLE_QUERY_TOKEN, null, CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, null);
						}
					}
				}
			}
		} else if (tryloadtimes == 0){
			Log.i("test", "loadActPeopleFromDB tryloadtimes = " + tryloadtimes);
			tryloadtimes ++;
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_ACTPEOPLE);
		} else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
		cursor.close();
	}

	public void loadPeopleFromDB(Cursor cursor, int queryToken) {

		Log.i("test", "loadPeopleFromDB queryToken = " + queryToken);
		int peopleId;
		if (cursor != null && cursor.getCount() != 0) {
			Log.i("test", "cursor.getCount() = "+cursor.getCount());
			if (cursor.moveToFirst()) {
				peopleId = cursor.getInt(UtilString.peoplePeopleIdIndex);
				Log.i("test", "peopleId = "+peopleId);
				if (queryToken == INTERESTPEOPLE_QUERY_TOKEN) {
					for (int i = 0; i < 6; i++) {
						Log.i("test", "actInterestPeopleId = "+actInterestPeopleId[i]);
						if (peopleId == actInterestPeopleId[i]) {
							interestPeoplePortrait[i] = cursor.getInt(UtilString.peopleProtraitIndex);
							interestPeopleName[i] = cursor.getString(UtilString.peopleNameIndex);
							Log.i("test", "interestPeoplePortrait = "+interestPeoplePortrait[i]);
							Log.i("test", "interestPeopleName = "+interestPeopleName[i]);
						}
					}
					refreshInterestPeopleView();
				} else if (queryToken == ATTENDPEOPLE_QUERY_TOKEN) {
					for (int i = 0; i < 6; i++) {
						if (peopleId == actAttendPeopleId[i]) {
							attendPeoplePortrait[i] = cursor.getInt(UtilString.peopleProtraitIndex);
							attendPeopleName[i] = cursor.getString(UtilString.peopleNameIndex);
						}
					}
					refreshAttendPeopleView();
				}
			}
		} else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
		cursor.close();
	}
	

	
	private void showProgress(ProgressBar pb){
		pb.setVisibility(View.VISIBLE);
	}
	private void dismissProgress(ProgressBar pb){
		if (pb != null) {
			pb.setVisibility(View.GONE);
		}
	}
}
