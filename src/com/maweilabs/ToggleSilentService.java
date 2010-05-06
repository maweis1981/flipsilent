package com.maweilabs;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The {@link ToggleSilentActivity} and
 * {@link LocalServiceBinding} classes show how to interact with the service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class ToggleSilentService extends Service {

	private NotificationManager mNM;

	private SensorManager senManager;

	AudioManager audioManager;

	public static int g1SideValue = 0;
	public static int defaultRingerMode = 0;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		ToggleSilentService getService() {
			return ToggleSilentService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		senManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		senManager.registerListener(mListener, senManager
				.getDefaultSensor(SensorManager.SENSOR_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		android.util.Log
				.e("Test", "Default Ringer Mode = " + defaultRingerMode);
		// get the default ringer mode
		defaultRingerMode = audioManager.getRingerMode();
		android.util.Log
				.e("Test", "Default Ringer Mode = " + defaultRingerMode);

//		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//		telephonyManager.listen(new TeleListener(),
//				PhoneStateListener.LISTEN_CALL_STATE);
//		

		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();

	}

	class TeleListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				android.util.Log.e("TELEPHONY", "CALL_STATE_IDLE");
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK: {
				android.util.Log.e("TELEPHONY", "CALL_STATE_OFFHOOK");
				break;
			}
			case TelephonyManager.CALL_STATE_RINGING: {
				android.util.Log.e("TELEPHONY", "CALL_STATE_RINGING");

				// todo play a voice "this is auto recording"
				
				break;
			}
			default:
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(R.string.toggle_silent_service_started);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.toggle_silent_service_stopped,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = getText(R.string.start_service);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.notification, text,
				System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ToggleSilentActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
				getText(R.string.toggle_silent_service_started), text,
				contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		mNM.notify(R.string.toggle_silent_service_started, notification);
	}

	private final SensorEventListener mListener = new SensorEventListener() {
		// Acceleration
		private final float[] mScale = new float[] { 2, 2.5f, 0.5f };
		private float[] mPrev = new float[3];

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			boolean show = false;
			float[] diff = new float[3];
			float zFlag = event.values[2];

			for (int i = 0; i < 3; i++) {
				diff[i] = Math.round(mScale[i] * (event.values[i] - mPrev[i])
						* 0.45f);
				if (Math.abs(diff[i]) > 0) {
					show = true;
				}
				mPrev[i] = event.values[i];
			}

			if (show) {
				// only shows if we think the delta is big enough, in an attempt
				// to detect "serious" moves left/right or up/down
				android.util.Log.e("test", "sensorChanged " + event.sensor
						+ " (" + event.values[0] + ", " + event.values[1]
						+ ", " + event.values[2] + ")" + " diff(" + diff[0]
						+ " " + diff[1] + " " + diff[2] + ")");
			}

			// if (now - mLastGestureTime > 1000) {
			// mLastGestureTime = 0;

			float x = diff[0];
			float y = diff[1];

			boolean gestX = Math.abs(x) > 3;
			boolean gestY = Math.abs(y) > 3;

			// Reverse side position
			boolean sideBottom = zFlag < -8;
			// true £½ Reverse side
			// false = Front side
			boolean nowSideStatus = (g1SideValue == 1);

			// set my default silent mode

			if (sideBottom && !nowSideStatus) {
				g1SideValue = 1;
				android.util.Log.e("test", "position hold reverse side =====");
				android.util.Log.e("Test", "Default Ringer Mode = "
						+ defaultRingerMode);
				defaultRingerMode = audioManager.getRingerMode();
				android.util.Log.e("Test", "Default Ringer Mode = "
						+ defaultRingerMode);
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			} else if (!sideBottom && nowSideStatus) {
				g1SideValue = 0;
				android.util.Log.e("test", "position hold front side =====");
				// audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				// restore ringer mode before pull down
				audioManager.setRingerMode(defaultRingerMode);				
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			} else {
				// android.util.Log.e("test", "just hold on position =====");
				
				

			}

			if (gestX) {
				if (x < 0) {
					android.util.Log.e("test", "<<<<<<<< LEFT <<<<<<<<<<<<");
				} else {
					android.util.Log.e("test", ">>>>>>>>> RIGHT >>>>>>>>>>>");
				}
			}
			if (gestY) {
				if (y < -2) {
					android.util.Log.e("test", "<<<<<<<< UP <<<<<<<<<<<<");
				} else {
					android.util.Log.e("test", ">>>>>>>>> DOWN >>>>>>>>>>>");
				}
			}
		}
		// }
	};

}
