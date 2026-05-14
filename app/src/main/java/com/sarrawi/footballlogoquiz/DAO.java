package com.sarrawi.footballlogoquiz;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class DAO {

	private SQLiteDatabase database;
	private DataBaseHandler dbHandler;

	private static final String TABLE_LEVELS = "levels";
	private static final String TABLE_LOGOS = "logos";
	private static final String TABLE_HINTS = "hints";
	private static final String TABLE_LOGO_HINTS = "logo_hints";

	private static final String LE_ID = "_leid";
	private static final String LE_COUNTRY = "le_country";
	private static final String LE_ORDER = "le_order";
	private static final String LE_OPEN = "le_open";
	private static final String LE_COMPLETED = "le_completed";
	private static final String LE_WEB_ID = "le_web_id";

	private static final String LO_ID = "_loid";
	private static final String LO_LEVEL = "lo_level";
	private static final String LO_COMPLETED = "lo_completed";
	private static final String LO_POINTS = "lo_points";
	private static final String LO_TRIES = "lo_tries";
	private static final String LO_LETTER = "lo_letter";
	private static final String LO_WEB_ID = "lo_web_id";

	private static final String HINTS_ID = "_hiid";
	private static final String TOTAL_HINTS = "total_hints";
	private static final String USED_HINTS = "used_hints";

	private static final String TABLE_SETTINGS = "settings";


	// Levels Table Columns names


	private static final String LE_FLAG_SDCARD = "le_flag_sdcard";


	private static final String LO_IMAGE = "lo_image";

	private static final String LO_IMAGE_SDCARD = "lo_image_sdcard";



	// Logo Hints Table Columns names

	private static final String LO_HI_ID = "_lo_hi_id";
	private static final String LO_HI_LOGO = "lo_hi_logo";

	public void close() {
		if (dbHandler != null) {
			dbHandler.close();
		}
	}
	public DAO(Context context) {
		dbHandler = new DataBaseHandler(context);
		try {
			dbHandler.createDataBase();
			dbHandler.openDataBase();
		} catch (IOException | SQLException e) {
			throw new Error("Unable to create/open database");
		}
	}

	public void open() throws SQLException {
		database = dbHandler.getWritableDatabase();
	}

	public void closeDatabase() {
		dbHandler.close();
	}

	// ===================== القيم المباشرة =====================

	public int getTotalScore() {
		open();
		int totalScore = 0;
		Cursor c = database.rawQuery("SELECT SUM(" + LO_POINTS + ") AS total_score FROM " + TABLE_LOGOS, null);
		if (c != null && c.moveToFirst()) {
			totalScore = c.getInt(c.getColumnIndexOrThrow("total_score"));
			c.close();
		}
		return totalScore;
	}

	public int getTotalLogos() {
		open();
		int total = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LO_ID + ") AS total_logos FROM " + TABLE_LOGOS, null);
		if (c != null && c.moveToFirst()) {
			total = c.getInt(c.getColumnIndexOrThrow("total_logos"));
			c.close();
		}
		return total;
	}

	public int getCompletedLogosCount() {
		open();
		int completed = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LO_ID + ") AS completed_logos_count FROM " + TABLE_LOGOS + " WHERE " + LO_COMPLETED + " = 1", null);
		if (c != null && c.moveToFirst()) {
			completed = c.getInt(c.getColumnIndexOrThrow("completed_logos_count"));
			c.close();
		}
		return completed;
	}

	public int getGoldMedalsCount() {
		open();
		int gold = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LO_ID + ") AS gold_medals_count FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 100", null);
		if (c != null && c.moveToFirst()) {
			gold = c.getInt(c.getColumnIndexOrThrow("gold_medals_count"));
			c.close();
		}
		return gold;
	}

	public int getSilverMedalsCount() {
		open();
		int silver = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LO_ID + ") AS silver_medals_count FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 80 OR " + LO_POINTS + " = 60", null);
		if (c != null && c.moveToFirst()) {
			silver = c.getInt(c.getColumnIndexOrThrow("silver_medals_count"));
			c.close();
		}
		return silver;
	}

	public int getBronzeMedalsCount() {
		open();
		int bronze = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LO_ID + ") AS bronze_medals_count FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 40 OR " + LO_POINTS + " = 20", null);
		if (c != null && c.moveToFirst()) {
			bronze = c.getInt(c.getColumnIndexOrThrow("bronze_medals_count"));
			c.close();
		}
		return bronze;
	}

	public int getLevelsCount() {
		open();
		int count = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LE_ID + ") AS count FROM " + TABLE_LEVELS, null);
		if (c != null && c.moveToFirst()) {
			count = c.getInt(c.getColumnIndexOrThrow("count"));
			c.close();
		}
		return count;
	}

	public int getOpenLevelsCount() {
		open();
		int openCount = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LE_ID + ") AS open_count FROM " + TABLE_LEVELS + " WHERE " + LE_OPEN + " = 1", null);
		if (c != null && c.moveToFirst()) {
			openCount = c.getInt(c.getColumnIndexOrThrow("open_count"));
			c.close();
		}
		return openCount;
	}

	public int getCompletedLevelsCount() {
		open();
		int completedCount = 0;
		Cursor c = database.rawQuery("SELECT COUNT(" + LE_ID + ") AS completed_count FROM " + TABLE_LEVELS + " WHERE " + LE_COMPLETED + " = 1", null);
		if (c != null && c.moveToFirst()) {
			completedCount = c.getInt(c.getColumnIndexOrThrow("completed_count"));
			c.close();
		}
		return completedCount;
	}

	public int getTotalHints() {
		open();
		int total = 0;
		Cursor c = database.rawQuery("SELECT " + TOTAL_HINTS + " FROM " + TABLE_HINTS + " WHERE " + HINTS_ID + " = 1", null);
		if (c != null && c.moveToFirst()) {
			total = c.getInt(c.getColumnIndexOrThrow(TOTAL_HINTS));
			c.close();
		}
		return total;
	}

	public int getUsedHints() {
		open();
		int used = 0;
		Cursor c = database.rawQuery("SELECT " + USED_HINTS + " FROM " + TABLE_HINTS + " WHERE " + HINTS_ID + " = 1", null);
		if (c != null && c.moveToFirst()) {
			used = c.getInt(c.getColumnIndexOrThrow(USED_HINTS));
			c.close();
		}
		return used;
	}

	public int getCurrentHints() {
		return getTotalHints() - getUsedHints();
	}

	public void addLevel(String le_country, String le_flag, int le_web_id) {
		open();

		// الحصول على أعلى قيمة order
		String query = "SELECT MAX(" + LE_ORDER + ") AS max_order FROM " + TABLE_LEVELS;
		Cursor cursor = database.rawQuery(query, null);

		int max_order = 0; // قيمة افتراضية في حالة عدم وجود مستويات
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				max_order = cursor.getInt(cursor.getColumnIndexOrThrow("max_order"));
			}
			cursor.close(); // أغلق الـ cursor بعد الاستخدام
		}

		// إضافة المستوى الجديد
		ContentValues values = new ContentValues();
		values.put("le_country", le_country);
		values.put("le_flag", le_flag);
		values.put("le_open", 0);
		values.put("le_completed", 0);
		values.put("le_flag_sdcard", 1);
		values.put("le_order", max_order + 1);
		values.put("le_web_id", le_web_id);

		database.insert(TABLE_LEVELS, null, values);
	}

	public void addLogo(String lo_name, String lo_image, int lo_level, String lo_wikipedia, String lo_info, String lo_player, int lo_web_id) {
		open();
		ContentValues v = new ContentValues();
		v.put("lo_name", lo_name);
		v.put("lo_image", lo_image);
		v.put("lo_level", lo_level);
		v.put("lo_wikipedia", lo_wikipedia);
		v.put("lo_info", lo_info);
		v.put("lo_player", lo_player);
		v.put("lo_tries", 0);
		v.put("lo_points", 0);
		v.put("lo_completed", "0");
		v.put("lo_image_sdcard", 1);
		v.put("lo_web_id", lo_web_id);

		database.insert("logos", null, v);

	}

	public int getLastLevel() {
		int lastLevel = 0; // قيمة افتراضية إذا لم يكن هناك أي مستوى
		String query = "SELECT " + LE_WEB_ID + " FROM " + TABLE_LEVELS + " ORDER BY " + LE_WEB_ID + " DESC LIMIT 1";
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, null);
			if (cursor != null && cursor.moveToFirst()) {
				lastLevel = cursor.getInt(cursor.getColumnIndexOrThrow(LE_WEB_ID));
			}
		} finally {
			if (cursor != null) {
				cursor.close(); // أغلق الـ cursor دائمًا
			}
		}

		return lastLevel;
	}

	public int getLastLogo() {
		int lastLogo = 0; // قيمة افتراضية إذا لم يكن هناك أي شعار
		String query = "SELECT " + LO_WEB_ID + " FROM " + TABLE_LOGOS + " ORDER BY " + LO_WEB_ID + " DESC LIMIT 1";
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, null);
			if (cursor != null && cursor.moveToFirst()) {
				lastLogo = cursor.getInt(cursor.getColumnIndexOrThrow(LO_WEB_ID));
			}
		} finally {
			if (cursor != null) {
				cursor.close(); // أغلق الـ cursor دائمًا
			}
		}

		return lastLogo;
	}

	public Cursor getOneLogo(String LogoID) {
		// Select All Query

		String query = "SELECT * FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " = '" + LogoID + "'";
		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor;

	}

	public int getLogosCount(int LevelID) {
		int count = 0; // قيمة افتراضية إذا لم يكن هناك أي شعار
		String query = "SELECT COUNT(" + LO_ID + ") AS logos_count FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LevelID;
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, null);
			if (cursor != null && cursor.moveToFirst()) {
				count = cursor.getInt(cursor.getColumnIndexOrThrow("logos_count"));
			}
		} finally {
			if (cursor != null) {
				cursor.close(); // أغلق الـ cursor دائمًا لتجنب تسرب الذاكرة
			}
		}

		return count;
	}
	public void setLogoCompleted(String loID, int points, int loLevel) {
		open();
		// تحديث الشعار بأنه مكتمل وإضافة النقاط
		ContentValues values = new ContentValues();
		values.put(LO_COMPLETED, 1); // استخدم الرقم مباشرة بدل السلسلة
		values.put(LO_POINTS, points);
		database.update(TABLE_LOGOS, values, LO_ID + "=?", new String[]{loID});

		// التحقق من عدد الشعارات غير المكتملة في المستوى
		String query = "SELECT COUNT(" + LO_ID + ") AS comp_logos FROM " + TABLE_LOGOS +
				" WHERE " + LO_LEVEL + " = ? AND " + LO_COMPLETED + " = 0";
		Cursor cursor = null;

		try {
			cursor = database.rawQuery(query, new String[]{String.valueOf(loLevel)});
			int is_level_completed = 0;
			if (cursor != null && cursor.moveToFirst()) {
				is_level_completed = cursor.getInt(cursor.getColumnIndexOrThrow("comp_logos"));
			}

			// إذا لم يتبق أي شعار غير مكتمل، اعتبر المستوى مكتمل
			if (is_level_completed == 0) {
				setLevelCompleted(loLevel);
			}
		} finally {
			if (cursor != null) {
				cursor.close(); // أغلق الـ cursor دائمًا لتجنب تسرب الذاكرة
			}
		}
	}

	public void setLevelCompleted(int leID) {
		open();
		ContentValues values = new ContentValues();
		values.put(LE_COMPLETED, 1); // استخدم الرقم مباشرة بدل السلسلة

		// تحديث مستوى بأنه مكتمل
		database.update(TABLE_LEVELS, values, LE_WEB_ID + "=?", new String[]{String.valueOf(leID)});
	}


	public void addTotalHints(int hints) {
		open();
		String query = "UPDATE " + TABLE_HINTS + " SET " + TOTAL_HINTS + " = " + TOTAL_HINTS + " + " + hints + " WHERE " + HINTS_ID + " = 1";
		database.execSQL(query);
	}



	public void setLevelOpened(String leID) {
		open();
		ContentValues values = new ContentValues();
		values.put(LE_OPEN, 1);
		// Update Row
		database.update(TABLE_LEVELS, values, LE_WEB_ID + "=?", new String[] { leID });
	}

	// عدد الشعارات المكتملة في مستوى معين
// عدد الشعارات المكتملة في مستوى معين


	public int getLevelCompletedLogosCount(int leID) {
		open();
		int count = 0;
		Cursor cursor = null;
		try {
			String query = "SELECT COUNT(" + LO_ID + ") AS count " +
					"FROM " + TABLE_LOGOS + " " +
					"WHERE " + LO_LEVEL + " = ? AND " + LO_COMPLETED + " = 1";
			cursor = database.rawQuery(query, new String[]{String.valueOf(leID)});
			if (cursor.moveToFirst()) {
				count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}


	// الحصول على المستوى التالي بعد مستوى معين
	public int getNextLevel(int leID) {
		open();
		int nextLevel = 0;
		Cursor cursor = null;
		try {
			String query = "SELECT " + LE_WEB_ID +
					" FROM " + TABLE_LEVELS +
					" WHERE " + LE_ORDER + " > (SELECT " + LE_ORDER +
					" FROM " + TABLE_LEVELS + " WHERE " + LE_WEB_ID + " = ?) LIMIT 1";
			cursor = database.rawQuery(query, new String[]{String.valueOf(leID)});
			if (cursor.moveToFirst()) {
				nextLevel = cursor.getInt(cursor.getColumnIndexOrThrow(LE_WEB_ID));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return nextLevel;
	}



	public void addUsedHint() {
		open();
		String query = "UPDATE " + TABLE_HINTS + " SET " + USED_HINTS + " = " + USED_HINTS + " + 1" + " WHERE " + HINTS_ID + " = 1";
		database.execSQL(query);
	}

	public Cursor getPrevLogo(String loID, String loLevel) {
		open();
		String whereQuery = "SELECT " + LO_ID + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " = " + loID;
		String query = "SELECT " + LO_ID + ", " + LO_IMAGE + ", " + LO_IMAGE_SDCARD + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " < (" + whereQuery + ") AND " + LO_LEVEL + " = " + loLevel + " ORDER BY " + LO_ID + " DESC LIMIT 1";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}

	public void setTries(String loID, int tries) {
		open();
		ContentValues values = new ContentValues();
		values.put(LO_TRIES, tries);
		// Update Row
		database.update(TABLE_LOGOS, values, LO_ID + "=?", new String[] { loID });
	}


	public Cursor getNextLogo(String loID, String loLevel) {
		open();
		String whereQuery = "SELECT " + LO_ID + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " = " + loID;
		String query = "SELECT " + LO_ID + ", " + LO_IMAGE + ", " + LO_IMAGE_SDCARD + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " > (" + whereQuery + ") AND " + LO_LEVEL + " = " + loLevel + " ORDER BY " + LO_ID + " ASC LIMIT 1";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}

	public Cursor getHintState(String loID) {
		open();
		String query = "SELECT *" + " FROM " + TABLE_LOGO_HINTS + " WHERE " + LO_HI_LOGO + " = " + loID;
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}

	public void updateHintState(String loHintID, String loHintField) {
		open();
		ContentValues logoHintsValues = new ContentValues();
		logoHintsValues.put(loHintField, 1);
		int result = database.update(TABLE_LOGO_HINTS, logoHintsValues, LO_HI_LOGO + "=?", new String[] { loHintID });
		if (result == 0) {
			logoHintsValues.put(LO_HI_LOGO, loHintID);
			database.insert(TABLE_LOGO_HINTS, null, logoHintsValues);
		}
	}

	public void addLetterHintPos(String loID, String pos) {
		open();
		ContentValues values = new ContentValues();
		values.put(LO_LETTER, pos);
		// Update Row
		database.update(TABLE_LOGOS, values, LO_ID + "=?", new String[] { loID });
	}



	public Cursor getHintsCount() {
		open();
		String query = "SELECT * FROM " + TABLE_HINTS + " WHERE _hiid = 1";
		return database.rawQuery(query, null);
	}

	public Cursor getLevels() {
		String level_score_query = "(SELECT SUM(" + LO_POINTS + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LE_WEB_ID + " AND " + LO_COMPLETED + " = 1)";
		String completed_logos_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LE_WEB_ID + " AND " + LO_COMPLETED + " = 1)";
		String query = "SELECT *, " + level_score_query + " AS level_score, COUNT(" + LO_ID + ") AS logos_count," + completed_logos_query + " AS completed_logos_count FROM " + TABLE_LEVELS + " LEFT JOIN " + TABLE_LOGOS + " ON " + LE_WEB_ID + " = " + LO_LEVEL + " GROUP BY  " + LE_COUNTRY
				+ " ORDER BY  " + LE_ORDER + " ASC";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}

	public Cursor getLevelLogos(int LevelID) {
		// Select All Query
		String level_score_query = "(SELECT SUM(" + LO_POINTS + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LevelID + " AND " + LO_COMPLETED + " = 1)";
		String query = "SELECT " + LE_WEB_ID + ", " + LE_COUNTRY + ", " + level_score_query + " AS level_score, " + TABLE_LOGOS + ".* FROM " + TABLE_LEVELS + " LEFT JOIN " + TABLE_LOGOS + " ON " + LO_LEVEL + " = " + LE_WEB_ID + " WHERE " + LE_WEB_ID + " = '" + LevelID + "'" + " GROUP BY " + LO_ID;
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}


//	public void resetGame() {
//		open();
//		ContentValues logosValues = new ContentValues();
//		logosValues.put(LO_TRIES, 0);
//		logosValues.put(LO_POINTS, 0);
//		logosValues.put(LO_COMPLETED, "0");
//		logosValues.put(LO_LETTER, "");
//		database.update(TABLE_LOGOS, logosValues, null, null);
//		ContentValues levelsValues = new ContentValues();
//		levelsValues.put(LE_OPEN, 0);
//		levelsValues.put(LE_COMPLETED, 0);
//		database.update(TABLE_LEVELS, levelsValues, null, null);
//		ContentValues hintsValues = new ContentValues();
//		hintsValues.put(TOTAL_HINTS, 8);
//		hintsValues.put(USED_HINTS, 0);
//		database.update(TABLE_HINTS, hintsValues, null, null);
//		String emptyQuery = "DELETE FROM " + TABLE_LOGO_HINTS;
//		database.execSQL(emptyQuery);
//		// Reopen first level
//		String query = "SELECT " + LE_ID + " FROM " + TABLE_LEVELS + " ORDER BY  " + LE_ORDER + " ASC";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//		String fLevel = String.valueOf(cursor.getInt(cursor.getColumnIndex(LE_ID)));
//		ContentValues fLevelValues = new ContentValues();
//		fLevelValues.put(LE_OPEN, 1);
//		database.update(TABLE_LEVELS, fLevelValues, LE_ID + "=?", new String[] { fLevel });
//	}

	public void resetGame() {
		open();

		// 1. إعادة ضبط جدول الشعارات
		ContentValues logosValues = new ContentValues();
		logosValues.put(LO_TRIES, 0);
		logosValues.put(LO_POINTS, 0);
		logosValues.put(LO_COMPLETED, "0");
		logosValues.put(LO_LETTER, "");
		database.update(TABLE_LOGOS, logosValues, null, null);

		// 2. إعادة ضبط جدول المستويات
		ContentValues levelsValues = new ContentValues();
		levelsValues.put(LE_OPEN, 0);
		levelsValues.put(LE_COMPLETED, 0);
		database.update(TABLE_LEVELS, levelsValues, null, null);

		// 3. إعادة ضبط جدول المساعدات (Hints)
		ContentValues hintsValues = new ContentValues();
		hintsValues.put(TOTAL_HINTS, 8);
		hintsValues.put(USED_HINTS, 0);
		database.update(TABLE_HINTS, hintsValues, null, null);

		// 4. مسح سجل تلميحات الشعارات المستخدمة
		String emptyQuery = "DELETE FROM " + TABLE_LOGO_HINTS;
		database.execSQL(emptyQuery);

		// 5. فتح المستوى الأول مجدداً
		String query = "SELECT " + LE_ID + " FROM " + TABLE_LEVELS + " ORDER BY " + LE_ORDER + " ASC";
		Cursor cursor = database.rawQuery(query, null);

		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					// الطريقة الآمنة لاستخراج رقم العمود في SDK 35
					int idColumnIndex = cursor.getColumnIndex(LE_ID);

					if (idColumnIndex != -1) {
						String fLevel = String.valueOf(cursor.getInt(idColumnIndex));

						ContentValues fLevelValues = new ContentValues();
						fLevelValues.put(LE_OPEN, 1);
						database.update(TABLE_LEVELS, fLevelValues, LE_ID + "=?", new String[]{fLevel});
					}
				}
			} finally {
				// إغلاق الكورسور ضروري جداً لتجنب تسريب الذاكرة (Memory Leak)
				cursor.close();
			}
		}
	}

	public Cursor getStatsScore() {
		String query = "SELECT SUM(" + LO_POINTS + ") AS total_score FROM " + TABLE_LOGOS;
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		return cursor;
	}

	public Cursor getStatsLevels() {
		String open_levels_query = "(SELECT COUNT(" + LE_ID + ") FROM " + TABLE_LEVELS + " WHERE " + LE_OPEN + " = 1" + ")";
		String completed_levels_query = "(SELECT COUNT(" + LE_ID + ") FROM " + TABLE_LEVELS + " WHERE " + LE_COMPLETED + " = 1" + ")";
		String query = "SELECT COUNT(" + LE_ID + ") AS levels_count," + open_levels_query + " AS open_levels_count, " + completed_levels_query + " AS completed_levels_count" + " FROM " + TABLE_LEVELS;
		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor;

	}

	public Cursor getStatsLogosAndMedals() {
		String completed_logos_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_COMPLETED + " = 1" + ")";
		String gold_medals_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 100" + ")";
		String silver_medals_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 80 OR " + LO_POINTS + " = 60" + ")";
		String bronze_models_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_POINTS + " = 40 OR " + LO_POINTS + " = 20" + ")";
		String query = "SELECT COUNT(" + LO_ID + ") AS total_logos," + completed_logos_query + " AS completed_logos_count, " + gold_medals_query + " AS gold_medals_count, " + silver_medals_query + " AS silver_medals_count, " + bronze_models_query + " AS bronze_medals_count" + " FROM " + TABLE_LOGOS;
		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor;

	}

}

//
//public class DAO {
//
//	// All Static variables
//

//	// ==============================================================================
//
//	public DAO(Context context) {
//		dbHandler = new DataBaseHandler(context);
//		try {
//
//			dbHandler.createDataBase();
//
//		} catch (IOException ioe) {
//
//			throw new Error("Unable to create database");
//
//		}
//		try {
//
//			dbHandler.openDataBase();
//
//		} catch (SQLException sqle) {
//
//			throw sqle;
//
//		}
//		// Log.e("path2",
//		// context.getDatabasePath("FootballLogoQuiz").toString());
//	}
//
//	// ==============================================================================
//
//	// Getting All Levels
//	public Cursor getLevels() {
//
//		String level_score_query = "(SELECT SUM(" + LO_POINTS + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LE_WEB_ID + " AND " + LO_COMPLETED + " = 1)";
//		String completed_logos_query = "(SELECT COUNT(" + LO_ID + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LE_WEB_ID + " AND " + LO_COMPLETED + " = 1)";
//		String query = "SELECT *, " + level_score_query + " AS level_score, COUNT(" + LO_ID + ") AS logos_count," + completed_logos_query + " AS completed_logos_count FROM " + TABLE_LEVELS + " LEFT JOIN " + TABLE_LOGOS + " ON " + LE_WEB_ID + " = " + LO_LEVEL + " GROUP BY  " + LE_COUNTRY
//				+ " ORDER BY  " + LE_ORDER + " ASC";
//		Cursor cursor = database.rawQuery(query, null);
//
//		cursor.moveToFirst();
//		return cursor;
//
//	}
//
//	// ==============================================================================
//
//	// Getting All Logos
//	public Cursor getLevelLogos(int LevelID) {
//		// Select All Query
//
//		String level_score_query = "(SELECT SUM(" + LO_POINTS + ") FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + LevelID + " AND " + LO_COMPLETED + " = 1)";
//		String query = "SELECT " + LE_WEB_ID + ", " + LE_COUNTRY + ", " + level_score_query + " AS level_score, " + TABLE_LOGOS + ".* FROM " + TABLE_LEVELS + " LEFT JOIN " + TABLE_LOGOS + " ON " + LO_LEVEL + " = " + LE_WEB_ID + " WHERE " + LE_WEB_ID + " = '" + LevelID + "'" + " GROUP BY " + LO_ID;
//
//		Cursor cursor = database.rawQuery(query, null);
//
//		cursor.moveToFirst();
//		return cursor;
//
//	}
//
//	// ==============================================================================
//
//	// Getting One Logo
//
//
//	// ==============================================================================
//
//	// Getting Levels Count
//	public Integer getLevelsCount() {
//
//		String query = "SELECT COUNT(" + LE_ID + ") AS count FROM " + TABLE_LEVELS;
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//		Integer count = cursor.getInt(cursor.getColumnIndex("count"));
//
//		return count;
//
//	}
//
//	// ==============================================================================
//
//	// Getting Logos Count

//
//	// ==============================================================================
//

//
//	// ==============================================================================
//
//	public void setLevelCompleted(int leID) {
//		open();
//		ContentValues values = new ContentValues();
//		values.put(LE_COMPLETED, "1");
//		// Log.e("count", leID);
//		// Update Row
//		database.update(TABLE_LEVELS, values, LE_WEB_ID + "=?", new String[] { String.valueOf(leID) });
//
//	}
//
//	// ==============================================================================
//
//	public void setTries(String loID, int tries) {
//		open();
//		ContentValues values = new ContentValues();
//		values.put(LO_TRIES, tries);
//
//		// Update Row
//		database.update(TABLE_LOGOS, values, LO_ID + "=?", new String[] { loID });
//	}
//
//	// ==============================================================================
//
//	public Integer getLevelCompletedLogosCount(int leID) {
//		open();
//
//		String query = "SELECT COUNT(" + LO_ID + ") AS count FROM " + TABLE_LOGOS + " WHERE " + LO_LEVEL + " = " + leID + " AND " + LO_COMPLETED + " = 1";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//		Integer count = cursor.getInt(cursor.getColumnIndex("count"));
//
//		return count;
//
//	}
//
//	// ==============================================================================
//
//	public void setLevelOpened(String leID) {
//		open();
//		ContentValues values = new ContentValues();
//		values.put(LE_OPEN, 1);
//
//		// Update Row
//		database.update(TABLE_LEVELS, values, LE_WEB_ID + "=?", new String[] { leID });
//	}
//
//	// ==============================================================================
//
//	public Integer getNextLevel(int leID) {
//		open();
//
//		String orderQuery = "SELECT " + LE_ORDER + " FROM " + TABLE_LEVELS + " WHERE " + LE_WEB_ID + " = " + leID;
//		String query = "SELECT " + LE_WEB_ID + " FROM " + TABLE_LEVELS + " WHERE " + LE_ORDER + " > (" + orderQuery + ") LIMIT 1";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		Integer nextLevel = 0;
//		if (cursor.getCount() > 0) {
//			nextLevel = cursor.getInt(cursor.getColumnIndex(LE_WEB_ID));
//		}
//
//		return nextLevel;
//
//	}
//
//	// ==============================================================================
//
//	public Cursor getPrevLogo(String loID, String loLevel) {
//		open();
//
//		String whereQuery = "SELECT " + LO_ID + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " = " + loID;
//		String query = "SELECT " + LO_ID + ", " + LO_IMAGE + ", " + LO_IMAGE_SDCARD + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " < (" + whereQuery + ") AND " + LO_LEVEL + " = " + loLevel + " ORDER BY " + LO_ID + " DESC LIMIT 1";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		return cursor;
//
//	}
//
//	// ==============================================================================
//
//	public Cursor getNextLogo(String loID, String loLevel) {
//		open();
//
//		String whereQuery = "SELECT " + LO_ID + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " = " + loID;
//		String query = "SELECT " + LO_ID + ", " + LO_IMAGE + ", " + LO_IMAGE_SDCARD + " FROM " + TABLE_LOGOS + " WHERE " + LO_ID + " > (" + whereQuery + ") AND " + LO_LEVEL + " = " + loLevel + " ORDER BY " + LO_ID + " ASC LIMIT 1";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		return cursor;
//
//	}
//
//	// ==============================================================================
//
//	public Cursor getStatsScore() {
//
//		String query = "SELECT SUM(" + LO_POINTS + ") AS total_score FROM " + TABLE_LOGOS;
//		Cursor cursor = database.rawQuery(query, null);
//
//		cursor.moveToFirst();
//		return cursor;
//
//	}
//
//	// ==============================================================================
//

//
//	// ==============================================================================
//

//
//	// ==============================================================================
//
//	public void resetGame() {
//		open();
//
//		ContentValues logosValues = new ContentValues();
//		logosValues.put(LO_TRIES, 0);
//		logosValues.put(LO_POINTS, 0);
//		logosValues.put(LO_COMPLETED, "0");
//		logosValues.put(LO_LETTER, "");
//		database.update(TABLE_LOGOS, logosValues, null, null);
//
//		ContentValues levelsValues = new ContentValues();
//		levelsValues.put(LE_OPEN, 0);
//		levelsValues.put(LE_COMPLETED, 0);
//		database.update(TABLE_LEVELS, levelsValues, null, null);
//
//		ContentValues hintsValues = new ContentValues();
//		hintsValues.put(TOTAL_HINTS, 8);
//		hintsValues.put(USED_HINTS, 0);
//		database.update(TABLE_HINTS, hintsValues, null, null);
//
//		String emptyQuery = "DELETE FROM " + TABLE_LOGO_HINTS;
//		database.execSQL(emptyQuery);
//		// Reopen first level
//
//		String query = "SELECT " + LE_ID + " FROM " + TABLE_LEVELS + " ORDER BY  " + LE_ORDER + " ASC";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//		String fLevel = String.valueOf(cursor.getInt(cursor.getColumnIndex(LE_ID)));
//
//		ContentValues fLevelValues = new ContentValues();
//		fLevelValues.put(LE_OPEN, 1);
//		database.update(TABLE_LEVELS, fLevelValues, LE_ID + "=?", new String[] { fLevel });
//	}
//
//	// ==============================================================================
//
//	public void addTotalHints(int hints) {
//		open();
//
//		String query = "UPDATE " + TABLE_HINTS + " SET " + TOTAL_HINTS + " = " + TOTAL_HINTS + " + " + hints + " WHERE " + HINTS_ID + " = 1";
//
//		database.execSQL(query);
//
//	}
//
//	// ==============================================================================
//
//	public void addUsedHint() {
//		open();
//
//		String query = "UPDATE " + TABLE_HINTS + " SET " + USED_HINTS + " = " + USED_HINTS + " + 1" + " WHERE " + HINTS_ID + " = 1";
//		database.execSQL(query);
//
//	}
//
//	// ==============================================================================
//
//	public void addLetterHintPos(String loID, String pos) {
//		open();
//		ContentValues values = new ContentValues();
//		values.put(LO_LETTER, pos);
//
//		// Update Row
//		database.update(TABLE_LOGOS, values, LO_ID + "=?", new String[] { loID });
//	}
//
//	// ==============================================================================
//
//	public void updateHintState(String loHintID, String loHintField) {
//		open();
//
//		ContentValues logoHintsValues = new ContentValues();
//		logoHintsValues.put(loHintField, 1);
//		int result = database.update(TABLE_LOGO_HINTS, logoHintsValues, LO_HI_LOGO + "=?", new String[] { loHintID });
//		if (result == 0) {
//			logoHintsValues.put(LO_HI_LOGO, loHintID);
//			database.insert(TABLE_LOGO_HINTS, null, logoHintsValues);
//		}
//	}
//
//	// ==============================================================================
//
//	public Cursor getHintsCount() {
//		open();
//
//		String query = "SELECT * " + " FROM " + TABLE_HINTS + " WHERE _hiid = 1";
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		return cursor;
//	}
//
//	// ==============================================================================
//
//	public Cursor getHintState(String loID) {
//		open();
//
//		String query = "SELECT *" + " FROM " + TABLE_LOGO_HINTS + " WHERE " + LO_HI_LOGO + " = " + loID;
//		Cursor cursor = database.rawQuery(query, null);
//		cursor.moveToFirst();
//
//		return cursor;
//	}
//
//	// ==============================================================================
//

//
//	// ==============================================================================
//

//
//	// ==============================================================================
//
//	public void addLevel(String le_country, String le_flag, int le_web_id) {
//		open();
//
//		String query = "SELECT MAX(" + LE_ORDER + ") AS max_order FROM " + TABLE_LEVELS;
//
//		Cursor cursor = database.rawQuery(query, null);
//
//		cursor.moveToFirst();
//		int max_order = cursor.getInt(cursor.getColumnIndex("max_order"));
//
//		ContentValues v = new ContentValues();
//		v.put("le_country", le_country);
//		v.put("le_flag", le_flag);
//		v.put("le_open", 0);
//		v.put("le_completed", 0);
//		v.put("le_flag_sdcard", 1);
//		v.put("le_order", max_order + 1);
//		v.put("le_web_id", le_web_id);
//
//		database.insert("levels", null, v);
//
//	}
//
//	// ==============================================================================
//

//
//	// ==============================================================================
//
//	// public Cursor getLevels2() {
//	//
//	// String query = "SELECT * FROM " + TABLE_LEVELS + " ORDER BY  " + LE_ORDER
//	// + " ASC";
//	// Cursor cursor = database.rawQuery(query, null);
//	//
//	// cursor.moveToFirst();
//	// return cursor;
//	//
//	// }
//	//
//	// public void addLevels2(String le_country, int le_id) {
//	//
//	// ContentValues v = new ContentValues();
//	// v.put("le_country", le_country);
//	// v.put("le_flag", le_country + ".png");
//	// v.put("le_open", 0);
//	// v.put("le_completed", 0);
//	// v.put("le_flag_sdcard", 0);
//	// v.put("le_order", le_id);
//	// v.put("le_status", 1);
//	// v.put("le_web_id", le_id);
//	//
//	// database.insert("levels_2", null, v);
//	//
//	// }
//	//
//	// public Cursor getLogos2() {
//	//
//	// String query = "SELECT * FROM " + TABLE_LOGOS + " ORDER BY  " + LO_LEVEL
//	// + " ASC";
//	// Cursor cursor = database.rawQuery(query, null);
//	//
//	// cursor.moveToFirst();
//	// return cursor;
//	//
//	// }
//	//
//	// public void addLogos2(String lo_name, int lo_level, String lo_wikipedia,
//	// String lo_info, String lo_player, int lo_id) {
//	//
//	// ContentValues v = new ContentValues();
//	// v.put("lo_name", lo_name);
//	// v.put("lo_image", String.valueOf(lo_id) + ".png");
//	// v.put("lo_level", lo_level);
//	// v.put("lo_wikipedia", lo_wikipedia);
//	// v.put("lo_info", lo_info);
//	// v.put("lo_player", lo_player);
//	// v.put("lo_tries", 0);
//	// v.put("lo_points", 0);
//	// v.put("lo_completed", "0");
//	// v.put("lo_image_sdcard", 0);
//	// v.put("lo_order", lo_id);
//	// v.put("lo_status", 1);
//	// v.put("lo_web_id", lo_id);
//	//
//	// database.insert("logos_2", null, v);
//	//
//	// }
//
//	// ==============================================================================
//
//	public void open() throws SQLException {
//		database = dbHandler.getWritableDatabase();
//
//	}
//
//	// ==============================================================================
//
//	public void closeDatabase() {
//		dbHandler.close();
//	}
//
//	// ==============================================================================
//
//}
