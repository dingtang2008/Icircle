package com.ile.icircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class nearbyActivity extends Activity implements OnClickListener{
	private RelativeLayout mTitle;
	String str1 = "人    气： 120";
	String str2 = "活动数： 90";
	TextView introduce;
	TextView textview1;
	TextView textview2;
	TextView detail_interest;
	TextView detail_addres;
	TextView detail_attend;
	int start = 8;
	int end = 11;
	private TextView act_title;
	private Button act_extend;

	private LinearLayout interestView;
	private LinearLayout attendView;
	private Integer[] mImageIds = {
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default,
			R.drawable.portrait_default
	}; //should be 37*37dip
	private String[] mStrings = { "pjol","sss", "sdad", "sda", "sdad","adad"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_layout);
		Intent intent = getIntent();

		String id = intent.getStringExtra(UtilString.LOCATIONID);
		String name = intent.getStringExtra(UtilString.LOCATIONNAME);

		mTitle = (RelativeLayout) findViewById(R.id.title);
		mTitle.findViewById(R.id.act_extend).setVisibility(View.GONE);
		mTitle.findViewById(R.id.act_back).setOnClickListener(this);

		act_title = (TextView) findViewById(R.id.act_title);
		act_title.setText(name);

		textview1 = (TextView) findViewById(R.id.classify_title);
		SpannableStringBuilder style = new SpannableStringBuilder(str1);
		style.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start,
				end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview1.setText(style);

		start = 5;
		end = 7;
		textview2 = (TextView) findViewById(R.id.classify_content);
		SpannableStringBuilder style_content = new SpannableStringBuilder(str2);
		style_content.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style_content.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview2.setText(style_content);

		detail_interest = (TextView) findViewById(R.id.detail_interest);
		detail_interest.setText("  90 人喜欢这地方");

		detail_addres = (TextView) findViewById(R.id.detail_addres);
		detail_addres.setText("  地点/电话");

		detail_attend = (TextView) findViewById(R.id.detail_attend);
		detail_attend.setText("  90个活动在这里");

		introduce = (TextView) findViewById(R.id.introduce);
		introduce.setText("　　测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测");
		

		ImageView peopleIcon;
		TextView peopleName;
		LinearLayout people;
		interestView = (LinearLayout) findViewById(R.id.interest_list);


		for (int i = 0; i < 6; i++) {
			people = (LinearLayout) interestView.getChildAt(i);
			peopleIcon = (ImageView) people.findViewById(R.id.ItemImage);
			peopleName = (TextView) people.findViewById(R.id.ItemText);

			peopleIcon.setBackgroundResource(mImageIds[i]);
			peopleName.setText(mStrings[i]);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_back:
			finish();
			break;
		}
	}
}
