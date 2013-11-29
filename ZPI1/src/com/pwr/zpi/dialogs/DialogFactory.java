package com.pwr.zpi.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.pwr.zpi.R;

public class DialogFactory {
	
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
