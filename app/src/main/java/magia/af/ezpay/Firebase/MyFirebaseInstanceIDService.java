package magia.af.ezpay.Firebase;

/**
 * Created by Alif on 10/5/2016.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.File;
import java.io.IOException;

import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.R;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "Refreshed token: " + refreshedToken);
        new AsyncPushToken().execute(refreshedToken);

    }


    private class AsyncPushToken extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.sendDeviceId(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            /*if (result != false) {
            } else {
            }*/
        }
    }

}