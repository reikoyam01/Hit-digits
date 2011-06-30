package jp.android.hitdigits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HitDigits extends Activity {
	/** Called when the activity is first created. */
	final int MAX_ELEM = 80;
	int max_num = 20;
	int[] digits = new int[MAX_ELEM + 1];
	public int digitsleft = MAX_ELEM;
	int ufo_num = 0;
	int[] txtViewIds = new int[MAX_ELEM + 1];
	TextView[] txtViews = new TextView[MAX_ELEM + 1];
	long sTime = -1;

	int hitsound1;
	int hitsound2;
	int clearsound;
	int MAX_SOUND_COUNT = 3;
	SoundPool soundPool;
	boolean sound_on = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// SET button
		Button btnset = (Button) findViewById(R.id.btnSet);
		btnset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sTime < 0)
					sTime = System.currentTimeMillis();

				digits[0] = (digits[0] + 1) % 10;
				setdigitimage(0);
			}
		});

		// Hit button
		Button btnhit = (Button) findViewById(R.id.btnHit);
		btnhit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int l = 1;

				if (sTime < 0)
					sTime = System.currentTimeMillis();

				for (l = 1; l <= max_num; l++) {
					if ((digits[0] == digits[l]) && (setimagegone(l) != 0)) {
						if (digits[l] == ufo_num) {
							playsound(hitsound2);
						} else {
							playsound(hitsound1);
						}
						digitsleft--;
						Log.v("test", "digitsleft=" + digitsleft);
						if (digitsleft <= 0) {
							playsound(clearsound);
							stageclear();
						}
						break;
					}
				}
			}
		});

		// Reset button
		Button btnReset = (Button) findViewById(R.id.btnRestart);
		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getNumPref();
				setdigits();
				ufo_num = (int) Math.round(Math.random() * 10) % 10;
				sTime = -1;
			}
		});
		
		// Initialize
		makedigits(MAX_ELEM);
		soundPool = new SoundPool(MAX_SOUND_COUNT, AudioManager.STREAM_MUSIC, 0);
		hitsound1 = soundPool.load(this, R.raw.sound1, 1);
		hitsound2 = soundPool.load(this, R.raw.sound0, 1);
		clearsound = soundPool.load(this, R.raw.sound0, 1);
		
		//
		getNumPref();
		setdigits();
		ufo_num = (int) Math.round(Math.random() * 10) % 10;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	// MENU - Settings
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem pref_item = menu.add("settings");
		OnMenuItemClickListener listener = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				openPref();
				return false;
			}
		};
		pref_item.setOnMenuItemClickListener(listener);
		return true;
	}

	public void openPref() {
		// Intent intent = new Intent(this, (Class<?>)Setting.class);
		// startActivity(intent);
		Intent intent = new Intent(this, (Class<?>) Setting.class);
		// intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	private void makedigits(int num) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);

		txtViews[0] = (TextView) findViewById(R.id.txtView0);
		int i = 1;
		for (i = 1; i <= num; i++) {
			if( txtViews[i] == null){
				txtViews[i] = new TextView(this);
				//txtViews[i].setTextSize(txtViews[0].getTextSize());
				txtViews[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
				layout.addView(txtViews[i]);
			}
		}
	}
	
	private void setdigits() {
		int l = 1;

		digits[0] = 0;
		digitsleft = max_num;
		setdigitimage(0);

		// RANDOM
		for (l = 1; l <= max_num; l++) {
			digits[l] = (int) Math.round(Math.random() * 10) % 10;
			setdigitimage(l);
		}
		for(; l <= MAX_ELEM; l++){
			digits[l] = -1;
			txtViews[l].setVisibility(View.GONE);			
		}

		TextView text = (TextView) findViewById(R.id.txtClear);
		text.setText("");
	}

	private void setdigitimage(int Id) {

		TextView txtView = txtViews[Id];

		txtView.setText("" + digits[Id]);
		txtView.setVisibility(View.VISIBLE);
	}

	private int setimagegone(int Id) {
		TextView txtView = txtViews[Id];
		int ret = 0;

		if (txtView.getVisibility() != View.GONE) {
			txtView.setVisibility(View.GONE);
			ret = 1;
		}

		return ret;
	}

	private void stageclear() {
		TextView text = (TextView) findViewById(R.id.txtClear);

		long eTime = System.currentTimeMillis();
		Log.v("Test", "time" + (eTime - sTime));
		text.setText("TIME: " + (eTime - sTime) );
		sTime = -1;
	}
	
	private void playsound(int soundid){
		getSoundPref();
		if(sound_on){
			soundPool.play(soundid, 1.0F, 1.0F, 0, 0, 1.0F);
		}
	}

	private void getSoundPref() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean checkBox = sp.getBoolean("settings_sound", true);
		sound_on = checkBox;
	}
	
	private void getNumPref() {
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		String text = sp.getString("settings_num", "30");
		int new_num = Integer.parseInt(text);
		if( max_num != new_num ){		
			max_num = new_num;
		}
	}
}