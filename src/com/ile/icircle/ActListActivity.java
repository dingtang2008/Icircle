package com.ile.icircle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ile.icircle.CircleHandle.RefreshFinishListener;
import com.ile.icircle.MyFriends.QueryHandler;
import com.ile.icircle.PullToRefreshListView.OnRefreshListener;
import com.ile.icircle.UserInfoActivity.btclick;

public class ActListActivity extends Activity implements OnSeekBarChangeListener,
ImageButton.OnClickListener {
	ListAdapter adapter;
	private SeekBar seekbar;
	private ListView listview;
	private LinearLayout list_footer;
	private ProgressDialog progressDialog;
	private TextView tv_msg;
	private LinearLayout loading;
	private ImageButton img;
	private int mYear;
	private int mMonth;
	private int mDay;
	private ExecutorService executorService;
	private static int PAGE_SIZE = 5;// 每页显示条数
	private int TOTAL_PAGE = 0;// 当前已经记在的页数
	private static int THREADPOOL_SIZE = 5;// 线程池的大小
	private Handler handler;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;
	private TextView[] tv;
	private Button act_back;

	private String classifyname;
	private int peopleId = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_bar);

		Intent intent = getIntent();
		newIntent(getIntent());
		mPictureGet = new PictureGet(this);
		mQueryHandler = new QueryHandler(this);
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}


	private void newIntent(Intent mIntent) {
		classifyname = mIntent.getStringExtra(UtilString.CLASSIFYNAME);
		Log.i("test", "classifyname = " + classifyname);

		RelativeLayout mTitle = (RelativeLayout) findViewById(R.id.title);
		mTitle.findViewById(R.id.act_extend).setVisibility(View.GONE);
		//mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		TextView mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(classifyname);

		list_footer = (LinearLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.list_footer, null);
		tv_msg = (TextView) list_footer.findViewById(R.id.tv_msg);
		loading = (LinearLayout) list_footer.findViewById(R.id.loading);
		textView1 = (TextView) findViewById(R.id.timeText1);
		textView2 = (TextView) findViewById(R.id.timeText2);
		textView3 = (TextView) findViewById(R.id.timeText3);
		textView4 = (TextView) findViewById(R.id.timeText4);
		textView5 = (TextView) findViewById(R.id.timeText5);
		act_back = (Button) findViewById(R.id.act_back);
		act_back.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
		tv = new TextView[] { textView1, textView2, textView3, textView4,
				textView5 };
		seekbar = (SeekBar) findViewById(R.id.seekBar);
		listview = (ListView) findViewById(R.id.listView1);
		img = (ImageButton) findViewById(R.id.btn_cal);
		listview.addFooterView(list_footer);
		listview.setOnItemClickListener(mOnItemClickListener);
		seekbar.setOnSeekBarChangeListener(this);
		img.setOnClickListener(this);
		((PullToRefreshListView) listview)
		.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				// Do work to refresh the list here.
				mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_ACTLIST);
//				new GetDataTask().execute();
				
			}
		});

		adapter = new AllActivityAdapter(this);
		listview.setAdapter(adapter);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		tv_msg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// executorService.submit(new GetHomeTimeLineThread());
				tv_msg.setVisibility(View.GONE);// 隐藏更多提示的TextView
				loading.setVisibility(View.VISIBLE);// 显示最下方的进度条
			}

		});
//		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		progressDialog = new ProgressDialog(ActListActivity.this);// 生成一个进度条
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(R.string.pull_refresh_wait);
		progressDialog.setMessage(getString(R.string.pull_refreshing));
//		handler = new GetHomeTimeLineHandler();
//		executorService.submit(new GetHomeTimeLineThread());// 耗时操作,开启一个新线程获取数据
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long arg3) {
			AllActivityAdapter.ViewHolder holder = (AllActivityAdapter.ViewHolder) view.getTag();
			Intent intent = new Intent(ActListActivity.this, DetailActActivity.class);
			intent.putExtra(UtilString.ACTID, holder.actId);
			Log.i("test", "holder.actId = " + holder.actId);
			startActivity(intent);
		}

	};

	private class GetDataTask extends
	AsyncTask<Void, Void, List<Map<String, Object>>> {

		@Override
		protected List<Map<String, Object>> doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				;
			}

			return addData();
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {

			// Call onRefreshComplete when the list has been refreshed.
			((AllActivityAdapter) adapter).addData(result);
			((PullToRefreshListView) listview).onRefreshComplete();

			super.onPostExecute(result);
		}

		private List<Map<String, Object>> addData() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "不在进行");
			map.put("status_color", UtilString.act_ending);
			map.put("title", "达州一中加拿大留学专题讲座");
			map.put("info", "29人感兴趣   17人参加");
			map.put("img", R.drawable.test_list_icon);
			map.put("ic", R.drawable.ic_to_attend);
			list.add(map);
			return list;
		}
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar mSeekBar) {
		// TODO Auto-generated method stub
		int seekProgress = mSeekBar.getProgress();
		int currentrogress = 0;
		if (seekProgress < 13) {
			currentrogress = 0;
		} else if (seekProgress >= 13 && seekProgress < 38) {
			currentrogress = 25;
		} else if (seekProgress >= 38 && seekProgress < 63) {
			currentrogress = 50;
		} else if (seekProgress >= 63 && seekProgress < 88) {
			currentrogress = 75;
		} else if (seekProgress >= 88) {
			currentrogress = 100;
		}

		for (TextView item : tv) {
			item.setTextColor(Color.parseColor(UtilString.grey_color));
		}
		tv[currentrogress / 25].setTextColor(Color.parseColor(UtilString.green_color));
		mSeekBar.setProgress(currentrogress);
	}

	public void onClick(View arg0) {
		showDialog(0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private void updateDisplay() {
		setTitle(new StringBuilder()
		// Month is 0 based so add 1
		.append(mMonth + 1).append("-").append(mDay).append("-")
		.append(mYear).append(" "));
	}

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

//	class GetHomeTimeLineHandler extends Handler {
//		@Override
//		public void handleMessage(Message msg) {
//			if (list != null && list.size() > 0) {
//
//				Log.i("test", "GetHomeTimeLineHandler");
//				((AllActivityAdapter) adapter).addData(list);
//			}
//			listview.setAdapter(adapter);
//			listview.setSelection((TOTAL_PAGE - 1) * PAGE_SIZE + 1);// 设置最新获取一页数据成功后显示数据的起始数据
//			progressDialog.dismiss();// 关闭进度条
//			loading.setVisibility(View.GONE);// 隐藏下方的进度条
//			tv_msg.setVisibility(View.VISIBLE);// 显示出更多提示TextView
//		}
//	}

//	class GetHomeTimeLineThread extends Thread {
//		@Override
//		public void run() {
//			refreshList();
//			Log.i("test", "GetHomeTimeLineThread");
//			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
//			handler.sendMessage(msg);
//		}
//
//		private List<Map<String, Object>> refreshList() {
//			Log.i("test", "refreshList");
//			// TODO Auto-generated method stub
//			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("status", "不在进行");
//			map.put("status_color", UtilString.act_ending);
//			map.put("title", "达州一中加拿大留学专题讲座");
//			map.put("info", "29人感兴趣   17人参加");
//			map.put("img", R.drawable.test_list_icon);
//			map.put("ic", R.drawable.ic_to_attend);
//			list.add(map);
//			return list;
//		}
//	}


	public int tryloadtimes = 0;
	private PictureGet mPictureGet;
	private QueryHandler mQueryHandler;
	private static final int ACT_LIST_QUERY_TOKEN = 101;
	private final static int DIALOG_REFRESH_DATA = 0;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mQueryHandler.removeCallbacksAndMessages(ACT_LIST_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_ACTLIST);
	}

	CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFRESH_ACTLIST:
				Log.i("test", this.toString() + "MSG_REFRESH_ACTLIST");
				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_ACTLIST);
				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
					@Override
					public void onRefreshFinish() {
						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
					}
				});
				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				adapter = new AllActivityAdapter(ActListActivity.this);
				list.clear();
				String selection = UtilString.concatenateWhere(CircleContract.Activity.ACT_INVITER_PERSONAL + "= ?", CircleContract.Activity.CLASSIFY + "= ?");
				String[] selectionArgs = {String.valueOf(0), classifyname};
				String sortOrder = CircleContract.Activity.PUBLISH_TIME + " DESC";
				mQueryHandler.startQuery(ACT_LIST_QUERY_TOKEN, null, CircleContract.Activity.CONTENT_URI, UtilString.actListProjection, selection, selectionArgs, sortOrder);
				break;
			default:
				break;
			}
		}
	};

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<ActListActivity> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<ActListActivity>((ActListActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (mActivity == null) {
				return;
			}
			if(token == ACT_LIST_QUERY_TOKEN) {
				mActivity.get().loadActListFromDB(cursor);
			}

		}
	}public void loadActListFromDB(Cursor cursor) { 
		if (cursor != null && cursor.getCount() != 0) { 
			cursor.moveToPosition(-1); 
			while(cursor.moveToNext()){ 
				Map<String, Object> map = new HashMap<String, Object>(); 
				map.put("actid", cursor.getInt(UtilString.actListActIdIndex)); 
				map.put("status", cursor.getString(UtilString.actListActStateIndex)); 
				map.put("status_color", UtilString.act_ending); 
				map.put("title", cursor.getString(UtilString.actListActTitleIndex)); 
				String info = getString(R.string.detail_interest_people) + "(" + cursor.getInt(UtilString.actListInterestIndex) + ")" + 
						getString(R.string.detail_attend_people) + "(" + cursor.getInt(UtilString.actListAttendIndex) + ")"; 

				map.put("info", info);
				map.put("img", cursor.getString(UtilString.actListActPosterIndex)); 
				int actId = cursor.getInt(UtilString.actListActIdIndex); 

				//                    String tag = checkActPepleState(actId, peopleId); 
				//                    if (tag.equals(UtilString.INTEREST)) { 
				//                            map.put("ic", R.drawable.ic_to_selected); 
				//                    } else  if(tag.equals(UtilString.ATTEND)) { 
				//                            map.put("ic", R.drawable.ic_to_selected); 
				//                    } 
				if (checkActPepleState(actId, peopleId)) { 
					map.put("ic", R.drawable.ic_to_selected); 
				} else {
					map.put("ic", 0); 
				}
				list.add(map); 
			}
			if (list != null && list.size() > 0) {
				((AllActivityAdapter) adapter).addData(list);
			}
			listview.setAdapter(adapter);
			listview.setSelection((TOTAL_PAGE - 1) * PAGE_SIZE + 1);// 设置最新获取一页数据成功后显示数据的起始数据
			if (progressDialog != null) {
				progressDialog.dismiss();// 关闭进度条
			}
			((PullToRefreshListView) listview).onRefreshComplete();
			loading.setVisibility(View.GONE);// 隐藏下方的进度条
			tv_msg.setVisibility(View.VISIBLE);// 显示出更多提示TextView
			//                        mAdapter.changeCursor(cursor); 
			//                        dismissProgress(); 
		} 
		cursor.close(); 
	} 

	public boolean checkActPepleState(int actId, int peopleId){ 
		//    String tag = ""; 
		boolean tag = false; 
		String selection = CircleContract.ActPeople.PEOPLE_ID + "= ?"; 
		String interestselection = UtilString.concatenateWhere(selection, CircleContract.ActPeople.INTREST_ACT_TAGID + "= ?"); 
		String attendselection = UtilString.concatenateWhere(selection, CircleContract.ActPeople.ATTEND_ACT_TAGID + "= ?"); 
		String[] selectionArgs = {String.valueOf(peopleId), String.valueOf(actId)}; 
		String sortOrder = null; 

		Cursor cursor = getContentResolver().query(CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, interestselection, selectionArgs, sortOrder); 
		if (cursor != null && cursor.getCount() > 0) { 
			//            tag = UtilString.INTEREST; 
			tag = true; 
		} 
		cursor.close(); 

		cursor =  getContentResolver().query(CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, attendselection, selectionArgs, sortOrder); 
		if (cursor != null && cursor.getCount() > 0) { 
			tag = true; 
			//            tag = UtilString.ATTEND; 
		} 
		cursor.close(); 

		return tag; 
	} 


}