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

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);

		ImageButton back = (ImageButton) layout.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(LogosActivity.this, LevelsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				startActivity(intent);

			}
		});

		TextView title = (TextView) layout.findViewById(R.id.title);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGII.TTF");
		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
		scoreTitle.setTypeface(tf);

		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);
		scoreValue.setTypeface(tf);
		

		db = new DAO(this);
		db.open();

//		c = db.getLevelLogos(getIntent().getIntExtra("LevelId", 0));
//
//		if (c.getCount() != 0) {
//
//			title.setText(c.getString(c.getColumnIndex("le_country")).toUpperCase());
//			scoreValue.setText(String.valueOf(c.getInt(c.getColumnIndex("level_score"))));
//
//			logosArray = new ArrayList<HashMap<String, String>>();
//
//			logosGrid = (GridView) findViewById(R.id.logosGrid);
// استخدام try-with-resources لضمان الإغلاق التلقائي للكورسور
		try (Cursor c = db.getLevelLogos(getIntent().getIntExtra("LevelId", 0))) {

			// استخدام moveToFirst بدلاً من getCount لضمان جهوزية البيانات للقراءة
			if (c != null && c.moveToFirst()) {

				// 1. استخراج الفهارس (Indices) أولاً لتجنب تحذيرات النظام
				int countryIdx = c.getColumnIndex("le_country");
				int scoreIdx = c.getColumnIndex("level_score");

				// 2. التحقق من وجود الأعمدة قبل القراءة
				if (countryIdx != -1 && scoreIdx != -1) {

					// قراءة اسم الدولة مع تحويله لحروف كبيرة وإزالة الفراغات بأمان
					String countryName = c.getString(countryIdx);
					title.setText(countryName != null ? countryName.trim().toUpperCase() : "");

					// قراءة النتيجة (Score) وتحديث النص
					scoreValue.setText(String.valueOf(c.getInt(scoreIdx)));
				}

				// 3. تجهيز المصفوفة والشبكة (Grid)
				logosArray = new ArrayList<HashMap<String, String>>();
				logosGrid = (GridView) findViewById(R.id.logosGrid);

				// هنا يمكنك البدء بحلقة do-while لاستخراج بقية الشعارات كما فعلنا سابقاً
			}
		} catch (Exception e) {
			Log.e("DB_ERROR", "إثر محاولة جلب شعارات المستوى حدث خطأ غير متوقع", e);
		}
		// 1. استخراج فهارس الأعمدة خارج الحلقة (Optimization)
		int idxId = c.getColumnIndex(KEY_ID);
		int idxName = c.getColumnIndex(KEY_NAME);
		int idxImage = c.getColumnIndex(KEY_IMAGE);
		int idxCompleted = c.getColumnIndex(KEY_COMPLETED);
		int idxImageSd = c.getColumnIndex(KEY_IMAGE_SDCARD);

// 2. التحقق من وجود الأعمدة (ليس -1) قبل بدء التكرار
		if (idxId != -1 && idxName != -1 && idxImage != -1) {
			do {
				HashMap<String, String> map = new HashMap<>();

				// قراءة البيانات باستخدام الفهارس الجاهزة
				map.put(KEY_ID, c.getString(idxId));
				map.put(KEY_NAME, c.getString(idxName));
				map.put(KEY_IMAGE, c.getString(idxImage));
				map.put(KEY_COMPLETED, c.getString(idxCompleted));
				map.put(KEY_IMAGE_SDCARD, c.getString(idxImageSd));

				logosArray.add(map);

//			} while (c.moveToNext()); // الاستمرار طالما يوجد صف تالٍ
//		}
//			do {
//				map = new HashMap<String, String>();
//
//				map.put(KEY_ID, c.getString(c.getColumnIndex(KEY_ID)));
//				map.put(KEY_NAME, c.getString(c.getColumnIndex(KEY_NAME)));
//				map.put(KEY_IMAGE, c.getString(c.getColumnIndex(KEY_IMAGE)));
//				map.put(KEY_COMPLETED, c.getString(c.getColumnIndex(KEY_COMPLETED)));
//				map.put(KEY_IMAGE_SDCARD, c.getString(c.getColumnIndex(KEY_IMAGE_SDCARD)));
//
//				logosArray.add(map);
			} while (c.moveToNext());

			adapter = new LogosAdapter(this, logosArray);
			logosGrid.setAdapter(adapter);

			// Click event for single list row
			logosGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					map = logosArray.get(position);

					Intent intent = new Intent(LogosActivity.this, GameActivity.class);
					intent.putExtra("LogoId", map.get(KEY_ID));
					startActivity(intent);

				}
			});

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
