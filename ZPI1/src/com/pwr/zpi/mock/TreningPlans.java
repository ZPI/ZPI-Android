package com.pwr.zpi.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionAdvanced;
import com.pwr.zpi.database.entity.WorkoutActionSimple;

public class TreningPlans {
	
	// for shared preferences
	public static final String TRENING_PLANS_IS_ENABLED_KEY = "is_enabled_trening_plan";
	public static final String TRENING_PLANS_ID_KEY = "trening_plan_key";
	public static final String TRENING_PLANS_START_DATE_KEY = "trening_plan_start_date";
	public static final String TRENING_PLAN_LAST_WORKOUT_DATE = "trening_plan_last_workout_date";
	
	static ArrayList<TreningPlan> plans;
	
	//TODO write plans here
	static {
		plans = new ArrayList<TreningPlan>();
		TreningPlan plan = new TreningPlan();
		plan.setName("Name");
		HashMap<Integer, Workout> w = new HashMap<Integer, Workout>();
		w.put(0, getSimpleWorkout2());
		w.put(2, new Workout()); // it means that 2 days from start of trening plan there's workout(empty)
		w.put(4, getSimpleWorkout());
		w.put(6, getSimpleWorkout2());
		w.put(8, new Workout());
		plan.setWorkouts(w);
		plans.add(plan);
		
		setPlansIDs();
	}
	
	private static void setPlansIDs() {
		long index = 0;
		for (TreningPlan plan : plans) {
			plan.setID(index);
			index++;
		}
	}
	
	private static Workout getSimpleWorkout() {
		Workout workout = new Workout();
		List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
		actions.add(new WorkoutActionAdvanced(23, 3D));
		actions.add(new WorkoutActionSimple(WorkoutAction.ACTION_SIMPLE_SPEED_FAST, WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME, 5000));
		actions.add(new WorkoutActionSimple(WorkoutAction.ACTION_SIMPLE_SPEED_SLOW, WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME, 9000));
		actions.add(new WorkoutActionSimple(WorkoutAction.ACTION_SIMPLE_SPEED_STEADY, WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE, 500));
		workout.setActions(actions);
		workout.setWarmUp(true);
		workout.setRepeatCount(1);
		return workout;
	}
	
	private static Workout getSimpleWorkout2() {
		Workout workout = new Workout();
		List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
		actions.add(new WorkoutActionAdvanced(10, 1D));
		actions.add(new WorkoutActionSimple(WorkoutAction.ACTION_SIMPLE_SPEED_STEADY, WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_DISTANCE, 32200));
		workout.setActions(actions);
		workout.setWarmUp(false);
		workout.setID(1212);
		workout.setName("workout 1 ");
		workout.setRepeatCount(1);
		return workout;
	}
	
	public static ArrayList<TreningPlan> getPlans() {
		return plans;
	}
	
	/**
	 * @param id - index of plan in array list
	 * @return
	 */
	public static TreningPlan getTreningPlan(long id) {
		return id == -1 ? null : plans.get((int) id);
	}
}
