package com.example.mferraco.sunshine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.TextPaint;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mferraco on 1/16/17.
 */

public class SunshineWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new SunshineWatchFaceEngine();
    }

    private class SunshineWatchFaceEngine extends Engine {

        Calendar mCalendar;

        Paint mBackgroundPaint;
        TextPaint mTimeTextPaint;

        float mXOffset;
        float mYOffset;

        SimpleDateFormat timeFormat;

        private final Typeface BOLD_TYPEFACE =
                Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(getColor(R.color.colorPrimary));

            mTimeTextPaint = new TextPaint();
            mTimeTextPaint.setColor(Color.WHITE);
            mTimeTextPaint.setTypeface(BOLD_TYPEFACE);
            mTimeTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.time_size));
            mTimeTextPaint.setAntiAlias(true);

            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            /* Update the time */
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            /* Set the background */
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);


            /* Set Time */

            // get hour
            String hourString;
            int hour = mCalendar.get(Calendar.HOUR);
            if (hour == 0) {
                hour = 12;
            }
            hourString = String.valueOf(hour);

            // get minute
            String minuteString;
            int minute = mCalendar.get(Calendar.MINUTE);
            minuteString = String.valueOf(minute);

            String concatTime = hourString + ":" + minuteString;

            Rect textBounds = new Rect();
            mTimeTextPaint.getTextBounds(concatTime, 0, concatTime.length(), textBounds);

            int textX = Math.abs(bounds.centerX() - textBounds.centerX());
            int textY = Math.abs(bounds.centerY() / 2 - textBounds.centerY());

            canvas.drawText(concatTime, textX, textY, mTimeTextPaint);
        }
    }
}
