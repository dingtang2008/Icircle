package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationActivity extends Activity implements OnClickListener {

	private RelativeLayout mTitle;
	private ListView locationlist;
	
	ArrayList<HashMap<String, Object>> alllocations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_layout);
		init();
	}
	
	private void init() {
		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.location_activity);
		mTitle.findViewById(R.id.act_extend).setOnClickListener(this);

		EditText seatch = (EditText) findViewById(R.id.searchBoxEditText);
		seatch.setOnClickListener(this);

		locationlist = (ListView) findViewById(R.id.location_list);

		alllocations = new ArrayList<HashMap<String, Object>>();

		int id = 0;
		for (int i = 0; i < 20; i++) {
			id = this.getResources().getIdentifier("test" + (i + 1), "drawable", "com.ile.icircle");
			Log.i("test", "id = "+ id);
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("userportrait", R.drawable.ic_default4);
			user.put("username", "浙大软件园" + (i + 1));
			user.put("userdistance", "300米" + (i + 1));
			user.put("useraddress", "中国浙江省杭州区西湖路浙大" + (i + 1) + "号");
			alllocations.add(user);
		}
		locationlist.setAdapter(new LocationListAdapter(this));
		locationlist.setOnItemClickListener(listener);
	}

	OnItemClickListener listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
			Intent intent = new Intent(LocationActivity.this, nearbyActivity.class);
			intent.putExtra(UtilString.LOCATIONID, position);
			intent.putExtra(UtilString.LOCATIONNAME,(String) alllocations.get(position).get("username"));
			startActivity(intent);
		}
		
	};
	
	static class ViewHolder {
		ImageView locationPortrait;
		TextView locationName;
		TextView locationDistance;
		TextView locationAddress;
		int position;
	}

	private class LocationListAdapter extends BaseAdapter {
		Context mContext;
		private LayoutInflater mInflater;

		public LocationListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return alllocations.size();
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
				convertView = mInflater.inflate(R.layout.location_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.locationPortrait = (ImageView) convertView
						.findViewById(R.id.location_poster);
				holder.locationName = (TextView) convertView
						.findViewById(R.id.location_name);
				holder.locationDistance = (TextView) convertView
						.findViewById(R.id.location_distance);
				holder.locationAddress = (TextView) convertView
						.findViewById(R.id.location_address);
				holder.position = position;

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			Integer portraitId = (Integer) alllocations.get(position).get("userportrait");
			String name = (String) alllocations.get(position).get("username");
			String distance = (String) alllocations.get(position).get("userdistance");
			String address = (String) alllocations.get(position).get("useraddress");

			holder.locationPortrait.setImageResource(portraitId);
			holder.locationName.setText(name);
			holder.locationDistance.setText(distance);
			holder.locationAddress.setText(address);
			
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
		default:
			break;
		}
	}
}
