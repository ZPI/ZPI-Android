package com.pwr.zpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pwr.zpi.adapters.AdapterFactory;
import com.pwr.zpi.adapters.AdapterFactory.AdapterType;
import com.pwr.zpi.adapters.GenericBaseAdapter;
import com.pwr.zpi.adapters.WorkoutActionsRowBuilder;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.views.CustomPicker;
import com.pwr.zpi.views.TopBar;

public class NewWorkoutActivity extends Activity implements OnClickListener, OnItemClickListener, TextWatcher {
	
	public static final int MY_REQUEST_CODE_ADD = 1;
	public static final int MY_REQUEST_CODE_EDIT = 2;
	private RelativeLayout addActionButton;
	private Button addThisWorkoutButton;
	private EditText workautNameEditText;
	private CheckBox isWarmUpCheckBox;
	private RelativeLayout warmUpLayout;
	private CustomPicker repeatPicker;
	private int editedPos;
	private ArrayList<WorkoutAction> workoutsActionList;
	private BaseAdapter workoutActionAdapter;
	private ListView workoutsListView;
	private AdapterContextMenuInfo info;
	private TextView workoutsRepeatsTextView;
	private Workout workout;
	private EditText picerEditText;
	private TopBar topBar;
	private RelativeLayout topBarLeftButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_workout_activity);
		initFields();
		addListeners();
		boolean isEdited = setDataIfEdit();
		if (!isEdited) {
			workautNameEditText.findFocus();
			workautNameEditText.selectAll();
		}
		else {
			//TODO remove focus from edit text, nie dziala mi google i nie moge sprawdzic jak to zrobic....
		}
	}
	
	private void initFields() {
		workoutsListView = (ListView) findViewById(R.id.listViewActions);
		workoutsActionList = new ArrayList<WorkoutAction>();
		
		View header = getLayoutInflater().inflate(R.layout.new_workout_header, null);
		View footer = getLayoutInflater().inflate(R.layout.new_workout_footer, null);
		workoutsListView.addHeaderView(header);
		workoutsListView.addFooterView(footer);
		workoutActionAdapter = AdapterFactory.getAdapter(AdapterType.WorkoutActionAdapter, this, workoutsActionList,
			null);
		workoutsListView.setAdapter(workoutActionAdapter);
		
		//all buttons are in header and footer
		addActionButton = (RelativeLayout) footer.findViewById(R.id.buttonNewWorkoutAction);
		addThisWorkoutButton = (Button) footer.findViewById(R.id.buttonWorkoutAdd);
		workautNameEditText = (EditText) header.findViewById(R.id.editTextWorkoutName);
		workoutsRepeatsTextView = (TextView) footer.findViewById(R.id.TextViewNewWorkoutRepeatValue);
		warmUpLayout = (RelativeLayout) footer.findViewById(R.id.RelativeLayoutNewWokoutWarmUp);
		
		int nr = getIntent().getIntExtra(PlaningActivity.WORKOUTS_NUMBER_TAG, 0);
		workautNameEditText.setText(getResources().getString(R.string.workouts) + (nr + 1));
		
		repeatPicker = (CustomPicker) footer.findViewById(R.id.customPickerRepeat);
		topBar = (TopBar) header.findViewById(R.id.topBarNewWorkout);
		
		topBarLeftButton = topBar.getLeftButton();
		
		workoutsRepeatsTextView.setText(repeatPicker.getValue() + " " + getResources().getString(R.string.times));
		
		isWarmUpCheckBox = (CheckBox) footer.findViewById(R.id.ToggleButtonWormUp);
		
		picerEditText = repeatPicker.getDisplayEditText();
		registerForContextMenu(workoutsListView);
	}
	
	private void addListeners() {
		addActionButton.setOnClickListener(this);
		addThisWorkoutButton.setOnClickListener(this);
		workoutsListView.setOnItemClickListener(this);
		picerEditText.addTextChangedListener(this);
		topBarLeftButton.setOnClickListener(this);
		warmUpLayout.setOnClickListener(this);
	}
	
	private void showActionChooseDialog() {
		final CharSequence[] items = getResources().getStringArray(R.array.choose_action);
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
					case 0:
						startActivityForResult(
							new Intent(NewWorkoutActivity.this, NewWorkoutActionSimpleActivity.class),
							MY_REQUEST_CODE_ADD);
						break;
					case 1:
						startActivityForResult(new Intent(NewWorkoutActivity.this,
							NewWorkoutActionAdvancedActivity.class), MY_REQUEST_CODE_ADD);
						break;
				}
				
			}
		};
		MyDialog.showAlertDialog(this, R.string.add_action, R.string.empty_string, R.string.empty_string,
			R.string.empty_string, null, null, items, itemsHandler);
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == addActionButton) {
			if (MainScreenActivity.REDUCED_VERSION) {
				startActivityForResult(new Intent(this, NewWorkoutActionSimpleActivity.class), MY_REQUEST_CODE_ADD);
			}
			else {
				showActionChooseDialog();
			}
		}
		else if (v == addThisWorkoutButton) {
			Intent returnIntent = new Intent();
			if (workout == null) {
				workout = new Workout();
			}
			workout.setName(workautNameEditText.getText().toString());
			workout.setRepeatCount(repeatPicker.getValue());
			workout.setWarmUp(isWarmUpCheckBox.isChecked());
			workout.setActions(workoutsActionList);
			
			returnIntent.putExtra(Workout.TAG, workout);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		else if (v == topBarLeftButton) {
			finish();
		}
		else if (v == warmUpLayout) {
			isWarmUpCheckBox.setChecked(!isWarmUpCheckBox.isChecked());
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
			case MY_REQUEST_CODE_ADD:
				
				if (resultCode == RESULT_OK) {
					WorkoutAction workoutAction = data.getParcelableExtra(WorkoutAction.TAG);
					workoutsActionList.add(workoutAction);
					workoutActionAdapter.notifyDataSetChanged();
				}
				if (resultCode == RESULT_CANCELED) {
					//Write your code if there's no result
				}
				break;
			case MY_REQUEST_CODE_EDIT:
				if (resultCode == RESULT_OK) {
					long ID = workoutsActionList.get(editedPos).getID();
					int index = workoutsActionList.get(editedPos).getOrderNumber();
					WorkoutAction workoutAction = data.getParcelableExtra(WorkoutAction.TAG);
					workoutsActionList.set(editedPos, workoutAction);
					workoutActionAdapter.notifyDataSetChanged();
					if (workout != null) {
						Database db = new Database(this);
						db.deleteWorkoutAction(workout.getID(), ID);
						db.updateWorkoutAction(ID, workoutAction, index);
					}
				}
				break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		editedPos = position - 1;
		
		WorkoutAction action = (WorkoutAction) adapter.getItemAtPosition(position);
		Intent intent = new Intent();
		if (action.isSimple()) {
			intent = new Intent(this, NewWorkoutActionSimpleActivity.class);
		}
		else if (action.isAdvanced()) {
			intent = new Intent(this, NewWorkoutActionAdvancedActivity.class);
		}
		
		intent.putExtra(WorkoutAction.TAG, action);
		startActivityForResult(intent, MY_REQUEST_CODE_EDIT);
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_action_menuitem:
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				
				if (workoutActionAdapter != null) {
					DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
						
						// remove
						@SuppressWarnings("unchecked")
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							WorkoutAction toDelete = (WorkoutAction) workoutActionAdapter.getItem(info.position - 1);
							((GenericBaseAdapter<WorkoutAction, WorkoutActionsRowBuilder>) workoutActionAdapter)
								.remove(toDelete);
							if (workout != null) {
								Database db = new Database(NewWorkoutActivity.this);
								db.deleteWorkoutAction(workout.getID(), toDelete.getID());
								db.close();
							}
						}
					};
					MyDialog.showAlertDialog(this, R.string.dialog_message_remove_action, R.string.empty_string,
						android.R.string.yes, android.R.string.no, positiveButtonHandler, null);
					
				}
				break;
			default:
				break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		switch (v.getId()) {
			case R.id.listViewActions:
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
				menu.setHeaderTitle(R.string.menu_ctx_actions);
				break;
			default:
				break;
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	//set privious data if we are editing view
	private boolean setDataIfEdit() {
		Intent intent = getIntent();
		if (intent.hasExtra(Workout.TAG)) {
			workout = intent.getParcelableExtra(Workout.TAG);
			workautNameEditText.setText(workout.getName());
			isWarmUpCheckBox.setChecked(workout.isWarmUp());
			repeatPicker.setValue(workout.getRepeatCount());
			workoutsActionList.addAll(workout.getActions());
			workoutActionAdapter.notifyDataSetChanged();
			addThisWorkoutButton.setText(R.string.edit_workout);
			return true;
		}
		return false;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		workoutsRepeatsTextView.setText(s + " " + getResources().getString(R.string.times));
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
