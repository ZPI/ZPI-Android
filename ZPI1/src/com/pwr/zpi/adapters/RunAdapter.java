package com.pwr.zpi.adapters;

import java.util.List;

import com.pwr.zpi.R;
import com.pwr.zpi.database.entity.SingleRun;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RunAdapter extends ArrayAdapter<SingleRun>{
	
	
	private Context context; 
	private int layoutResourceId;    
    
    public RunAdapter(Context context, int layoutResourceId, List<SingleRun> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RunHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RunHolder();
            holder.date = (TextView)row.findViewById(R.id.textViewItemDate);
            holder.distance = (TextView)row.findViewById(R.id.textViewItemDistance);
            holder.time = (TextView)row.findViewById(R.id.textViewItemTime);
            
            row.setTag(holder);
        }
        else
        {
            holder = (RunHolder)row.getTag();
        }
        
        SingleRun run = getItem(position);
        
        holder.time.setText(convertTime(run.getRunTime()));
        holder.date.setText(DateFormat.format("yyyy.MM.dd, kk:mm", run.getStartDate()));
        holder.distance.setText(String.format("%.3fkm",run.getDistance()/1000));
        
        return row;
    }
    private String convertTime(long time)
    {
    	long hours = time / 3600000;
		long minutes = (time / 60000) - hours * 60;
		long seconds = (time / 1000) - hours * 3600 - minutes * 60;
		String hourZero = (hours < 10) ? "0" : "";
		String minutesZero = (minutes < 10) ? "0" : "";
		String secondsZero = (seconds < 10) ? "0" : "";

		return String.format("%s%d:%s%d:%s%d", hourZero, hours,
				minutesZero, minutes, secondsZero, seconds);
    	
    }
    
    static class RunHolder
    {
        TextView date;
        TextView distance;
        TextView time;
    }
}

