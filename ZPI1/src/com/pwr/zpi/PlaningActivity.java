package com.pwr.zpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.adapters.WorkoutAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

public class PlaningActivity extends Activity implements GestureListener, OnClickListener, OnItemClickListener {
	
	public static final int MY_RESULT_CODE = 2;
	public static final String WORKOUTS_NUMBER_TAG = "work_count";
	GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private Button newWorkoutButton;
	private ListView workoutsListView;
	private ArrayList<Workout> workoutsList;
	private WorkoutAdapter workoutAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planing_activity);
		prepareGestureListener();
		addListeners();
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tab1);
		tabSpecs.setIndicator("Workouts");
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tab2);
		tabSpecs.setIndicator("Workouts");
		tabHost.addTab(tabSpecs);
		
		workoutsList = getWorkoutsFromDB();
		workoutAdapter = new WorkoutAdapter(this, R.layout.workouts_list_item, workoutsList);
		
		workoutsListView = (ListView) findViewById(R.id.listViewWorkouts);
		workoutsListView.setAdapter(workoutAdapter);
		newWorkoutButton = (Button) findViewById(R.id.buttonNewWorkout);
		newWorkoutButton.setOnClickListener(this);
	}
	
	private ArrayList<Workout> getWorkoutsFromDB()
	{
		ArrayList<Workout> workouts;
		Database database = new Database(this);
		workouts = (ArrayList<Workout>) database.getAllWorkoutNames();
		return (workouts != null) ? workouts : new ArrayList<Workout>();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
	}
	
	private void prepareGestureListener() {
		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector(this, false, false, false, true));
		gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == newWorkoutButton)
		{
			Intent intent = new Intent(PlaningActivity.this, NewWorkoutActivity.class);
			intent.putExtra(WORKOUTS_NUMBER_TAG, workoutsList.size());
			startActivityForResult(intent, MY_RESULT_CODE);
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int positon, long id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == MY_RESULT_CODE) {
			
			if (resultCode == RESULT_OK) {
				Workout workout = new Workout();
				ArrayList<WorkoutAction> actions = data.getParcelableArrayListExtra(NewWorkoutActivity.LIST_TAG);
				workout.setActions(actions);
				
				workout.setRepeatCount(data.getIntExtra(NewWorkoutActivity.REPEAT_TAG, 1));
				workout.setName(data.getStringExtra(NewWorkoutActivity.NAME_TAG));
				workout.setWarmUp(data.getBooleanExtra(NewWorkoutActivity.WORMUP_TAG, false));
				Database database = new Database(this);
				database.insertWorkout(workout);
				workoutsList.add(workout);
				workoutAdapter.notifyDataSetChanged();
			}
			if (resultCode == RESULT_CANCELED) {
				//Write your code if there's no result
			}
		}
	}
}