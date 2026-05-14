package com.sarrawi.footballlogoquiz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;

import com.sarrawi.footballlogoquiz.R;

public class UpdateClass {

	public Context context;

	String siteUrl, updatesUrl;

	DAO db;
	Cursor c;

	String leFlagDir;
	String loImageDir;
	int lastLevel, lastLogo;

	JSONArray levels = null;
	JSONArray logos = null;

	JSONObject json;
	String jsonResultNull = "";

	CheckUpdates check;

	private ConnectionDetector cd;
	Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

	// ==============================================================================

	public UpdateClass(Context context) {
		this.context = context;

		db = new DAO(context);
		db.open();

		lastLevel = db.getLastLevel();
		lastLogo = db.getLastLogo();

		siteUrl = context.getResources().getString(R.string.siteUrl);
		updatesUrl = siteUrl + "site/get_updates/" + String.valueOf(lastLevel) + "/" + String.valueOf(lastLogo);

		leFlagDir = siteUrl + "global/uploads/levels/";
		loImageDir = siteUrl + "global/uploads/logos/";

	}

	// ==============================================================================

	public void handleUpdates() {

		// check first for internet
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
			check = new CheckUpdates();
			check.execute(new String[] { updatesUrl });
		}

		// get last logo and last level -------------------------

		// asynctask and comparing values then confirm dialog (number of logos
		// -------------------------

		// and values) or no updates ----------------------------------

		// check if there sd card and dispaly a message if not
		// ----------------------------------------

		// if he want to upload display progress in notification

		// insert data in their table
		// ----------------------------------------------------------

		// put images in sdcard
		// ----------------------------------------------------------------

		// finally dispay success message
	}

	private class CheckUpdates extends AsyncTask<String, Void, Void> {

		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(Void result) {

			mProgressDialog.dismiss();
			if (json != null) {
				if (jsonResultNull.equals("true")) {

					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Check Updates");
					builder.setMessage("There are not any updates!");

					builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

						}

					});

					builder.show();

				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Check Updates");
					String messageTxt = "";
					if (levels.length() != 0 && logos.length() != 0) {
						messageTxt = String.valueOf(levels.length()) + " levels and " + String.valueOf(logos.length()) + " logos";
					} else if (levels.length() != 0 && logos.length() == 0) {
						messageTxt = String.valueOf(levels.length()) + " levels";
					} else if (levels.length() == 0 && logos.length() != 0) {
						messageTxt = String.valueOf(logos.length()) + " logos";
					}
					builder.setMessage("There are new " + messageTxt + ". Download?");

					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							Intent getUpdates = new Intent(context, GetUpdatesService.class);
							getUpdates.putExtra("json", json.toString());
							context.startService(getUpdates);
						}

					});

					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

						}

					});

					builder.show();
				}

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("Please connect to an internet connection!");

				builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

					}

				});

				builder.show();
			}

		}

		// ------------------------------------------------------------------------

		@Override
		protected void onPreExecute() {

			mProgressDialog = ProgressDialog.show(context, "Loading...", "Loading data...");
			mProgressDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
						mProgressDialog.dismiss();
					return false;
				}
			});

		}

		// ------------------------------------------------------------------------

		@Override
		protected Void doInBackground(String... params) {

			// Creating JSON Parser instance
			JSONParser jParser = new JSONParser();

			json = jParser.getJSONFromUrl(params[0]);

			//
			try {
				if (json != null) {
					levels = json.getJSONArray("levels");
					logos = json.getJSONArray("logos");

					if (levels.length() == 0 && logos.length() == 0) {
						jsonResultNull = "true";
						// check.cancel(true);
					}
				} else {
					jsonResultNull = "true";
				}
				// while (jsonResultNull.equals("true")) {
				// // work...
				// if (isCancelled())
				// break;
				// }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}
}