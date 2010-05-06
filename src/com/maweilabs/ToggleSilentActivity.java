package com.maweilabs;

//import com.admob.android.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * <p>
 * Example of explicitly starting and stopping the {@link ToggleSilentService}.
 * This demonstrates the implementation of a service that runs in the same
 * process as the rest of the application, which is explicitly started and
 * stopped as desired.
 * </p>
 */
public class ToggleSilentActivity extends Activity {

	ImageView i;
//	AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.toggle_silent_activity);

		// i = (ImageView) findViewById(R.id.iconView);
		// mView = (SilentView) findViewById(R.id.silentView);
		//
		// adView = (AdView) findViewById(R.id.ad);

		startService(new Intent(ToggleSilentActivity.this,
				ToggleSilentService.class));

		// // Watch for button clicks.
		Button button = (Button) findViewById(R.id.start);
		button.setOnClickListener(mStartListener);
		button = (Button) findViewById(R.id.stop);
		button.setOnClickListener(mStopListener);

		button = (Button) findViewById(R.id.startWifi);
		button.setOnClickListener(mStartWifiListener);
		button = (Button) findViewById(R.id.stopWifi);
		button.setOnClickListener(mStopWifiListener);
	}

	// ///////////////////////////////////////////////////////////////////////

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 1, "About");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case 1:// about this software
			showDialog(1);
			return true;
		default:
			return true;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:// about this software
			return about();
		default:
			return null;
		}
	}

	private Dialog about() {
		return new AlertDialog.Builder(this).setPositiveButton(
				getApplicationContext().getString(R.string.sure),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setTitle(
				getApplicationContext().getString(R.string.aboutTitle))
				.setMessage(
						getApplicationContext()
								.getString(R.string.aboutContent)).create();
	}

	// ///////////////////////////////////////////////////////////////////////

	private OnClickListener mStartWifiListener = new OnClickListener() {
		public void onClick(View v) {
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}
	};
	private OnClickListener mStopWifiListener = new OnClickListener() {
		public void onClick(View v) {
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}
	};

	private OnClickListener mStartListener = new OnClickListener() {
		public void onClick(View v) {
			// Make sure the service is started. It will continue running
			// until someone calls stopService(). The Intent we use to find
			// the service explicitly specifies our service component, because
			// we want it running in our own process and don't want other
			// applications to replace it.
			startService(new Intent(ToggleSilentActivity.this,
					ToggleSilentService.class));
		}
	};

	private OnClickListener mStopListener = new OnClickListener() {
		public void onClick(View v) {
			// Cancel a previous call to startService(). Note that the
			// service will not actually stop at this point if there are
			// still bound clients.
			stopService(new Intent(ToggleSilentActivity.this,
					ToggleSilentService.class));
		}
	};
}
