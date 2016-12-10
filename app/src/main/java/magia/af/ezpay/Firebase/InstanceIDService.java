package magia.af.ezpay.Firebase;

/**
 * Created by Alif on 10/5/2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Belal on 5/27/2016.
 */

/*
* rt
* */
//Class extending FirebaseInstanceIdService
public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        getSharedPreferences("EZpay", 0).edit().putString("push", refreshedToken).apply();
    }
}