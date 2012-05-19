package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ClassifyActivity extends Activity implements OnClickListener {

	private GridView mClassifyAct;
	private RelativeLayout mTitle;
	EditText search;
	
	ArrayList<HashMap<String, Object>> lstImageItem;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classify_layout);
		init();
	}

	private void init() {
		mTitle = (RelativeLayout) findViewById(R.id.title);
		((TextView)mTitle.findViewById(R.id.act_title)).setText(R.string.act_classify);
		mTitle.findViewById(R.id.act_extend).setOnClickListener(this);
		ImageView extend_add = (ImageView) mTitle.findViewById(R.id.act_extend);
		extend_add.setBackgroundResource(R.drawable.add_act_selector);
		extend_add.setOnClickListener(this);

		mClassifyAct = (GridView) findViewById(R.id.act_by_classify);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 8; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			map.put(UtilString.PANEL_CONTENT_IMAGE_KEY, mImageIds[i]);
			map.put(UtilString.PANEL_CONTENT_TEXT_KEY, getString(mStringIds[i]));
			map.put(UtilString.PANEL_CONTENT_COUNT_KEY, "(" + mClassifyCountTest[i]+ ")");
			lstImageItem.add(map);
		}
		SimpleAdapter mClassifyActAdapter = new SimpleAdapter(
				this,
				lstImageItem,
				R.layout.classify_content,
				new String[] {UtilString.PANEL_CONTENT_IMAGE_KEY, UtilString.PANEL_CONTENT_TEXT_KEY, UtilString.PANEL_CONTENT_COUNT_KEY },
				new int[] {R.id.ItemImage, R.id.ItemText, R.id.ItemCount });
		mClassifyAct.setAdapter(mClassifyActAdapter);
		mClassifyAct.setOnItemClickListener(mOnItemClickListener);

		search = (EditText) findViewById(R.id.searchBoxEditText);
		search.setHint("校园十大歌手");
		search.setOnClickListener(this);
	}
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ClassifyActivity.this,ActListActivity.class);
			Log.i("test", "arg2 = " + arg2);
			Log.i("test", "getString(mStringIds[arg2]) = " + getString(mStringIds[arg2]));
			intent.putExtra(UtilString.CLASSIFYID, arg2);
			intent.putExtra(UtilString.CLASSIFYNAME, getString(mStringIds[arg2]));
			startActivity(intent);
		}
		
	};
	
	
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
}
