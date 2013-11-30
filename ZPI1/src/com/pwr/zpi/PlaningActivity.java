package com.pwr.zpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.adapters.TreningPlansAdapter;
import com.pwr.zpi.adapters.WorkoutAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;
import com.pwr.zpi.mock.TreningPlans;

public class PlaningActivity extends Activity implements GestureListener, OnItemClickListener {
	public static final String ID_TAG = "id";
	public static final String IS_NEW_TAG = "is_new";
	public static final int MY_REQUEST_CODE_ADD = 1;
	public static final int MY_REQUEST_CODE_EDIT = 2;
	public static final int WORKOUT_REQUEST2 = 0x3;
	public static final String WORKOUTS_NUMBER_TAG = "work_count";
	private GestureDetector gestureDetector;
	private MyGestureDetector myGestureDetector;
	private View.OnTouchListener gestureListener;
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private Button newWorkoutButton;
	private ImageButton plansMainScreenButton;
	private ImageButton workoutsMainScreenButton;
	private ListView workoutsListView;
	private ListView traningPlansListView;
	private ArrayList<TreningPlan> plansList;
	private ArrayList<Workout> workoutsList;
	private WorkoutAdapter workoutAdapter;
	private TreningPlansAdapter plansAdapter;
	private View mCurrent;
	private AdapterContextMenuInfo info;
	
	private Button tab1Button;
	private Button tab2Button;
	private TabHost tabHost;
	private Button currentTabButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planing_activity);
		prepareGestureListener();
		
		tabHost = (TabHost) findViewById(R.id.tabhost);
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
		
		plansList = getTraningPlans();
		plansAdapter = new TreningPlansAdapter(this, R.layout.workouts_list_item, plansList);
		
		workoutsListView = (ListView) findViewById(R.id.listViewWorkouts);
		workoutsListView.setAdapter(workoutAdapter);
		traningPlansListView = (ListView) findViewById(R.id.listViewPlaningActTraningPlans);
		traningPlansListView.setAdapter(plansAdapter);
		
		newWorkoutButton = (Button) findViewById(R.id.buttonNewWorkout);
		plansMainScreenButton = (ImageButton) findViewById(R.id.imageButtonTraningPlansMainScreen);
		workoutsMainScreenButton = (ImageButton) findViewById(R.id.imageButtonPlaningActMainScreen);
		//newWorkoutButton.setOnClickListener(this);
		
		LinearLayout tab1 = (LinearLayout) findViewById(R.id.linearLayoutPlansTab1);
		LinearLayout tab2 = (LinearLayout) findViewById(R.id.linearLayoutPlansTab2);
		tab1Button = (Button) tab1.findViewById(R.id.buttonTabLeft);
		tab2Button = (Button) tab2.findViewById(R.id.buttonTabRight);
		tab1Button.setText(getResources().getString(R.string.workouts));
		tab2Button.setText(getResources().getString(R.string.trening_plans));
		currentTabButton = tab1Button;
		setTab(0);
		registerForContextMenu(workoutsListView);
		addListeners();
	}
	
	private ArrayList<TreningPlan> getTraningPlans() {
		// FIXME remove mock, change to read from db
		return TreningPlans.getPlans();
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
		plansMainScreenButton.setOnTouchListener(gestureListener);
		workoutsMainScreenButton.setOnTouchListener(gestureListener);
		traningPlansListView.setOnItemClickListener(this);
		tab1Button.setOnTouchListener(gestureListener);
		tab2Button.setOnTouchListener(gestureListener);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (!myGestureDetector.isFlingDetected()) {
			Log.i(PlaningActivity.class.getSimpleName(), "item clicked");
			if (adapter == workoutsListView) {
				Intent intent = new Intent(PlaningActivity.this, WorkoutActivity.class);
				Workout workout = (Workout) adapter.getItemAtPosition(position);
				//new workout
				
				intent.putExtra(IS_NEW_TAG, false);
				intent.putExtra(ID_TAG, workout.getID());
				
				startActivityForResult(intent, WORKOUT_REQUEST2);
				//overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
			}
			else if (adapter == traningPlansListView) {
				Log.i(PlaningActivity.class.getSimpleName(), "plan activity starting");
				Intent intent = new Intent(PlaningActivity.this, PlansActivity.class);
				TreningPlan plan = (TreningPlan) adapter.getItemAtPosition(position);
				intent.putExtra(PlansActivity.ID_KEY, plan.getID());
				
				startActivity(intent);
			}
		}
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
					MyDialog.showAlertDialog(this, R.string.dialog_message_remove_workout, R.string.empty_string,
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
		if (mCurrent != null) {
			View v = mCurrent;
			switch (v.getId())
			
			{
				case R.id.buttonNewWorkout:
					Intent intent = new Intent(PlaningActivity.this, NewWorkoutActivity.class);
					intent.putExtra(WORKOUTS_NUMBER_TAG, workoutsList.size());
					startActivityForResult(intent, MY_REQUEST_CODE_ADD);
					break;
				case R.id.imageButtonPlaningActMainScreen:
					finish();
					overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
					
					break;
				case R.id.imageButtonTraningPlansMainScreen:
					
					finish();
					overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
					
					break;
				case R.id.buttonTabLeft:
					setTab(0);
					break;
				case R.id.buttonTabRight:
					setTab(1);
					break;
			}
		}
		
	}
	
	private void setTab(int nr) {
		tabHost.setCurrentTab(nr);
		currentTabButton.setSelected(false);
		switch (nr) {
			case 0:
				tab1Button.setSelected(true);
				tab2Button.setSelected(false);
				break;
			case 1:
				tab2Button.setSelected(true);
				tab1Button.setSelected(false);
				break;
		}
	}
	
}
