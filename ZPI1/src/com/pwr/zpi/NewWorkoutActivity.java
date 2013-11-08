package com.pwr.zpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.pwr.zpi.adapters.WorkoutActionsAdapter;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.views.CustomPicker;

public class NewWorkoutActivity extends Activity implements OnClickListener {
	
	public static final String NAME_TAG = "name";
	public static final String LIST_TAG = "list";
	public static final String REPEAT_TAG = "repeat";
	public static final String WORMUP_TAG = "wormup";
	
	public static final int MY_RESULT_CODE = 1;
	private Button addActionButton;
	private Button addThisWorkoutButton;
	private EditText workautNameEditText;
	private ToggleButton isWarmUpToggleButton;
	private CustomPicker repeatPicker;
	
	ArrayList<WorkoutAction> workoutsActionList;
	WorkoutActionsAdapter workoutActionAdapter;
	ListView workautsListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_workout_activity);
		initFields();
		addListeners();
	}
	
	private void initFields()
	{
		workautsListView = (ListView) findViewById(R.id.listViewActions);
		workoutsActionList = new ArrayList<WorkoutAction>();
		
		View header = getLayoutInflater().inflate(R.layout.new_workout_header, null);
		View footer = getLayoutInflater().inflate(R.layout.new_workout_footer, null);
		workautsListView.addHeaderView(header);
		workautsListView.addFooterView(footer);
		workoutActionAdapter = new WorkoutActionsAdapter(this, R.layout.workouts_action_list_item, workoutsActionList);
		workautsListView.setAdapter(workoutActionAdapter);
		
		//all buttons are in header and footer
		addActionButton = (Button) footer.findViewById(R.id.buttonNewWorkoutAction);
		addThisWorkoutButton = (Button) footer.findViewById(R.id.buttonWorkoutAdd);
		workautNameEditText = (EditText) header.findViewById(R.id.editTextWorkoutName);
		int nr = getIntent().getIntExtra(PlaningActivity.WORKOUTS_NUMBER_TAG, 0);
		workautNameEditText.setText(getResources().getString(R.string.workouts) + (nr + 1));
		workautNameEditText.selectAll();
		repeatPicker = (CustomPicker) footer.findViewById(R.id.customPickerRepeat);
		isWarmUpToggleButton = (ToggleButton) footer.findViewById(R.id.ToggleButtonWormUp);
	}
	
	private void addListeners()
	{
		addActionButton.setOnClickListener(this);
		addThisWorkoutButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v == addActionButton)
		{
			startActivityForResult(new Intent(this, NewWorkoutActionActivity.class), MY_RESULT_CODE);
		}
		else if (v == addThisWorkoutButton)
		{
			Intent returnIntent = new Intent();
			returnIntent.putExtra(NAME_TAG, workautNameEditText.getText().toString());
			returnIntent.putParcelableArrayListExtra(LIST_TAG, workoutsActionList);
			returnIntent.putExtra(WORMUP_TAG, isWarmUpToggleButton.isChecked());	//TODO przetestowaæ czy dzia³a ok
			returnIntent.putExtra(REPEAT_TAG, repeatPicker.getValue());
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == MY_RESULT_CODE) {
			
			if (resultCode == RESULT_OK) {
				WorkoutAction workoutAction = data.getParcelableExtra(WorkoutAction.TAG);
				workoutsActionList.add(workoutAction);
				workoutActionAdapter.notifyDataSetChanged();
			}
			if (resultCode == RESULT_CANCELED) {
				//Write your code if there's no result
			}
		}
	}
	
}
