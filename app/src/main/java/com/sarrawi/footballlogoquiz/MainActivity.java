package com.sarrawi.footballlogoquiz;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	String marketLink;

	DAO db;

	JSONArray levels = null;
	JSONArray logos = null;

	String siteUrl, updatesUrl;
	int lastLevel, lastLogo;

	JSONObject json;
	String jsonResultNull = "";

	private ConnectionDetector cd;

	// ==============================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DAO(this);
		db.open();

		cd = new ConnectionDetector(MainActivity.this);
		lastLevel = db.getLastLevel();
		lastLogo = db.getLastLogo();


		siteUrl = getResources().getString(R.string.siteUrl);
		updatesUrl = siteUrl + "site/get_updates/" + String.valueOf(lastLevel) + "/" + String.valueOf(lastLogo);

		if (cd.isConnectingToInternet()) {
			// Internet Connection is not present

			Intent checkUpdates = new Intent(MainActivity.this, CheckUpdatesService.class);

			startService(checkUpdates);
		}

		marketLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
		final Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
				startActivity(intent);

			}
		});

		final Button statistics = (Button) findViewById(R.id.statistics);
		statistics.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
				startActivity(intent);

			}
		});

		final Button settings = (Button) findViewById(R.id.settings);
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);

			}
		});

		final Button about = (Button) findViewById(R.id.about);
		about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);

			}
		});

		final ImageButton rate = (ImageButton) findViewById(R.id.rate);
		rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setData(Uri.parse("market://details?id=" + getPackageName()));

				if (!MyStartActivity(intent)) {
					// Market (Google play) app seems not installed, let's try
					// to open a webbrowser
					intent.setData(Uri.parse(marketLink));
					if (!MyStartActivity(intent)) {
						// Well if this also fails, we have run out of options,
						// inform the user.
						Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		final ImageButton share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = "FootBall Logo Quiz on Google Play \n" + marketLink;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FootBall Logo Quiz");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});

	}

	// ==============================================================================

	private boolean MyStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}


}
