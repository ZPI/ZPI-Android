package com.pwr.zpi.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.database.entity.WorkoutActionSimple;
import com.pwr.zpi.utils.Pair;

public class TreningPlans {
	
	// for shared preferences
	public static final String TRENING_PLANS_IS_ENABLED_KEY = "is_enabled_trening_plan";
	public static final String TRENING_PLANS_ID_KEY = "trening_plan_key";
	public static final String TRENING_PLANS_START_DATE_KEY = "trening_plan_start_date";
	public static final String TRENING_PLAN_LAST_WORKOUT_DATE = "trening_plan_last_workout_date";
	
	public static final long ONE_MINUTE = 60 * 1000;
	
	static ArrayList<TreningPlan> plans;
	
	//TODO write plans here
	static {
		plans = new ArrayList<TreningPlan>();
		
		plans.add(getFirstTreningPlan());
		
		setPlansIDs();
	}
	
	private static void setPlansIDs() {
		long index = 0;
		for (TreningPlan plan : plans) {
			plan.setID(index);
			index++;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static TreningPlan getFirstTreningPlan() {
		TreningPlan plan = new TreningPlan();
		plan.setName("Prosty plan treningowy");
		HashMap<Integer, Workout> w = new HashMap<Integer, Workout>();
		
		//first week
		w.put(0, getFirstTreningPlanTwoActionsWorkout(1, 5, 5));
		w.put(2, getFirstTreningPlanTwoActionsWorkout(1, 5, 5));
		w.put(4, getFirstTreningPlanTwoActionsWorkout(1, 5, 5));
		w.put(5, getFirstTreningPlanTwoActionsWorkout(1, 5, 5));
		
		//second week
		w.put(7, getFirstTreningPlanTwoActionsWorkout(2, 4, 5));
		w.put(9, getFirstTreningPlanTwoActionsWorkout(2, 4, 5));
		w.put(11, getFirstTreningPlanTwoActionsWorkout(2, 4, 5));
		w.put(12, getFirstTreningPlanTwoActionsWorkout(2, 4, 5));
		
		//third week
		w.put(14, getFirstTreningPlanTwoActionsWorkout(3, 3, 5));
		w.put(16, getFirstTreningPlanTwoActionsWorkout(3, 3, 5));
		w.put(18, getFirstTreningPlanTwoActionsWorkout(3, 3, 5));
		w.put(19, getFirstTreningPlanTwoActionsWorkout(3, 3, 5));
		
		//fourth week
		w.put(21, getFirstTreningPlanTwoActionsWorkout(5, 2.5, 4));
		w.put(23, getFirstTreningPlanTwoActionsWorkout(5, 2.5, 4));
		w.put(25, getFirstTreningPlanTwoActionsWorkout(5, 2.5, 4));
		w.put(26, getFirstTreningPlanTwoActionsWorkout(5, 2.5, 4));
		
		//fifth week
		w.put(28, getFirstTreningPlanTwoActionsWorkout(7, 3, 3));
		w.put(30, getFirstTreningPlanTwoActionsWorkout(7, 3, 3));
		w.put(32, getFirstTreningPlanTwoActionsWorkout(7, 3, 3));
		w.put(33, getFirstTreningPlanTwoActionsWorkout(7, 3, 3));
		
		//sixth week
		w.put(35, getFirstTreningPlanTwoActionsWorkout(8, 2, 3));
		w.put(37, getFirstTreningPlanTwoActionsWorkout(8, 2, 3));
		w.put(39, getFirstTreningPlanTwoActionsWorkout(8, 2, 3));
		w.put(40, getFirstTreningPlanTwoActionsWorkout(8, 2, 3));
		
		//seventh week
		w.put(42, getFirstTreningPlanTwoActionsWorkout(9, 1, 3));
		w.put(44, getFirstTreningPlanTwoActionsWorkout(9, 1, 3));
		w.put(46, getFirstTreningPlanTwoActionsWorkout(9, 1, 3));
		w.put(47, getFirstTreningPlanTwoActionsWorkout(9, 1, 3));
		
		//eight week
		w.put(49, getFirstTreningPlanTwoActionsWorkout(13, 2, 2));
		w.put(51, getFirstTreningPlanTwoActionsWorkout(13, 2, 2));
		w.put(53, getFirstTreningPlanTwoActionsWorkout(13, 2, 2));
		w.put(54, getFirstTreningPlanTwoActionsWorkout(13, 2, 2));
		
		//ninth week
		w.put(56, getFirstTreningPlanTwoActionsWorkout(14, 1, 2));
		w.put(58, getFirstTreningPlanTwoActionsWorkout(14, 1, 2));
		w.put(60, getFirstTreningPlanTwoActionsWorkout(14, 1, 2));
		w.put(61, getFirstTreningPlanTwoActionsWorkout(14, 1, 2));
		
		//tenth week
		w.put(63, getFirstTreningPlanOneActionsWorkout(30, 1));
		w.put(65, getFirstTreningPlanOneActionsWorkout(30, 1));
		w.put(67, getFirstTreningPlanOneActionsWorkout(30, 1));
		w.put(68, getFirstTreningPlanOneActionsWorkout(30, 1));
		
		plan.setWorkouts(w);
		return plan;
	}
	
	@SuppressWarnings("unchecked")
	private static Workout getFirstTreningPlanTwoActionsWorkout(double firstActionTimeMinutes,
		double secondActionTimeMinutes, int repeats) {
		return getWorkoutForActions(
			getActions(new Pair<Integer, Long>(WorkoutAction.ACTION_SIMPLE_SPEED_STEADY,
				(long) (firstActionTimeMinutes * ONE_MINUTE)), new Pair<Integer, Long>(
				WorkoutAction.ACTION_SIMPLE_SPEED_SLOW, (long) (secondActionTimeMinutes * ONE_MINUTE))), repeats);
	}
	
	@SuppressWarnings("unchecked")
	private static Workout getFirstTreningPlanOneActionsWorkout(double firstActionTimeMinutes, int repeats) {
		return getWorkoutForActions(getActions(new Pair<Integer, Long>(WorkoutAction.ACTION_SIMPLE_SPEED_STEADY,
			(long) (firstActionTimeMinutes * ONE_MINUTE))), repeats);
	}
	
	private static List<WorkoutAction> getActions(Pair<Integer, Long>... pairs) {
		List<WorkoutAction> actions = new ArrayList<WorkoutAction>();
		for (Pair<Integer, Long> pair : pairs) {
			actions.add(getSimpleActionTimeValueType(pair.first, pair.second));
		}
		return actions;
	}
	
	private static Workout getWorkoutForActions(List<WorkoutAction> actions, int repeats) {
		Workout workout = new Workout();
		workout.setActions(actions);
		workout.setRepeatCount(repeats);
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
	
	public static WorkoutActionSimple getSimpleActionTimeValueType(int speedType, double time) {
		return new WorkoutActionSimple(speedType, WorkoutAction.ACTION_SIMPLE_VALUE_TYPE_TIME, time);
	}
	
}
