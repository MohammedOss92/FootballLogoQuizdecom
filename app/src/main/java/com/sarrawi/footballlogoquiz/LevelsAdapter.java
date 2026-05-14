package com.sarrawi.footballlogoquiz;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.sarrawi.footballlogoquiz.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LevelsAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	Context context;
	Typeface tf;

	String leFlagDir;
	String siteUrl;

	private ImageLoader imgLoader;

	public LevelsAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		context = a;

		imgLoader = new ImageLoader(activity);

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// ==============================================================================

	public int getCount() {
		return data.size();
	}

	// ==============================================================================

	public Object getItem(int position) {
		return position;
	}

	// ==============================================================================

	public long getItemId(int position) {
		return position;
	}

	// ==============================================================================

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.level_row_grid, null);
		} else {
			vi = convertView;
		}

		HashMap<String, String> level = new HashMap<String, String>();
		level = data.get(position);

		TextView leCountry = (TextView) vi.findViewById(R.id.leCountry); // country
		leCountry.setText(level.get(LevelsActivity.KEY_COUNTRY).toUpperCase().trim());

		if (level.get(LevelsActivity.KEY_OPEN).equals("1")) {

			LinearLayout unlockedArea = (LinearLayout) vi.findViewById(R.id.unlockedArea);
			unlockedArea.setVisibility(View.VISIBLE);

			ImageView lock = (ImageView) vi.findViewById(R.id.lock); // flag
			lock.setVisibility(View.GONE);

			ImageView leFlag = (ImageView) vi.findViewById(R.id.leFlag); // flag
			TextView leScore = (TextView) vi.findViewById(R.id.leScore);
			TextView leLogosNum = (TextView) vi.findViewById(R.id.leLogosNum);

			ProgressBar logosBrogress = (ProgressBar) vi.findViewById(R.id.logosProgress);

			int sLogos = Integer.parseInt(level.get(LevelsActivity.KEY_COMPLETED_LOGOS_COUNT));
			int tLogos = Integer.parseInt(level.get(LevelsActivity.KEY_LOGOS_COUNT));
			logosBrogress.setMax(tLogos);

			logosBrogress.setProgress(sLogos);

			// Setting all values in listview

			leScore.setText("Score : " + level.get(LevelsActivity.KEY_LEVEL_SCORE));
			leLogosNum.setText(level.get(LevelsActivity.KEY_COMPLETED_LOGOS_COUNT) + "/" + level.get(LevelsActivity.KEY_LOGOS_COUNT));

			if (Integer.parseInt(level.get(LevelsActivity.KEY_FLAG_SDCARD)) == 0) {
				AssetManager assetManager = context.getAssets();
				InputStream istr = null;
				try {
					istr = assetManager.open("levels/" + level.get(LevelsActivity.KEY_FLAG));
				} catch (IOException e) {
					Log.e("assets", assetManager.toString());
					e.printStackTrace();
				}
				Bitmap bmp = BitmapFactory.decodeStream(istr);
				leFlag.setImageBitmap(bmp);
			} else {

				siteUrl = context.getResources().getString(R.string.siteUrl);

				leFlagDir = siteUrl + "global/uploads/levels/";
				imgLoader.DisplayImage(leFlagDir + level.get(LevelsActivity.KEY_FLAG), leFlag);

			}
		} else {
			LinearLayout unlockedArea = (LinearLayout) vi.findViewById(R.id.unlockedArea);
			unlockedArea.setVisibility(View.GONE);

			ImageView lock = (ImageView) vi.findViewById(R.id.lock); // flag
			lock.setVisibility(View.VISIBLE);
		}

		return vi;
	}
}