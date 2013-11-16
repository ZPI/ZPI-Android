package com.pwr.zpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.pwr.zpi.adapters.WorkoutActionsAdapter;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.views.CustomPicker;

public class NewWorkoutActivity extends Activity implements OnClickListener, OnItemClickListener {
	
	public static final String NAME_TAG = "name";
	public static final String LIST_TAG = "list";
	public static final String REPEAT_TAG = "repeat";
	public static final String WORMUP_TAG = "wormup";
	
	public static final int MY_REQUEST_CODE_ADD = 1;
	public static final int MY_REQUEST_CODE_EDIT = 2;
	private Button addActionButton;
	private Button addThisWorkoutButton;
	private EditText workautNameEditText;
	private ToggleButton isWarmUpToggleButton;
	private CustomPicker repeatPicker;
	private int editedPos;
	private ArrayList<WorkoutAction> workoutsActionList;
	private WorkoutActionsAdapter workoutActionAdapter;
	private ListView workoutsListView;
	private AdapterContextMenuInfo info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_workout_activity);
		initFields();
		addListeners();
	}
	
	private void initFields()
	{
		workoutsListView = (ListView) findViewById(R.id.listViewActions);
		workoutsActionList = new ArrayList<WorkoutAction>();
		
		View header = getLayoutInflater().inflate(R.layout.new_workout_header, null);
		View footer = getLayoutInflater().inflate(R.layout.new_workout_footer, null);
		workoutsListView.addHeaderView(header);
		workoutsListView.addFooterView(footer);
		workoutActionAdapter = new WorkoutActionsAdapter(this, R.layout.workouts_action_list_item, workoutsActionList);
		workoutsListView.setAdapter(workoutActionAdapter);
		
		//all buttons are in header and footer
		addActionButton = (Button) footer.findViewById(R.id.buttonNewWorkoutAction);
		addThisWorkoutButton = (Button) footer.findViewById(R.id.buttonWorkoutAdd);
		workautNameEditText = (EditText) header.findViewById(R.id.editTextWorkoutName);
		int nr = getIntent().getIntExtra(PlaningActivity.WORKOUTS_NUMBER_TAG, 0);
		workautNameEditText.setText(getResources().getString(R.string.workouts) + (nr + 1));
		workautNameEditText.selectAll();
		repeatPicker = (CustomPicker) footer.findViewById(R.id.customPickerRepeat);
		isWarmUpToggleButton = (ToggleButton) footer.findViewById(R.id.ToggleButtonWormUp);
		registerForContextMenu(workoutsListView);
	}
	
	private void addListeners()
	{
		addActionButton.setOnClickListener(this);
		addThisWorkoutButton.setOnClickListener(this);
		workoutsListView.setOnItemClickListener(this);
	}
	
	private void showActionChooseDialog()
	{
		final CharSequence[] items = getResources().getStringArray(R.array.choose_action);
		MyDialog dialog = new MyDialog();
		DialogInterface.OnClickListener itemsHandler = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				switch (item)
				{
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
		dialog.showAlertDialog(this, R.string.dialog_choose_what_to_display, R.string.empty_string,
			R.string.empty_string, R.string.empty_string, null, null, items, itemsHandler);
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == addActionButton)
		{
			if (MainScreenActivity.REDUCED_VERSION) {
				startActivityForResult(new Intent(this, NewWorkoutActionSimpleActivity.class), MY_REQUEST_CODE_ADD);
			}
			else {
				showActionChooseDialog();
			}
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
					WorkoutAction workoutAction = data.getParcelableExtra(WorkoutAction.TAG);
					workoutsActionList.set(editedPos, workoutAction);
					workoutActionAdapter.notifyDataSetChanged();
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
					MyDialog dialog = new MyDialog();
					DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
						
						// romove
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							WorkoutAction toDelete = workoutActionAdapter.getItem(info.position - 1);
							workoutActionAdapter.remove(toDelete);
						}
					};
					dialog.showAlertDialog(this, R.string.dialog_message_remove_action,
						R.string.empty_string, android.R.string.yes,
						android.R.string.no, positiveButtonHandler, null);
					
				}
				break;
			default:
				break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
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
}
