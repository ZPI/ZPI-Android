package com.pwr.zpi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class RowBuilder<T> {
	
	public boolean isTagOK(Object tag, T item) {
		return true;
	}
	
	public View inflateView(LayoutInflater inflater, ViewGroup parent, int[] layoutResourcesIDs, T item) {
		return inflater.inflate(layoutResourcesIDs[0], parent, false);
	}
	
	public abstract AbstractRowHolder buildRowHolder(View row, T item);
	
	public abstract void fillRowDataToHolder(AbstractRowHolder holder, T item, int position, int[] layoutResourcesIDs);
}
