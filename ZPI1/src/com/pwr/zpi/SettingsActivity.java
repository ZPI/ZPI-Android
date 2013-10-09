package com.pwr.zpi;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
	}

	@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_down_anim, R.anim.out_down_anim);
	}
	
}