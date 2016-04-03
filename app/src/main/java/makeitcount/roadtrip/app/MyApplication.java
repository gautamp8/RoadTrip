package makeitcount.roadtrip.app;

import com.firebase.client.Firebase;

import makeitcount.roadtrip.helper.MyPreferenceManager;

/**
 * Created by root on 3/4/16.
 */
public class MyApplication extends android.app.Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    private MyPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
}
