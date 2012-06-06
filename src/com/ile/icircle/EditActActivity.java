package com.ile.icircle;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ile.icircle.CircleHandle.RefreshFinishListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditActActivity extends Activity implements OnClickListener {
	PictureGet mPictureGet;

	private RelativeLayout mTitle;
	private boolean islimited = false;

	private int userId = 1;
	private boolean isLogin = false;

	private static final int REQUESTFRIEND = 1;

	private static final int SETECT_TIME_DIALOG = 0;
	private static final int DATE_DIALOG = 1;
	private static final int TIME_DIALOG = 2;
	private static final int LOCATION_DIALOG = 3;

	private ImageView friendslimit;
	private ImageView posterPreview;
	private Spinner classifySpinner;
	private TextView actStartDateView;
	private TextView actStartTimeView;
	private TextView actEndDateView;
	private TextView actEndTimeView;
	private EditText locationEditor; 

	EditText titleEditor;
	EditText introduceEditor;
	private LinearLayout friendsView;
	private LinearLayout friend;
	private ImageView friendIcon;
	private TextView friendName;

	private String actTitle = "";
	private String actIntroduce = "";
	private String actLocation = "";
	private String actClassify = "";
	private String actStartDate = "";
	private String actStartTime = "";
	private String actEndDate = "";
	private String actEndTime = "";
	private Bitmap actPoster;

	private int time_dialog_type = -1;
	private int time_dialog_index = 0;
	String[] mStrings;
	private String actPosterUrl = "";

	//	private Integer[] mFriedsImageIds = {
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default,
	//			R.drawable.portrait_default
	//	}; //should be 37*37dip
	//	private String[] mFriedsStrings = { "pjol","sss", "sdad", "sda", "sdad"};

	private ContentValues mValues;
	private ContentValues invitervalues;
	private int[] Inviterfriends;

	private ProgressDialog pd;
	private QueryHandler mQueryHandler;
	private static final int FRIENDS_QUERY_TOKEN = 101;
	private int[] friendsId = new int[5];
	private int[] friendsPortrait = new int[5];
	private String[] friendsName =  new String[5];
	private int tryloadtimes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()       
		//		.detectDiskReads()       
		//		.detectDiskWrites()       
		//		.detectNetwork()   // or .detectAll() for all detectable problems       
		//		.penaltyLog()       
		//		.build());       
		//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()       
		//		.detectLeakedSqlLiteObjects()    
		//		.penaltyLog()       
		//		.penaltyDeath()       
		//		.build());
		setContentView(R.layout.act_edit_layout);
		mPictureGet = new PictureGet(this);
		mValues = new ContentValues();
		invitervalues = new ContentValues();
		mQueryHandler = new QueryHandler(this);
		init();
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mQueryHandler.removeCallbacksAndMessages(FRIENDS_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_FRIENDSHIP);
		mCircleHandle.removeMessages(CircleHandle.MSG_INSERT_DATA);
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		if (actPoster != null) {
			actPoster.recycle();
			actPoster = null;
		}
	}

	private void init() {

		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.title_editor);
		mTitle.findViewById(R.id.act_extend).setVisibility(View.GONE);
		mTitle.findViewById(R.id.act_back).setVisibility(View.GONE);

		initEditorView();
		initShowView();
		reflashViews();
	}

	private void initShowView() {
		classifySpinner = (Spinner) findViewById(R.id.classify_editor);
		posterPreview = (ImageView) findViewById(R.id.poster_preview);
		actStartDateView = (TextView) findViewById(R.id.start_date);
		actStartTimeView = (TextView) findViewById(R.id.start_time);
		actEndDateView = (TextView) findViewById(R.id.end_date);
		actEndTimeView = (TextView) findViewById(R.id.end_time);

		mStrings = getResources().getStringArray(R.array.act_classify);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, mStrings);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		classifySpinner.setAdapter(spinnerAdapter);
		classifySpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);


		friendsView = (LinearLayout) findViewById(R.id.friends_list);
		//		for (int i = 0; i < 5; i++) {
		//			friend = (LinearLayout) friendsView.getChildAt(i);
		//			friendIcon = (ImageView) friend.findViewById(R.id.ItemImage);
		//			friendName = (TextView) friend.findViewById(R.id.ItemText);
		//
		//			friendIcon.setBackgroundResource(mFriedsImageIds[i]);
		//			friendName.setText(mFriedsStrings[i]);
		//		}
	}

	public void refreshFriendsView(){
		Bitmap mPortraitBitmap;
		for (int i = 0; i < 5; i++) {
			friend = (LinearLayout) friendsView.getChildAt(i);
			friendIcon = (ImageView) friend.findViewById(R.id.ItemImage);
			friendName = (TextView) friend.findViewById(R.id.ItemText);
			mPortraitBitmap = BitmapFactory.decodeResource(getResources(), friendsPortrait[i]);
			Log.i("test", "mPortraitBitmap = " + mPortraitBitmap);
			mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
			if (mPortraitBitmap != null) {
				friendIcon.setImageBitmap(mPortraitBitmap);
			} else {
				friendIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.portrait_default));
			}
			//			friendIcon.setBackgroundResource(friendsPortrait[i]);
			friendName.setText(friendsName[i]);
		}
	}

	public void reflashViews(){
		actStartDateView.setText(actStartDate);
		actStartTimeView.setText(actStartTime);
		actEndDateView.setText(actEndDate);
		actEndTimeView.setText(actEndTime);
		posterPreview.setImageBitmap(actPoster);
		locationEditor.setText(actLocation);
		mValues.put(CircleContract.Activity.CLASSIFY_TITLE, actClassify);
		mValues.put(CircleContract.Activity.CLASSIFY_INTRODUCE, actTitle);
		mValues.put(CircleContract.Activity.ACT_INTRODUCE, actIntroduce);
		mValues.put(CircleContract.Activity.START_TIME, actStartDate + " " + actStartTime);
		mValues.put(CircleContract.Activity.END_TIME, actEndDate + " " + actEndTime);
		mValues.put(CircleContract.Activity.LOCATION, actLocation);
		mValues.put(CircleContract.Activity.ACT_INVITER_PERSONAL, islimited ? 0 : 1);
		mValues.put(CircleContract.Activity.STATE, getString(R.string.act_publish_state));
	}

	public void cleanViews(){
		actStartDateView.setText("");
		actStartTimeView.setText("");
		actEndDateView.setText("");
		actEndTimeView.setText("");
		locationEditor.setText("");
		titleEditor.setText("");
		introduceEditor.setText("");
		posterPreview.setImageBitmap(null);
	}

	private void initEditorView() {
		findViewById(R.id.bar_timestart).setOnClickListener(this);
		findViewById(R.id.bar_timeend).setOnClickListener(this);
		findViewById(R.id.bar_friends_editor).setOnClickListener(this);

		findViewById(R.id.btn_timestart_editor).setOnClickListener(this);
		findViewById(R.id.btn_timeend_editor).setOnClickListener(this);
		//findViewById(R.id.btn_location_editor).setOnClickListener(this);
		findViewById(R.id.btn_location_editor).setVisibility(View.GONE);
		findViewById(R.id.btn_poster_editor).setOnClickListener(this);
		findViewById(R.id.btn_friends_editor).setOnClickListener(this);
		findViewById(R.id.btn_publish).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.limit_View).setOnClickListener(this);

		friendslimit = (ImageView) findViewById(R.id.limit_editor);
		friendslimit.setOnClickListener(this);
		if (islimited) {
			friendslimit.setBackgroundResource(R.drawable.checkbox_unselected);
			islimited = false;
		} else {
			friendslimit.setBackgroundResource(R.drawable.checkbox_selected);
			islimited = true;
		}

		titleEditor = (EditText) findViewById(R.id.title_editor);
		titleEditor.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actTitle = s.toString();
			}
		});

		introduceEditor = (EditText) findViewById(R.id.introduce_editor);
		introduceEditor.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actIntroduce = s.toString();
			}
		});

		locationEditor = (EditText) findViewById(R.id.location_editor);
		introduceEditor.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actLocation = s.toString();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationEditor.setFocusableInTouchMode(true);
		titleEditor.setFocusableInTouchMode(true);
		introduceEditor.setFocusableInTouchMode(true);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		forbiddenSomeFocuse();
	}

	private void forbiddenSomeFocuse(){
		locationEditor.setFocusableInTouchMode(false);
		titleEditor.setFocusableInTouchMode(false);
		introduceEditor.setFocusableInTouchMode(false);
		classifySpinner.setFocusableInTouchMode(false);
		locationEditor.setFocusable(false);
		titleEditor.setFocusable(false);
		introduceEditor.setFocusable(false);
		classifySpinner.setFocusable(false);

	}

	OnItemSelectedListener spinnerOnItemSelectedListener = new OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> adapter, View view, int position,
				long id) {
			actClassify = mStrings[position];

		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	DialogInterface.OnClickListener  buttonOnClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			Log.i("test", "which = "+which);
			if (which >= 0) {
				time_dialog_index = which;
			}

			Log.i("test", "time_dialog_index = "+time_dialog_index);
			switch(which) { 
			case DialogInterface.BUTTON_POSITIVE:
				if(time_dialog_index == 0){
					showDialog(DATE_DIALOG);
				} else if (time_dialog_index == 1){
					showDialog(TIME_DIALOG);
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				if (dialog != null) {
					dialog.dismiss();
				}
				break;
			}
		}
	};

	DialogInterface.OnClickListener  getPicOnClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) { 
			dialog.dismiss();  
			switch (which) {  
			case 0: {  
				String status = Environment.getExternalStorageState();  
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					mPictureGet.capturefile = new File(PictureGet.PHOTO_DIR, mPictureGet.getPhotoFileName());  
					try {  
						mPictureGet.capturefile.createNewFile();  
						i.putExtra(MediaStore.EXTRA_OUTPUT,  
								Uri.fromFile(mPictureGet.capturefile));
					} catch (IOException e) {  
						// TODO Auto-generated catch block  
						e.printStackTrace();  
					}  

					startActivityForResult(i, PictureGet.PHOTO_WITH_CAMERA);
				} else {  
					Toast.makeText(EditActActivity.this, getString(R.string.err_no_sdcard), Toast.LENGTH_LONG);  
				}  
				break;  
			}  
			case 1:
				Intent intent = new Intent();  
				intent.setType("image/*");  
				intent.setAction(Intent.ACTION_GET_CONTENT); 
				startActivityForResult(intent, PictureGet.PHOTO_WITH_DATA);  
				break;  
			}  
		}
	};

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog dialog = null;  
		switch(id) {  
		case SETECT_TIME_DIALOG:
			time_dialog_index = 0;
			AlertDialog.Builder builder = new AlertDialog.Builder(this); 

			if (time_dialog_type == 0) {
				builder.setTitle(R.string.start_time_select);  
			} else if (time_dialog_type == 1){
				builder.setTitle(R.string.end_time_select);  
			}

			builder.setSingleChoiceItems(new String[] { getString(R.string.data_select), getString(R.string.time_select), }
			, 0, buttonOnClick);  

			builder.setPositiveButton(getString(R.string.act_confirm_editor), buttonOnClick);  

			builder.setNegativeButton(getString(R.string.act_cancel_editor), buttonOnClick);  

			dialog = builder.create();  
			break;
		case DATE_DIALOG:
			Calendar c = Calendar.getInstance();
			dialog = new DatePickerDialog(
					this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {

							Calendar c = Calendar.getInstance();
							c.set(Calendar.YEAR, year);
							c.set(Calendar.MONDAY, month);
							c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
							Date mDate = c.getTime();
							SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
							String formatDate = format.format(mDate);
							if (time_dialog_type == 0) {
								actStartDate = formatDate;
							} else if (time_dialog_type == 1){
								actEndDate= formatDate;
							}
							reflashViews();
						}
					},
					c.get(Calendar.YEAR), 
					c.get(Calendar.MONTH), 
					c.get(Calendar.DAY_OF_MONTH)
					);
			break;
		case TIME_DIALOG:
			c=Calendar.getInstance();
			dialog=new TimePickerDialog(
					this, 
					new TimePickerDialog.OnTimeSetListener(){
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							Calendar c = Calendar.getInstance();
							c.set(Calendar.HOUR_OF_DAY, hourOfDay);
							c.set(Calendar.MINUTE, minute);
							Date mDate = c.getTime();
							SimpleDateFormat  format = new SimpleDateFormat("hh-mm");
							String formatTime = format.format(mDate);
							if (time_dialog_type == 0) {
								actStartTime = formatTime;
							} else if (time_dialog_type == 1){
								actEndTime= formatTime;
							}
							reflashViews();
						}
					},
					c.get(Calendar.HOUR_OF_DAY),
					c.get(Calendar.MINUTE),
					false
					);
			break;
		case PictureGet.GET_POSTER_DIALOG:  
			dialog = mPictureGet.creatDialog(getPicOnClick);
			break;
		case LOCATION_DIALOG:   
			//			if (places != null && places.length > 0) {
			//				//final ListAdapter adapter = new ArrayAdapter<String>(this,  
			//				//		android.R.layout.simple_list_item_1, places); 
			//
			//				final AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);  
			//				mbuilder.setTitle(getString(R.string.get_location));  
			//				mbuilder.setSingleChoiceItems(places, 0, getLocationOnClick);
			//				dialog = mbuilder.create();
			//			}
			break;
		default:  
			dialog = null;  
		}

		return dialog; 
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.bar_timestart:
		case R.id.btn_timestart_editor:
			time_dialog_type = 0;
			showDialog(SETECT_TIME_DIALOG);
			break;
		case R.id.bar_timeend:
		case R.id.btn_timeend_editor:
			time_dialog_type = 1;
			showDialog(SETECT_TIME_DIALOG);
			break;
		case R.id.btn_poster_editor:
			//select pic intent here
			showDialog(PictureGet.GET_POSTER_DIALOG);
			break;
		case R.id.bar_friends_editor:
		case R.id.btn_friends_editor:
			intent = new Intent(this, MyFriends.class);
			intent.putExtra(UtilString.USERID, userId);
			startActivityForResult(intent, REQUESTFRIEND);
			break;
		case R.id.btn_publish:

			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_INSERT_DATA);

			break;
		case R.id.btn_cancel:
			cleanViews();
			finish();
			break;
		case R.id.limit_View:
		case R.id.limit_editor:
			if (islimited) {
				friendslimit.setBackgroundResource(R.drawable.checkbox_unselected);
				islimited = false;
			} else {
				friendslimit.setBackgroundResource(R.drawable.checkbox_selected);
				islimited = true;
			}
			break;
		default:
			break;
		}
	}
	
	CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFRESH_FRIENDSHIP:
				Log.i("test", "MSG_REFRESH_FRIENDSHIP");
//				mCircleHandle.refreshFriendShip();
//				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);

				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_FRIENDSHIP);
				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
					@Override
					public void onRefreshFinish() {
						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
					}
				});
				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
				String[] selectionArgs = {String.valueOf(userId)};
				String sortOrder = CircleContract.Friendship.TIME_MAKE_FRIEND;
				String selection = CircleContract.Friendship.PEOPLE_ID + "= ?";
				mQueryHandler.startQuery(FRIENDS_QUERY_TOKEN, null, CircleContract.Friendship.CONTENT_URI, UtilString.friendshipProjection, selection, selectionArgs, sortOrder);
				break;
			case CircleHandle.MSG_INSERT_DATA:
				showProgress();
				actPosterUrl = PictureGet.saveBitmap(actPoster);
				mValues.put(CircleContract.Activity.POSTER, actPosterUrl);
				Date myDate = new Date(System.currentTimeMillis());  
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String date = format.format(myDate);
				mValues.put(CircleContract.Activity.PUBLISH_TIME, date);
				long tagId = System.currentTimeMillis() + userId;
				mValues.put(CircleContract.Activity.TAG_ID, tagId);
				mCircleHandle.insertNewAct(mValues);
				mCircleHandle.uploadPicture(actPoster);
				
				if (Inviterfriends != null){
					for (int i= 0; i < Inviterfriends.length; i ++) {
						invitervalues.put(CircleContract.FriendInviters.ACT_ID, tagId);
						invitervalues.put(CircleContract.FriendInviters.FRIEND_ID, Inviterfriends[i]);
						mCircleHandle.insertInviters(invitervalues);
					}
				}

				dismissProgress();
				finish();
				break;
			default:
				break;
			}
		}
	};

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<EditActActivity> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<EditActActivity>((EditActActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.i("test", "token = "+token);
			if (mActivity == null) {
				return;
			}
			mActivity.get().loadFriendDataFromDB(cursor);

		}
	}

	public void loadFriendDataFromDB(Cursor cursor) {
		Log.i("test", "loadFriendDataFromDB");
		if (cursor != null && cursor.getCount() != 0) {
			Log.i("test", "cursor.getCount() = "+cursor.getCount());
			//			friendsId = new int[5];
			//			friendsPortrait = new int[5];
			//			friendsName = new String[5];
			for (int i = 0; i < 5; i++) {
				if (cursor.moveToPosition(i)) {
					friendsId[i] = cursor.getInt(UtilString.friendshipFriendIdIndex);
					Log.i("test", "friendsId[i] = "+friendsId[i]);
					if (friendsId[i] != 0) {
						String[] selectionArgs = {String.valueOf(friendsId[i])};
						String selection = CircleContract.People.PEOPLE_ID + "= ?";
						Cursor mTempCursor = getContentResolver().query(CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, null);
						if (mTempCursor != null && mTempCursor.moveToFirst()) {
							friendsPortrait[i] = cursor.getInt(UtilString.peopleProtraitIndex);
							Log.i("test", "friendsPortrait[i] = "+friendsPortrait[i]);
							friendsName[i] = cursor.getString(UtilString.peopleNameIndex);
						}
						mTempCursor.close();
					}
				}
			}
			refreshFriendsView();
		} else if (tryloadtimes == 0){
			Log.i("test", "loadActPeopleFromDB tryloadtimes = " + tryloadtimes);
			tryloadtimes ++;
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_FRIENDSHIP);
		} else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
		cursor.close();
	}

	private void showProgress(){
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {  
			switch (requestCode) {  
			case PictureGet.PHOTO_WITH_CAMERA:
				actPoster = mPictureGet.getPic(mPictureGet.capturefile.getAbsolutePath());
				break;  

			case PictureGet.PHOTO_WITH_DATA:
				Uri uri = data.getData();  
				String scheme = uri.getScheme();  
				if (scheme.equalsIgnoreCase("file")) {
					actPoster = mPictureGet.getPic(uri.getPath());
				} else if (scheme.equalsIgnoreCase("content")) {
					actPoster = mPictureGet.getPicFromDatabase(uri);
				}  
				break;  
			case REQUESTFRIEND:
				Inviterfriends = data.getIntArrayExtra(UtilString.INVITER_FRIENDS);
				return;  
			}
		}
		if (actPoster != null){
			actPoster = mPictureGet.resizeBitmap(actPoster, 202, 121);
			Log.i("test", "actPoster = " + actPoster);
		}
		reflashViews();
		super.onActivityResult(requestCode, resultCode, data);  
	}  
}
