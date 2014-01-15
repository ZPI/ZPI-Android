package com.pwr.zpi.dialogs;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.WorkoutAction;
import com.pwr.zpi.views.CustomPicker;

public class DialogFactory {
	
	public static Dialog getRepeatsDialog(Context context, int layoutID,
		final ArrayList<WorkoutAction> workoutsActionList, final BaseAdapter workoutActionAdapter) //nie mam pomyslu jak to inaczej zrobic :/
	{
		
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(layoutID);
		dialog.setTitle(context.getResources().getString(R.string.repeats_dialog_title));
		// set the custom dialog components - text, image and button
		final CustomPicker fromAction = (CustomPicker) dialog
			.findViewById(R.id.customPickerRepeatsDialogFirstAction);
		final CustomPicker toAction = (CustomPicker) dialog
			.findViewById(R.id.customPickerRepeatsDialogSecondAction);
		final CustomPicker repeatTimes = (CustomPicker) dialog.findViewById(R.id.customPickerRepeatsDialogRepeats);
		int maxSize = workoutsActionList.size();
		fromAction.setMax(maxSize);
		toAction.setMax(maxSize);
		Button dialogOkButton = (Button) dialog.findViewById(R.id.buttonRepeatsDialogOkButton);
		Button dialogCancelButton = (Button) dialog.findViewById(R.id.buttonRepeatsDialogCancelButton);
		
		// if button is clicked, close the custom dialog
		dialogCancelButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialogOkButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int from = fromAction.getValue();
				int to = toAction.getValue();
				int howMany = repeatTimes.getValue();
				
				//zalorzy³em, ze lepiej wstawiac w srodek linked listy
				LinkedList<WorkoutAction> linkedList = new LinkedList<WorkoutAction>();
				linkedList.addAll(workoutsActionList);
				int index = to;
				for (int i = 0; i < howMany - 1; i++)
				{
					for (int j = from - 1; j < to; j++)
					{
						WorkoutAction action = workoutsActionList.get(j);
						linkedList.add(index, action);
						index++;
					}
				}
				workoutsActionList.clear();
				workoutsActionList.addAll(linkedList);
				workoutActionAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		return dialog;
	}
	
	private static AlertDialog getDialog(DialogsEnum dialogType, Context context, OnClickListener positiveListener,
		OnClickListener negativeListener, OnClickListener itemsListener) {
		
		AlertDialog dialog = null;
		CharSequence[] items = dialogType.getItems() == 0 ? null : context.getResources().getStringArray(
			dialogType.getItems());
		dialog = MyDialog.getAlertDialog(context, dialogType.getTitle(), dialogType.getMessage(),
			dialogType.getPositiveButton(),
			dialogType.getNegativeButton() == 0 ? R.string.empty_string : dialogType.getNegativeButton(),
			positiveListener, negativeListener, items, itemsListener);
		return dialog;
	}
	
	public static AlertDialog getDialog(DialogsEnum dialogType, Context context, OnClickListener positiveListener,
		OnClickListener negativeListener) {
		return getDialog(dialogType, context, positiveListener, negativeListener, null);
	}
	
	public static AlertDialog getDialog(DialogsEnum dialogType, Context context, OnClickListener itemsListener) {
		return getDialog(dialogType, context, null, null, itemsListener);
	}
	
	public static AlertDialog getDialogSingleButton(DialogsEnum dialogType, Context context,
		OnClickListener positiveListener) {
		return getDialog(dialogType, context, positiveListener, null, null);
	}
}
