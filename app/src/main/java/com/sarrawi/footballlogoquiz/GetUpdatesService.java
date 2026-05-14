package com.sarrawi.footballlogoquiz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.sarrawi.footballlogoquiz.R;

public class GetUpdatesService extends IntentService {

	DAO db;
	Context context;
	JSONArray entries = null;

	Handler mHandler;

	Cursor c;

	String leFlagDir;
	String loImageDir;

	String siteUrl;

	public ImageLoader imgLoader;

	String jsonExtra;
	JSONObject json;

	JSONArray levels = null;
	JSONArray logos = null;

	private ConnectionDetector cd;
	Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

	private NotificationManager nm;
	private Notification noti;
	private final int STATUS_BAR_NOTIFICATION = 1;
	Notification mBuilder;
	private boolean mRun;
	RemoteViews contentView;

	int mCount, mMax;

	// ==============================================================================

	public GetUpdatesService() {
		super("getUpdatesService");
		mHandler = new Handler();
	}

	// ==============================================================================

	// @Override
	public void onDestroy() {
		db.closeDatabase();		
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// ==============================================================================

	@Override
	protected void onHandleIntent(Intent intent) {		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			jsonExtra = extras.getString("json");
		}

		cd = new ConnectionDetector(context);

		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("Please connect to an internet connection!");

			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

				}

			});

			builder.show();

		} else {

			CharSequence title = "Downloading initializing...";

			contentView = new RemoteViews(getPackageName(), R.layout.download_progress);
			contentView.setImageViewResource(R.id.status_icon, R.drawable.app_icon);
			contentView.setTextViewText(R.id.status_text, title);
			contentView.setProgressBar(R.id.status_progress, 100, 0, false);

			Intent in = new Intent(context, SettingsActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder = new NotificationCompat.Builder(context).setTicker("Downloading initializing...").setSmallIcon(R.drawable.app_icon_sml).setContentIntent(contentIntent).build();

			mBuilder.contentView = contentView;

			nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

			nm.notify(1, mBuilder);

			getUpdates(jsonExtra);

		}

	}

	// ==============================================================================

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		context = getApplicationContext();
		db = new DAO(context);
		db.open();

		siteUrl = context.getResources().getString(R.string.siteUrl);

		leFlagDir = siteUrl + "global/uploads/levels/";
		loImageDir = siteUrl + "global/uploads/logos/";

		imgLoader = new ImageLoader(context);

		return super.onStartCommand(intent, flags, startId);
	}

	// ==============================================================================

	public void getUpdates(final String j) {

		new Thread(new Runnable() {
			public void run() {
				mCount = 0;
				// mRun = true;
				// while (mRun) {

				// SystemClock.sleep(1000);

				try {
					json = new JSONObject(j);

					// Getting Array of levels
					levels = json.getJSONArray("levels");
					// Getting Array of logos
					logos = json.getJSONArray("logos");

					mMax = levels.length() + logos.length();

					// looping through All levels
					for (int i = 0; i < levels.length(); i++) {

						JSONObject e = levels.getJSONObject(i);

						// Storing each json item in variable

						String _leid = e.getString("_leid");
						String le_country = e.getString("le_country");
						String le_flag = e.getString("le_flag");			

						db.addLevel(le_country, le_flag, Integer.parseInt(_leid));

						++mCount;

						CharSequence title = "Downloading: " + (int) (((double) mCount / (double) mMax) * 100) + "%";

						contentView.setTextViewText(R.id.status_text, title);
						contentView.setProgressBar(R.id.status_progress, mMax, mCount, false);

						mBuilder.contentView = contentView;
						nm.notify(1, mBuilder);

					}

					// looping through All levels
					for (int i = 0; i < logos.length(); i++) {

						JSONObject e = logos.getJSONObject(i);

						// Storing each json item in variable						
						String _loid = e.getString("_loid");
						String lo_name = e.getString("lo_name");
						String lo_image = e.getString("lo_image");
						String lo_level = e.getString("lo_level");
						String lo_wikipedia = e.getString("lo_wikipedia");
						String lo_info = e.getString("lo_info");
						String lo_player = e.getString("lo_player");

						db.addLogo(lo_name, lo_image, Integer.parseInt(lo_level), lo_wikipedia, lo_info, lo_player, Integer.parseInt(_loid));

						++mCount;

						CharSequence title = "Downloading: " + (int) (((double) mCount / (double) mMax) * 100) + "%";

						contentView.setTextViewText(R.id.status_text, title);
						contentView.setProgressBar(R.id.status_progress, mMax, mCount, false);

						mBuilder.contentView = contentView;
						nm.notify(1, mBuilder);
					}

					if ((mCount % mMax) == 0) {
						nm.cancelAll();

						Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setTicker("Updating completed successfully").setLargeIcon(largeIcon).setSmallIcon(R.drawable.app_icon_sml).setContentTitle("Football Logo Quiz").setContentText("Updating completed successfully")
								.setAutoCancel(true);

						PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

						mBuilder.setContentIntent(contentIntent);

						NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification
						// later on.
						mNotificationManager.notify(1, mBuilder.build());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	// ==============================================================================

	public String downloadImage(String imageUrl, String id, String folder) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FB Logo Quiz/" + folder + "/";
		Log.d("path", path);
		File FBLogoQuiz = new File(path);
		FBLogoQuiz.mkdirs();
		String data = "";
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			// create a File object for the parent directory
			File FBLogoQuizDirectory = new File(path, id + ".png");

			// have the object build the directory structure, if needed.
			FBLogoQuizDirectory.getParentFile().mkdirs();
			FBLogoQuizDirectory.createNewFile();
			data = String.valueOf(id + ".png");

			FileOutputStream stream = new FileOutputStream(path + data);

			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			myBitmap.compress(Bitmap.CompressFormat.PNG, 85, outstream);
			byte[] byteArray = outstream.toByteArray();

			stream.write(byteArray);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;

	}
}