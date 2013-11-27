package com.pwr.zpi.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pwr.zpi.R;

public class MyDialog {
	
	public AlertDialog showAlertDialog(Context context, int title, int message, int positiveButton, int negativeButton,
		DialogInterface.OnClickListener positiveButtonHandler, DialogInterface.OnClickListener negativeButtonHandler,
		CharSequence[] items, DialogInterface.OnClickListener itemsHandler) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// Add the buttons
		if (!(title == R.string.empty_string)) {
			builder.setTitle(title);
		}
		if (!(message == R.string.empty_string)) {
			builder.setMessage(message);
		}
		if (!(positiveButton == R.string.empty_string)) {
			builder
			.setPositiveButton(positiveButton, positiveButtonHandler);
		}
		if (!(negativeButton == R.string.empty_string)) {
			builder
			.setNegativeButton(negativeButton, negativeButtonHandler);
		}
		builder.setItems(items, itemsHandler);
		// Set other dialog properties
		
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
	
	public AlertDialog showAlertDialog(Context context, int title, int message, int positiveButton, int negativeButton,
		DialogInterface.OnClickListener positiveButtonHandler, DialogInterface.OnClickListener negativeButtonHandler) {
		return showAlertDialog(context, title, message, positiveButton, negativeButton, positiveButtonHandler,
			negativeButtonHandler, null, null);
	}
	
}
