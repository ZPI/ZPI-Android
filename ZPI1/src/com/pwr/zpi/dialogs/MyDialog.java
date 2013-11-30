package com.pwr.zpi.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pwr.zpi.R;

public class MyDialog {
	
	private MyDialog() {}
	
	public static void showAlertDialog(Context context, int title, int message, int positiveButton, int negativeButton,
		DialogInterface.OnClickListener positiveButtonHandler, DialogInterface.OnClickListener negativeButtonHandler,
		CharSequence[] items, DialogInterface.OnClickListener itemsHandler) {
		
		AlertDialog dialog = getAlertDialog(context, title, message, positiveButton, negativeButton,
			positiveButtonHandler, negativeButtonHandler, items, itemsHandler);
		dialog.show();
	}
	
	public static void showAlertDialog(Context context, int title, int message, int positiveButton, int negativeButton,
		DialogInterface.OnClickListener positiveButtonHandler, DialogInterface.OnClickListener negativeButtonHandler) {
		showAlertDialog(context, title, message, positiveButton, negativeButton, positiveButtonHandler,
			negativeButtonHandler, null, null);
	}
	
	public static AlertDialog getAlertDialog(Context context, int title, int message, int positiveButton,
		int negativeButton, DialogInterface.OnClickListener positiveButtonHandler,
		DialogInterface.OnClickListener negativeButtonHandler) {
		return getAlertDialog(context, title, message, positiveButton, negativeButton, positiveButtonHandler,
			negativeButtonHandler, null, null);
	}
	
	public static AlertDialog getAlertDialog(Context context, int title, int message, int positiveButton,
		int negativeButton, DialogInterface.OnClickListener positiveButtonHandler,
		DialogInterface.OnClickListener negativeButtonHandler, CharSequence[] items,
		DialogInterface.OnClickListener itemsHandler) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// Add the buttons
		if (!(title == R.string.empty_string)) {
			builder.setTitle(title);
		}
		if (!(message == R.string.empty_string)) {
			builder.setMessage(message);
		}
		if (!(positiveButton == R.string.empty_string)) {
			builder.setPositiveButton(positiveButton, positiveButtonHandler);
		}
		if (!(negativeButton == R.string.empty_string)) {
			builder.setNegativeButton(negativeButton, negativeButtonHandler);
		}
		builder.setItems(items, itemsHandler);
		// Set other dialog properties
		
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
}
