package com.pwr.zpi.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GenericBaseAdapter<T, E extends RowBuilder<T>> extends BaseAdapter {
	
	private final Context context;
	private final List<T> data;
	private final int[] layoutResourcesIDs;
	private final LayoutInflater inflater;
	private final RowBuilder<T> rowBuilder;
	
	public GenericBaseAdapter(Context context, List<T> data, E rowBuilder, int... layoutResourcesIDs) {
		super();
		this.context = context;
		this.data = data;
		this.layoutResourcesIDs = layoutResourcesIDs;
		this.rowBuilder = rowBuilder;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AbstractRowHolder holder;
		T item = (T) getItem(position);
		
		if (row != null && row.getTag() != null && rowBuilder.isTagOK(row.getTag(), item)) {
			holder = (AbstractRowHolder) row.getTag();
		}
		else {
			row = rowBuilder.inflateView(inflater, parent, getLayoutResourcesIDs(), item);
			holder = rowBuilder.buildRowHolder(row, item);
			
			row.setTag(holder);
		}
		
		rowBuilder.fillRowDataToHolder(holder, item, position, getLayoutResourcesIDs());
		
		return row;
	}
	
	public void remove(T item) {
		data.remove(item);
		notifyDataSetChanged();
	}
	
	public Context getContext() {
		return context;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public int[] getLayoutResourcesIDs() {
		return layoutResourcesIDs;
	}
}
