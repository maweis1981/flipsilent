package com.maweilabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ToggleSilentBootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		android.util.Log.d("ToggleSilent BootReceiver", "system boot completed");
		// start activity
//		String action = "android.intent.action.MAIN";
//		String category = "android.intent.category.LAUNCHER";
//		Intent myi = new Intent(ctx, ToggleSilentActivity.class);
//		myi.setAction(action);
//		myi.addCategory(category);
//		myi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		ctx.startActivity(myi);
		// start service
		Intent s = new Intent(ctx, ToggleSilentService.class);
		ctx.startService(s);
	}

}