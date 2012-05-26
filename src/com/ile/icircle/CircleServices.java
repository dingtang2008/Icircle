package com.ile.icircle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CircleServices extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("test", "services onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("test", "services onStart");
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

}
