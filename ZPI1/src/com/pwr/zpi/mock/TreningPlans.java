package com.pwr.zpi.mock;

import java.util.ArrayList;
import java.util.HashMap;

import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;

public class TreningPlans {
	
	static ArrayList<TreningPlan> plans;
	
	//TODO write plans here
	static {
		plans = new ArrayList<TreningPlan>();
		TreningPlan plan = new TreningPlan();
		plan.setName("Name");
		HashMap<Integer, Workout> w = new HashMap<Integer, Workout>();
		w.put(2, new Workout()); // it means that 2 days from start of trening plan there's workout(empty)
		w.put(4, new Workout());
		w.put(6, new Workout());
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
	
	public static ArrayList<TreningPlan> getPlans() {
		return plans;
	}
	
	/**
	 * @param id - index of plan in array list
	 * @return
	 */
	public static TreningPlan getTreningPlan(long id) {
		return plans.get((int) id);
	}
}
