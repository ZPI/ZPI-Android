package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WorkoutsActivity extends Activity implements OnClickListener{
	
	Button addActionButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_activity);
		
		addActionButton = (Button) findViewById(R.id.buttonNewIntervalAction);
		addActionButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v== addActionButton)
		{
			startActivity(new Intent(WorkoutsActivity.this,NewWorkoutActionActivity.class));
		}
		
	}

}
