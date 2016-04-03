package makeitcount.roadtrip.Activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Formatter;
import java.util.Locale;

import makeitcount.roadtrip.Models.Driver;
import makeitcount.roadtrip.R;
import makeitcount.roadtrip.app.Config;
import makeitcount.roadtrip.gcm.GcmIntentService;
import makeitcount.roadtrip.gps.CLocation;
import makeitcount.roadtrip.gps.IBaseGpsListener;

public class MainActivity extends AppCompatActivity implements IBaseGpsListener {
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private double speed = 30.000;
    private Handler mHandler;

    TextView currentSpeed;
    TextView upvotes;
    TextView name;
    TextView location;
    TextView avgSpeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Firebase mainRef = new Firebase("https://makeitcount.firebaseio.com/");

        upvotes = (TextView) findViewById(R.id.counter);
        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        avgSpeed = (TextView) findViewById(R.id.speed);

        final ImageButton upVoteButton = (ImageButton) findViewById(R.id.upVote);
        ImageButton downVoteButton = (ImageButton) findViewById(R.id.downVote);

        Button proximity = (Button) findViewById(R.id.button);
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        // Video Player.
        final VideoView AppetiVideo = (VideoView) findViewById(R.id.driveVideo);
        try {
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drive);
            AppetiVideo.setVideoURI(video);
            AppetiVideo.setZOrderOnTop(true);
            AppetiVideo.start();
            AppetiVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Firebase driverRef = mainRef.child("driver1");
        Log.d("driver1 ref",driverRef.toString());
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot",dataSnapshot.toString());
                Driver driver1 = dataSnapshot.getValue(Driver.class);
                upvotes.setText(driver1.getUpvotes().toString());
                name.setText("Driver Name:" + driver1.getName());
                location.setText("Driver Location: "+ driver1.getLocation());
                avgSpeed.setText("Average Speed: "+ driver1.getSpeed().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase upvotesRef = driverRef.child("upvotes");

        upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvotesRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if(currentData.getValue() == null) {
                            currentData.setValue(1);
                        } else {
                            currentData.setValue((Long) currentData.getValue() + 1);
                        }
                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }
                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });
            }
        });

        downVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upvotesRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if(currentData.getValue() == null) {
                            currentData.setValue(0);
                        } else {
                            if ((Long) currentData.getValue()>0) {
                                currentData.setValue((Long) currentData.getValue() - 1);
                            }
                        }
                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }
                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    Snackbar.make(findViewById(android.R.id.content), "Push Notification: You have crossed Speed Limit!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }

        currentSpeed = (TextView) findViewById(R.id.txtCurrentSpeed);
        currentSpeed.setText(String.valueOf(speed)+"miles/hour");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
            Toast.makeText(MainActivity.this, "Permission Denied. Exiting.", Toast.LENGTH_LONG).show();
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            this.updateSpeed(null);
        }

        mHandler = new Handler();
        startRepeatingTask();
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void finish() {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        float nCurrentSpeed = 0;

        if (location != null) {
            location.setUseMetricunits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "miles/hour";
        if (this.useMetricUnits()) {
            strUnits = "km/hour";
        }

        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
//        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {
        if (speed<=55) {
            speed += 5.000;
        }
        else {
            speed = 30;
        }
        currentSpeed.setText(String.valueOf(speed)+"miles/hour");
        if (speed>=45){
            Toast.makeText(MainActivity.this,"Speed Limit Exceeded.",Toast.LENGTH_SHORT).show();
        }
        if (speed == 50){
            Notification.Builder builder = new Notification.Builder(MainActivity.this);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            builder.setAutoCancel(false);
            builder.setTicker("Alert!");
            builder.setContentTitle("Accident Prone Area!");
            builder.setContentText("Drive Slow. This is an accident prone area. " +
                    "Last Accident: 14-05-2015" +
                    "Driver Name: John Doe" +
                    "Time: 14:00 hrs");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setOngoing(true);
            builder.setNumber(100);
            builder.build();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            Notification notification = builder.getNotification();
            notificationManager.notify(1000, notification);
        }
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void addProximityAlert (double latitude, double longitude, float radius, long expiration, PendingIntent intent){

    }


}

