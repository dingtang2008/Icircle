package com.ile.icircle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.ile.icircle.CircleHandle.RefreshFinishListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyFriends extends Activity  implements OnClickListener {

	private static String TAG = "MyFriends";
	private RelativeLayout mTitle;
	EditText search;
	private ListView friendslist;
	private int userId;
	private boolean isSelectAll = false;

	private TextView friendsCount;
	private ImageView selectAll;


	//	ArrayList<HashMap<String, Object>> lstImageItem;
	//	ArrayList<HashMap<String, Object>> allfriends;
	//	private boolean[] isFriendsChecked;

	public int tryloadtimes = 0;
	private PictureGet mPictureGet;
	private QueryHandler mQueryHandler;
	private static final int FRIENDS_QUERY_TOKEN = 101;
	private final static int DIALOG_REFRESH_DATA = 0;

	private ProgressDialog pd;
	private FriendsCursorAdapter mAdapter;
//	HashMap<String, Integer> selectedFriends;
	ArrayList<Integer> selectedFriends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfriends_layout);
		newIntent(getIntent());
//		selectedFriends = new HashMap<String, Integer>();
		selectedFriends = new ArrayList<Integer>();
		mPictureGet = new PictureGet(this);
		pd = new ProgressDialog(this);
		pd.setTitle(R.string.loading_data);
		mQueryHandler = new QueryHandler(this);
		mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter.getCursor() != null) {
			mAdapter.getCursor().close();
		}
		mQueryHandler.removeCallbacksAndMessages(FRIENDS_QUERY_TOKEN);
		mCircleHandle.removeMessages(CircleHandle.LOADER_DATA);
		mCircleHandle.removeMessages(CircleHandle.MSG_REFRESH_ACTPEOPLE);
		mCircleHandle.removeMessages(CircleHandle.MSG_INSERT_DATA);
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	private void newIntent(Intent mIntent) {
		userId = mIntent.getIntExtra(UtilString.USERID, -1);
		Log.i("test", "actId = "+userId);

		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.act_classify);
		mTitle.findViewById(R.id.act_extend).setOnClickListener(this);
		search = (EditText) findViewById(R.id.searchBoxEditText);
		search.setHint("校园十大歌手");
		search.setOnClickListener(this);
		TextView mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(getString(R.string.frirends));

		friendslist = (ListView) findViewById(R.id.myfriends_list);

		//		allfriends = new ArrayList<HashMap<String, Object>>();
		//		isFriendsChecked = new boolean[20];
		//		int id = 0;
		//		for (int i = 0; i < 20; i++) {
		//			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
		//			Log.i("test", "id = "+ id);
		//			HashMap<String, Object> user = new HashMap<String, Object>();
		//			user.put("userportrait", R.drawable.portrait_default);
		//			user.put("username", "测试用户名" + (i + 1));
		//			isFriendsChecked[i] = false;
		//			allfriends.add(user);
		//		}
		mAdapter = new FriendsCursorAdapter(this);
		friendslist.setAdapter(mAdapter);
		friendslist.setOnItemClickListener(listener);


		friendsCount = (TextView) findViewById(R.id.friends_count);
		selectAll = (ImageView) findViewById(R.id.select_all_checkbox);
		selectAll.setOnClickListener(this);
		findViewById(R.id.btn_confirm).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
	}

	OnItemClickListener listener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
			ViewHolder mholder = (ViewHolder)view.getTag();
			mAdapter.setAllSelect(false, false);
			Log.w(TAG, "mholder.time = "+mholder.time);
			if (selectedFriends.contains(mholder.friendId)){
				selectedFriends.remove((Object)mholder.friendId);
				Log.w(TAG, "position1 = "+position);
				mholder.friendSelect.setBackgroundResource(R.drawable.checkbox_unselected);
			} else {
				Log.w(TAG, "position2 = "+position);
				selectedFriends.add( mholder.friendId);
				mholder.friendSelect.setBackgroundResource(R.drawable.checkbox_selected);
			}
			
			Log.w(TAG, "selectedFriends.size() = "+selectedFriends.size());
			Log.w(TAG, "mAdapter.getCount() = "+mAdapter.getCount());
			if (selectedFriends.size() == mAdapter.getCount()) {
				selectAll.setBackgroundResource(R.drawable.checkbox_selected);
			} else {
				selectAll.setBackgroundResource(R.drawable.checkbox_unselected);
			}
		}

	};

	CircleHandle mCircleHandle = new CircleHandle(this){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case CircleHandle.MSG_REFRESH_ACTPEOPLE:
				Log.i("test", this.toString() + "MSG_REFRESH_ACTPEOPLE");
//				mCircleHandle.refreshActPeople();
//				mCircleHandle.refreshPeople();
//				mCircleHandle.sendEmptyMessage(CircleHandle.LOADER_DATA);

				mCircleHandle.refreshTask.execute(CircleHandle.MSG_REFRESH_ACTPEOPLE);
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
			default:
				break;
			}
		}
	};

	class QueryHandler extends AsyncQueryHandler {
		public final WeakReference<MyFriends> mActivity;
		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mActivity = new WeakReference<MyFriends>((MyFriends) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			Log.i("test", "token = "+token);
			if (mActivity == null) {
				return;
			}
			if(token == FRIENDS_QUERY_TOKEN) {
				mActivity.get().loadFriendsFromDB(cursor);
			}

		}
	}

	public void loadFriendsFromDB(Cursor cursor) {
		if (cursor != null && cursor.getCount() != 0) {
			mAdapter.changeCursor(cursor);
			dismissProgress();
		} else if (tryloadtimes == 0){
			tryloadtimes ++;
			showDialog(DIALOG_REFRESH_DATA);
		}  else {
			Toast.makeText(this, R.string.dialog_data_empty_title, 0);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;  
		switch (id) {
		case DIALOG_REFRESH_DATA:
			AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);  
			mbuilder.setTitle(getString(R.string.dialog_data_empty_title));  
			mbuilder.setMessage(R.string.dialog_data_empty);
			mbuilder.setPositiveButton(R.string.act_confirm_editor, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mCircleHandle.sendEmptyMessage(CircleHandle.MSG_REFRESH_ACTPEOPLE);
				}
			});
			mbuilder.setNegativeButton(R.string.act_cancel_editor, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null) {
						dialog.dismiss();
						dismissProgress();
					}
				}
			});
			dialog = mbuilder.create();
			break;

		default:
			break;
		}
		return dialog;
	}


	static class ViewHolder {
		ImageView friendPortrait;
		TextView friendName;
		ImageView friendSelect;
		int friendId;
		int position;
		String time;
	}

	private class FriendsCursorAdapter extends CursorAdapter {
		private LayoutInflater mInflater;
		private boolean isAllSelect = false;
		private boolean needrefresh = false;

		public FriendsCursorAdapter(Context context) {
			super(context, null, false);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			setView(view, context , cursor);
		}

		public void setAllSelect(boolean refresh, boolean isselect) {
			needrefresh = refresh;
			isAllSelect = isselect;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.myfriends_list_item, null);
			ViewHolder holder = new ViewHolder(); 
			holder = new ViewHolder();
			holder.friendPortrait = (ImageView) view.findViewById(R.id.friend_portrait);
			holder.friendName = (TextView) view.findViewById(R.id.friend_name);
			holder.friendSelect = (ImageView) view.findViewById(R.id.friend_select_checkbox);
			view.setTag(holder);
			return view;
		}

		private void setView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder)view.getTag();
			int portraitImg;
			Bitmap mPortraitBitmap = null;
			int id;
			String time = "";
			String username = "";
			if (cursor != null) {
				id = cursor.getInt(UtilString.friendshipIdIndex);
				time = cursor.getString(UtilString.friendshiptimeIndex);
				int friendId = cursor.getInt(UtilString.friendshipFriendIdIndex);
				if (friendId != 0) {
					String[] selectionArgs = {String.valueOf(friendId)};
					String selection = CircleContract.People.PEOPLE_ID + "= ?";
					String sortOrder = null;
					Cursor peopleCursor = context.getContentResolver().query(CircleContract.People.CONTENT_URI, UtilString.peopleProjection, selection, selectionArgs, sortOrder);
					if (peopleCursor != null && peopleCursor.moveToFirst()) {
						portraitImg = peopleCursor.getInt(UtilString.peopleProtraitIndex);
						username = peopleCursor.getString(UtilString.peopleNameIndex);
						mPortraitBitmap = BitmapFactory.decodeResource(getResources(), portraitImg);
						mPortraitBitmap = mPictureGet.resizeBitmap(mPortraitBitmap, 56, 56);
						if (mPortraitBitmap != null) {
							holder.friendPortrait.setImageBitmap(mPortraitBitmap);
						} else {
							holder.friendPortrait.setBackgroundResource(R.drawable.portrait_default);
						}
						holder.friendName.setText(username);
						holder.time = time;
						holder.position = cursor.getPosition();
						holder.friendId = friendId;
					}
					if (needrefresh) {
						if (isAllSelect){
							selectedFriends.add(friendId);
//							selectedFriends.put(time, friendId);
							holder.friendSelect.setBackgroundResource(R.drawable.checkbox_selected);
						} else {
							holder.friendSelect.setBackgroundResource(R.drawable.checkbox_unselected);
							selectedFriends.remove(friendId);
						}
					}
					if (selectedFriends.contains(holder.friendId)){
						holder.friendSelect.setBackgroundResource(R.drawable.checkbox_selected);
					} else {
						//selectedFriends.remove(holder.friendId);
						holder.friendSelect.setBackgroundResource(R.drawable.checkbox_unselected);
					}
					peopleCursor.close();
				} else {
					view.setVisibility(View.GONE);
					//need remove this data because the people is null
				}
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			super.changeCursor(cursor);
		}
	}

	private void showProgress(String title){
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBoxEditText:
			break;
		case R.id.act_extend:
			break;
		case R.id.select_all_checkbox:
			if (isSelectAll) {
				selectAll.setBackgroundResource(R.drawable.checkbox_unselected);
				mAdapter.setAllSelect(true, false);
				selectAll(false);
				//				for (int i = 0; i < isFriendsChecked.length; i++) {
				//					isFriendsChecked[i] = false;
				//				}
				isSelectAll = false;
			} else {
				isSelectAll = true;
				mAdapter.setAllSelect(true, true);
				selectAll(true);
				selectAll.setBackgroundResource(R.drawable.checkbox_selected);
				//				for (int i = 0; i < isFriendsChecked.length; i++) {
				//					isFriendsChecked[i] = true;
				//				}
			}
			break;
		case R.id.btn_confirm:
			//Do confirm on services
			ContentValues values = new ContentValues();
			int[] InviterFriends = new int[selectedFriends.size()];
			for(int i = 0; i < selectedFriends.size(); i ++) {
				InviterFriends[i] = selectedFriends.get(i);
			}
			Intent data = new Intent();
			data.putExtra(UtilString.INVITER_FRIENDS, InviterFriends);
			setResult(RESULT_OK, data);
			finish();
			break;
		case R.id.btn_cancel:
			//			for (int i = 0; i < isFriendsChecked.length; i++) {
			//				isFriendsChecked[i] = false;
			//				friendslist.setAdapter(new LiveListAdapter(this));
			//			}
			finish();
			break;
		default:
			break;
		}
	}

	public void selectAll(boolean isSelected){
		Log.w(TAG, "selectAll");
		Cursor cursor = mAdapter.getCursor();
		if (cursor == null) {
			Log.w(TAG, "cursor is null.");
			return;
		}

		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			int friendId = cursor.getInt(UtilString.friendshipFriendIdIndex);;
			int position = cursor.getPosition();
			String time = cursor.getString(UtilString.friendshiptimeIndex);

			if (isSelected) {
				selectedFriends.add(friendId);
			} else {
				selectedFriends.remove(friendId);
			}
		}

		int count = friendslist.getChildCount();
		Log.w(TAG, "count = " + count);
		for (int i = 0; i < count; i++) { 
			View view = friendslist.getChildAt(i);
			if (isSelected) {
				view.findViewById(R.id.friend_select_checkbox).setBackgroundResource(R.drawable.checkbox_selected);
			} else {
				view.findViewById(R.id.friend_select_checkbox).setBackgroundResource(R.drawable.checkbox_unselected);
			}
		}
	}

}
