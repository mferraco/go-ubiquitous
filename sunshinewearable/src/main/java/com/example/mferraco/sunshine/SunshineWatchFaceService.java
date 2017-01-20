package com.example.mferraco.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by mferraco on 1/16/17.
 */

public class SunshineWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener {

        Calendar mCalendar;

        Paint mBackgroundPaint;

        private boolean mRegisteredTimeZoneReceiver;

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            // initialize watch face here

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(getColor(R.color.colorPrimary));

            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            // get device features (burn-in, low-bit ambient)
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();

            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            // the wearable switched between modes
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            // draw watch face

            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            // the watch face becomes visible or invisible

            if (visible) {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());
            } else {
                unregisterReceiver();
            }
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }

            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }

            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            DataMap dataMap;

            for (DataEvent event : dataEventBuffer) {

                // Check the data type
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    // Check the data path
                    String path = event.getDataItem().getUri().getPath();
                    if (path.equals("/weatherData")) {
                    }
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap);
                }
            }
        }
    }
}
