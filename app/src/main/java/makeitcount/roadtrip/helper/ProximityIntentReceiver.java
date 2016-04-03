package makeitcount.roadtrip.helper;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;

import makeitcount.roadtrip.Activity.MainActivity;
import makeitcount.roadtrip.R;

/**
 * Created by root on 3/4/16.
 */
public class ProximityIntentReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;

    Notification notification;
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }else {
            Log.d(getClass().getSimpleName(), "exiting");
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setAutoCancel(false);
        builder.setTicker("Alert!");
        builder.setContentTitle("Accident Prone Area!");
        builder.setContentText("Drive Slow. This is an accident prone area. " +
                "Last Accident: 14-05-2015" +
                "Driver Name: John Doe" +
                "Time: 14:00 hrs");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        notification = builder.getNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
