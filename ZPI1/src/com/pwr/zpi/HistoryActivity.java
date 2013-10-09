package com.pwr.zpi;

import android.app.Activity;
import android.os.Bundle;

public class HistoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
	}

	@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);

    }
	
}