package com.ile.icircle;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

public class CirclesTabActivity extends TabActivity implements OnTabChangeListener{
	private TabHost tabHost;
	private TabWidget tabWidget;
	
	private int[] ic_tab_normal_id = {
			R.drawable.ic_hot_act_normal,
			R.drawable.ic_classify_act_normal,
			R.drawable.ic_location_act_normal,
			R.drawable.ic_user_center_normal,
			R.drawable.ic_message_normal
	};
	private int[] ic_tab_seleted_id = {
			R.drawable.ic_hot_act_selected,
			R.drawable.ic_classify_act_selected,
			R.drawable.ic_location_act_selected,
			R.drawable.ic_user_center_selected,
			R.drawable.ic_message_selected
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);
		
		Intent intent = getIntent();
		String mSchool = intent.getStringExtra(UtilString.SCHOOLNAME); 
		Log.i("test", "mSchool = " + mSchool);
		
		Editor shareEditor = getSharedPreferences(UtilString.SCHOOLNAME, 0).edit();
		shareEditor.putString(UtilString.SCHOOLNAME, mSchool);
		shareEditor.commit();
		
		Resources res = getResources();

		
		LayoutInflater mInflater = LayoutInflater.from(this);

		View tabView0 = mInflater.inflate(R.layout.tabview, null);
		View tabView1 = mInflater.inflate(R.layout.tabview, null);
		View tabView2 = mInflater.inflate(R.layout.tabview, null);
		View tabView3 = mInflater.inflate(R.layout.tabview, null);
		View tabView4 = mInflater.inflate(R.layout.tabview, null);
		
		tabHost = getTabHost();
		tabWidget = (TabWidget)findViewById(android.R.id.tabs);
		tabHost.addTab(tabHost.newTabSpec("hotAct")
				.setIndicator(getTabView(getText(R.string.hot_activity),
						res.getDrawable(ic_tab_normal_id[0]), tabView0))
						.setContent(new Intent().setClass(this, HotActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("classify")
				.setIndicator(getTabView(getText(R.string.classify_activity),
						res.getDrawable(ic_tab_normal_id[1]), tabView1))
						.setContent(new Intent().setClass(this, ClassifyActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("location")
				.setIndicator(getTabView(getText(R.string.location_activity),
						res.getDrawable(ic_tab_normal_id[2]), tabView2))
						.setContent(new Intent().setClass(this, LocationActivity.class)));
		tabHost.setOnTabChangedListener(this);
		tabHost.addTab(tabHost.newTabSpec("userCenter")
				.setIndicator(getTabView(getText(R.string.user_center),
						res.getDrawable(ic_tab_normal_id[3]), tabView3))
						.setContent(new Intent().setClass(this, UserCenter.class)));
		tabHost.setOnTabChangedListener(this);
		tabHost.addTab(tabHost.newTabSpec("message")
				.setIndicator(getTabView(getText(R.string.message),
						res.getDrawable(ic_tab_normal_id[4]), tabView4))
						.setContent(new Intent().setClass(this, MessageActivity.class)));
		tabHost.setOnTabChangedListener(this);

		tabHost.setCurrentTab(0);
		
		for (int i =0; i < tabWidget.getChildCount(); i++) {
			View tabview = tabWidget.getChildAt(i);
			TextView tabTitle = (TextView) tabview.findViewById(R.id.tab_title);
			ImageView tabImg= (ImageView) tabview.findViewById(R.id.tab_img);
			if(tabHost.getCurrentTab()==i){
				tabview.setBackgroundResource(ic_tab_seleted_id[i]);
				tabTitle.setVisibility(View.GONE);
				tabImg.setVisibility(View.GONE);
			}
			else {
				tabview.setBackgroundDrawable(null);
				tabTitle.setVisibility(View.VISIBLE);
				tabImg.setVisibility(View.VISIBLE);
			}
		}
		tabHost.setPadding(tabHost.getPaddingLeft(), tabHost.getPaddingTop(), 
				tabHost.getPaddingRight(), tabHost.getPaddingBottom()-5);
	}

	public View getTabView(CharSequence title, Drawable drawable, View tabView){
		TextView tabTitle = (TextView) tabView.findViewById(R.id.tab_title);
		ImageView tabImg= (ImageView) tabView.findViewById(R.id.tab_img);
		tabImg.setImageDrawable(drawable);
		tabTitle.setText(title);
		return tabView;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		for (int i =0; i < tabWidget.getChildCount(); i++) {
			View tabview = tabWidget.getChildAt(i);
			TextView tabTitle = (TextView) tabview.findViewById(R.id.tab_title);
			ImageView tabImg= (ImageView) tabview.findViewById(R.id.tab_img);
			if(tabHost.getCurrentTab()==i){
				tabview.setBackgroundResource(ic_tab_seleted_id[i]);
				tabTitle.setVisibility(View.GONE);
				tabImg.setVisibility(View.GONE);
			}
			else {
				tabview.setBackgroundDrawable(null);
				tabTitle.setVisibility(View.VISIBLE);
				tabImg.setVisibility(View.VISIBLE);
			}
		}
	}
}