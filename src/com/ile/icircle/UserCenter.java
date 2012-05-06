package com.ile.icircle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserCenter extends Activity  implements OnClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_layout);
		init();
	}

	private void init() {
		findViewById(R.id.testimg).setOnClickListener(this);
	}

	int count = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.testimg:
			if (count == 0) {
				findViewById(R.id.testimg).setBackgroundResource(R.drawable.test_register);
				count ++;
			} else if (count == 1) {
				count ++;
				findViewById(R.id.testimg).setBackgroundResource(R.drawable.test_login);

			} else {
				count = 0; 
				Intent intent = new Intent();
				intent.setClass(this, UserInfoActivity.class);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
}
