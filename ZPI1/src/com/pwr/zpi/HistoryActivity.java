package com.pwr.zpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pwr.zpi.adapters.RunAdapter;
import com.pwr.zpi.database.Database;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.dialogs.MyDialog;
import com.pwr.zpi.listeners.GestureListener;
import com.pwr.zpi.listeners.MyGestureDetector;

public class HistoryActivity extends Activity implements GestureListener,
	OnItemClickListener {
	
	GestureDetector gestureDetector;
	MyGestureDetector myGestureDetector;
	private View.OnTouchListener gestureListener;
	private ListView listViewThisWeek;
	private ListView listViewThisMonth;
	private ListView listViewAll;
	private ImageButton mainSceenButton;
	
	private static final String TAB_SPEC_1_TAG = "TabSpec1";
	private static final String TAB_SPEC_2_TAG = "TabSpec2";
	private static final String TAB_SPEC_3_TAG = "TabSpec3";
	public static final String ID_TAG = "id";
	public static final String DISTANCE_TAG = "dist";
	public static final String TIME_TAG = "time";
	public static final String NAME_TAG = "name";
	List<SingleRun> run_data;
	
	private static final int FILTER_MONTH = 0;
	private static final int FILTER_WEEK = 1;
	
	private Button tab1Button;
	private Button tab2Button;
	private Button tab3Button;
	
	private TabHost tabHost;
	private View mCurrent;
	// context item stuff
	AdapterContextMenuInfo info;
	RunAdapter adapter;
	RunAdapter adapterThisWeek;
	RunAdapter adapterThisMonth;
	RunAdapter adapterThisAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
		prepareGestureListener();
		initTabs();
		initFields();
		addListeners();
	}
	
	private void initTabs()
	{
		tabHost = (TabHost) findViewById(R.id.tabhostHistory);
		tabHost.setup();
		TabSpec tabSpecs = tabHost.newTabSpec(TAB_SPEC_1_TAG);
		tabSpecs.setContent(R.id.tabThisWeek);
		tabSpecs.setIndicator(getResources().getString(R.string.this_week));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_2_TAG);
		tabSpecs.setContent(R.id.tabThisMonth);
		tabSpecs.setIndicator(getResources().getString(R.string.this_month));
		tabHost.addTab(tabSpecs);
		tabSpecs = tabHost.newTabSpec(TAB_SPEC_3_TAG);
		tabSpecs.setContent(R.id.tabAll);
		tabSpecs.setIndicator(getResources().getString(R.string.all));
		tabHost.addTab(tabSpecs);
		
		LinearLayout tab1 = (LinearLayout) findViewById(R.id.linearLayoutHistoryTab1);
		LinearLayout tab2 = (LinearLayout) findViewById(R.id.linearLayoutHistoryTab2);
		LinearLayout tab3 = (LinearLayout) findViewById(R.id.linearLayoutHistoryTab3);
		tab1Button = (Button) tab1.findViewById(R.id.buttonTabLeft);
		tab2Button = (Button) tab2.findViewById(R.id.buttonTabMiddle);
		tab3Button = (Button) tab3.findViewById(R.id.buttonTabRight);
		tab1Button.setText(getResources().getString(R.string.this_week));
		tab2Button.setText(getResources().getString(R.string.this_month));
		tab3Button.setText(getResources().getString(R.string.all));
		setTab(0);
		
	}
	
	private void initFields()
	{
		listViewThisWeek = (ListView) findViewById(R.id.listViewThisWeek);
		listViewThisMonth = (ListView) findViewById(R.id.listViewThisMonth);
		listViewAll = (ListView) findViewById(R.id.listViewAll);
		
		run_data = readfromDB();
		
		Collections.sort(run_data);
		adapterThisAll = new RunAdapter(this,
			R.layout.history_run_list_item, run_data);
		ArrayList<SingleRun> run_data_month = (ArrayList<SingleRun>) removeOlderThen(
			new ArrayList<SingleRun>(run_data), FILTER_MONTH);
		adapterThisMonth = new RunAdapter(this,
			R.layout.history_run_list_item, run_data_month);
		ArrayList<SingleRun> run_data_week = (ArrayList<SingleRun>) removeOlderThen(new ArrayList<SingleRun>(
			run_data_month), FILTER_WEEK);
		adapterThisWeek = new RunAdapter(this,
			R.layout.history_run_list_item, run_data_week);
		listViewThisWeek.setAdapter(adapterThisWeek);
		listViewThisMonth.setAdapter(adapterThisMonth);
		listViewAll.setAdapter(adapterThisAll);
		
		registerForContextMenu(listViewThisWeek);
		registerForContextMenu(listViewThisMonth);
		registerForContextMenu(listViewAll);
		
		mainSceenButton = (ImageButton) findViewById(R.id.buttonHistoryMainScreen);
		//adapterThisWeek.getFilter().filter();
	}
	
	private List<SingleRun> removeOlderThen(List<SingleRun> runs, int type)
	{
		Calendar cal = Calendar.getInstance();
		switch (type) {
			case FILTER_MONTH:
				cal.add(Calendar.DATE, -31);        //czy 30? czy wyœwietlaæ tylko bie¿¹cy miesi¹c?
				break;
			case FILTER_WEEK:
				cal.add(Calendar.DATE, -7);
				break;
			default:
				break;
		}
		
		Date lastMonth = cal.getTime();
		Iterator<SingleRun> it = runs.iterator();
		while (it.hasNext())
		{
			SingleRun singleRun = it.next();
			if (singleRun.getStartDate().before(lastMonth)) {
				it.remove();
			}
		}
		
		return runs;
	}
	
	private List<SingleRun> readfromDB() {
		Database db = new Database(this);
		List<SingleRun> runs;
		runs = db.getAllRuns();
		if (runs == null) {
			runs = new ArrayList<SingleRun>();
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayoutNoRunHistory);
			rl.setVisibility(View.VISIBLE);
			tabHost.setVisibility(View.GONE);
			
		}
		return runs;
	}
	
	private void prepareGestureListener() {
		// Gesture detection
		myGestureDetector = new MyGestureDetector(this,
			false, false, true, false);
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
		
	}
	
	@Override
	public void onLeftToRightSwipe() {
		
		finish();
		overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
	}
	
	@Override
	public void onRightToLeftSwipe() {
		// TODO Auto-generated method stub
		
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
		listViewThisWeek.setOnItemClickListener(this);
		listViewThisMonth.setOnItemClickListener(this);
		listViewAll.setOnItemClickListener(this);
		listViewThisWeek.setOnTouchListener(gestureListener);
		listViewThisMonth.setOnTouchListener(gestureListener);
		listViewAll.setOnTouchListener(gestureListener);
		mainSceenButton.setOnTouchListener(gestureListener);
		tab1Button.setOnTouchListener(gestureListener);
		tab2Button.setOnTouchListener(gestureListener);
		tab3Button.setOnTouchListener(gestureListener);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
		long id) {
		if (!myGestureDetector.isFlingDetected())
		{
			Intent intent = new Intent(HistoryActivity.this,
				SingleRunHistoryActivity.class);
			SingleRun selectedValue = (SingleRun) adapter
				.getItemAtPosition(position);
			intent.putExtra(ID_TAG, selectedValue.getRunID());
			intent.putExtra(DISTANCE_TAG, selectedValue.getDistance());
			intent.putExtra(TIME_TAG, selectedValue.getRunTime());
			intent.putExtra(NAME_TAG, selectedValue.getName());
			startActivity(intent);
			overridePendingTransition(R.anim.in_left_anim, R.anim.out_left_anim);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		switch (v.getId()) {
			case R.id.listViewAll:
			case R.id.listViewThisMonth:
			case R.id.listViewThisWeek:
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
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_action_menuitem:
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				adapter = null; // when adapters will change use interface here ;)
				switch (tabHost.getCurrentTab()) {
					case 0:
						adapter = (RunAdapter) listViewThisWeek.getAdapter();
						break;
					case 1:
						adapter = (RunAdapter) listViewThisMonth.getAdapter();
						break;
					case 2:
						adapter = (RunAdapter) listViewAll.getAdapter();
						break;
					default:
						break;
				}
				if (adapter != null) {
					MyDialog dialog = new MyDialog();
					DialogInterface.OnClickListener positiveButtonHandler = new DialogInterface.OnClickListener() {
						
						// romove
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							SingleRun toDelete = adapter.getItem(info.position);
							
							adapterThisWeek.remove(toDelete);
							adapterThisMonth.remove(toDelete);
							adapterThisAll.remove(toDelete);
							Database db = new Database(HistoryActivity.this);
							db.deleteRun(toDelete.getRunID());
							db.close();
						}
					};
					dialog.showAlertDialog(this, R.string.dialog_message_romve,
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
	public void onSingleTapConfirmed(MotionEvent e) {
		if (mCurrent != null)
		{
			switch (mCurrent.getId())
			{
				case R.id.buttonHistoryMainScreen:
					finish();
					overridePendingTransition(R.anim.in_right_anim, R.anim.out_right_anim);
					
					break;
				default:
					if (mCurrent == tab1Button) {
						setTab(0);
					}
					else if (mCurrent == tab2Button) {
						setTab(1);
					}
					if (mCurrent == tab3Button) {
						setTab(2);
					}
					break;
			}
		}
		
	}
	
	private void setTab(int nr)
	{
		tabHost.setCurrentTab(nr);
		switch (nr)
		{
			case 0:
				tab1Button.setSelected(true);
				tab2Button.setSelected(false);
				tab3Button.setSelected(false);
				break;
			case 1:
				tab2Button.setSelected(true);
				tab1Button.setSelected(false);
				tab3Button.setSelected(false);
				break;
			case 2:
				tab3Button.setSelected(true);
				tab2Button.setSelected(false);
				tab1Button.setSelected(false);
				break;
		}
	}
	
}