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
import android.widget.TextView;

public class LogosAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	Context context;
	Typeface tf;

	String loImageDir;
	String siteUrl;
	
	private ImageLoader imgLoader;
	
	public LogosAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.logo_row_grid, null);

		ImageView loImage = (ImageView) vi.findViewById(R.id.loImage); // logo
		ImageView loState = (ImageView) vi.findViewById(R.id.loState); // completed

		HashMap<String, String> logo = new HashMap<String, String>();
		logo = data.get(position);

		if (Integer.parseInt(logo.get(LogosActivity.KEY_IMAGE_SDCARD)) == 0) {
			AssetManager assetManager = context.getAssets();
			InputStream istr = null;
			try {
				istr = assetManager.open("logos/" + logo.get(LogosActivity.KEY_IMAGE));
			} catch (IOException e) {
				Log.e("assets", assetManager.toString());
				e.printStackTrace();
			}
			Bitmap bmp = BitmapFactory.decodeStream(istr);
			loImage.setImageBitmap(bmp);
		} else {
			
			siteUrl = context.getResources().getString(R.string.siteUrl);

			loImageDir = siteUrl + "global/uploads/logos/";			
			imgLoader.DisplayImage(loImageDir + logo.get(LogosActivity.KEY_IMAGE), loImage);

		}

		if (logo.get(LogosActivity.KEY_COMPLETED).equals("1")) {
			loState.setVisibility(View.VISIBLE);
		}

		return vi;
	}
}