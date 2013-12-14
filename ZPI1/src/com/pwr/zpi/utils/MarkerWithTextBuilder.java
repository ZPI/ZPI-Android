package com.pwr.zpi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;

import com.pwr.zpi.R;

public class MarkerWithTextBuilder {
	
	public static BitmapDrawable markerWithText(Context context, int km)
	{
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.distance_pin);
		
		bm = bm.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(bm);
		
		int pixelTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
			context.getResources().getDimension(R.dimen.small_text_size), context.getResources().getDisplayMetrics());
		
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(pixelTextSize);
		canvas.drawText(km + "", canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
		
		return new BitmapDrawable(bm);
		
	}
}
