package com.pwr.zpi;

import android.app.Activity;
import android.os.Bundle;

public class PlaningActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planing_activity);
	}


	@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
}