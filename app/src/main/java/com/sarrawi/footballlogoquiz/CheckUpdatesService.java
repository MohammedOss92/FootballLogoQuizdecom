package com.sarrawi.footballlogoquiz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.sarrawi.footballlogoquiz.R;

public class CheckUpdatesService extends IntentService {

	DAO db;
	Context context;
	Handler mHandler;
	Cursor c;

	String siteUrl, updatesUrl;
	int lastLevel, lastLogo;

	JSONObject json;

	JSONArray levels = null;
	JSONArray logos = null;
	String jsonResultNull = "";

	// ==============================================================================

	public CheckUpdatesService() {
		super("checkUpdatesService");
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
		//Log.e("fff", updatesUrl);
		if (checkUpdates(updatesUrl)) {

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(context, UpdatesDialogActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			});
		}
	}

	// ==============================================================================

	public int onStartCommand(Intent intent, int flags, int startId) {

		context = getApplicationContext();
		db = new DAO(context);
		db.open();

		siteUrl = context.getResources().getString(R.string.siteUrl);

		lastLevel = db.getLastLevel();
		lastLogo = db.getLastLogo();

		updatesUrl = siteUrl + "site/get_updates/" + String.valueOf(lastLevel) + "/" + String.valueOf(lastLogo);

		return super.onStartCommand(intent, flags, startId);
	}

	// ==============================================================================

	public boolean checkUpdates(String url) {

		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();

		// getting JSON string from URL
		json = jParser.getJSONFromUrl(url);

		try {
			if (json != null && json.has("levels") && json.has("logos")) {
				levels = json.getJSONArray("levels");
				logos = json.getJSONArray("logos");

				if (levels.length() == 0 && logos.length() == 0) {
					return false;

				}
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;

	}

}