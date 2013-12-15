package com.pwr.zpi.adapters;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.database.entity.TreningPlan;
import com.pwr.zpi.database.entity.Workout;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.utils.Pair;

public class AdapterFactory {
	
	private AdapterFactory() {}
	
	@SuppressWarnings("unchecked")
	public static BaseAdapter getAdapter(AdapterType type, Context context, List<?> data, Object additionalData) {
		switch (type) {
			case SplitsAdapter:
				return new GenericBaseAdapter<Pair<Integer, Pair<Long, Long>>, SplitsRowBuilder>(context,
					(List<Pair<Integer, Pair<Long, Long>>>) data, new SplitsRowBuilder(),
					R.layout.splits_activity_list_item);
			case DrawerWorkoutsAdapter:
				return new GenericBaseAdapter<WorkoutAction, DrawerWorkoutsRowBuilder>(context,
					(List<WorkoutAction>) data, new DrawerWorkoutsRowBuilder((Workout) additionalData, context),
					R.layout.workout_drawer_list_item);
			case RunAdapter:
				return new GenericBaseAdapter<SingleRun, RunAdapterRowBuilder>(context, (List<SingleRun>) data,
					new RunAdapterRowBuilder(), R.layout.history_run_list_item);
			case TreningPlansAdapter:
				return new GenericBaseAdapter<TreningPlan, TreningPlansRowBuilder>(context, (List<TreningPlan>) data,
					new TreningPlansRowBuilder(), R.layout.workouts_list_item);
			case WorkoutActionAdapter:
				return new GenericBaseAdapter<WorkoutAction, WorkoutActionsRowBuilder>(context,
					(List<WorkoutAction>) data, new WorkoutActionsRowBuilder(context),
					R.layout.workouts_action_simple_list_item, R.layout.workout_action_advanced_list_item);
			case WorkoutAdapter:
				return new GenericBaseAdapter<Workout, WorkoutRowBuilder>(context, (List<Workout>) data,
					new WorkoutRowBuilder(), R.layout.workouts_list_item);
			default:
				return null;
		}
	}
	
	public enum AdapterType {
		SplitsAdapter, DrawerWorkoutsAdapter, RunAdapter, TreningPlansAdapter, WorkoutActionAdapter, WorkoutAdapter;
	}
}