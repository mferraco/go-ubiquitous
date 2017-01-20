package com.example.mferraco.sunshine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Class responsible for sending data to the wearable when necessary
 */

public class WearableDataManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private String mPath;

    private DataMap mDataMap;

    private Context mContext;

    public WearableDataManager(String path, DataMap dataMap, Context context) {
        mPath = path;
        mDataMap = dataMap;
        mContext = context;
    }

    public void sendDataToWearable() {
        // Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // connection failed
    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection suspended
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new WearableDataLayerThread(mPath, mDataMap).start();
    }

    class WearableDataLayerThread extends Thread {

        String mPath;

        DataMap mDataMap;

        WearableDataLayerThread(String path, DataMap dataMap) {
            mPath = path;
            mDataMap = dataMap;
        }

        @Override
        public void run() {
            // Construct the data request to send to the wearable over the data layer
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(mPath);
            putDataMapRequest.getDataMap().putAll(mDataMap);
            PutDataRequest dataRequest = putDataMapRequest.asPutDataRequest().setUrgent();
            DataApi.DataItemResult dataItemResult = Wearable.DataApi.putDataItem(mGoogleApiClient, dataRequest).await();

            if (dataItemResult.getStatus().isSuccess()) {
                // successfully sent data
                //mGoogleApiClient.disconnect();
            } else {
                // failed to send data
                //mGoogleApiClient.disconnect();
            }
        }
    }
}
