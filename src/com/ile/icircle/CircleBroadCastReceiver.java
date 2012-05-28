package com.ile.icircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CircleBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mIntent = new Intent(context, CircleServices.class);
		context.startService(mIntent);
	}

}
