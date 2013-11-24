package com.pwr.zpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.adapters.WorkoutAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class PlaningActivity extends FragmentActivity implements GestureListener, OnItemClickListener {
	public static final String ID_TAG = "id";
	public static final String IS_NEW_TAG = "is_new";
	public static final int MY_REQUEST_CODE_ADD = 1;
	public static final int MY_REQUEST_CODE_EDIT = 2;
	public static final int WORKOUT_REQUEST2 = 0x3;
	public static final String WORKOUTS_NUMBER_TAG = "work_count";
	GestureDetector gestureDetector;
	MyGestureDetector myGestureDetector;
	private View.OnTouchListener gestureListener;
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private Button newWorkoutButton;
	private ListView workoutsListView;
	private ArrayList<Workout> workoutsList;
	private WorkoutAdapter workoutAdapter;
	private View mCurrent;
	private AdapterContextMenuInfo info;
	private CaldroidFragment caldroidFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planing_activity);
		prepareGestureListener();
		
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tab1);
		tabSpecs.setIndicator(getResources().getString(R.string.workouts));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tab2);
		tabSpecs.setIndicator(getResources().getString(R.string.trening_plans));
		tabHost.addTab(tabSpecs);
		
		workoutsList = getWorkoutsFromDB();
		workoutAdapter = new WorkoutAdapter(this, R.layout.workouts_list_item, workoutsList);
		
		workoutsListView = (ListView) findViewById(R.id.listViewWorkouts);
		workoutsListView.setAdapter(workoutAdapter);
		newWorkoutButton = (Button) findViewById(R.id.buttonNewWorkout);
		//newWorkoutButton.setOnClickListener(this);
		registerForContextMenu(workoutsListView);
		
		prepareCalendar(savedInstanceState);
		
		addListeners();
	}
	
	private void prepareCalendar(Bundle savedInstanceState) {
		caldroidFragment = new CaldroidFragment();
		// If Activity is created after rotation
		if (savedInstanceState != null) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
		}
		// If activity is created from fresh
		else {
			Bundle args = new Bundle();
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
			
			// Uncomment this to customize startDayOfWeek
			// args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
			// CaldroidFragment.TUESDAY); // Tuesday
			caldroidFragment.setArguments(args);
		}
		
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.tab2, caldroidFragment);
		t.commit();
	}
	
	private ArrayList<Workout> getWorkoutsFromDB() {
		ArrayList<Workout> workouts;
		Database database = new Database(this);
		workouts = (ArrayList<Workout>) database.getAllWorkoutNames();
		database.close();
		return (workouts != null) ? workouts : new ArrayList<Workout>();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
	
	private void prepareGestureListener() {
		// Gesture detection
		myGestureDetector = new MyGestureDetector(this, false, false, false, true);
		gestureDetector = new GestureDetector(this, myGestureDetector);
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mCurrent = v;
				return gestureDetector.onTouchEvent(event);
			}
		};
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureListener.onTouch(null, event);
	}
	
	@Override
	public void onLeftToRightSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRightToLeftSwipe() {
		finish();
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
	
	@Override
	public void onUpToDownSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDownToUpSwipe() {
		// TODO Auto-generated method stub
		
	}
	
	private void addListeners() {
		newWorkoutButton.setOnTouchListener(gestureListener);
		workoutsListView.setOnTouchListener(gestureListener);
		workoutsListView.setOnItemClickListener(this);
		
		//FIXME  do the listener...
		caldroidFragment.setCaldroidListener(new CaldroidListener() {
			
			@Override
			public void onSelectDate(Date date, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLongClickDate(Date date, View view) {
				// TODO Auto-generated method stub
				super.onLongClickDate(date, view);
			}
			
			@Override
			public void onChangeMonth(int month, int year) {
				// TODO Auto-generated method stub
				super.onChangeMonth(month, year);
			}
			
			@Override
			public void onCaldroidViewCreated() {
				// TODO Auto-generated method stub
				super.onCaldroidViewCreated();
			}
		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (!myGestureDetector.isFlingDetected()) {
			Intent intent = new Intent(PlaningActivity.this, WorkoutActivity.class);
			Workout workout = (Workout) adapter.getItemAtPosition(position);
			//new workout
			
			intent.putExtra(IS_NEW_TAG, false);
			intent.putExtra(ID_TAG, workout.getID());
			
			startActivityForResult(intent, WORKOUT_REQUEST2);
			//overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
			
		}
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case MY_REQUEST_CODE_ADD:
				
				if (resultCode == RESULT_OK) {
					Workout workout = data.getParcelableExtra(Workout.TAG);
					
					Database database = new Database(this);
					workout.setID(database.insertWorkout(workout));
					database.close();
					workoutsList.add(workout);
					workoutAdapter.notifyDataSetChanged();
				}
				break;
			case WORKOUT_REQUEST2:
				if (resultCode == RESULT_OK) {
					setResult(RESULT_OK, data);
					finish();
					overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
				}
				break;
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_action_menuitem:
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				
				if (workoutAdapter != null) {
					MyDialog dialog = new MyDialog();
					DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
						
						// romove
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							Workout toDelete = workoutAdapter.getItem(info.position);
							workoutAdapter.remove(toDelete);
							Database db = new Database(PlaningActivity.this);
							db.deleteWorkout(toDelete.getID());
							db.close();
						}
					};
					dialog.showAlertDialog(this, R.string.dialog_message_remove_workout, R.string.empty_string,
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
			case R.id.listViewWorkouts:
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);
				menu.setHeaderTitle(R.string.menu_ctx_actions);
				break;
			default:
				break;
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public void onSingleTapConfirmed(MotionEvent e) {
		View v = mCurrent;
		if (v == newWorkoutButton) {
			Intent intent = new Intent(PlaningActivity.this, NewWorkoutActivity.class);
			intent.putExtra(WORKOUTS_NUMBER_TAG, workoutsList.size());
			startActivityForResult(intent, MY_REQUEST_CODE_ADD);
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
	}
	
}