package com.ile.icircle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
	
	private int userId;
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

	private Integer[] mFriedsImageIds = {
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default
	}; //should be 37*37dip
	private String[] mFriedsStrings = { "pjol","sss", "sdad", "sda", "sdad"};

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
		
		init();
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
		for (int i = 0; i < 5; i++) {
			friend = (LinearLayout) friendsView.getChildAt(i);
			friendIcon = (ImageView) friend.findViewById(R.id.ItemImage);
			friendName = (TextView) friend.findViewById(R.id.ItemText);

			friendIcon.setBackgroundResource(mFriedsImageIds[i]);
			friendName.setText(mFriedsStrings[i]);
		}
	}

	public void reflashViews(){
		actStartDateView.setText(actStartDate);
		actStartTimeView.setText(actStartTime);
		actEndDateView.setText(actEndDate);
		actEndTimeView.setText(actEndTime);
		posterPreview.setImageBitmap(actPoster);
		locationEditor.setText(actLocation);
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
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actTitle = s.toString();
			}
		});

		introduceEditor = (EditText) findViewById(R.id.introduce_editor);
		introduceEditor.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				actIntroduce = s.toString();
			}
		});

		locationEditor = (EditText) findViewById(R.id.location_editor);
		introduceEditor.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
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
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position,
				long id) {
			actClassify = mStrings[position];

		}

		@Override
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
					Toast.makeText(EditActActivity.this, getString(R.string.err_no_sdcard), Toast.LENGTH_LONG);  
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
			Log.i("test", "您选择");
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
							Log.i("test", "您选择了：" + formatDate);
							reflashViews();
						}
					},
					c.get(Calendar.YEAR), // 传入年份
					c.get(Calendar.MONTH), // 传入月份
					c.get(Calendar.DAY_OF_MONTH) // 传入天数
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
							Log.i("test", "您选择了：" + formatTime);
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

	@Override
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
			//Do publish on services
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

	/* 
	 * 选择图片的回传处理 
	 */  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {  
			switch (requestCode) {  
			case PictureGet.PHOTO_WITH_CAMERA://获取拍摄的文件  
				actPoster = mPictureGet.getPic(mPictureGet.capturefile.getAbsolutePath());
				break;  

			case PictureGet.PHOTO_WITH_DATA://获取从图库选择的文件  
				Uri uri = data.getData();  
				String scheme = uri.getScheme();  
				if (scheme.equalsIgnoreCase("file")) {
					actPoster = mPictureGet.getPic(uri.getPath());
				} else if (scheme.equalsIgnoreCase("content")) {
					actPoster = mPictureGet.getPicFromDatabase(uri);
				}  
				break;  
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
