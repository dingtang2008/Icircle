package com.ile.icircle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.ile.icircle.DetailActActivity.QueryHandler;
import com.ile.icircle.ScrollLayout.OnViewChangeListener;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DetailActExtendActivity extends Activity  implements OnClickListener, OnItemClickListener{

	private RelativeLayout mTitle;
	private TextView mtitle;

	private String extendContent; 
	private int actTagId;

	private static final String PANEL_CONTENT_IMAGE_KEY = "ItemImage";
	private static final String PANEL_CONTENT_TEXT_KEY = "ItemText";
	ArrayList<HashMap<String, Object>> peopleImageItem;
	ArrayList<HashMap<String, Object>> attendImageItem;
	private GridView peopleView;

	private Integer[] mImageIds; //should be 37*37dip
	private String[] mStrings = {
			"pjol","sss", "sdad", "sda", "sdad","adad",
			"pjol","sss", "sdad", "sda", "sdad","adad",	
			"pjol","sss", "sdad", "sda", "sdad","adad",	
			"pjol","sss", "sdad", "sda", "sdad","adad",	
			"pjol","sss", "sdad", "sda", "sdad","adad",	
			"pjol","sss", "sdad", "sda", "sdad","adad"	
	};


	QueryHandler mQueryHandler;
	private static final int ACT_INTERESTPEOPLE_QUERY_TOKEN = 101;
	private static final int ACT_ATTENDPEOPLE_QUERY_TOKEN = 102;
	private static final int PEOPLE_QUERY_TOKEN = 103;

	private int[] actInterestPeopleId;
	private int[] actAttendPeopleId;

	public int tryloadtimes = 0;

	private PictureGet mPictureGet;
	private mCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_detail_extend_layout);
		newIntent(getIntent());
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter.getCursor() != null) {
			mAdapter.getCursor().close();
		}
		mQueryHandler.removeCallbacksAndMessages(ACT_INTERESTPEOPLE_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(ACT_ATTENDPEOPLE_QUERY_TOKEN);
		mQueryHandler.removeCallbacksAndMessages(PEOPLE_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFLESH_ACTPEOPLE);
		mCircleHandle.removeMessages(CircleHandle.MSG_START_QUERY);
	}


	private void newIntent(Intent mIntent) {
		mPictureGet = new PictureGet(this);
		mAdapter = new mCursorAdapter(this);

		extendContent = mIntent.getStringExtra(UtilString.DETAILEXTEND);
		actTagId = mIntent.getIntExtra(UtilString.ACTID, -1);
		Log.i("test", "actTagId = "+actTagId);
		mQueryHandler = new QueryHandler(this);

		String[] selectionArgs = {String.valueOf(actTagId)};
		String selection = null;
		String sortOrder = null;
		if (extendContent !=null) {
			if (extendContent.equals(UtilString.ATTEND)) {
				//choose query cursor
				selection = CircleContract.ActPeople.INTREST_ACT_TAGID + "= ?";
				mQueryHandler.startQuery(ACT_INTERESTPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
			} else if (extendContent.equals(UtilString.INTEREST)){
				//choose query cursor
				selection = CircleContract.ActPeople.ATTEND_ACT_TAGID + "= ?";
				mQueryHandler.startQuery(ACT_ATTENDPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
			} else {
				//error message
				finish();
			}
		} else {
			//error message
			finish();
		}

		mTitle = (RelativeLayout) findViewById(R.id.title);
		mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		Button extend_fans = (Button) mTitle.findViewById(R.id.act_extend);
		extend_fans.setVisibility(View.VISIBLE);
		extend_fans.setBackgroundResource(R.drawable.fans_act_selector);
		extend_fans.setOnClickListener(this);

		mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(mIntent.getStringExtra(UtilString.DETAILEXTENDTITLE));
		//mtitle.setOnClickListener(this);

		peopleView = (GridView) findViewById(R.id.peopel_view);
		peopleImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 36; i++) {
			//mImageIds[i] = R.drawable.portrait_default;
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put(PANEL_CONTENT_IMAGE_KEY, R.drawable.portrait_default);
			map.put(PANEL_CONTENT_TEXT_KEY, mStrings[i]);
			peopleImageItem.add(map);
		}
		SimpleAdapter mPeopleActAdapter = new SimpleAdapter(
				this,
				peopleImageItem,
				R.layout.people_grid_item,
				new String[] {PANEL_CONTENT_IMAGE_KEY, PANEL_CONTENT_TEXT_KEY},
				new int[] {R.id.ItemImage, R.id.ItemText});
		//peopleView.setAdapter(mPeopleActAdapter);
		peopleView.setAdapter(mAdapter);
		peopleView.setOnItemClickListener(this);

	}

	CircleHandle mCircleHandle = new CircleHandle(this){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFLESH_ACTPEOPLE:
				Log.i("test", this.toString() + "MSG_REFLESH_ACTPEOPLE");
				mCircleHandle.refreshActPeople();
				mCircleHandle.refreshPeople();
				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
				break;
			case CircleHandle.LOADER_DATA:
				Log.i("test", "LOADER_DATA");
				String[] selectionArgs = {String.valueOf(actTagId)};
				String sortOrder = null;
				String selection = null;
				if (extendContent !=null) {
					if (extendContent.equals(UtilString.ATTEND)) {
						//choose query cursor
						selection = CircleContract.ActPeople.INTREST_ACT_TAGID + "= ?";
						mQueryHandler.startQuery(ACT_INTERESTPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
					} else if (extendContent.equals(UtilString.INTEREST)){
						//choose query cursor
						selection = CircleContract.ActPeople.ATTEND_ACT_TAGID + "= ?";
						mQueryHandler.startQuery(ACT_ATTENDPEOPLE_QUERY_TOKEN, null, CircleContract.ActPeople.CONTENT_URI, UtilString.peopleActProjection, selection, selectionArgs, sortOrder);
					} else {
						//error message
						finish();
					}
				} 
				break;
			default:
				break;
			}
		}
	};

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<DetailActExtendActivity> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<DetailActExtendActivity>((DetailActExtendActivity) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.i("test", "token = "+token);
			if (mActivity == null) {
				return;
			}
			if(token == ACT_INTERESTPEOPLE_QUERY_TOKEN || token == ACT_ATTENDPEOPLE_QUERY_TOKEN) {
				mActivity.get().loadActPeopleFromDB(cursor);
			}

		}
	}

	public void loadActPeopleFromDB(Cursor cursor) {
		if (cursor != null && cursor.getCount() != 0) {
			mAdapter.changeCursor(cursor);
		} else if (tryloadtimes == 0){
			Log.i("test", "loadActPeopleFromDB tryloadtimes = " + tryloadtimes);
			tryloadtimes ++;
			mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFLESH_ACTPEOPLE);
		}  else {
			//error message no data
		}
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.act_back:
			finish();
			break;
		case R.id.act_extend:
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//		Intent intent = new Intent(this,ActListActivity.class);
		//		intent.putExtra(UtilString.CLASSIFYID, arg2);
		//		intent.putExtra(UtilString.CLASSIFYNAME, getString(mStringIds[arg2]));
		//		startActivity(intent);
	}



	static class ViewHolder {
		ImageView portrait;
		TextView name;
		int position;
	}

	private final class mCursorAdapter extends CursorAdapter {
		private LayoutInflater mInflater;

		public mCursorAdapter(Context context) {
			super(context, null, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			setView(view, context , cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.people_grid_item, null); 
			ViewHolder holder = new ViewHolder();
			holder.portrait = (ImageView) view.findViewById(R.id.ItemImage);
			holder.name = (TextView) view.findViewById(R.id.ItemText);
			view.setTag(holder);
			return view;
		}

		private void setView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder)view.getTag();
			int portraitImg;
			Bitmap mPortraitBitmap;
			String name;
			if (cursor != null) {
				int actPeopleId = cursor.getInt(UtilString.actPeoplePeopleIdIndex);
				if (actPeopleId != 0) {
					String[] selectionArgs = {String.valueOf(actPeopleId)};
					String selection = CircleContract.People.PEOPLE_ID + "= ?";
					String sortOrder = null;
					Cursor peopleCursor = context.getContentResolver().query(CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, sortOrder);
					if (peopleCursor != null && peopleCursor.moveToFirst()) {
						portraitImg = peopleCursor.getInt(UtilString.peopleProtraitIndex);
						name = peopleCursor.getString(UtilString.peopleNameIndex);
						mPortraitBitmap = BitmapFactory.decodeResource(getResources(), portraitImg);
						mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
						holder.portrait.setImageBitmap(mPortraitBitmap);
						holder.name.setText(name);
					}
					peopleCursor.close();
				}
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			super.changeCursor(cursor);
		}

	}

}
