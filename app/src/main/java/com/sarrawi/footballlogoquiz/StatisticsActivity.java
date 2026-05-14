package com.sarrawi.footballlogoquiz;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatisticsActivity extends Activity {

	DAO db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		db = new DAO(this);
		db.open();

		// إعداد شريط العنوان
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);
		TextView title = (TextView) layout.findViewById(R.id.title);
		title.setText("statistics".toUpperCase());

		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);
		scoreTitle.setText("");
		scoreValue.setText("");

		// 1. إحصائيات النقاط (Score Stats)
		try (Cursor cScore = db.getStatsScore()) {
			if (cScore != null && cScore.moveToFirst()) {
				int idxTotalScore = cScore.getColumnIndex("total_score");
				if (idxTotalScore != -1) {
					TextView totalScore = (TextView) findViewById(R.id.sTotalScoreValue);
					totalScore.setText(String.valueOf(cScore.getInt(idxTotalScore)));
				}
			}
		}

		// 2. إحصائيات التلميحات (Hints Stats)
		try (Cursor cHints = db.getHintsCount()) {
			if (cHints != null && cHints.moveToFirst()) {
				int idxTotal = cHints.getColumnIndex("total_hints");
				int idxUsed = cHints.getColumnIndex("used_hints");

				if (idxTotal != -1 && idxUsed != -1) {
					int total = cHints.getInt(idxTotal);
					int used = cHints.getInt(idxUsed);

					((TextView) findViewById(R.id.sTotalHintsValue)).setText(String.valueOf(total));
					((TextView) findViewById(R.id.sUsedHintsValue)).setText(String.valueOf(used));
					((TextView) findViewById(R.id.sCurrentHintsValue)).setText(String.valueOf(total - used));
				}
			}
		}

		// 3. إحصائيات الشعارات والميداليات (Logos & Medals Stats)
		try (Cursor cLogos = db.getStatsLogosAndMedals()) {
			if (cLogos != null && cLogos.moveToFirst()) {
				int idxTotalLogos = cLogos.getColumnIndex("total_logos");
				int idxCompLogos = cLogos.getColumnIndex("completed_logos_count");
				int idxGold = cLogos.getColumnIndex("gold_medals_count");
				int idxSilver = cLogos.getColumnIndex("silver_medals_count");
				int idxBronze = cLogos.getColumnIndex("bronze_medals_count");

				if (idxTotalLogos != -1) {
					((TextView) findViewById(R.id.sTotalLogosValue)).setText(String.valueOf(cLogos.getInt(idxTotalLogos)));
					((TextView) findViewById(R.id.sCompletedLogosValue)).setText(String.valueOf(cLogos.getInt(idxCompLogos)));
					((TextView) findViewById(R.id.sGoldMedalsValue)).setText(String.valueOf(cLogos.getInt(idxGold)));
					((TextView) findViewById(R.id.sSilverMedalsValue)).setText(String.valueOf(cLogos.getInt(idxSilver)));
					((TextView) findViewById(R.id.sBronzeMedalsValue)).setText(String.valueOf(cLogos.getInt(idxBronze)));
				}
			}
		}

		// 4. إحصائيات المستويات (Levels Stats)
		try (Cursor cLevels = db.getStatsLevels()) {
			if (cLevels != null && cLevels.moveToFirst()) {
				int idxTotalLvls = cLevels.getColumnIndex("levels_count");
				int idxOpenLvls = cLevels.getColumnIndex("open_levels_count");
				int idxCompLvls = cLevels.getColumnIndex("completed_levels_count");

				if (idxTotalLvls != -1) {
					((TextView) findViewById(R.id.sTotalLevelsValue)).setText(String.valueOf(cLevels.getInt(idxTotalLvls)));
					((TextView) findViewById(R.id.sOpenLevelsValue)).setText(String.valueOf(cLevels.getInt(idxOpenLvls)));
					((TextView) findViewById(R.id.sCompletedLevelsValue)).setText(String.valueOf(cLevels.getInt(idxCompLvls)));
				}
			}
		} catch (Exception e) {
			Log.e("DB_ERROR", "حدث خطأ إثر محاولة استخراج الإحصائيات", e);
		}

		// زر الرجوع
		ImageButton back = (ImageButton) layout.findViewById(R.id.back);
		back.setOnClickListener(v -> finish());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (db != null) db.close();
	}
}
//package com.sarrawi.footballlogoquiz;
//
//import com.sarrawi.footballlogoquiz.R;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.database.Cursor;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class StatisticsActivity extends Activity {
//
//	DAO db;
//	Cursor c;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_statistics);
//
//		db = new DAO(this);
//		db.open();
//		// c = db.
//
//		RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);
//
//		TextView title = (TextView) layout.findViewById(R.id.title);
//		title.setText("statistics".toUpperCase());
//
//		TextView scoreTitle = (TextView) layout.findViewById(R.id.scoreTitle);
//		TextView scoreValue = (TextView) layout.findViewById(R.id.scoreValue);
//
//		scoreTitle.setText("");
//		scoreValue.setText("");
//
//		c = db.getStatsScore();
//		TextView totalScore = (TextView) findViewById(R.id.sTotalScoreValue);
//		totalScore.setText(String.valueOf(c.getInt(c.getColumnIndex("total_score"))));
//
//		c = db.getHintsCount();
//		TextView totalHints = (TextView) findViewById(R.id.sTotalHintsValue);
//		totalHints.setText(String.valueOf(c.getInt(c.getColumnIndex("total_hints"))));
//
//		TextView usedHints = (TextView) findViewById(R.id.sUsedHintsValue);
//		usedHints.setText(String.valueOf(c.getInt(c.getColumnIndex("used_hints"))));
//
//		TextView currentHints = (TextView) findViewById(R.id.sCurrentHintsValue);
//		currentHints.setText(String.valueOf(c.getInt(c.getColumnIndex("total_hints")) - c.getInt(c.getColumnIndex("used_hints"))));
//
//		c = db.getStatsLogosAndMedals();
//		TextView totalLogos = (TextView) findViewById(R.id.sTotalLogosValue);
//		totalLogos.setText(String.valueOf(c.getInt(c.getColumnIndex("total_logos"))));
//
//		TextView completedLogos = (TextView) findViewById(R.id.sCompletedLogosValue);
//		completedLogos.setText(String.valueOf(c.getInt(c.getColumnIndex("completed_logos_count"))));
//
//		TextView goldMedals = (TextView) findViewById(R.id.sGoldMedalsValue);
//		goldMedals.setText(String.valueOf(c.getInt(c.getColumnIndex("gold_medals_count"))));
//
//		TextView silverMedals = (TextView) findViewById(R.id.sSilverMedalsValue);
//		silverMedals.setText(String.valueOf(c.getInt(c.getColumnIndex("silver_medals_count"))));
//
//		TextView bronzeMedals = (TextView) findViewById(R.id.sBronzeMedalsValue);
//		bronzeMedals.setText(String.valueOf(c.getInt(c.getColumnIndex("bronze_medals_count"))));
//
//		c = db.getStatsLevels();
//		TextView totalLevels = (TextView) findViewById(R.id.sTotalLevelsValue);
//		totalLevels.setText(String.valueOf(c.getInt(c.getColumnIndex("levels_count"))));
//
//		TextView openLevels = (TextView) findViewById(R.id.sOpenLevelsValue);
//		openLevels.setText(String.valueOf(c.getInt(c.getColumnIndex("open_levels_count"))));
//
//		TextView completedLevels = (TextView) findViewById(R.id.sCompletedLevelsValue);
//		completedLevels.setText(String.valueOf(c.getInt(c.getColumnIndex("completed_levels_count"))));
//
//		completedLevels.setText(String.valueOf(String.valueOf(c.getInt(c.getColumnIndex("completed_levels_count")))));
//
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
//
//}
