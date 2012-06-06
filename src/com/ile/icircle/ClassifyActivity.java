package com.ile.icircle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.ile.icircle.CircleHandle.RefreshFinishListener;
import com.ile.icircle.LocationActivity.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyActivity extends Activity implements OnClickListener {

	private GridView mClassifyAct;
	private RelativeLayout mTitle;
	EditText search;
	
	ArrayList<HashMap<String, Object>> cursorListItems = new ArrayList<HashMap<String, Object>>();

	private Integer[] mStringIds = { R.string.str_act_all,
			R.string.str_act_music,R.string.str_act_welfare, 
			R.string.str_act_lecture,R.string.str_act_life,
			R.string.str_act_sport,R.string.str_act_travel,
			R.string.str_act_other};

	private Integer[] mImageIds = {
			R.drawable.test7,
			R.drawable.test2,
			R.drawable.test3,
			R.drawable.test4,
			R.drawable.test5,
			R.drawable.test6,
			R.drawable.test7,
			R.drawable.test8
	};
	
	private Integer[] mClassifyCountTest = { 54,3, 4,11,12,7,9,13 };

	private PictureGet mPictureGet;
	ProgressDialog pd;
	public int tryloadtimes = 0;
	
	private static final int ACT_CLASSIFY_QUERY_TOKEN = 101;
	private final static int DIALOG_REFRESH_DATA = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classify_layout);
		init();
		mPictureGet = new PictureGet(this);
		pd = new ProgressDialog(this);
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
//		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_CLASSIFY);
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
		search.setHint("校园十大歌手");
		search.setOnClickListener(this);
		
		mClassifyAct = (GridView) findViewById(R.id.act_by_classify);
		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < mStringIds.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(UtilString.PANEL_CONTENT_IMAGE_KEY, "");
			map.put(UtilString.PANEL_CONTENT_TEXT_KEY, getString(mStringIds[i]));
			map.put(UtilString.PANEL_CONTENT_COUNT_KEY, "(" + mClassifyCountTest[i]+ ")");
			items.add(map);
		}
		mClassifyAct.setOnItemClickListener(mOnItemClickListener);
		refreshviews(items);
	}
	
	public void refreshviews(ArrayList<HashMap<String, Object>> items) {
		ClassifyActAdapter mClassifyActAdapter = new ClassifyActAdapter(this, items);
		mClassifyAct.setAdapter(mClassifyActAdapter);
	}
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ClassifyActivity.this, ActListActivity.class);
			Log.i("test", "arg2 = " + arg2);
			Log.i("test", "getString(mStringIds[arg2]) = " + getString(mStringIds[arg2]));
			intent.putExtra(UtilString.CLASSIFYID, arg2);
			intent.putExtra(UtilString.CLASSIFYNAME, getString(mStringIds[arg2]));
			startActivity(intent);
		}
		
	};
	
	public CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
//			case CircleHandle.MSG_REFRESH_CLASSIFY:
//				Log.i("test", this.toString() + "MSG_REFRESH_CLASSIFY");
//				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_CLASSIFY);
//				mCircleHandle.setRefreshFinishListener(new RefreshFinishListener() {
//					@Override
//					public void onRefreshFinish() {
//						mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
//					}
//				});
//				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
//				dismissProgress();
//				showProgress();
				ClassifyActTask mClassifyActTask;
				for (int i = 0; i < mStringIds.length; i++) {
					mClassifyActTask = new ClassifyActTask(getString(mStringIds[i]));
					mClassifyActTask.execute(getString(mStringIds[i]));
				}
				refreshviews(cursorListItems);
//				mQueryHandler.startQuery(ACT_CLASSIFY_QUERY_TOKEN, null, CircleContract.Activity.CONTENT_URI, UtilString.classifyProjection, selection, selectionArgs, sortOrder);
				break;
			default:
				break;
			}
		}
	};
	
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
			String selection = UtilString.concatenateWhere(CircleContract.Activity.ACT_INVITER_PERSONAL + "= ?", CircleContract.Activity.CLASSIFY_TITLE + "= ?");
			String[] selectionArgs = {String.valueOf(0), type};
			String sortOrder = CircleContract.Activity.ATTEND_PEOPLE + " DESC";
			Cursor cursor = getContentResolver().query(CircleContract.Activity.CONTENT_URI, UtilString.classifyProjection, selection, selectionArgs, sortOrder);
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor cursor) {
			if (cursor != null) {
				loadClassifyFromDB(cursor, type);
			}
		}

	};

	public void loadClassifyFromDB(Cursor cursor, String type) {
		int count = cursor.getCount();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (cursor.moveToFirst() && count != 0) {
			String posterUrl = cursor.getString(UtilString.classifyActPosterIndex);
			map.put(UtilString.PANEL_CONTENT_IMAGE_KEY, posterUrl);
			map.put(UtilString.PANEL_CONTENT_TEXT_KEY, type);
			map.put(UtilString.PANEL_CONTENT_COUNT_KEY, "(" + count + ")");
			cursorListItems.add(map);
		}
		cursor.close();
	}

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		AlertDialog dialog = null;
//		switch (id) {
//		case DIALOG_REFRESH_DATA:
//			dialog = mCircleHandle.creatRefreshDialog(this);
//			break;
//		default:
//			break;
//		}
//		return dialog;
//	}

//	private void showProgress(){
//		pd = new ProgressDialog(this);
//		pd.setTitle(R.string.refresh_data);
//		pd.show();
//	}
//	private void dismissProgress(){
//		if (pd != null) {
//			pd.dismiss();
//			pd = null;
//		}
//	}
	
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
			// TODO Auto-generated method stub
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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
