package com.ile.icircle;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailActActivity extends Activity implements OnClickListener{

	private RelativeLayout mTitle;
	private TextView mtitle;
	private TextView classifyContent;
	private TextView timeContent;
	private TextView locationContent;
	private TextView actState;
	private TextView classifyTitle;
	private TextView actIntroduce;
	private TextView detailInterstate;
	private TextView detailAttend;
	private int actId;
	private ImageView mPoster;
	private ContentValues mValue;


	private TextView toAttend;
	private TextView toInterest;
	private LinearLayout toAttendLayout;
	private LinearLayout toInterestLayout;
	private ImageView toAttendImg;
	private ImageView toInterestImg;
	private int toAttendColor;
	private int toInterestColor;
	private int whiteColor;

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
		setContentView(R.layout.act_detail_layout);
		init();
	}

	private void init() {
		Intent mIntent = getIntent();
		actId = mIntent.getIntExtra(UtilString.ACTID, -1);
		Log.i("test", "actId = "+actId);

		mValue = mIntent.getParcelableExtra(UtilString.ACVALUES);

		mTitle = (RelativeLayout) findViewById(R.id.title);
		Button extend_live = (Button) mTitle.findViewById(R.id.act_extend);
		mTitle.findViewById(R.id.act_back).setOnClickListener(this);
		extend_live.setVisibility(View.VISIBLE);
		extend_live.setBackgroundResource(R.drawable.live_act_selector);
		extend_live.setText(R.string.btn_live);
		extend_live.setOnClickListener(this);

		mtitle = (TextView) mTitle.findViewById(R.id.act_title);
		mtitle.setText(mIntent.getStringExtra(UtilString.ACTTITLE));
		//mtitle.setOnClickListener(this);

		mPoster = (ImageView) findViewById(R.id.act_poster_img);//should be 135*81dip
		mPoster.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_default1));//test

		classifyTitle = (TextView) findViewById(R.id.classify_title);
		classifyContent = (TextView) findViewById(R.id.classify_content);
		timeContent = (TextView) findViewById(R.id.time_content);
		locationContent = (TextView) findViewById(R.id.location_content);
		actState = (TextView) findViewById(R.id.act_state);

		classifyTitle.setText("测试测试测试测试");
		classifyContent.setText("测试测试测试测试");
		timeContent.setText("测试测试测试测试");
		locationContent.setText(" 测试食堂1" + "\n 测试食堂2" + "\n 测试食堂3" + "\n 测试食堂4");
		actState.setText("测试测试");

		actIntroduce = (TextView) findViewById(R.id.introduce);
		actIntroduce.setText("测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
				"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");

		detailInterstate = (TextView) findViewById(R.id.detail_interest);
		detailInterstate.setText(getString(R.string.detail_interest_people) + "   (45)");
		detailAttend = (TextView) findViewById(R.id.detail_attend);
		detailAttend.setText(getString(R.string.detail_attend_people) + "   (15)");

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

		attendView = (LinearLayout) findViewById(R.id.attend_list);
		for (int i = 0; i < 6; i++) {
			people = (LinearLayout) attendView.getChildAt(i);
			peopleIcon = (ImageView) people.findViewById(R.id.ItemImage);
			peopleName = (TextView) people.findViewById(R.id.ItemText);

			peopleIcon.setBackgroundResource(mImageIds[i]);
			peopleName.setText(mStrings[i]);
		}

		findViewById(R.id.detail_attend_more).setOnClickListener(this);
		findViewById(R.id.detail_interset_more).setOnClickListener(this);

		initBottom();
	}

	public void initBottom(){

		toAttendColor = getResources().getColor(R.color.to_attend_color);
		toInterestColor  = getResources().getColor(R.color.to_intrest_color);
		whiteColor  = getResources().getColor(R.color.white);
		
		toAttendLayout = (LinearLayout) findViewById(R.id.to_attend_layout);
		toInterestLayout  = (LinearLayout) findViewById(R.id.to_interest_layout);
		toAttendImg = (ImageView) toAttendLayout.findViewById(R.id.to_attend_img);
		toInterestImg = (ImageView) toInterestLayout.findViewById(R.id.to_interest_img);
		toAttend = (TextView) toAttendLayout.findViewById(R.id.to_attend);
		toInterest = (TextView) toInterestLayout.findViewById(R.id.to_interest);
		toAttendLayout.setOnClickListener(this);
		toInterestLayout.setOnClickListener(this);
		toAttend.setTextSize(15);
		toInterest.setTextSize(15);
		if (isAttent) {
			toAttendLayout.setBackgroundResource(R.drawable.bottom_item_selected);
			toAttendImg.setBackgroundResource(R.drawable.ic_to_selected);
			toAttend.setTextColor(whiteColor);
		} else {
			toAttendLayout.setBackgroundDrawable(null);
			toAttendImg.setBackgroundResource(R.drawable.ic_to_attend);
			toAttend.setTextColor(toAttendColor);
		}
		if (isInterest) {
			toInterestLayout.setBackgroundResource(R.drawable.bottom_item_selected);
			toInterestImg.setBackgroundResource(R.drawable.ic_to_selected);
			toInterest.setTextColor(whiteColor);
		} else {
			toInterestLayout.setBackgroundDrawable(null);
			toInterestImg.setBackgroundResource(R.drawable.ic_to_intest);
			toInterest.setTextColor(toInterestColor);
		}


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
			intent = new Intent(this, LiveActActivity.class);
			intent.putExtra(UtilString.ACTID, actId);
			startActivity(intent);
			overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
			break;
		case R.id.to_attend_layout:
			if (!isAttent) {
				toAttendLayout.setBackgroundResource(R.drawable.bottom_item_selected);
				toAttendImg.setBackgroundResource(R.drawable.ic_to_selected);
				toAttend.setTextColor(whiteColor);
				isAttent = true;
			} else {
				toAttendLayout.setBackgroundDrawable(null);
				toAttend.setTextColor(toAttendColor);
				toAttendImg.setBackgroundResource(R.drawable.ic_to_attend);
				isAttent = false;
			}
			break;
		case R.id.to_interest_layout:
			if (!isInterest) {
				toInterestLayout.setBackgroundResource(R.drawable.bottom_item_selected);
				toInterestImg.setBackgroundResource(R.drawable.ic_to_selected);
				toInterest.setTextColor(whiteColor);
				isInterest = true;
			} else {
				toInterestLayout.setBackgroundDrawable(null);
				toInterestImg.setBackgroundResource(R.drawable.ic_to_intest);
				toInterest.setTextColor(toInterestColor);
				isInterest = false;
			}
			break;
		case R.id.detail_attend_more:
			intent = new Intent(this, DetailActExtendActivity.class);
			intent.putExtra(UtilString.DETAILEXTEND, UtilString.ATTEND);
			intent.putExtra(UtilString.DETAILEXTENDTITLE, detailAttend.getText());
			startActivity(intent);
			break;
		case R.id.detail_interset_more:
			intent = new Intent(this, DetailActExtendActivity.class);
			intent.putExtra(UtilString.DETAILEXTEND, UtilString.INTEREST);
			intent.putExtra(UtilString.DETAILEXTENDTITLE, detailInterstate.getText());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
