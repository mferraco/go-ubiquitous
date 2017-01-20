package com.example.mferraco.sunshine;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * A service that listens for the data from the handheld device.
 */

public class ListenerService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/weatherData";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        DataMap dataMap;

        for (DataEvent event : dataEventBuffer) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                }
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.v("myTag", "DataMap received on watch: " + dataMap);
            }
        }
    }
}
