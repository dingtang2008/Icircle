package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyFriends extends Activity  implements OnClickListener {

	private RelativeLayout mTitle;
	EditText search;
	private ListView friendslist;
	private int userId;
	private boolean isSelectAll = false;

	private TextView friendsCount;
	private ImageView selectAll;
	
	
	ArrayList<HashMap<String, Object>> lstImageItem;
	ArrayList<HashMap<String, Object>> allfriends;
	private boolean[] isFriendsChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfriends_layout);
		init();
	}

	private void init() {
		Intent mIntent = getIntent();
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

		allfriends = new ArrayList<HashMap<String, Object>>();
		isFriendsChecked = new boolean[20];
		int id = 0;
		for (int i = 0; i < 20; i++) {
			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
			Log.i("test", "id = "+ id);
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("userportrait", R.drawable.portrait_default);
			user.put("username", "测试用户名" + (i + 1));
			isFriendsChecked[i] = false;
			allfriends.add(user);
		}
		friendslist.setAdapter(new LiveListAdapter(this));
		friendslist.setOnItemClickListener(listener);
		

		friendsCount = (TextView) findViewById(R.id.friends_count);
		selectAll = (ImageView) findViewById(R.id.select_all_checkbox);
		selectAll.setOnClickListener(this);
		findViewById(R.id.btn_confirm).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
	}

	public void reflashViews(){

		friendslist.setAdapter(new LiveListAdapter(this));
	}
	
	OnItemClickListener listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
			Log.i("test", "listener isFriendsChecked[ " + position + "] = " + isFriendsChecked[position]);
			if (isFriendsChecked[position]){
				isFriendsChecked[position] = false;
				view.findViewById(R.id.friend_select_checkbox).setBackgroundResource(R.drawable.checkbox_unselected);
			} else {
				isFriendsChecked[position] = true;
				view.findViewById(R.id.friend_select_checkbox).setBackgroundResource(R.drawable.checkbox_selected);
			}
			reflashViews();
		}
		
	};
	
	
	static class ViewHolder {
		ImageView friendPortrait;
		TextView friendName;
		ImageView friendSelect;
		int position;
	}

	private class LiveListAdapter extends BaseAdapter {
		Context mContext;
		private LayoutInflater mInflater;

		public LiveListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allfriends.size();
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
				convertView = mInflater.inflate(R.layout.myfriends_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.friendPortrait = (ImageView) convertView
						.findViewById(R.id.friend_portrait);
				holder.friendName = (TextView) convertView
						.findViewById(R.id.friend_name);
				holder.friendSelect = (ImageView) convertView
						.findViewById(R.id.friend_select_checkbox);
				holder.position = position;

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			Integer portraitId = (Integer) allfriends.get(position).get("userportrait");
			String username = (String) allfriends.get(position).get("username");

			holder.friendPortrait.setImageResource(portraitId);
			holder.friendName.setText(username);

			Log.i("test", "isFriendsChecked[ " + position + "] = " + isFriendsChecked[position]);
			if (isFriendsChecked[position]){
				holder.friendSelect.setImageResource(R.drawable.checkbox_selected);
			} else {
				holder.friendSelect.setImageResource(R.drawable.checkbox_unselected);
			}
			
			return convertView;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBoxEditText:
			break;
		case R.id.act_extend:
			break;
		case R.id.select_all_checkbox:
			if (isSelectAll) {
				selectAll.setBackgroundResource(R.drawable.checkbox_unselected);
				for (int i = 0; i < isFriendsChecked.length; i++) {
					isFriendsChecked[i] = false;
				}
				isSelectAll = false;
			} else {
				isSelectAll = true;
				selectAll.setBackgroundResource(R.drawable.checkbox_selected);
				for (int i = 0; i < isFriendsChecked.length; i++) {
					isFriendsChecked[i] = true;
				}
			}
			friendslist.setAdapter(new LiveListAdapter(this));
			break;
		case R.id.btn_confirm:
			//Do confirm on services
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
}
