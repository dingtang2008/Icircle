package com.ile.icircle;

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
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.SeekBar.OnSeekBarChangeListener;

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
	private static int PAGE_SIZE = 5;// 每页显示的微博条数
	private int TOTAL_PAGE = 0;// 当前已经记在的微博页数
	private static int THREADPOOL_SIZE = 5;// 线程池的大小
	private Handler handler;
	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;
	private TextView[] tv;
	private Button act_back;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_bar);

		Intent intent = getIntent();

		String id = intent.getStringExtra(UtilString.CLASSIFYID);
		String name = intent.getStringExtra(UtilString.CLASSIFYNAME);
		Log.i("test", "name = " + name);
		RelativeLayout mTitle = (RelativeLayout) findViewById(R.id.title);
		mTitle.findViewById(R.id.act_extend).setVisibility(View.GONE);
		//mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		TextView mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(name);
		
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
						new GetDataTask().execute();
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
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		progressDialog = new ProgressDialog(ActListActivity.this);// 生成一个进度条
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("请稍等");
		progressDialog.setMessage("正在读取数据中!");
		handler = new GetHomeTimeLineHandler();
		executorService.submit(new GetHomeTimeLineThread());// 耗时操作,开启一个新线程获取数据
		progressDialog.show();
	}

	private ContentValues mCulValue;
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mCulValue = new ContentValues();
			mCulValue.put("classifytitle", "test");
			mCulValue.put("classify", "test");
			mCulValue.put("location", "test");
			mCulValue.put("time", "test");
			mCulValue.put("state", "test");
			Intent intent = new Intent(ActListActivity.this, DetailActActivity.class);
			intent.putExtra(UtilString.ACTTITLE, getString(R.string.btn_detail));
			intent.putExtra(UtilString.ACTID, arg2);
			intent.putExtra(UtilString.ACVALUES, mCulValue);
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
			map.put("status_color", "#85e705");
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
			item.setTextColor(Color.parseColor("#829581"));
		}
		tv[currentrogress / 25].setTextColor(Color.parseColor("#367b3c"));
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

	private List<Map<String, Object>> list;

	class GetHomeTimeLineHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (list != null)

				((AllActivityAdapter) adapter).addData(list);
			listview.setAdapter(adapter);
			listview.setSelection((TOTAL_PAGE - 1) * PAGE_SIZE + 1);// 设置最新获取一页数据成功后显示数据的起始数据
			progressDialog.dismiss();// 关闭进度条
			loading.setVisibility(View.GONE);// 隐藏下方的进度条
			tv_msg.setVisibility(View.VISIBLE);// 显示出更多提示TextView
		}
	}

	class GetHomeTimeLineThread extends Thread {
		@Override
		public void run() {
			refreshList();
			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
			handler.sendMessage(msg);
		}

		private List<Map<String, Object>> refreshList() {
			// TODO Auto-generated method stub
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "不在进行");
			map.put("status_color", "#85e705");
			map.put("title", "达州一中加拿大留学专题讲座");
			map.put("info", "29人感兴趣   17人参加");
			map.put("img", R.drawable.test_list_icon);
			map.put("ic", R.drawable.ic_to_attend);
			list.add(map);
			return list;
		}
	}
}