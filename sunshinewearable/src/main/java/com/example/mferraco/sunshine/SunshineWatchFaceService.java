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
        TextPaint mDateTextPaint;

        float mXOffset;
        float mYOffset;

        SimpleDateFormat timeFormat;
        SimpleDateFormat dateFormat;

        private final Typeface BOLD_TYPEFACE =
                Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(getColor(R.color.colorPrimary));

            // time text configuration
            mTimeTextPaint = new TextPaint();
            mTimeTextPaint.setColor(Color.WHITE);
            mTimeTextPaint.setTypeface(BOLD_TYPEFACE);
            mTimeTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.time_size));
            mTimeTextPaint.setAntiAlias(true);
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // date text configuration
            mDateTextPaint = new TextPaint();
            mDateTextPaint.setColor(Color.WHITE);
            mDateTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.date_size));
            mDateTextPaint.setAntiAlias(true);
            dateFormat = new SimpleDateFormat("EEEE, MMM. d yyyy", Locale.getDefault());

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

            /* ===== Update the time ===== */
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            /* ===== Set the background ===== */
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            /* ===== Set & Draw Time ===== */
            String timeString = timeFormat.format(now);
            Rect textBounds = new Rect();
            mTimeTextPaint.getTextBounds(timeString, 0, timeString.length(), textBounds);

            int textX = Math.abs(bounds.centerX() - textBounds.centerX());
            int textY = Math.abs(bounds.centerY() / 2 - textBounds.centerY());

            canvas.drawText(timeString, textX, textY, mTimeTextPaint);

            /* ===== Set & Draw Date ===== */
            String dateString = dateFormat.format(now);
            Rect dateTextBounds = new Rect();
            mDateTextPaint.getTextBounds(dateString, 0, dateString.length(), dateTextBounds);
            int dateTextX = Math.abs(bounds.centerX() - dateTextBounds.centerX());
            int dateTextY = Math.abs(textY + dateTextBounds.height() + 10);

            canvas.drawText(dateString, dateTextX, dateTextY, mDateTextPaint);

            /* ===== Set & Draw the Weather ===== */

        }
    }
}
