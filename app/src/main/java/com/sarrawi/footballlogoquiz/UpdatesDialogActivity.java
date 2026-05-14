package com.sarrawi.footballlogoquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class UpdatesDialogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(UpdatesDialogActivity.this);
		builder.setTitle("Check Updates");
		builder.setMessage("There are new updates. Check for updates from settings.");

		builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(UpdatesDialogActivity.this, SettingsActivity.class);
				finish();
				startActivity(intent);
			}

		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				finish();
			}

		});

		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
					finish();
				return false;
			}
		});

		builder.show();
	}

	// ==============================================================================

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
	}


}
