package com.pwr.zpi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pwr.zpi.R;

public class CustomPicker extends RelativeLayout implements OnClickListener, OnLongClickListener, OnTouchListener {
	// DatePicker reference 
	
	private int value;
	private View myPickerView;
	
	private Button plusButton;
	private EditText displayEditText;
	private Button minusButton;
	private int min;
	private int max;
	private int digitsNumber;
	private int incrementValue;
	private Handler repeatUpdateHandler;
	private boolean mAutoIncrement = false;
	private boolean mAutoDecrement = false;
	public static final int REP_DELAY = 50;
	private static final int ORIENTATION_VERTICAL = 0;
	private static final int ORIENTATION_HORIZONTAL = 1;
	private int orientation;
	private static final int VISIBILITY_VISIBLE = 0;
	private static final int VISIBILITY_GONE = 1;
	private int visibility;
	
	// Constructor start
	public CustomPicker(Context context) {
		this(context, null);
		
		init(context);
	}
	
	public CustomPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CustomPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
			attrs,
			R.styleable.CustomPicker,
			0, 0);
		
		try {
			min = typedArray.getInteger(R.styleable.CustomPicker_minValue, 0);
			max = typedArray.getInteger(R.styleable.CustomPicker_maxValue, 0);
			digitsNumber = typedArray.getInteger(R.styleable.CustomPicker_digitNumber, 1);
			incrementValue = typedArray.getInteger(R.styleable.CustomPicker_incrementValue, 1);
			visibility = typedArray.getInteger(R.styleable.CustomPicker_editTextVisibility, VISIBILITY_VISIBLE);
			orientation = typedArray.getInteger(R.styleable.CustomPicker_orientation, ORIENTATION_VERTICAL);
		}
		finally {
			typedArray.recycle();
		}
		
		LayoutInflater inflator = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch (orientation) {
			case ORIENTATION_VERTICAL:
				myPickerView = inflator.inflate(R.layout.picker_control, null);
				break;
			case ORIENTATION_HORIZONTAL:
				myPickerView = inflator.inflate(R.layout.picker_control_horizontal, null);
				break;
		
		}
		
		this.addView(myPickerView);
		
		initializeReference();
		
	}
	
	private void init(Context mContext) {
		LayoutInflater inflator = (LayoutInflater) mContext
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		myPickerView = inflator.inflate(R.layout.picker_control, null);
		this.addView(myPickerView);
		
		initializeReference();
	}
	
	private void initializeReference() {
		repeatUpdateHandler = new Handler();
		plusButton = (Button) (myPickerView.findViewById(R.id.picker_plus));
		plusButton.setOnClickListener(this);
		plusButton.setOnLongClickListener(this);
		plusButton.setOnTouchListener(this);
		displayEditText = (EditText) (myPickerView.findViewById(R.id.picker_display));
		minusButton = (Button) (myPickerView.findViewById(R.id.picker_minus));
		minusButton.setOnClickListener(this);
		minusButton.setOnLongClickListener(this);
		minusButton.setOnTouchListener(this);
		
		if (visibility == VISIBILITY_GONE)
		{
			displayEditText.setVisibility(View.GONE);
		}
		
		initData();
		
	}
	
	public void initData() {
		value = min;
		sendToDisplay();
	}
	
	public void reset() {
		value = 0;
		initData();
		sendToDisplay();
	}
	
	private void sendToDisplay() {
		
		displayEditText.setText(String.format("%0" + digitsNumber + "d", value));
	}
	
	public void setMax(int max)
	{
		this.max = max;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int l)
	{
		value = l;
		sendToDisplay();
	}
	
	public EditText getDisplayEditText()
	{
		return displayEditText;
	}
	
	public void decrement() {
		value -= incrementValue;
		if (value < min) {
			value = max - (max - min) % incrementValue;
		}
		try {
			sendToDisplay();
		}
		catch (Exception e) {
			Log.e("", e.toString());
		}
	}
	
	public void increment() {
		value += incrementValue;
		if (value > max) {
			value = min;
		}
		try {
			sendToDisplay();
		}
		catch (Exception e) {
			Log.e("", e.toString());
		}
	}
	
	@Override
	public void onClick(View v) {
		displayEditText.requestFocus();
		
		if (v == plusButton)
		{
			increment();
		}
		else if (v == minusButton)
		{
			decrement();
		}
		
	}
	
	class RptUpdater implements Runnable {
		@Override
		public void run() {
			if (mAutoIncrement) {
				increment();
				repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
			}
			else if (mAutoDecrement) {
				decrement();
				repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
			}
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			if (mAutoIncrement) {
				mAutoIncrement = false;
			}
			if (mAutoDecrement) {
				mAutoDecrement = false;
			}
		}
		return false;
		
	}
	
	@Override
	public boolean onLongClick(View v) {
		if (v == plusButton) {
			mAutoIncrement = true;
		}
		else if (v == minusButton) {
			mAutoDecrement = true;
		}
		repeatUpdateHandler.post(new RptUpdater());
		return false;
	}
}
