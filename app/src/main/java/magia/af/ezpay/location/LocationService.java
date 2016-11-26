package magia.af.ezpay.location;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Android Developer on 11/17/2016.
 */

public class LocationService extends Service {

  private final String TAG = "PostLocation";
  LocationManager manager;
  Location location;
  Timer timer = new Timer();
  Handler handler = new Handler();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.e(TAG, "onStartCommand");
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    Log.e(TAG, "onCreate");
    manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location == null) {
      location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);

    Log.e(TAG, "onStart: 0" );

    if (location != null) {
      showMyLocation(location);
      Log.e(TAG, "onStart: 1" );
    }

    android.location.LocationListener locationListener = new android.location.LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        Log.e(TAG, "onStart: 2" );
        showMyLocation(location);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }

      @Override
      public void onProviderEnabled(String provider) {

      }

      @Override
      public void onProviderDisabled(String provider) {

      }
    };
    if (ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      Log.e(TAG, "onStart: permission" );

      return;
    }
    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 70, locationListener);



    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        handler.post(new Runnable() {
          @Override
          public void run() {

          }
        });
      }

    }, 0L, 1000 * 600);

  }

  private void showMyLocation(final Location location) {
    Log.e(TAG, "onLocationChanged: " + location.getAccuracy());

    new PostLocation(LocationService.this).execute(location.getLatitude(), location.getLongitude(),(double)location.getAccuracy());

  }
}
