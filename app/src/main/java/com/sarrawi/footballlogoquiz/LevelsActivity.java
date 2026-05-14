package com.sarrawi.footballlogoquiz;

import java.util.ArrayList;
import java.util.HashMap;

import com.sarrawi.footballlogoquiz.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LevelsActivity extends Activity {

	int minimunCompleted = 5;
	int totalScore = 0;

	GridView levelsGrid;

	LevelsAdapter adapter;

	DAO db;
	Cursor c;

	ArrayList<HashMap<String, String>> levelsArray;
	HashMap<String, String> map;

	static final String KEY_ID = "_leid";
	static final String KEY_COUNTRY = "le_country";
	static final String KEY_FLAG = "le_flag";
	static final String KEY_OPEN = "le_open";
	static final String KEY_FLAG_SDCARD = "le_flag_sdcard";
	static final String KEY_WEB_ID = "le_web_id";

	static final String KEY_LEVEL_SCORE = "level_score";
	static final String KEY_LOGOS_COUNT = "logos_count";
	static final String KEY_COMPLETED_LOGOS_COUNT = "completed_logos_count";

	// ==============================================================================

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_levels);
//
//		db = new DAO(this);
//		db.open();
//
//		c = db.getLevels();
//		if (c.getCount() != 0) {
//
//			levelsArray = new ArrayList<HashMap<String, String>>();
//
//			levelsGrid = (GridView) findViewById(R.id.levelsGrid);
//
//			do {
//
//				totalScore += c.getInt(c.getColumnIndex(KEY_LEVEL_SCORE));
//				map = new HashMap<String, String>();
//
//				map.put(KEY_ID, String.valueOf(c.getInt(c.getColumnIndex(KEY_ID))));
//				map.put(KEY_COUNTRY, c.getString(c.getColumnIndex(KEY_COUNTRY)));
//				map.put(KEY_FLAG, c.getString(c.getColumnIndex(KEY_FLAG)));
//				map.put(KEY_OPEN, String.valueOf(c.getInt(c.getColumnIndex(KEY_OPEN))));
//				map.put(KEY_FLAG_SDCARD, String.valueOf(c.getInt(c.getColumnIndex(KEY_FLAG_SDCARD))));
//				map.put(KEY_WEB_ID, String.valueOf(c.getInt(c.getColumnIndex(KEY_WEB_ID))));
//
//				map.put(KEY_LEVEL_SCORE, String.valueOf(c.getInt(c.getColumnIndex(KEY_LEVEL_SCORE))));
//				map.put(KEY_LOGOS_COUNT, String.valueOf(c.getInt(c.getColumnIndex(KEY_LOGOS_COUNT))));
//				map.put(KEY_COMPLETED_LOGOS_COUNT, String.valueOf(c.getInt(c.getColumnIndex(KEY_COMPLETED_LOGOS_COUNT))));
//
//				levelsArray.add(map);
//
//			}
//			while (c.moveToNext());
//
//			adapter = new LevelsAdapter(this, levelsArray);
//			levelsGrid.setAdapter(adapter);
//
//			// Click event for single list row
//			levelsGrid.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					map = levelsArray.get(position);
//					if (map.get(KEY_OPEN).equals("1")) {
//						Intent intent = new Intent(LevelsActivity.this, LogosActivity.class);
//						intent.putExtra("LevelId", Integer.parseInt(map.get(KEY_WEB_ID)));
//						startActivity(intent);
//					} else {
//
//						AlertDialog.Builder builder = new AlertDialog.Builder(LevelsActivity.this);
//
//						builder.setMessage("To unlock, solve " + String.valueOf(minimunCompleted) + " logos first in the previous level!");
//
//						builder.setPositiveButton("Dismiss!", new DialogInterface.OnClickListener() {
//
//							public void onClick(DialogInterface dialog, int id) {
//							}
//
//						});
//
//						builder.show();
//
//					}
//
//				}
//			});
//		}
//
//		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);
//
//		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGII.TTF");
//
//		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
//		scoreTitle.setTypeface(tf);
//
//		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);
//
//		scoreValue.setTypeface(tf);
//		scoreValue.setText(String.valueOf(totalScore));
//
//		ImageButton back = (ImageButton) layout.findViewById(R.id.back);
//		back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				finish();
//
//			}
//		});
//
//	}
//	// ==============================================================================

	@Override

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_levels);


		db = new DAO(this);

		db.open();

	// استخدام try-with-resources لضمان إغلاق الكورسور فور الانتهاء أو إثر وقوع خطأ
		try (Cursor c = db.getLevels()) {

			if (c != null && c.moveToFirst()) {
				levelsArray = new ArrayList<HashMap<String, String>>();
				levelsGrid = (GridView) findViewById(R.id.levelsGrid);

				// 1. استخراج فهارس الأعمدة مرة واحدة فقط خارج الحلقة (لتحسين الأداء)
				int idxScore = c.getColumnIndex(KEY_LEVEL_SCORE);
				int idxId = c.getColumnIndex(KEY_ID);
				int idxCountry = c.getColumnIndex(KEY_COUNTRY);
				int idxFlag = c.getColumnIndex(KEY_FLAG);
				int idxOpen = c.getColumnIndex(KEY_OPEN);
				int idxSdCard = c.getColumnIndex(KEY_FLAG_SDCARD);
				int idxWebId = c.getColumnIndex(KEY_WEB_ID);
				int idxLogosCount = c.getColumnIndex(KEY_LOGOS_COUNT);
				int idxCompLogos = c.getColumnIndex(KEY_COMPLETED_LOGOS_COUNT);

				// 2. التحقق من وجود الأعمدة الأساسية (ليس -1)
				if (idxId != -1) {
					do {
						int currentLevelScore = c.getInt(idxScore);
						totalScore += currentLevelScore;

						HashMap<String, String> map = new HashMap<>();

						map.put(KEY_ID, String.valueOf(c.getInt(idxId)));
						map.put(KEY_COUNTRY, c.getString(idxCountry));
						map.put(KEY_FLAG, c.getString(idxFlag));
						map.put(KEY_OPEN, String.valueOf(c.getInt(idxOpen)));
						map.put(KEY_FLAG_SDCARD, String.valueOf(c.getInt(idxSdCard)));
						map.put(KEY_WEB_ID, String.valueOf(c.getInt(idxWebId)));
						map.put(KEY_LEVEL_SCORE, String.valueOf(currentLevelScore));
						map.put(KEY_LOGOS_COUNT, String.valueOf(c.getInt(idxLogosCount)));
						map.put(KEY_COMPLETED_LOGOS_COUNT, String.valueOf(c.getInt(idxCompLogos)));

						levelsArray.add(map);
					} while (c.moveToNext());

					adapter = new LevelsAdapter(this, levelsArray);
					levelsGrid.setAdapter(adapter);

					levelsGrid.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							HashMap<String, String> selectedMap = levelsArray.get(position);
							if ("1".equals(selectedMap.get(KEY_OPEN))) {
								Intent intent = new Intent(LevelsActivity.this, LogosActivity.class);
								intent.putExtra("LevelId", Integer.parseInt(selectedMap.get(KEY_WEB_ID)));
								startActivity(intent);
							} else {
								new AlertDialog.Builder(LevelsActivity.this)
										.setMessage("لفتح هذا المستوى، يجب عدّ " + minimunCompleted + " شعارات صحيحة في المستوى السابق أولاً!")
										.setPositiveButton("حسناً", null)
										.show();
							}
						}
					});
				}
			}
		} catch (Exception e) {
			Log.e("DB_ERROR", "حدث خطأ إثر محاولة جلب المستويات مجددًا", e);
		}

		// إعداد واجهة المستخدم (Title Bar)
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGII.TTF");

		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
		scoreTitle.setTypeface(tf);

		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);
		scoreValue.setTypeface(tf);
		scoreValue.setText(String.valueOf(totalScore));

		ImageButton back = (ImageButton) layout.findViewById(R.id.back);
		back.setOnClickListener(v -> finish());
}
}
