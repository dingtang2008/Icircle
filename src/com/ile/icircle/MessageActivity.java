package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageActivity extends Activity implements OnClickListener {

	private RelativeLayout mTitle;
	private ListView messagelist;
	private ListView notificationlist;
	private static final String DEFAULT_VIEW = "defaultView";

	private LinearLayout notificationView;
	private RelativeLayout personalMsgView;
	private TextView notificationCounter;
	private TextView personalMsgCounter;
	private TextView newMsgNumber;
	SharedPreferences mSharedPreferences;
	private static int notifyReadyCount = 0;
	private static int msgReadyCount = 0;

	ArrayList<HashMap<String, Object>> allNotification;
	ArrayList<HashMap<String, Object>> allPersonalMessages;

	private boolean[] isNotificationItemsRead;
	private boolean[] isMessageItemsRead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_msg_layout);
		mSharedPreferences = getSharedPreferences(UtilString.MSG_SHAREDPREFERENCES, 0);
		init();
		refleshViews();
	}

	private void init() {
		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.message);
		mTitle.findViewById(R.id.act_extend).setOnClickListener(this);

		messagelist = (ListView) findViewById(R.id.message_list);
		notificationlist = (ListView) findViewById(R.id.notification_list);

		allNotification = new ArrayList<HashMap<String, Object>>();
		allPersonalMessages = new ArrayList<HashMap<String, Object>>();
		isNotificationItemsRead = new boolean[5];
		int id = 0;
		for (int i = 0; i < 5; i++) {
			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
			Log.i("test", "id = "+ id);
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("notificationsender", "幸福小宝贝");
			user.put("notificationcontent", "邀请你参加“节能一小时”活动" + (i + 1));
			user.put("notificationtime", "10:20AM" + (i + 1));
			isNotificationItemsRead[i] = false;
			allNotification.add(user);
		}
		notificationlist.setOnItemClickListener(notificationlistlistener);

		isMessageItemsRead = new boolean[20];
		for (int i = 0; i < 20; i++) {
			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
			Log.i("test", "id = "+ id);
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("msgsenderportrait", R.drawable.portrait_default);
			user.put("msgsender", "幸福小宝贝" + (i + 1));
			user.put("msgcontent", "今天天气真好，一起去爬山吧" + (i + 1));
			user.put("msgtime", "10:20AM" + (i + 1));
			isMessageItemsRead[i] = false;
			allPersonalMessages.add(user);
		}
		messagelist.setOnItemClickListener(messagelistlistener);

		notificationView = (LinearLayout) findViewById(R.id.msg_notify);
		notificationView.setOnClickListener(this);
		personalMsgView = (RelativeLayout) findViewById(R.id.msg_personal);
		personalMsgView.setOnClickListener(this);
		notificationCounter = (TextView) findViewById(R.id.msg_notify_counter);
		personalMsgCounter = (TextView) findViewById(R.id.msg_personal_counter);
		newMsgNumber = (TextView) findViewById(R.id.msg_number);
	}

	OnItemClickListener notificationlistlistener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
			if (allNotification.size()-notifyReadyCount > 0 && !isNotificationItemsRead[position]) {
				notifyReadyCount ++;
				isNotificationItemsRead[position] = true;
				notificationCounter.setText((allNotification.size()-notifyReadyCount) + "/" + allNotification.size());
				view.setBackgroundResource(R.drawable.bar_bg_pressed);
			}

		}

	};

	OnItemClickListener messagelistlistener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
			if (allPersonalMessages.size()-msgReadyCount > 0 && !isMessageItemsRead[position]) {
				msgReadyCount ++;
				isMessageItemsRead[position] = true;
				personalMsgCounter.setText((allPersonalMessages.size()-msgReadyCount) + "/" + allPersonalMessages.size());
				view.setBackgroundResource(R.drawable.bar_bg_pressed);
			}
		}

	};
	private void refleshViews() {
		int defaultView = mSharedPreferences.getInt(DEFAULT_VIEW, 0);
		Log.i("test", "defaultView = " + defaultView);
		if (defaultView == 0) {
			notificationView.setBackgroundResource(R.drawable.msg_notify_selected);
			personalMsgView.setBackgroundResource(R.drawable.msg_personal_normal);
			notificationlist.setVisibility(View.VISIBLE);
			messagelist.setVisibility(View.GONE);
			personalMsgCounter.setVisibility(View.GONE);
			notificationCounter.setVisibility(View.VISIBLE);
			newMsgNumber.setVisibility(View.VISIBLE);
			notificationCounter.setText((allNotification.size()-notifyReadyCount) + "/" + allNotification.size());
			newMsgNumber.setText((allPersonalMessages.size()-msgReadyCount)+"");
			notificationlist.setAdapter(new NoteListAdapter(this));
			notificationView.setOnClickListener(this);
		} else {
			notificationView.setBackgroundResource(R.drawable.msg_notify_normal);
			personalMsgView.setBackgroundResource(R.drawable.msg_personal_selected);
			notificationlist.setVisibility(View.GONE);
			notificationCounter.setVisibility(View.GONE);
			messagelist.setVisibility(View.VISIBLE);
			newMsgNumber.setVisibility(View.GONE);
			personalMsgCounter.setVisibility(View.VISIBLE);
			personalMsgCounter.setText((allPersonalMessages.size()-msgReadyCount) + "/" + allPersonalMessages.size());
			messagelist.setAdapter(new MsgListAdapter(this));
			personalMsgView.setOnClickListener(this);
		}
	}

	static class ViewHolder {
		TextView notificationSender;
		TextView notificationContent;
		TextView notificationTime;
		ImageView msgSenderPortrait;
		TextView msgSender;
		TextView msgContent;
		TextView msgTime;
		int notificationPosition;
		int msgPosition;
	}

	private class NoteListAdapter extends BaseAdapter {
		Context mContext;
		private LayoutInflater mInflater;

		public NoteListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return allNotification.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.act_msg_notification_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.notificationSender = (TextView) convertView
						.findViewById(R.id.notification_sender);
				holder.notificationContent = (TextView) convertView
						.findViewById(R.id.notification_content);
				holder.notificationTime = (TextView) convertView
						.findViewById(R.id.notification_time);
				holder.notificationPosition = position;

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			if (!isNotificationItemsRead[position]){
				convertView.setBackgroundResource(R.drawable.bar_bg_normal);
			} else {
				convertView.setBackgroundResource(R.drawable.bar_bg_pressed);
			}

			String sender = (String) allNotification.get(position).get("notificationsender");
			String content = (String) allNotification.get(position).get("notificationcontent");
			String time = (String) allNotification.get(position).get("notificationtime");

			holder.notificationSender.setText(sender);
			holder.notificationContent.setText(content);
			holder.notificationTime.setText(time);

			return convertView;
		}
	}

	private class MsgListAdapter extends BaseAdapter {
		Context mContext;
		private LayoutInflater mInflater;

		public MsgListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return allPersonalMessages.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.act_msg_personal_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.msgSenderPortrait = (ImageView) convertView.findViewById(R.id.msgsender_portrait);
				holder.msgSender = (TextView) convertView
						.findViewById(R.id.msg_sender);
				holder.msgContent = (TextView) convertView
						.findViewById(R.id.msg_content);
				holder.msgTime = (TextView) convertView
						.findViewById(R.id.msg_time);
				holder.msgPosition = position;

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			if (!isMessageItemsRead[position]){
				convertView.setBackgroundResource(R.drawable.bar_bg_normal);
			} else {
				convertView.setBackgroundResource(R.drawable.bar_bg_pressed);
			}

			Integer portraitId = (Integer) allPersonalMessages.get(position).get("msgsenderportrait");

			String sender = (String) allPersonalMessages.get(position).get("msgsender");
			String content = (String) allPersonalMessages.get(position).get("msgcontent");
			String time = (String) allPersonalMessages.get(position).get("msgtime");
			holder.msgSenderPortrait.setImageResource(portraitId);
			holder.msgSender.setText(sender);
			holder.msgContent.setText(content);
			holder.msgTime.setText(time);

			return convertView;
		}
	}


	public void onClick(View v) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		Log.i("test", v.toString());
		switch (v.getId()) {
		case R.id.msg_notify:
			mEditor.putInt(DEFAULT_VIEW, 0);
			mEditor.commit();
			refleshViews();
			break;
		case R.id.msg_personal:
			mEditor.putInt(DEFAULT_VIEW, 1);
			mEditor.commit();
			refleshViews();
			break;
		default:
			break;
		}
	}
}
