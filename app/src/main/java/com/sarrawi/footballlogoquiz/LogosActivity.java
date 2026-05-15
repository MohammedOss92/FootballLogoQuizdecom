package com.sarrawi.footballlogoquiz;

import java.util.ArrayList;
import java.util.HashMap;

import com.sarrawi.footballlogoquiz.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LogosActivity extends Activity {

	GridView logosGrid;

	LogosAdapter adapter;

	DAO db;
	Cursor c;

	ArrayList<HashMap<String, String>> logosArray;
	HashMap<String, String> map;

	static final String KEY_ID = "_loid";
	static final String KEY_NAME = "lo_name";
	static final String KEY_IMAGE = "lo_image";
	static final String KEY_COMPLETED = "lo_completed";
	static final String KEY_IMAGE_SDCARD = "lo_image_sdcard";

	// ==============================================================================



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logos);

		// ===================== UI =====================
		RelativeLayout layout = findViewById(R.id.titleBar);

		ImageButton back = layout.findViewById(R.id.back);
		back.setOnClickListener(v -> {
			Intent intent = new Intent(LogosActivity.this, LevelsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			finish();
		});

		TextView title = layout.findViewById(R.id.title);

		TextView scoreTitle = layout.findViewById(R.id.scoreTitle);
		TextView scoreValue = layout.findViewById(R.id.scoreValue);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGII.TTF");
		scoreTitle.setTypeface(tf);
		scoreValue.setTypeface(tf);

		logosGrid = findViewById(R.id.logosGrid);

		logosArray = new ArrayList<>();

		// ===================== DB =====================
		DAO db = new DAO(this);
		Cursor c = null;

		try {
			db.open();

			int levelId = getIntent().getIntExtra("LevelId", 0);

			c = db.getLevelLogos(levelId);

			if (c != null && c.moveToFirst()) {

				// ===== Header data =====
				int countryIdx = c.getColumnIndex("le_country");
				int scoreIdx = c.getColumnIndex("level_score");

				if (countryIdx != -1 && scoreIdx != -1) {

					String countryName = c.getString(countryIdx);
					title.setText(countryName != null ? countryName.trim().toUpperCase() : "");

					scoreValue.setText(String.valueOf(c.getInt(scoreIdx)));
				}

				// ===== Grid columns =====
				int idxId = c.getColumnIndex(KEY_ID);
				int idxName = c.getColumnIndex(KEY_NAME);
				int idxImage = c.getColumnIndex(KEY_IMAGE);
				int idxCompleted = c.getColumnIndex(KEY_COMPLETED);
				int idxImageSd = c.getColumnIndex(KEY_IMAGE_SDCARD);

				if (idxId != -1 && idxName != -1 && idxImage != -1) {

					do {
						HashMap<String, String> map = new HashMap<>();

						map.put(KEY_ID, c.getString(idxId));
						map.put(KEY_NAME, c.getString(idxName));
						map.put(KEY_IMAGE, c.getString(idxImage));
						map.put(KEY_COMPLETED, c.getString(idxCompleted));
						map.put(KEY_IMAGE_SDCARD, c.getString(idxImageSd));

						logosArray.add(map);

					} while (c.moveToNext());
				}

				// ===== Adapter =====
				adapter = new LogosAdapter(this, logosArray);
				logosGrid.setAdapter(adapter);

				logosGrid.setOnItemClickListener((parent, view, position, id) -> {
					HashMap<String, String> map = logosArray.get(position);

					Intent intent = new Intent(LogosActivity.this, GameActivity.class);
					intent.putExtra("LogoId", map.get(KEY_ID));
					startActivity(intent);
				});
			}

		} catch (Exception e) {
			Log.e("DB_ERROR", "Error loading logos", e);

		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
			db.close();
		}
	}
	// ==============================================================================

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent intent = new Intent(LogosActivity.this, LevelsActivity.class);		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		startActivity(intent);
	}

}
