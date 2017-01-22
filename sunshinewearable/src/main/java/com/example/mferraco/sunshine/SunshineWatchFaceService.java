package com.example.mferraco.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mferraco on 1/16/17.
 */

public class SunshineWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new SunshineWatchFaceEngine();
    }

    private class SunshineWatchFaceEngine extends Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private final String TAG = SunshineWatchFaceEngine.class.getSimpleName();

        private final String WEARABLE_DATA_PATH = "/weatherData";

        Calendar mCalendar;

        Paint mBackgroundPaint;
        TextPaint mTimeTextPaint;
        TextPaint mDateTextPaint;
        TextPaint mHighTextPaint;
        TextPaint mLowTextPaint;

        float mXOffset;
        float mYOffset;

        SimpleDateFormat timeFormat;
        SimpleDateFormat dateFormat;

        String mHigh = "";
        String mLow = "";
        int mWeatherId = 0;

        Bitmap mWeatherImage;

        Resources mResources;

        boolean mRegisteredReceiver = false;

        private final Typeface BOLD_TYPEFACE =
                Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);


        // TODO: figure out if I need this??
        final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // logic to update when time zone changes or when new weather data is received
                Log.d(TAG, "Received broadcast");
            }
        };

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchFaceService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

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

            // date text configuration
            mDateTextPaint = new TextPaint();
            mDateTextPaint.setColor(Color.WHITE);
            mDateTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.date_size));
            mDateTextPaint.setAntiAlias(true);

            // high/low text configuration
            mHighTextPaint = new TextPaint();
            mLowTextPaint = new TextPaint();
            mHighTextPaint.setColor(Color.WHITE);
            mLowTextPaint.setColor(Color.WHITE);
            mHighTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.weather_size));
            mHighTextPaint.setAntiAlias(true);
            mLowTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.weather_size));
            mLowTextPaint.setAntiAlias(true);

            mCalendar = Calendar.getInstance();

            mResources = getApplicationContext().getResources();

            initFormats();
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
            mHigh = "15";
            mHigh = mHigh + (char) 0x00B0;
            Rect highTextBounds = new Rect();
            mHighTextPaint.getTextBounds(mHigh, 0, mHigh.length(), highTextBounds);
            int highTextX = Math.abs(bounds.centerX() + 35);
            int highTextY = Math.abs(dateTextY + highTextBounds.height() + 50);
            canvas.drawText(mHigh, highTextX, highTextY, mHighTextPaint);

            /* ===== Set & Draw the Weather ===== */
            mLow = "5";
            mLow = mLow + (char) 0x00B0;
            Rect lowTextBounds = new Rect();
            mLowTextPaint.getTextBounds(mLow, 0, mLow.length(), lowTextBounds);
            int lowTextX = Math.abs(bounds.centerX() - lowTextBounds.centerX());
            int lowTextY = Math.abs(dateTextY + lowTextBounds.height() + 50);
            canvas.drawText(mLow, lowTextX, lowTextY, mLowTextPaint);

            int weatherImageX = bounds.centerX() - lowTextBounds.centerX() - 75;
            int weatherImageY = Math.abs(dateTextY + lowTextBounds.height());
            mWeatherImage = BitmapFactory.decodeResource(mResources, WearableWeatherUtils.getWeatherResource(mWeatherId));
            canvas.drawBitmap(mWeatherImage, weatherImageX, weatherImageY, null); // Or use a Paint if you need it
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGoogleApiClient.connect();

                registerReceiver();

                mCalendar.setTimeZone(TimeZone.getDefault());
                initFormats();
            } else {
                unregisterReceiver();

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
        }

        private void registerReceiver() {
            if (mRegisteredReceiver) {
                return;
            }

            mRegisteredReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            SunshineWatchFaceService.this.registerReceiver(mBroadcastReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredReceiver) {
                return;
            }
            mRegisteredReceiver = false;
            SunshineWatchFaceService.this.unregisterReceiver(mBroadcastReceiver);
        }

        private void initFormats() {
            dateFormat = new SimpleDateFormat("EEEE, MMM. d yyyy", Locale.getDefault());
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }

        /* ========== Google API Callbacks ========== */

        @Override
        public void onDataChanged(DataEventBuffer dataEventBuffer) {
            // handle data change here
            Log.d(TAG, "Data changed event");

            DataMap dataMap;

            for (DataEvent event : dataEventBuffer) {

                // Check the data type
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    // Check the data path
                    String path = event.getDataItem().getUri().getPath();
                    if (path.equals(WEARABLE_DATA_PATH)) {
                    }
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    mHigh = String.valueOf(dataMap.getInt("max"));
                    mLow = String.valueOf(dataMap.getInt("min"));
                    mWeatherId = dataMap.getInt("weatherId");
                    invalidate();
                }
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, SunshineWatchFaceEngine.this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            // handle suspended connection
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // handle failure
        }
    }
}
