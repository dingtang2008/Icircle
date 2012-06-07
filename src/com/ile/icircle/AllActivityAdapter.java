package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllActivityAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

	private Context mContext;

	public AllActivityAdapter(Context context) {
		Log.i("test", "AllActivityAdapter");
		this.mInflater = LayoutInflater.from(context);
		Log.i("test", "mData = " +mData);
		mContext = context;
		//		mData = getData();
		//		mData = new ArrayList<Map<String, Object>>();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final class ViewHolder {

		public TextView status;
		public ImageView img;
		public TextView title;
		public TextView info;
		public ImageView ic_status;
		public int actId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.alllist, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.ic_status = (ImageView) convertView
					.findViewById(R.id.ic_status);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		PictureGet mPictureGet = new PictureGet(mContext);
		if (mData.get(position).get("img") != null) {
			String posterUrl = (String) mData.get(position).get("img");
			Bitmap mPoster = null;
			if(posterUrl.startsWith("http://")) {

			} else if(posterUrl.startsWith("/mnt/sdcard/")) {
				mPoster = mPictureGet.getPic(posterUrl);
			}
			holder.img.setImageBitmap(mPoster);
		}
		holder.status.setText((String) mData.get(position).get("status"));
		holder.status.setTextColor(Color.parseColor((String) mData
				.get(position).get("status_color")));
		holder.title.setText((String) mData.get(position).get("title"));
		if (mData.get(position).get("info") != null)
			holder.info.setText((String) mData.get(position).get("info"));
		if (mData.get(position).get("ic") != null)
			holder.ic_status.setBackgroundResource((Integer) mData
					.get(position).get("ic"));
		
		holder.actId = (Integer) mData.get(position).get("actid");
		/*
		 * holder.ic_status.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { showInfo(); } });
		 */

		return convertView;

	}

	public void addData(List<Map<String, Object>> list) {
		mData.addAll(list);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "正在进行");
		map.put("status_color", UtilString.act_living);
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("info", "29人感兴趣   17人参加");
		map.put("img", R.drawable.test_list_icon);
		map.put("ic", R.drawable.ic_to_attend);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "已经结束");
		map.put("status_color", UtilString.act_ending);
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("info", "229人感兴趣   117人参加");
		map.put("img", R.drawable.btn_cal);
		map.put("ic", R.drawable.ic_to_intest);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "即将开始");
		map.put("status_color", UtilString.act_starting);
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("info", "229人感兴趣   17人参加");
		map.put("img", R.drawable.btn_cal);
		map.put("ic", R.drawable.ic_to_selected);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "正在进行");
		map.put("status_color", "#85e705");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("info", "29人感兴趣   117人参加");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "已经结束");
		map.put("status_color", "#b1b1b1");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "即将开始");
		map.put("status_color", "#ff6c00");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "正在进行");
		map.put("status_color", "#85e705");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "已经结束");
		map.put("status_color", "#b1b1b1");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "即将开始");
		map.put("status_color", "#ff6c00");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "正在进行");
		map.put("status_color", "#85e705");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "已经结束");
		map.put("status_color", "#b1b1b1");
		map.put("title", "达州一中加拿大留学专题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("status", "即将开始");
		map.put("status_color", "#ff6c00");
		map.put("title", "达d拿大d题讲座");
		map.put("img", R.drawable.btn_cal);
		list.add(map);

		return list;
	}

	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo() {

	}
}