package com.pwr.zpi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityNoMapActivity extends AbstractActivityActivity{

	private Button buttonPause;
	private Button buttonResume;
	private Button buttonStop;
	private BigDataField field1;
	private BigDataField field2;
	private BigDataField field3;
	private BigDataField field4;
	private ImageView imageViewGoToScreen1;
	
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		setContentView(R.layout.activity_nomap_activity);
		initFields();
		field2.textView.setText("It works");
	}
	private void initFields()
	{
		buttonPause = (Button) findViewById(R.id.buttonNoMapPause);
		buttonResume = (Button) findViewById(R.id.buttonNoMapResume);
		buttonStop = (Button) findViewById(R.id.buttonNoMapStop);
		imageViewGoToScreen1 = (ImageView) findViewById(R.id.imageButtonGoToActivityActivity);
		field1 = new BigDataField();
		field2 = new BigDataField();
		field3 = new BigDataField();
		field4 = new BigDataField();
		initBigDataField(field1,findViewById(R.id.layoutNoMapField1));
		initBigDataField(field2,findViewById(R.id.layoutNoMapField2));
		initBigDataField(field3,findViewById(R.id.layoutNoMapField3));
		initBigDataField(field4,findViewById(R.id.layoutNoMapField4));
	}
	private void initBigDataField(BigDataField bigDataField, View parent)
	{
		//TODO check if this is working
		bigDataField.textView = (TextView) parent.findViewById(R.id.textViewNoMapField1);
		bigDataField.textViewDescription = (TextView) parent.findViewById(R.id.textViewNoMapField1Discription);
		bigDataField.textViewUnit = (TextView) parent.findViewById(R.id.textViewNoMapField1Unit);

	}
	
	//container for data field
	private class BigDataField{
		protected TextView textViewDescription;
		protected TextView textView;
		protected TextView textViewUnit;
	}
}
