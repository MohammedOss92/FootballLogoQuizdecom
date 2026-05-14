package com.sarrawi.footballlogoquiz;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import android.view.View;
import android.view.View.OnClickListener;


public class GameActivity extends Activity implements OnTouchListener {

	int minimunCompleted = 5;
	GridView ballsGrid;

	LinearLayout spacesGrid1, spacesGrid2, spacesGrid3, spacesGrid4;
	TextView[] spaceViews;

	RelativeLayout completedLayout;
	ImageButton back;
	BallsAdapter baAdapter;

	DAO db;
	Cursor c;

	String loImageDir;
	private ImageLoader imgLoader;

	ArrayList<HashMap<String, String>> ballsArray;
	HashMap<String, String> ballsMap;

	ArrayList<HashMap<String, String>> spacesArray;
	HashMap<String, String> spacesMap;

	ArrayList<HashMap<String, String>> positionsArray;
	HashMap<String, String> positionsMap;

	static final String KEY_ID = "_loid";
	static final String KEY_NAME = "lo_name";
	static final String KEY_LEVEL = "lo_level";
	static final String KEY_WIKIPEDIA = "lo_wikipedia";
	static final String KEY_LETTER = "lo_letter";
	static final String KEY_PLAYER = "lo_player";
	static final String KEY_INFO = "lo_info";
	static final String KEY_IMAGE = "lo_image";
	static final String KEY_TRIES = "lo_tries";
	static final String KEY_POINTS = "lo_points";
	static final String KEY_COMPLETED = "lo_completed";
	static final String KEY_IMAGE_SDCARD = "lo_image_sdcard";
	static final String KEY_WEB_ID = "lo_web_id";

	static final String KEY_BALL = "ball";
	static final String KEY_SPACE = "space";

	static final String KEY_BALL_POSITION = "ball_position";
	static final String KEY_SPACE_POSITION = "space_position";

	int countSpaces;
	int loTries;
	int loPoints;
	int result;
	int hints;

	String logoId;
	String siteUrl, urlToShare;

	Animation animSlideDown, animSlideUp, animBlink, animShake, animShakeWhistle, animShakeBall, animWrongFade;
	MediaPlayer sound;
	String loName, loImageFile, isLoCompleted, loWikipedia, loLetter, loPlayer, loInfo, loImageSDCard;
	int loLevel, loWebId;
	char[] alphabetBallsArray, alphabetSpacesArray;

	SharedPreferences mSharedPreferences;
	Editor editor;

	LinearLayout leftHints, rightHints;
	RelativeLayout layout;
	TextView hintsTitle, hintsValue;
	TextView wrong;

	ImageButton hide, letter, player, facebook, twitter, info;

	ImageView loImage;

	LayoutInflater layoutInflater;
	View popuplayout;

	View popupView;
	PopupWindow popupWindow;
	Button btnOk;

	int isLetterHintOn = 0;

	private InterstitialAd interstitial;

	// ==============================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		imgLoader = new ImageLoader(getApplicationContext());

		AdView ad = (AdView) findViewById(R.id.adView);
		if (ad != null) {
			ad.loadAd(new AdRequest.Builder().build());
		}
		
		// Create the interstitial.
		AdRequest interstitialRequest = new AdRequest.Builder().build();
		InterstitialAd.load(this, getString(R.string.interstitial_id), interstitialRequest,
				new InterstitialAdLoadCallback() {
					@Override
					public void onAdLoaded(InterstitialAd ad) {
						interstitial = ad; // تم التحميل بنجاح
					}

					@Override
					public void onAdFailedToLoad(LoadAdError adError) {
						interstitial = null; // فشل التحميل
					}
				});
		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.

		db = new DAO(this);
		db.open();

		siteUrl = getResources().getString(R.string.siteUrl);

		logoId = getIntent().getStringExtra("LogoId");
		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
		editor = mSharedPreferences.edit();

		layout = (RelativeLayout) findViewById(R.id.titleBar);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGII.TTF");

		TextView title = (TextView) layout.findViewById(R.id.title);
		title.setText("");

		hintsTitle = (TextView) layout.findViewById(R.id.scoreTitle);
		hintsTitle.setTypeface(tf);
		hintsTitle.setTextColor(Color.parseColor("#82b440"));
		hintsTitle.setText("HINTS");

		hintsValue = (TextView) layout.findViewById(R.id.scoreValue);

		hintsValue.setTypeface(tf);
		hintsValue.setTextColor(Color.parseColor("#82b440"));
		hintsValue.setText(String.valueOf(getHintsNumber()));

		hide = (ImageButton) findViewById(R.id.hide);
		letter = (ImageButton) findViewById(R.id.letter);
		player = (ImageButton) findViewById(R.id.player);
		facebook = (ImageButton) findViewById(R.id.facebook);
		twitter = (ImageButton) findViewById(R.id.twitter);
		info = (ImageButton) findViewById(R.id.info);

		// spacesGrid = (GridView) findViewById(R.id.spacesGrid);

		spacesGrid1 = (LinearLayout) findViewById(R.id.spacesGrid1);
		spacesGrid2 = (LinearLayout) findViewById(R.id.spacesGrid2);
		spacesGrid3 = (LinearLayout) findViewById(R.id.spacesGrid3);
		spacesGrid4 = (LinearLayout) findViewById(R.id.spacesGrid4);
		ballsGrid = (GridView) findViewById(R.id.ballsGrid);

		completedLayout = (RelativeLayout) findViewById(R.id.completedLayout);
		loImage = (ImageView) findViewById(R.id.logo);

		leftHints = (LinearLayout) findViewById(R.id.leftHints);
		rightHints = (LinearLayout) findViewById(R.id.rightHints);

		c = db.getOneLogo(logoId);

//		if (c.getCount() != 0) {
//			loTries = c.getInt(c.getColumnIndex("lo_tries"));
//			loName = c.getString(c.getColumnIndex("lo_name")).trim();
//			loImageFile = c.getString(c.getColumnIndex("lo_image")).trim();
//			loLevel = c.getInt(c.getColumnIndex("lo_level"));
//			loWikipedia = c.getString(c.getColumnIndex("lo_wikipedia")).trim();
//			loLetter = c.getString(c.getColumnIndex("lo_letter"));
//			loPlayer = c.getString(c.getColumnIndex("lo_player")).trim();
//			loInfo = c.getString(c.getColumnIndex("lo_info")).trim();
//			isLoCompleted = c.getString(c.getColumnIndex("lo_completed"));
//			loImageSDCard = c.getString(c.getColumnIndex("lo_image_sdcard"));
//			loWebId = c.getInt(c.getColumnIndex("lo_web_id"));
//		}

		// نفترض أن الاستعلام يتم هنا، نستخدم try-with-resources للإغلاق التلقائي
		try (Cursor c = db.getOneLogo(logoId)) {

			if (c != null && c.moveToFirst()) {

				// 1. استخراج أرقام الأعمدة أولاً لضمان الأداء والأمان
				int idxTries = c.getColumnIndex("lo_tries");
				int idxName = c.getColumnIndex("lo_name");
				int idxImage = c.getColumnIndex("lo_image");
				int idxLevel = c.getColumnIndex("lo_level");
				int idxWiki = c.getColumnIndex("lo_wikipedia");
				int idxLetter = c.getColumnIndex("lo_letter");
				int idxPlayer = c.getColumnIndex("lo_player");
				int idxInfo = c.getColumnIndex("lo_info");
				int idxComp = c.getColumnIndex("lo_completed");
				int idxSdCard = c.getColumnIndex("lo_image_sdcard");
				int idxWebId = c.getColumnIndex("lo_web_id");

				// 2. التحقق من صحة أرقام الأعمدة (≥ 0) لتجنب الانهيار
				if (idxTries != -1 && idxName != -1 && idxImage != -1) {

					loTries = c.getInt(idxTries);

					// استخدام trim() بأمان بعد التأكد من أن القيمة ليست null
					String name = c.getString(idxName);
					loName = (name != null) ? name.trim() : "";

					String img = c.getString(idxImage);
					loImageFile = (img != null) ? img.trim() : "";

					loLevel = c.getInt(idxLevel);

					String wiki = c.getString(idxWiki);
					loWikipedia = (wiki != null) ? wiki.trim() : "";

					loLetter = c.getString(idxLetter);

					String player = c.getString(idxPlayer);
					loPlayer = (player != null) ? player.trim() : "";

					String info = c.getString(idxInfo);
					loInfo = (info != null) ? info.trim() : "";

					isLoCompleted = c.getString(idxComp);
					loImageSDCard = c.getString(idxSdCard);
					loWebId = c.getInt(idxWebId);
				}
			}
		} catch (Exception e) {
			Log.e("DatabaseError", "لا يمكن عدّ البيانات أو استخراجها مجددًا إثر خطأ في القاعدة", e);
		}



		if (db.getLogosCount(loLevel) < 5) {
				minimunCompleted = db.getLogosCount(loLevel);
			}

			if (loLetter == null || loLetter.equals("")) {
				loLetter = "1000";
			}

			if (Integer.parseInt(loImageSDCard) == 0) {
				AssetManager assetManager = getAssets();
				InputStream istr = null;
				try {
					istr = assetManager.open("logos/" + loImageFile);
				} catch (IOException e) {
					Log.e("assets", assetManager.toString());
					e.printStackTrace();
				}
				Bitmap bmp = BitmapFactory.decodeStream(istr);
				loImage.setImageBitmap(bmp);
			} else {

				siteUrl = getResources().getString(R.string.siteUrl);

				loImageDir = siteUrl + "global/uploads/logos/";

				imgLoader.DisplayImage(loImageDir + loImageFile, loImage);

			}

//			if (isLoCompleted.equals("1")) {
//
//				getCompletedLogoLayout(c.getInt(c.getColumnIndex(KEY_POINTS)));
//			}
		if ("1".equals(isLoCompleted)) {
			// 1. استخراج رقم العمود أولاً وتخزينه في متغير
			int idxPoints = c.getColumnIndex(KEY_POINTS);

			// 2. التحقق من أن العمود موجود فعلياً في قاعدة البيانات (قيمته ليست -1)
			if (idxPoints != -1) {
				getCompletedLogoLayout(c.getInt(idxPoints));
			} else {
				// في حال عدم وجود العمود، نمرر قيمة افتراضية (مثلاً 0) لتجنب توقف التطبيق
				getCompletedLogoLayout(0);
			}
		}
			else {
				final int duration = playSound(R.raw.whistle);

				animShakeWhistle = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_whistle);
				// animSlideDown.setDuration(duration);

				final ImageView whistle = (ImageView) findViewById(R.id.whistle);
				whistle.setVisibility(View.VISIBLE);
				whistle.startAnimation(animShakeWhistle);

				animShakeWhistle.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						whistle.setVisibility(View.GONE);
						loImage.post(new Runnable() {
							@Override
							public void run() {
								if (loImage.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.no_image).getConstantState())) {
									AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
									builder.setTitle("Internet Connection Error");
									builder.setMessage("You have to connect to the internet to get this logo then refresh!");
									builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int id) {
											// showInterstitialAd();

											Intent intent = getIntent();
											intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
											finish();
											startActivity(intent);

										}

									});

									builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int id) {

											Intent intent = new Intent(GameActivity.this, LogosActivity.class);
											intent.putExtra("LevelId", loLevel);
											intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											finish();
											startActivity(intent);
										}

									});

									builder.show();
								} else {

									generateSpaces(loName);
									generateLetters(loName);

									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											executeSpaceHintIfAlreadyUsed();
											executeBallsHintIfAlreadyUsed();
										}
									}, 0);

									leftHints.setVisibility(View.VISIBLE);
									rightHints.setVisibility(View.VISIBLE);

									hide.setOnClickListener(hintClickHandler);
									letter.setOnClickListener(hintClickHandler);
									player.setOnClickListener(hintClickHandler);
									facebook.setOnClickListener(hintClickHandler);
									twitter.setOnClickListener(hintClickHandler);
									info.setOnClickListener(hintClickHandler);

									if (loPlayer == null || loPlayer.trim().equals("")) {
										player.setVisibility(View.INVISIBLE);
									}

									if (loInfo == null || loInfo.trim().equals("")) {
										info.setVisibility(View.INVISIBLE);
									}

								}
							}
						});

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});

			}



		back =(ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (completedLayout.getVisibility() != View.VISIBLE) {
					AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
					builder.setTitle("Exit!");
					builder.setMessage("Are you sure you want to exit?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							// showInterstitialAd();
							Intent intent = new Intent(GameActivity.this, LogosActivity.class);
							intent.putExtra("LevelId", loLevel);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							finish();
							startActivity(intent);
						}

					});

					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
						}

					});

					builder.show();
				} else {
					Intent intent = new Intent(GameActivity.this, LogosActivity.class);
					intent.putExtra("LevelId", loLevel);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);
				}

			}
		});

		setButtonsStateForUsedHints();
	}

	// ==============================================================================

	public void generateSpaces(String loName) {

		alphabetSpacesArray = loName.toCharArray();
		spacesArray = new ArrayList<HashMap<String, String>>();

		spaceViews = new TextView[loName.length()];
		for (int i = 0; i < loName.length(); i++) {
			spacesMap = new HashMap<String, String>();
			spacesMap.put(KEY_SPACE, Character.toString(loName.charAt(i)));

			spacesArray.add(spacesMap);

			spaceViews[i] = new TextView(this);
			// letterViews[i].setText(Character.toString(loName.charAt(i)));

			Configuration config = getResources().getConfiguration();

			int width = 0;
			int height = 0;
			int textSize = 0;

			if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
				width = 30;
				height = 30;
				textSize = 24;
			} else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				width = LayoutParams.WRAP_CONTENT;
				height = LayoutParams.WRAP_CONTENT;
				textSize = 24;
			} else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
				width = 50;
				height = 50;
				textSize = 32;
			} else {
				width = 60;
				height = 60;
				textSize = 40;
			}

			// http://www.designbyexperience.com/px-to-dp-converter/

			spaceViews[i].setLayoutParams(new LayoutParams(width, height));

			spaceViews[i].setGravity(Gravity.CENTER);
			spaceViews[i].setTextColor(Color.WHITE);
			if (!Character.toString(loName.charAt(i)).equals(" ")) {
				spaceViews[i].setBackgroundResource(R.drawable.space);
				spaceViews[i].setGravity(Gravity.CENTER);
				spaceViews[i].setTextAppearance(this, android.R.style.TextAppearance_Large);
				spaceViews[i].setTextColor(Color.WHITE);
				spaceViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
				spaceViews[i].setTypeface(Typeface.DEFAULT_BOLD);
			} else {
				spaceViews[i].setBackgroundColor(Color.TRANSPARENT);
				spaceViews[i].setPadding(2, 0, 2, 0);
				spaceViews[i].setVisibility(View.INVISIBLE);
				spaceViews[i].setText(" ");
			}

			// add to layout
			if (i < 8) {
				spacesGrid1.addView(spaceViews[i]);
			} else if (i < 16) {
				spacesGrid2.addView(spaceViews[i]);
			} else if (i < 24) {
				spacesGrid3.addView(spaceViews[i]);
			} else if (i < 32) {
				spacesGrid4.addView(spaceViews[i]);
			}
			spaceViews[i].setOnClickListener(new spacesItemClickHandler(i));
		}

	}

	// ==============================================================================

	public void generateLetters(String loName) {

		int y = 18;
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		countSpaces = loName.length() - loName.replace(" ", "").length();

		alphabetBallsArray = loName.toCharArray();

		ballsArray = new ArrayList<HashMap<String, String>>();

		ballsGrid.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}

		});

		loName = loName.replace(" ", "");

		for (int i = 0; i < loName.length(); i++) {

			ballsMap = new HashMap<String, String>();
			ballsMap.put(KEY_BALL, Character.toString(loName.charAt(i)));
			ballsMap.put("is_real", "1");

			ballsArray.add(ballsMap);

			if (i == loName.length() - 1) {
				if (loName.length() >= y) {
					y = loName.length() + 4;
				}
				for (int x = loName.length(); x < y; x++) {
					Random random = new Random();
					int chNum = random.nextInt(alphabet.length());
					ballsMap = new HashMap<String, String>();
					ballsMap.put(KEY_BALL, Character.toString(alphabet.charAt(chNum)));
					ballsMap.put("is_real", "0");
					ballsArray.add(ballsMap);
				}
			}
		}

		Collections.shuffle(ballsArray);

		baAdapter = new BallsAdapter(this, ballsArray);
		ballsGrid.setAdapter(baAdapter);

		positionsArray = new ArrayList<HashMap<String, String>>();

		// Click event for single grid row
		ballsGrid.setOnItemClickListener(ballsItemClickHandler);

	}

	// ==============================================================================

	private void addLetters(int position) {
		for (int i = 0; i < spaceViews.length; i++) {

			TextView leSpace = (TextView) spaceViews[i];
			if (leSpace.getVisibility() == View.INVISIBLE) {
				continue;
			}
			// TextView leSpace = (TextView) v.findViewById(R.id.letterSpace);
			if (leSpace.getText().equals("") || leSpace.getText().equals("?")) {
				leSpace.setText(ballsArray.get(position).get(KEY_BALL).toUpperCase());

				positionsMap = new HashMap<String, String>();
				positionsMap.put(KEY_BALL_POSITION, String.valueOf(position));

				positionsMap.put(KEY_SPACE_POSITION, String.valueOf(i));

				positionsArray.add(positionsMap);

				checkIfFinal();

				break;
			}
		}

	}

	// ==============================================================================

	private void checkIfFinal() {

		if (spaceViews.length == positionsArray.size() + countSpaces) {
			for (int x = 0; x < spaceViews.length; x++) {
				// View vFinal = (View) spacesGrid1.getChildAt(x);
				TextView leSpaceFinal = (TextView) spaceViews[x];
				if (leSpaceFinal.getText().toString().equals(String.valueOf(alphabetBallsArray[x]).toUpperCase()) == false) {
					if (loTries < 4) {
						loTries++;
						db.setTries(logoId, loTries);
						result = 0;
					}
					break;
				} else {
					if (x == spaceViews.length - 1) {

						loPoints = 0;
						switch (loTries) {
						case 0:
							loPoints = 100;
							break;

						case 1:
							loPoints = 80;
							break;

						case 2:
							loPoints = 60;
							break;

						case 3:
							loPoints = 40;
							break;

						case 4:
							loPoints = 20;
							break;
						}
						result = 1;
						db.setLogoCompleted(logoId, loPoints, loLevel);
						addHints(logoId);
					}
				}

			}

			isRight(result);

		}
	}

	// ==============================================================================

	private void addHints(String loID) {
		hints = 0;
		if (loPoints == 100) {
			hints = 2;

		} else if (loPoints > 0 && loPoints < 100) {
			hints = 1;

		}
		db.addTotalHints(hints);

		hintsValue.setText(String.valueOf(getHintsNumber()));

	}

	// ==============================================================================

	private void isRight(int result) {
		editor.putInt("playingNum", mSharedPreferences.getInt("playingNum", 0) + 1);
		editor.commit();
		
		if (result == 0) {

			if (mSharedPreferences.getInt("vibrate", 1) == 1) {

				Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
				// Vibrate for 500 milliseconds
				v.vibrate(500);

				animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				loImage.startAnimation(animShake);
			}

			playSound(R.raw.wrong_crowd);

			wrong = (TextView) findViewById(R.id.wrong);
			wrong.setVisibility(View.VISIBLE);

			animWrongFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wrong_fade);
			wrong.startAnimation(animWrongFade);		

		} else {

			playSound(R.raw.right_crowd);

			int completedLogosCount = db.getLevelCompletedLogosCount(loLevel);
			if (completedLogosCount >= minimunCompleted) {
				int nextLevel = db.getNextLevel(loLevel);
				if (nextLevel != 0) {
					db.setLevelOpened(String.valueOf(nextLevel));
				}
			}
			getCompletedLogoLayout(loPoints);

		}
		
		if (mSharedPreferences.getInt("playingNum", 0) >= 3) {
			showInterstitialAd();
			editor.putInt("playingNum", 0);
			editor.commit();
		}		
	}

	// ==============================================================================

	private int playSound(int file) {

		int duration = 1000;
		sound = new MediaPlayer();

		AssetFileDescriptor fd = getResources().openRawResourceFd(file);
		try {
			if (mSharedPreferences.getInt("sound", 1) == 0) {
				sound.setVolume(0, 0);
			}
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
			duration = sound.getDuration();

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

		return duration;
	}

	// ==============================================================================

	private void getCompletedLogoLayout(int loPoints) {

		ImageView medal = (ImageView) findViewById(R.id.medal);
		TextView pointsRate = (TextView) findViewById(R.id.pointsRate);

		Drawable medalImage = null;
		String pRate = "";

		switch (loPoints) {
		case 100:
			medalImage = getResources().getDrawable(R.drawable.gold_medal);
			pRate = "perfect!";
			break;

		case 80:
			medalImage = getResources().getDrawable(R.drawable.silver_medal);
			pRate = "very good!";
			break;

		case 60:
			medalImage = getResources().getDrawable(R.drawable.silver_medal);
			pRate = "very good!";
			break;

		case 40:
			medalImage = getResources().getDrawable(R.drawable.bronze_medal);
			pRate = "good!";
			break;

		case 20:
			medalImage = getResources().getDrawable(R.drawable.bronze_medal);
			pRate = "good!";
			break;
		}

		medal.setImageDrawable(medalImage);
		pointsRate.setText(pRate.toUpperCase());

		spacesGrid1.setVisibility(View.GONE);
		spacesGrid2.setVisibility(View.GONE);
		spacesGrid3.setVisibility(View.GONE);
		spacesGrid4.setVisibility(View.GONE);
		ballsGrid.setVisibility(View.GONE);
		completedLayout.setVisibility(View.VISIBLE);

		TextView loNameView = (TextView) findViewById(R.id.loName);
		loNameView.setText(loName.toUpperCase());

		loNameView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});

		TextView pointsView = (TextView) findViewById(R.id.points);
		pointsView.setText(String.valueOf(loPoints) + " Pt");

		final ImageButton wikipedia = (ImageButton) findViewById(R.id.wikipedia);
		if (!loWikipedia.equals("") && loWikipedia != null && URLUtil.isValidUrl(loWikipedia)) {

			wikipedia.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri.parse(loWikipedia);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			});
		} else {
			wikipedia.setVisibility(View.GONE);
		}

		leftHints.setVisibility(View.GONE);
		rightHints.setVisibility(View.GONE);
		getPrevLogo();
		getNextLogo();

//		showInterstitialAd();
	}

	// ==============================================================================

	public void showInterstitialAd() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (interstitial != null) {

					interstitial.show(GameActivity.this);
				}

			}
		}, 2000);

	}

	// ==============================================================================

//	private void getPrevLogo() {
//		LinearLayout prevLogoButton = findViewById(R.id.prevLogoButton);
//		ImageView prevLogo = findViewById(R.id.prevLogo);
//
//		Cursor pCur = db.getPrevLogo(logoId, String.valueOf(loLevel));
//
//		if (pCur.getCount() > 0) {
//			final String prevLogoId = pCur.getString(pCur.getColumnIndex("_loid"));
//
//			prevLogoButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(GameActivity.this, GameActivity.class);
//					intent.putExtra("LogoId", prevLogoId);
//					finish();
//					startActivity(intent);
//				}
//			});
//
//			String prevLogoImageName = pCur.getString(pCur.getColumnIndex("lo_image")).trim();
//			String prevLogoImageSDCard = pCur.getString(pCur.getColumnIndex("lo_image_sdcard")).trim();
//
//			if (Integer.parseInt(prevLogoImageSDCard) == 0) {
//				AssetManager assetManager = getAssets();
//				InputStream istr = null;
//				try {
//					istr = assetManager.open("logos/" + prevLogoImageName);
//					Bitmap bmp = BitmapFactory.decodeStream(istr);
//					prevLogo.setImageBitmap(bmp);
//				} catch (IOException e) {
//					Log.e("assets", assetManager.toString());
//					e.printStackTrace();
//				}
//			} else {
//				String siteUrl = getResources().getString(R.string.siteUrl);
//				String loImageDir = siteUrl + "global/uploads/logos/";
//				imgLoader.DisplayImage(loImageDir + prevLogoImageName, prevLogo);
//			}
//
//		} else {
//			prevLogoButton.setVisibility(View.GONE);
//		}
//
//		pCur.close(); // مهم جدًا لإغلاق الكورسور بعد الاستخدام
//	}

	private void getPrevLogo() {
		LinearLayout prevLogoButton = findViewById(R.id.prevLogoButton);
		ImageView prevLogo = findViewById(R.id.prevLogo);

		// استخدام try-with-resources لضمان إغلاق الكورسور تلقائيًا (إدارة تلقائية للموارد)
		try (Cursor pCur = db.getPrevLogo(logoId, String.valueOf(loLevel))) {

			// فحص وجود نتائج وتحريك المؤشر للصف الأول (ضروري جدًا)
			if (pCur != null && pCur.moveToFirst()) {

				// 1. استخراج الفهارس (Indices) أولاً لتجنب تحذيرات SDK 35
				int idIdx = pCur.getColumnIndex("_loid");
				int imgIdx = pCur.getColumnIndex("lo_image");
				int sdIdx = pCur.getColumnIndex("lo_image_sdcard");

				// التحقق من أن الأعمدة المطلوبة موجودة في قاعدة البيانات
				if (idIdx != -1 && imgIdx != -1 && sdIdx != -1) {

					final String prevLogoId = pCur.getString(idIdx);

					prevLogoButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(GameActivity.this, GameActivity.class);
							intent.putExtra("LogoId", prevLogoId);
							finish();
							startActivity(intent);
						}
					});

					// قراءة البيانات مع معالجة المسافات الزائدة (trim)
					String imgName = pCur.getString(imgIdx);
					String prevLogoImageName = (imgName != null) ? imgName.trim() : "";

					String sdValue = pCur.getString(sdIdx);
					String prevLogoImageSDCard = (sdValue != null) ? sdValue.trim() : "0";

					// معالجة عرض صورة الشعار السابق
					if ("0".equals(prevLogoImageSDCard)) {
						AssetManager assetManager = getAssets();
						// استخدام try-with-resources لفتح وتيار البيانات لضمان إغلاقه
						try (InputStream istr = assetManager.open("logos/" + prevLogoImageName)) {
							Bitmap bmp = BitmapFactory.decodeStream(istr);
							prevLogo.setImageBitmap(bmp);
						} catch (IOException e) {
							Log.e("assets", "لا يمكن فتح صورة الشعار السابق من الأصول مجددًا", e);
						}
					} else {
						String siteUrl = getResources().getString(R.string.siteUrl);
						String loImageDir = siteUrl + "global/uploads/logos/";
						imgLoader.DisplayImage(loImageDir + prevLogoImageName, prevLogo);
					}
				}
			} else {
				// لا نتائج (لا يوجد شعار سابق)، يتم إخفاء الزر فورًا
				prevLogoButton.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.e("DB_ERROR", "إثر محاولة جلب الشعار السابق حدث خطأ غير متوقع", e);
		}
		// ملاحظة: لم نعد بحاجة لـ pCur.close() يدويًا لأن try-with-resources تتولى ذلك.
	}
	// ==============================================================================

	private void getNextLogo() {
//
//		LinearLayout nextLogoButton = (LinearLayout) findViewById(R.id.nextLogoButton);
//		ImageView nextLogo = (ImageView) findViewById(R.id.nextLogo);
//
//		Cursor nCur = db.getNextLogo(logoId, String.valueOf(loLevel));
//
//		if (nCur.getCount() > 0) {
//			final String nextLogoId = nCur.getString(nCur.getColumnIndex(KEY_ID));
//			nextLogoButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(GameActivity.this, GameActivity.class);
//					intent.putExtra("LogoId", nextLogoId);
//					finish();
//					startActivity(intent);
//
//				}
//			});
//			String nextLogoImageName = nCur.getString(nCur.getColumnIndex(KEY_IMAGE)).trim();
//			String nextLogoImageSDCard = nCur.getString(nCur.getColumnIndex(KEY_IMAGE_SDCARD)).trim();
//
//			if (Integer.parseInt(nextLogoImageSDCard) == 0) {
//				AssetManager assetManager = getAssets();
//				InputStream istr = null;
//				try {
//					istr = assetManager.open("logos/" + nextLogoImageName);
//				} catch (IOException e) {
//					Log.e("assets", assetManager.toString());
//					e.printStackTrace();
//				}
//				Bitmap bmp = BitmapFactory.decodeStream(istr);
//				nextLogo.setImageBitmap(bmp);
//			} else {
//
//				siteUrl = getResources().getString(R.string.siteUrl);
//
//				loImageDir = siteUrl + "global/uploads/logos/";
//				imgLoader.DisplayImage(loImageDir + nextLogoImageName, nextLogo);
//
//			}
//
//		} else {
//			nextLogoButton.setVisibility(View.GONE);
//		}
//	}

		LinearLayout nextLogoButton = (LinearLayout) findViewById(R.id.nextLogoButton);
		ImageView nextLogo = (ImageView) findViewById(R.id.nextLogo);

// استخدام try-with-resources لضمان إغلاق nCur تلقائياً ومنع تسريب الذاكرة
		try (Cursor nCur = db.getNextLogo(logoId, String.valueOf(loLevel))) {

			// استخدام moveToFirst() بدلاً من getCount() لضمان وضع المؤشر في المكان الصحيح للقراءة
			if (nCur != null && nCur.moveToFirst()) {

				// 1. استخراج الفهارس أولاً لتجنب تحذير getColumnIndex can be -1
				int idIdx = nCur.getColumnIndex(KEY_ID);
				int imgIdx = nCur.getColumnIndex(KEY_IMAGE);
				int sdIdx = nCur.getColumnIndex(KEY_IMAGE_SDCARD);

				// التحقق من وجود الأعمدة قبل محاولة القراءة
				if (idIdx != -1 && imgIdx != -1 && sdIdx != -1) {

					final String nextLogoId = nCur.getString(idIdx);

					nextLogoButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(GameActivity.this, GameActivity.class);
							intent.putExtra("LogoId", nextLogoId);
							finish();
							startActivity(intent);
						}
					});

					String nextLogoImageName = nCur.getString(imgIdx).trim();
					String nextLogoImageSDCard = nCur.getString(sdIdx).trim();

					// معالجة عرض الصورة
					if ("0".equals(nextLogoImageSDCard)) {
						AssetManager assetManager = getAssets();
						// استخدام try-with-resources لفتح وإغلاق تيار البيانات (InputStream)
						try (InputStream istr = assetManager.open("logos/" + nextLogoImageName)) {
							Bitmap bmp = BitmapFactory.decodeStream(istr);
							nextLogo.setImageBitmap(bmp);
						} catch (IOException e) {
							Log.e("assets", "لا يمكن فتح صورة الشعار من الأصول (Assets)", e);
						}
					} else {
						siteUrl = getResources().getString(R.string.siteUrl);
						loImageDir = siteUrl + "global/uploads/logos/";
						imgLoader.DisplayImage(loImageDir + nextLogoImageName, nextLogo);
					}
				}
			} else {
				// في حال لا نتائج (لا يوجد شعار تالي)، يتم إخفاء الزر
				nextLogoButton.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.e("DB_ERROR", "حدث خطأ أثناء محاولة الانتقال للشعار التالي", e);
		}
		}
	// ==============================================================================

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	// ==============================================================================

	@Override
	public void onBackPressed() {
		if (completedLayout.getVisibility() != View.VISIBLE) {

			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			builder.setTitle("Exit!");
			builder.setMessage("Are you sure you want to exit?");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					// showInterstitialAd();
					Intent intent = new Intent(GameActivity.this, LogosActivity.class);
					intent.putExtra("LevelId", loLevel);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);
				}

			});

			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
				}

			});

			builder.show();
		} else {
			Intent intent = new Intent(GameActivity.this, LogosActivity.class);
			intent.putExtra("LevelId", loLevel);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(intent);
		}

	}

	// ==============================================================================

	private View.OnClickListener hintClickHandler = new View.OnClickListener() {
		public void onClick(View v) {
			GameActivity.this.getHint(v.getId());
		}
	};

	// ==============================================================================

	public class spacesItemClickHandler implements View.OnClickListener {
		private final int position;

		public spacesItemClickHandler(final int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			TextView leSpace = (TextView) spaceViews[position];
			if (!leSpace.getText().equals("") && position != Integer.parseInt(loLetter)) {
				for (int i = 0; i < positionsArray.size(); i++) {

					if (positionsArray.get(i).get(KEY_SPACE_POSITION).equals(String.valueOf(position))) {

						int ballPos = Integer.parseInt(positionsArray.get(i).get(KEY_BALL_POSITION));
						ballsGrid.getChildAt(ballPos).setVisibility(View.VISIBLE);

						leSpace.setText("");
						playSound(R.raw.space);

						positionsArray.remove(i);

						break;
					}

				}
			}
		}
	}

	// ==============================================================================

	private GridView.OnItemClickListener ballsItemClickHandler = new GridView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if (spaceViews.length > positionsArray.size() + countSpaces) {
				ballsGrid.getChildAt(position).setVisibility(View.INVISIBLE);

				playSound(R.raw.kick);

				addLetters(position);
			}
		}
	};

	// ==============================================================================

	private void getHint(final int viewId) {
		int remainHints = Integer.parseInt(hintsValue.getText().toString());
		if (isHintUsed(viewId) != 1) {
			if (remainHints < 1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
				builder.setTitle("Hints!");
				builder.setMessage("You don't have enough hints!");

				builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
					}

				});

				builder.show();
			} else {

				if (viewId == R.id.letter && isLetterHintOn == 1) {
					stopLetterHint();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
					builder.setTitle("Hints");

//					String message = "";
//					switch (viewId) {
//					case R.id.hide:
//						message = "Remove the wrong letters!\nCost : 1 hint";
//						break;
//					case R.id.letter:
//						message = "Show one letter!\nCost : 1 hint";
//						break;
//
//					case R.id.player:
//						message = "Show one player name!\nCost : 1 hint";
//						break;
//
//					case R.id.facebook:
//						message = "Ask you friends on facebook!\nCost : 1 hint";
//						break;
//
//					case R.id.twitter:
//						message = "Ask your friends on twitter!\nCost : 1 hint";
//						break;
//
//					case R.id.info:
//						message = "Show a clue sentence of the answer!\nCost : 1 hint";
//						break;
//					}
					String message = "";

					if (viewId == R.id.hide) {
						message = "Remove the wrong letters!\nCost : 1 hint";
					}
					else if (viewId == R.id.letter) {
						message = "Show one letter!\nCost : 1 hint";
					}
					else if (viewId == R.id.player) {
						message = "Show one player name!\nCost : 1 hint";
					}
					else if (viewId == R.id.facebook) {
						message = "Ask you friends on facebook!\nCost : 1 hint";
					}
					else if (viewId == R.id.twitter) {
						message = "Ask your friends on twitter!\nCost : 1 hint";
					}
					else if (viewId == R.id.info) {
						message = "Show a clue sentence of the answer!\nCost : 1 hint";
					}

					builder.setMessage(message);

					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							if (viewId != R.id.letter) {
								db.addUsedHint();
								hintsValue.setText(String.valueOf(getHintsNumber()));
							}
							executeHint(viewId);
						}

					});

					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
						}

					});

					builder.show();
				}

			}

		} else {
			executeHint(viewId);
		}

	}

	// ==============================================================================

	int isHintUsed(int viewId) {
		int state = 0;
		Cursor c = null;
		try {
			c = db.getHintState(logoId);
//			if (c != null && c.moveToFirst()) {
//				switch (viewId) {
//					case R.id.hide: {
//						int index = c.getColumnIndex("lo_hi_hide");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//					case R.id.letter: {
//						int index = c.getColumnIndex("lo_hi_letter");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//					case R.id.player: {
//						int index = c.getColumnIndex("lo_hi_player");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//					case R.id.facebook: {
//						int index = c.getColumnIndex("lo_hi_facebook");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//					case R.id.twitter: {
//						int index = c.getColumnIndex("lo_hi_twitter");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//					case R.id.info: {
//						int index = c.getColumnIndex("lo_hi_info");
//						if (index != -1) state = c.getInt(index);
//						break;
//					}
//				}
//			}
			if (c != null && c.moveToFirst()) {
				String columnName = "";

				if (viewId == R.id.hide) {
					columnName = "lo_hi_hide";
				}
				else if (viewId == R.id.letter) {
					columnName = "lo_hi_letter";
				}
				else if (viewId == R.id.player) {
					columnName = "lo_hi_player";
				}
				else if (viewId == R.id.facebook) {
					columnName = "lo_hi_facebook";
				}
				else if (viewId == R.id.twitter) {
					columnName = "lo_hi_twitter";
				}
				else if (viewId == R.id.info) {
					columnName = "lo_hi_info";
				}

				// تنفيذ استخراج البيانات بشكل موحد لتقليل تكرار الكود
				if (!columnName.isEmpty()) {
					int index = c.getColumnIndex(columnName);
					if (index != -1) {
						state = c.getInt(index);
					}
				}
			}
		} finally {
			if (c != null) c.close();
		}
		return state;
	}


	// ==============================================================================

	private void setButtonsStateForUsedHints() {
		Cursor c = null;
		try {
			c = db.getHintState(logoId);

			if (c != null && c.moveToFirst()) {
				// Hide button
				int hideIndex = c.getColumnIndex("lo_hi_hide");
				if (hideIndex != -1 && c.getInt(hideIndex) == 1) {
					hide.setSelected(true);
					hide.setEnabled(false);
				}

				// Letter button
				int letterIndex = c.getColumnIndex("lo_hi_letter");
				if (letterIndex != -1 && c.getInt(letterIndex) == 1) {
					letter.setSelected(true);
					letter.setEnabled(false);
				}

				// Player hint
				int playerIndex = c.getColumnIndex("lo_hi_player");
				if (playerIndex != -1 && c.getInt(playerIndex) == 1) {
					player.setAlpha(140);
				}

				// Facebook hint
				int facebookIndex = c.getColumnIndex("lo_hi_facebook");
				if (facebookIndex != -1 && c.getInt(facebookIndex) == 1) {
					facebook.setAlpha(140);
				}

				// Twitter hint
				int twitterIndex = c.getColumnIndex("lo_hi_twitter");
				if (twitterIndex != -1 && c.getInt(twitterIndex) == 1) {
					twitter.setAlpha(140);
				}

				// Info hint
				int infoIndex = c.getColumnIndex("lo_hi_info");
				if (infoIndex != -1 && c.getInt(infoIndex) == 1) {
					info.setAlpha(140);
				}
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}


	// ==============================================================================

//	private void executeHint(int viewId) {
//
//		switch (viewId) {
//		case R.id.hide:
//
//			db.updateHintState(logoId, "lo_hi_hide");
//			playSound(R.raw.explosion);
//
//			animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
//			hide.setSelected(true);
//			hide.setEnabled(false);
//
//			for (int i = 0; i < ballsArray.size(); i++) {
//				if (ballsArray.get(i).get("is_real").equals("0")) {
//
//					for (int x = 0; x < positionsArray.size(); x++) {
//						if (positionsArray.get(x).get(KEY_BALL_POSITION).equals(String.valueOf(i))) {
//							String spacePos = positionsArray.get(x).get(GameActivity.KEY_SPACE_POSITION);
//
//							TextView leSpaceHide = (TextView) spaceViews[Integer.parseInt(spacePos)];
//							// leSpaceHide.setAnimation(animBlink);
//							leSpaceHide.setText("");
//							positionsArray.remove(x);
//						}
//					}
//					ballsGrid.getChildAt(i).setAnimation(animBlink);
//					ballsGrid.getChildAt(i).setVisibility(View.INVISIBLE);
//				}
//			}
//
//			break;
//		case R.id.letter:
//
//			animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
//
//			// if (isHintUsed(R.id.letter) == 0 && loLetter.equals("1000")) {
//			isLetterHintOn = 1;
//			for (int i = 0; i < spaceViews.length; i++) {
//				final int position = i;
//				final TextView leSpaceLetter = (TextView) spaceViews[i];
//				if (leSpaceLetter.getVisibility() == View.INVISIBLE) {
//					continue;
//				}
//				if (leSpaceLetter.getText().equals("")) {
//					leSpaceLetter.setText("?");
//					// leSpaceLetter.setAnimation(animBlink);
//
//					leSpaceLetter.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//
//							if (leSpaceLetter.getText().equals("?")) {
//								db.updateHintState(logoId, "lo_hi_letter");
//								letter.setSelected(true);
//								letter.setEnabled(false);
//
//								hideBall(position);
//
//								leSpaceLetter.setText(String.valueOf(alphabetSpacesArray[position]).toUpperCase());
//								leSpaceLetter.setTextColor(Color.YELLOW);
//
//								db.addUsedHint();
//								hintsValue.setText(String.valueOf(getHintsNumber()));
//
//								db.addLetterHintPos(logoId, String.valueOf(position));
//								loLetter = String.valueOf(position);
//								stopLetterHint();
//								checkIfFinal();
//
//							}
//						}
//					});
//					animShakeBall = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_ball);
//					ballsGrid.setOnItemClickListener(new OnItemClickListener() {
//
//						@SuppressLint("MissingPermission")
//						@Override
//						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//							Toast.makeText(GameActivity.this, "Can't add a letter, stop letter hint first!", Toast.LENGTH_LONG).show();
//
//							if (mSharedPreferences.getInt("vibrate", 1) == 1) {
//
//								Vibrator v = (Vibrator) GameActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
//								if (v != null) {
//									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//										// للـ API 26 وما فوق
//										v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//									} else {
//										// للنسخ القديمة
//										v.vibrate(500);
//									}
//								}
//
//
//								animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
//								ballsGrid.startAnimation(animShake);
//							}
//
//						}
//
//					});
//				}
//			}
//
//			break;
//
//		case R.id.player:
//			db.updateHintState(logoId, "lo_hi_player");
//			player.setAlpha(140);
//
//			animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//
//			final LinearLayout playerHint = (LinearLayout) findViewById(R.id.player_popup);
//
//			TextView playerName = (TextView) findViewById(R.id.playerName);
//			playerName.setText(loPlayer.toUpperCase().trim());
//
//			playerHint.setVisibility(View.VISIBLE);
//			playerHint.bringToFront();
//			playerHint.startAnimation(animSlideDown);
//
//			btnOk = (Button) findViewById(R.id.okPlayer);
//			btnOk.setOnClickListener(new Button.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//					playerHint.startAnimation(animSlideUp);
//				}
//			});
//
//			break;
//
//		case R.id.facebook:
//			db.updateHintState(logoId, "lo_hi_facebook");
//			facebook.setAlpha(140);
//			urlToShare = siteUrl + "site/show_logo/" + String.valueOf(loWebId);
//
//			Intent fIntent = new Intent(Intent.ACTION_SEND);
//			fIntent.setType("text/plain");
//			fIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
//
//			String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
//			fIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
//
//			startActivity(fIntent);
//			break;
//
//		case R.id.twitter:
//			db.updateHintState(logoId, "lo_hi_twitter");
//			twitter.setAlpha(140);
//			urlToShare = siteUrl + "site/show_logo/" + String.valueOf(loWebId);
//			// Url shortenUrl = as("bitlyapidemo",
//			// "R_0da49e0a9118ff35f52f629d2d71bf07").call(shorten(urlToShare));
//			Intent tTntent = new Intent(Intent.ACTION_SEND);
//
//			String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s", urlEncode("Who knows this logo ? " + urlToShare));
//			tTntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
//
//			startActivity(tTntent);
//			break;
//
//		case R.id.info:
//			db.updateHintState(logoId, "lo_hi_info");
//			info.setAlpha(140);
//			animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//
//			final LinearLayout infoHint = (LinearLayout) findViewById(R.id.info_popup);
//
//			TextView infoText = (TextView) findViewById(R.id.infoText);
//			infoText.setText(loInfo.trim());
//
//			infoHint.setVisibility(View.VISIBLE);
//			infoHint.bringToFront();
//			infoHint.startAnimation(animSlideDown);
//
//			btnOk = (Button) findViewById(R.id.okInfo);
//			btnOk.setOnClickListener(new Button.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//					infoHint.startAnimation(animSlideUp);
//				}
//			});
//
//			break;
//		}
//
//	}
private void executeHint(int viewId) {

	// تحويل switch إلى if-else للتوافق مع Gradle 8+ و SDK 35
	if (viewId == R.id.hide) {
		db.updateHintState(logoId, "lo_hi_hide");
		playSound(R.raw.explosion);

		animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
		hide.setSelected(true);
		hide.setEnabled(false);

		for (int i = 0; i < ballsArray.size(); i++) {
			if ("0".equals(ballsArray.get(i).get("is_real"))) {
				for (int x = 0; x < positionsArray.size(); x++) {
					if (positionsArray.get(x).get(KEY_BALL_POSITION).equals(String.valueOf(i))) {
						String spacePos = positionsArray.get(x).get(GameActivity.KEY_SPACE_POSITION);

						TextView leSpaceHide = (TextView) spaceViews[Integer.parseInt(spacePos)];
						leSpaceHide.setText("");
						positionsArray.remove(x);
						x--; // تقليل المؤشر بعد الحذف لتجنب تجاوز العناصر
					}
				}
				if (ballsGrid.getChildAt(i) != null) {
					ballsGrid.getChildAt(i).setAnimation(animBlink);
					ballsGrid.getChildAt(i).setVisibility(View.INVISIBLE);
				}
			}
		}
	}
	else if (viewId == R.id.letter) {
		animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
		isLetterHintOn = 1;

		for (int i = 0; i < spaceViews.length; i++) {
			final int position = i;
			final TextView leSpaceLetter = (TextView) spaceViews[i];

			if (leSpaceLetter.getVisibility() == View.INVISIBLE) continue;

			if (leSpaceLetter.getText().equals("")) {
				leSpaceLetter.setText("?");
				leSpaceLetter.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (leSpaceLetter.getText().equals("?")) {
							db.updateHintState(logoId, "lo_hi_letter");
							letter.setSelected(true);
							letter.setEnabled(false);
							hideBall(position);
							leSpaceLetter.setText(String.valueOf(alphabetSpacesArray[position]).toUpperCase());
							leSpaceLetter.setTextColor(Color.YELLOW);
							db.addUsedHint();
							hintsValue.setText(String.valueOf(getHintsNumber()));
							db.addLetterHintPos(logoId, String.valueOf(position));
							loLetter = String.valueOf(position);
							stopLetterHint();
							checkIfFinal();
						}
					}
				});

				ballsGrid.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Toast.makeText(GameActivity.this, "Stop letter hint first!", Toast.LENGTH_LONG).show();
						if (mSharedPreferences.getInt("vibrate", 1) == 1) {
							Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							if (vib != null) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
									vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
								} else {
									vib.vibrate(500);
								}
							}
							animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
							ballsGrid.startAnimation(animShake);
						}
					}
				});
			}
		}
	}
	else if (viewId == R.id.player) {
		db.updateHintState(logoId, "lo_hi_player");
		player.setAlpha(140);
		animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

		final LinearLayout playerHint = findViewById(R.id.player_popup);
		TextView playerName = findViewById(R.id.playerName);
		playerName.setText(loPlayer.toUpperCase().trim());

		playerHint.setVisibility(View.VISIBLE);
		playerHint.bringToFront();
		playerHint.startAnimation(animSlideDown);

		findViewById(R.id.okPlayer).setOnClickListener(v -> {
			animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
			playerHint.startAnimation(animSlideUp);
			playerHint.setVisibility(View.GONE); // إخفاء بعد الحركة
		});
	}
	else if (viewId == R.id.facebook) {
		db.updateHintState(logoId, "lo_hi_facebook");
		facebook.setAlpha(140);
		urlToShare = siteUrl + "site/show_logo/" + loWebId;
		String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl)));
	}
	else if (viewId == R.id.twitter) {
		db.updateHintState(logoId, "lo_hi_twitter");
		twitter.setAlpha(140);
		urlToShare = siteUrl + "site/show_logo/" + loWebId;
		String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s",
				Uri.encode("Who knows this logo? " + urlToShare));
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl)));
	}
	else if (viewId == R.id.info) {
		db.updateHintState(logoId, "lo_hi_info");
		info.setAlpha(140);
		animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

		final LinearLayout infoHint = findViewById(R.id.info_popup);
		TextView infoText = findViewById(R.id.infoText);
		infoText.setText(loInfo.trim());

		infoHint.setVisibility(View.VISIBLE);
		infoHint.bringToFront();
		infoHint.startAnimation(animSlideDown);

		findViewById(R.id.okInfo).setOnClickListener(v -> {
			animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
			infoHint.startAnimation(animSlideUp);
			infoHint.setVisibility(View.GONE);
		});
	}
}
	// ==============================================================================

	private void hideBall(int pos) {

		boolean foundIt = false;
		int invisibleChar = 0;
		String spaceChar = String.valueOf(alphabetSpacesArray[pos]).toUpperCase();

		View vBallHint;
		for (int i = 0; i < ballsGrid.getChildCount(); i++) {
			vBallHint = (View) ballsGrid.getChildAt(i);

			TextView leBallLetter = (TextView) vBallHint.findViewById(R.id.letterBall);

			if (leBallLetter.getText().equals(spaceChar) && ballsArray.get(i).get("is_real").equals("1")) {

				if (vBallHint.getVisibility() == View.INVISIBLE) {

					invisibleChar = i;
					continue;

				}

				positionsMap = new HashMap<String, String>();
				positionsMap.put(KEY_BALL_POSITION, String.valueOf(i));
				positionsMap.put(KEY_SPACE_POSITION, String.valueOf(pos));
				positionsArray.add(positionsMap);

				vBallHint.setAnimation(animBlink);
				vBallHint.setVisibility(View.INVISIBLE);
				foundIt = true;
				break;
			}

		}

		if (foundIt == false) {
			for (int x = 0; x < positionsArray.size(); x++) {
				int ball = Integer.parseInt(positionsArray.get(x).get(KEY_BALL_POSITION));
				int space = Integer.parseInt(positionsArray.get(x).get(KEY_SPACE_POSITION));
				if (ball == invisibleChar) {
					String thisChar = String.valueOf(alphabetSpacesArray[space]).toUpperCase();

					TextView leSpaceLetter = (TextView) spaceViews[space];
					if (!leSpaceLetter.getText().equals(thisChar)) {

						leSpaceLetter.setText("");
						positionsArray.remove(x);
					}

				}
			}

			vBallHint = (View) ballsGrid.getChildAt(invisibleChar);

			positionsMap = new HashMap<String, String>();
			positionsMap.put(KEY_BALL_POSITION, String.valueOf(invisibleChar));
			positionsMap.put(KEY_SPACE_POSITION, String.valueOf(pos));
			positionsArray.add(positionsMap);

			vBallHint.setAnimation(animBlink);
			vBallHint.setVisibility(View.INVISIBLE);
			foundIt = true;
		}

	}

	// ==============================================================================

	public void stopLetterHint() {
		isLetterHintOn = 0;
		for (int i = 0; i < spaceViews.length; i++) {

			final TextView leSpaceLetter2 = (TextView) spaceViews[i];
			if (leSpaceLetter2.getVisibility() == View.INVISIBLE) {
				continue;
			}
			if (leSpaceLetter2.getText().equals("?")) {
				leSpaceLetter2.setText("");
			}

			spaceViews[i].setOnClickListener(new spacesItemClickHandler(i));
		}

		ballsGrid.setOnItemClickListener(ballsItemClickHandler);
	}

	// ==============================================================================

		public void executeSpaceHintIfAlreadyUsed() {

			if (isHintUsed(R.id.letter) == 1) {
				int pos = Integer.parseInt(loLetter);

				TextView leSpaceLetter = (TextView) spaceViews[pos];
				leSpaceLetter.setText(String.valueOf(alphabetSpacesArray[pos]).toUpperCase());
				leSpaceLetter.setTextColor(Color.YELLOW);

				for (int i = 0; i < ballsGrid.getChildCount(); i++) {
					View vBallHint = (View) ballsGrid.getChildAt(i);
					TextView leBallLetter = (TextView) vBallHint.findViewById(R.id.letterBall);

					String spaceChar = String.valueOf(alphabetSpacesArray[pos]).toUpperCase();
					if (leBallLetter.getText().equals(spaceChar) && ballsArray.get(i).get("is_real").equals("1")) {
						positionsMap = new HashMap<String, String>();
						positionsMap.put(KEY_BALL_POSITION, String.valueOf(i));
						positionsMap.put(KEY_SPACE_POSITION, String.valueOf(pos));
						positionsArray.add(positionsMap);

						vBallHint.setVisibility(View.GONE);
						break;
					}
				}
			}

		}

	// ==============================================================================

	public void executeBallsHintIfAlreadyUsed() {
		if (isHintUsed(R.id.hide) == 1) {
			for (int i = 0; i < ballsArray.size(); i++) {
				if (ballsArray.get(i).get("is_real").equals("0")) {
					ballsGrid.getChildAt(i).setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	// ==============================================================================

	private static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.wtf("encoder error", "UTF-8 should always be supported", e);
			throw new RuntimeException("URLEncoder.encode() failed for " + s);
		}
	}

	// ==============================================================================

	private int getHintsNumber() {
		int hintsNumber = 0;
		Cursor cHints = null;
		try {
			cHints = db.getHintsCount();
			if (cHints != null && cHints.moveToFirst()) {
				int totalHints = cHints.getInt(cHints.getColumnIndexOrThrow("total_hints"));
				int usedHints = cHints.getInt(cHints.getColumnIndexOrThrow("used_hints"));
				hintsNumber = totalHints - usedHints;
			}
		} finally {
			if (cHints != null) {
				cHints.close();
			}
		}
		return hintsNumber;
	}

}
