package com.ile.icircle;

import java.util.ArrayList;
import java.util.HashMap;

import com.ile.icircle.ScrollLayout.OnViewChangeListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DetailActExtendActivity extends Activity  implements OnClickListener{

	private RelativeLayout mTitle;
	private TextView mtitle;
	
	private String extendContent; 

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_detail_extend_layout);
		init();
	}

	private void init() {
		Intent mIntent = getIntent();
		
		extendContent = mIntent.getStringExtra(UtilString.DETAILEXTEND);
		
		if (extendContent !=null) {
			if (extendContent.equals(UtilString.ATTEND)) {
				//choose cursor
			} else if (extendContent.equals(UtilString.INTEREST)){
				//choose cursor
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
		peopleView.setAdapter(mPeopleActAdapter);
		//peopleView.setOnItemClickListener(this);

	}

	boolean isAttent = false;
	boolean isInterest = false;

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

}
