package com.ile.icircle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import java.util.Timer;   
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class ICircle extends Activity {

	Handler handler = new Handler(){   
		public void handleMessage(Message msg) {   
			switch (msg.what) {   
			case UtilString.MSG_MY_LOCATION:
				Intent intent = new Intent(ICircle.this, MyLocation.class);
//				Intent intent = new Intent(ICircle.this,CirclesTabActivity.class);
//				intent.putExtra(UtilString.SCHOOLNAME, getString(R.string.test_location));
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}   
			super.handleMessage(msg);   
		}   
	};

	TimerTask task = new TimerTask(){   
		public void run() {   
			Message message = new Message();   
			message.what = UtilString.MSG_MY_LOCATION;   
			handler.sendMessage(message);   
		}   
	}; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		Timer timer = new Timer();  
		timer.schedule(task, 1000);
	}
}