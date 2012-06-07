package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyActivity extends Activity implements OnClickListener {

	private static final String TAG = "ClassifyActivity";

	private GridView mClassifyAct;
	private RelativeLayout mTitle;
	EditText search;

	ArrayList<HashMap<String, Object>> cursorListItems = new ArrayList<HashMap<String, Object>>();

	private PictureGet mPictureGet;
	ProgressDialog pd;
	public int tryloadtimes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classify_layout);
		init();
		mPictureGet = new PictureGet(this);
		pd = new ProgressDialog(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_INSERT_DATA);
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	private void init() {
		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.act_classify);
		mTitle.findViewById(R.id.act_extend).setOnClickListener(this);
		ImageView extend_add = (ImageView) mTitle.findViewById(R.id.act_extend);
		extend_add.setBackgroundResource(R.drawable.add_act_selector);
		extend_add.setOnClickListener(this);


		search = (EditText) findViewById(R.id.searchBoxEditText);
		search.setHint(R.string.default_search_hint);
		search.setOnClickListener(this);

		mClassifyAct = (GridView) findViewById(R.id.act_by_classify);
		mClassifyAct.setOnItemClickListener(mOnItemClickListener);
	}

	public void refreshviews(ArrayList<HashMap<String, Object>> items) {
		ClassifyActAdapter mClassifyActAdapter = new ClassifyActAdapter(this, items);
		mClassifyAct.setAdapter(mClassifyActAdapter);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ClassifyActivity.this, ActListActivity.class);
			Log.i(TAG, "arg2 = " + arg2);
			Log.i(TAG, "arg3 = " + arg3);
			Log.i(TAG, "getString(mStringIds[arg2]) = " + getString(UtilString.mStringIds[arg2]));
			intent.putExtra(UtilString.CLASSIFYID, arg2);
			intent.putExtra(UtilString.CLASSIFYNAME, getString(UtilString.mStringIds[arg2]));
			startActivity(intent);
		}

	};

	public CircleHandle mCircleHandle = new CircleHandle(this){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.LOADER_DATA:
				ClassifyActTask mClassifyActTask;
				if (i < UtilString.mStringIds.length) {
					mClassifyActTask = new ClassifyActTask(getString(UtilString.mStringIds[i]));
					mClassifyActTask.execute(getString(UtilString.mStringIds[i]));
				}
				break;
			default:
				break;
			}
		}
	};
	int i = 0;
	class ClassifyActTask extends AsyncTask<String, Integer, Cursor> {

		String type = "";
		public ClassifyActTask(String classifyType){
			type = classifyType;
		}

		@Override
		protected Cursor doInBackground(String... strings) {
			if (!type.equals(strings[0])){
				return null;
			}
			Log.i(TAG, "type = " + type);
			String selection = UtilString.concatenateWhere(CircleContract.Activity.ACT_INVITER_PERSONAL + "= ?", CircleContract.Activity.CLASSIFY + "= ?");
			String[] selectionArgs = {String.valueOf(0), type};
			String sortOrder = CircleContract.Activity.ATTEND_PEOPLE + " DESC";
			Cursor cursor = getContentResolver().query(CircleContract.Activity.CONTENT_URI, UtilString.classifyProjection, selection, selectionArgs, sortOrder);
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor cursor) {
			Log.i(TAG, "i = " + i);
			if (cursor != null) {
				loadClassifyFromDB(cursor, type);
			}
			Log.i(TAG, "type = " + type);
			if (type.equals(getResources().getString(UtilString.mStringIds[UtilString.mStringIds.length - 1]))) {
				refreshviews(cursorListItems);
			}
		}

	};

	public void loadClassifyFromDB(Cursor cursor, String type) {
		int count = cursor.getCount();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (cursor.moveToFirst() && count != 0) {
			String posterUrl = cursor.getString(UtilString.classifyActPosterIndex);
			map.put(UtilString.PANEL_CONTENT_IMAGE_KEY, posterUrl);
		} else {
			map.put(UtilString.PANEL_CONTENT_IMAGE_KEY, "");
		}
		map.put(UtilString.PANEL_CONTENT_TEXT_KEY, type);
		map.put(UtilString.PANEL_CONTENT_COUNT_KEY, "(" + count + ")");
		cursorListItems.add(map);
		cursor.close();

		i++;
		ClassifyActTask mClassifyActTask;
		if (i < UtilString.mStringIds.length) {
			mClassifyActTask = new ClassifyActTask(getString(UtilString.mStringIds[i]));
			mClassifyActTask.execute(getString(UtilString.mStringIds[i]));
		}
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.searchBoxEditText:
			break;
		case R.id.act_extend:
			intent = new Intent(this, EditActActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	static class ViewHolder {
		ImageView classifyPic;
		TextView classifyName;
		TextView classifyCount;
		int position;
	}

	class ClassifyActAdapter extends BaseAdapter {

		ArrayList<HashMap<String, Object>> mListItem;
		private LayoutInflater mInflater;

		ClassifyActAdapter(Context context, ArrayList<HashMap<String, Object>> lstItem){
			mListItem = lstItem;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.classify_content, null);

				holder = new ViewHolder();
				holder.classifyPic = (ImageView) convertView.findViewById(R.id.ItemImage);
				holder.classifyName = (TextView) convertView.findViewById(R.id.ItemText);
				holder.classifyCount = (TextView) convertView.findViewById(R.id.ItemCount);

				holder.position = position;

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			String posterUrl = (String) mListItem.get(position).get(UtilString.PANEL_CONTENT_IMAGE_KEY);
			String name = (String) mListItem.get(position).get(UtilString.PANEL_CONTENT_TEXT_KEY);
			String count = (String) mListItem.get(position).get(UtilString.PANEL_CONTENT_COUNT_KEY);

			holder.classifyName.setText(name);
			holder.classifyCount.setText(count);

			Bitmap mPoster = null;
			if(posterUrl.startsWith("http://")) {

			} else if(posterUrl.startsWith("/mnt/sdcard/")) {
				mPoster = mPictureGet.getPic(posterUrl);
			}

			if (mPoster != null) {
				mPoster = mPictureGet.resizeBitmap(mPoster, 202, 121);
				holder.classifyPic.setImageBitmap(mPoster);
			} else {
				holder.classifyPic.setImageResource(R.drawable.ic_default1);}

			return convertView;
		}

	}  
}
