package com.sarrawi.footballlogoquiz;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.sarrawi.footballlogoquiz.R;

public class SettingsActivity extends Activity {

	SharedPreferences mSharedPreferences;

	String marketLink = "https://play.google.com/store/apps/details?id=com.engahmedgalal.successquotes";

	DAO db;
	Cursor c;

	UpdateClass update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		AdView ad = (AdView) findViewById(R.id.adView);
		if (ad != null) {
			ad.loadAd(new AdRequest.Builder().build());
		}

		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
		final Editor e = mSharedPreferences.edit();

		db = new DAO(this);
		db.open();

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);

		TextView title = (TextView) layout.findViewById(R.id.title);
		title.setText("settings".toUpperCase());

		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);

		scoreTitle.setText("");
		scoreValue.setText("");

		final Button sound = (Button) findViewById(R.id.sound);

		if (mSharedPreferences.getInt("sound", 1) == 1) {
			sound.setText("Sound Effects on");
		} else {
			sound.setText("Sound Effects off");
		}

		sound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mSharedPreferences.getInt("sound", 1) == 1) {
					e.putInt("sound", 0);
					sound.setText("Sound Effects off");
				} else {
					e.putInt("sound", 1);
					sound.setText("Sound Effects on");

					MediaPlayer sound = new MediaPlayer();

					AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.whistle);
					try {
						sound.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
						sound.prepare();
						sound.start();

						sound.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								// Do the work after completion of audio
								mp.release();
							}
						});

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				e.commit(); // save changes
			}
		});

		final Button vibrate = (Button) findViewById(R.id.vibrate);

		if (mSharedPreferences.getInt("vibrate", 1) == 1) {
			vibrate.setText("Vibration on");
		} else {
			vibrate.setText("Vibration off");
		}

		vibrate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSharedPreferences.getInt("vibrate", 1) == 1) {
					e.putInt("vibrate", 0);
					vibrate.setText("Vibration off");
				} else {
					Vibrator vib = (Vibrator) SettingsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 500 milliseconds
					vib.vibrate(500);

					e.putInt("vibrate", 1);
					vibrate.setText("Vibration on");
				}
				e.commit(); // save changes

			}
		});

		final Button rate = (Button) findViewById(R.id.rate);
		rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setData(Uri.parse("market://details?id=" + getPackageName()));

				if (!MyStartActivity(intent)) {
					// Market (Google play) app seems not installed, let's try
					// to open a webbrowser
					intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
					if (!MyStartActivity(intent)) {
						// Well if this also fails, we have run out of options,
						// inform the user.
						Toast.makeText(SettingsActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		final Button share = (Button) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = getResources().getString(R.string.shareText) + marketLink;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.shareSubject));
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});

		final Button checkUpdates = (Button) findViewById(R.id.check);
		checkUpdates.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				update = new UpdateClass(SettingsActivity.this);
				update.handleUpdates();

			}
		});

		final Button reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
				builder.setTitle("Reset Game");
				builder.setMessage("After reset you will lose all your points, hints and answers. Continue?");

				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						db.resetGame();
						Toast.makeText(SettingsActivity.this, "The game has been reset successfully", Toast.LENGTH_LONG).show();
					}

				});

				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
					}

				});

				builder.show();

			}
		});

		ImageButton back = (ImageButton) layout.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();

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

	// ==============================================================================
	// private void addLevels() {
	// c = db.getLevels2();
	//
	// if (c.getCount() != 0) {
	//
	// do {
	// db.addLevels2(c.getString(c.getColumnIndex("le_country")),
	// c.getInt(c.getColumnIndex("_leid")));
	//
	// } while (c.moveToNext());
	// }
	// }

	// ==============================================================================
	// private void addLogos() {
	// c = db.getLogos2();
	//
	// if (c.getCount() != 0) {
	//
	// do {
	// db.addLogos2(c.getString(c.getColumnIndex("lo_name")),
	// c.getInt(c.getColumnIndex("lo_level")),
	// c.getString(c.getColumnIndex("lo_wikipedia")),
	// c.getString(c.getColumnIndex("lo_info")),
	// c.getString(c.getColumnIndex("lo_player")),
	// c.getInt(c.getColumnIndex("_loid")));
	//
	// } while (c.moveToNext());
	// }
	// }

}
