package com.pwr.zpi.dialogs;

import com.pwr.zpi.R;

public enum DialogsEnum {
	SelectThisTraining(R.string.trening_plan_select_title, R.string.trening_plan_select_message, android.R.string.yes,
		android.R.string.no), ChangeTreningPlan(R.string.trening_plan_change_title,
		R.string.trening_plan_change_message, android.R.string.yes, android.R.string.no), Confirmation(
		R.string.trening_plan_confirm_title, R.string.trening_plan_confirm_message, android.R.string.ok), DisableThisTreningPlan(
		R.string.trening_plan_disable_title, R.string.trening_plan_disable_message, android.R.string.yes,
		android.R.string.no), ConfirmationDisable(R.string.trening_plan_confirm_disable_title,
		R.string.trening_plan_confirm_disable_message, android.R.string.ok), NoTTSData(R.string.no_tts_dialog_title,
		R.string.no_tts_dialog_message, android.R.string.yes, android.R.string.no);
	
	int title;
	int message;
	int positiveButton;
	int negativeButton;
	int items;
	
	private DialogsEnum(int title, int message, int positiveButton, int negativeButton, int items) {
		this.title = title;
		this.message = message;
		this.positiveButton = positiveButton;
		this.negativeButton = negativeButton;
		this.items = items;
	}
	
	private DialogsEnum(int title, int message, int positiveButton, int negativeButton) {
		this(title, message, positiveButton, negativeButton, 0);
	}
	
	private DialogsEnum(int title, int message, int positiveButton) {
		this(title, message, positiveButton, 0);
	}
	
	public int getTitle() {
		return title;
	}
	
	public int getMessage() {
		return message;
	}
	
	public int getPositiveButton() {
		return positiveButton;
	}
	
	public int getNegativeButton() {
		return negativeButton;
	}
	
	public int getItems() {
		return items;
	}
}