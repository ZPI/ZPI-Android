package com.pwr.zpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AfterActivityActivity extends Activity implements OnClickListener {
	
	private Button buttonSave;
	private Button buttonDiscard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.after_activity_activity);
		initFields();
		addListeners();
		super.onCreate(savedInstanceState);
	}
	
	private void initFields()
	{
		buttonDiscard = (Button) findViewById(R.id.buttonAfterActivityDiscard);
		buttonSave = (Button) findViewById(R.id.buttonAfterActivitySave);
		
	}
	
	private void addListeners()
	{
		buttonDiscard.setOnClickListener(this);
		buttonSave.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		switch (v.getId())
		{
			case R.id.buttonAfterActivitySave:
				returnIntent.putExtra(ActivityActivity.SAVE_TAG, true);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			case R.id.buttonAfterActivityDiscard:
				returnIntent.putExtra(ActivityActivity.SAVE_TAG, false);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
		}
		
	}
}
