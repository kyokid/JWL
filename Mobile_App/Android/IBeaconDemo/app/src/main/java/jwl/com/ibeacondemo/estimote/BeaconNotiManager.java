package jwl.com.ibeacondemo.estimote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.*;
import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jwl.com.ibeacondemo.MainActivity;

/**
 * Created by HaVH on 1/15/17.
 */

public class BeaconNotiManager {
    public static final String TAG = "BeaconNotification";

    private BeaconManager beaconManager;

    private List<Region> regionToMonitor = new ArrayList<>();

    private HashMap<String, String> enterMessages = new HashMap<>();

    private HashMap<String, String> exitMessages = new HashMap<>();


    private Context context;

    private int notificationID = 0;

    public BeaconNotiManager(Context context) {
        this.context = context;

        beaconManager = new BeaconManager(context);

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.d(TAG, "onEnteredRegion" + region.getIdentifier());

                String message = enterMessages.get(region.getIdentifier());
                if (message != null) {
                    showNotification(message);
                }

            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d(TAG, "onExitedRegion" + region.getIdentifier());
                String message = exitMessages.get(region.getIdentifier());
                if (message != null) {
                    showNotification(message);
                }
            }
        });
    }

    public void startMonitoring() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (Region region :regionToMonitor) {
                    beaconManager.startMonitoring(region);
                }
            }
        });
    }

    public void addNotification(jwl.com.ibeacondemo.estimote.Beacon beacon, String enterMessage, String exitMessage) {
        Region region = beacon.toBeaconRegion();
        enterMessages.put(region.getIdentifier(), enterMessage);
        exitMessages.put(region.getIdentifier(), exitMessage);
        regionToMonitor.add(region);
    }

    private void showNotification(String message) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Beacon Notification")
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, builder.build());


    }
}
