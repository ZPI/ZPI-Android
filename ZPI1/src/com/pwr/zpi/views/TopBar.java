package com.pwr.zpi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pwr.zpi.R;

public class TopBar extends RelativeLayout {
	
	private View myTopBar;
	
	private Button rightButton;
	private TextView titleTextView;
	private RelativeLayout leftButton;
	private TextView leftButtonTextView;
	private String titleText;
	private String rightButtonText;
	private String leftButtonText;
	private ImageView leftBackArrowImageView;
	private boolean hasLeftButton;
	private boolean hasRightButton;
	private boolean hasTitle;
	
	// Constructor start
	public TopBar(Context context) {
		this(context, null);
		
		init(context);
	}
	
	public TopBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public TopBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopBar, 0, 0);
		
		try {
			hasLeftButton = typedArray.getBoolean(R.styleable.TopBar_hasLeftButton, false);
			hasRightButton = typedArray.getBoolean(R.styleable.TopBar_hasRightButton, false);
			hasTitle = typedArray.getBoolean(R.styleable.TopBar_hasTitle, true);
			titleText = typedArray.getString(R.styleable.TopBar_titleText);
			leftButtonText = typedArray.getString(R.styleable.TopBar_leftButtonText);
			rightButtonText = typedArray.getString(R.styleable.TopBar_rightButtonText);
			
		}
		finally {
			typedArray.recycle();
		}
		
		init(context);
		
	}
	
	private void init(Context mContext) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		myTopBar = inflator.inflate(R.layout.top_bar, null);
		
		this.addView(myTopBar);
		myTopBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		initializeReference();
	}
	
	private void initializeReference() {
		
		leftButton = (RelativeLayout) myTopBar.findViewById(R.id.relativeLayoutTopBarBackButton);
		rightButton = (Button) myTopBar.findViewById(R.id.buttonTopBarRightButton);
		titleTextView = (TextView) myTopBar.findViewById(R.id.textViewTopBarTitle);
		leftButtonTextView = (TextView) myTopBar.findViewById(R.id.textViewTopBarBack);
		leftBackArrowImageView = (ImageView) myTopBar.findViewById(R.id.imageViewTopBarBackArrow);
		
		initData();
		
	}
	
	public void initData() {
		if (!hasLeftButton) {
			leftButton.setVisibility(View.INVISIBLE);
		}
		if (!hasRightButton) {
			rightButton.setVisibility(View.INVISIBLE);
		}
		if (!hasTitle) {
			titleTextView.setVisibility(View.INVISIBLE);
		}
		
		sendToDisplay();
	}
	
	public void reset() {
		initData();
		sendToDisplay();
	}
	
	private void sendToDisplay() {
		
		rightButton.setText(rightButtonText);
		leftButtonTextView.setText(leftButtonText);
		titleTextView.setText(titleText);
	}
	
	public Button getRightButton() {
		return rightButton;
	}
	
	public RelativeLayout getLeftButton() {
		return leftButton;
	}
	
	public void setLeftButtonText(String text) {
		leftButtonText = text;
		sendToDisplay();
	}
	
	public void setLeftButtonText(int resource) {
		leftButtonText = getContext().getString(resource);
		sendToDisplay();
	}
	
	public void setRightButtonText(String text) {
		rightButtonText = text;
		sendToDisplay();
	}
	
	public void setRightButtonText(int resource) {
		rightButtonText = getContext().getString(resource);
		sendToDisplay();
	}
	
	public void showLeftArrowImageView(boolean show) {
		leftBackArrowImageView.setVisibility(show ? View.VISIBLE : View.GONE);
	}
	
}
