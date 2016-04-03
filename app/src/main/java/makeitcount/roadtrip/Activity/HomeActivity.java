package makeitcount.roadtrip.Activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import makeitcount.roadtrip.R;
import makeitcount.roadtrip.helper.ProximityIntentReceiver;

public class HomeActivity extends AppCompatActivity {
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 100; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire
    private static final String PROX_ALERT_INTENT = "makeitcount.roadtrip.Activity.MainActivity";
    private LocationManager locationManager;

    private static double latitude = 0.00;
    private static double longitude = 0.00;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Firebase.setAndroidContext(this);
        Firebase mainRef = new Firebase("https://makeitcount.firebaseio.com/data1");
        mainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Trip crashTrip = dataSnapshot.getValue(Trip.class);
//                latitude = crashTrip.getLocation().getLatitude();
//                longitude = crashTrip.getLocation().getLongitude();
                addProximityAlert(23.4353,78.453);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    private void addProximityAlert(double latitude, double longitude) {
        Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
            Toast.makeText(HomeActivity.this, "Permission Denied. Exiting.", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new ProximityIntentReceiver(), filter);
        Toast.makeText(getApplicationContext(),"Alert Added", Toast.LENGTH_SHORT).show();
        finish();
        Intent intentz = new Intent(this, MainActivity.class);
        this.startActivity(intentz);
    }
}
