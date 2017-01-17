package jwl.com.ibeacondemo;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

import java.util.ArrayList;
import java.util.List;

import jwl.com.ibeacondemo.estimote.Beacon;
import jwl.com.ibeacondemo.estimote.BeaconNotiManager;

/**
 * Created by HaVH on 1/16/17.
 */

public class MyApplication extends Application{
    private static final String UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6E";
    private boolean beaconNotificationEnabled = false;
    private List<Beacon> beaconList = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        beaconList.add(new Beacon(UUID, 1 , 1));
        EstimoteSDK.initialize(getApplicationContext(), "ibeacondemo-el5", "5111290bf074a3ff553336435d3f91bb");

    }

    public void enableBeaconNotifications() {
        if (beaconNotificationEnabled) {
            return;
        }

        BeaconNotiManager beaconNotiManager = new BeaconNotiManager(this);
        for (Beacon beacon : beaconList) {
            beaconNotiManager.addNotification(beacon, "Enter", "Exit");
        }

        beaconNotiManager.startMonitoring();

        beaconNotificationEnabled = true;
    }

    public boolean isBeaconNotificationEnabled() {
        return beaconNotificationEnabled;
    }
}
