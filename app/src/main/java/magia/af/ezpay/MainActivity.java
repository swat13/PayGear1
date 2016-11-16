package magia.af.ezpay;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsListFragment;
import magia.af.ezpay.fragments.ProfileFragment;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {


  public RelativeLayout darkDialog, waitingDialog;
  public FriendsListFragment friendsListFragment;
  public BarCodeGet barCodeGet;
  public LinearLayout friendsLayout, barcodeReader, profileLayout;
  public int fragment_status = 0;
  RSSFeed _feed;
  public String description;
  public int amount;
  private int position;
  private Location phoneLocation;
  double lat1 = 0;
  double lat2 = 0;
  double lng1 = 0;
  double lng2 = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
    waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
    friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
    barcodeReader = (LinearLayout) findViewById(R.id.barcode_reader);
    profileLayout = (LinearLayout) findViewById(R.id.profile_layout);

    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location == null) {
      location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    }

    final LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
        Log.e("Flat", location.getLatitude() + "");
        Log.e("Flng", location.getLongitude() + "");
        showMyAddress(location);
      }

      public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

      }

      public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

      }

      public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

      }
    };
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    if (location != null) {
      lng1 = location.getLongitude();
      lat1 = location.getLatitude();
      showMyAddress(location);
    }

    Log.i("DISTANCE", distance(lat1, lng1, lat2, lng2) + "");


    _feed = (RSSFeed) getIntent().getSerializableExtra("contact");


    FragmentManager fm = getSupportFragmentManager();
    if (fm != null) {
      friendsListFragment = new FriendsListFragment().getInstance(_feed);
      fm.beginTransaction().replace(R.id.detail_fragment, friendsListFragment).addToBackStack(null).commit();
    }


  }

  private void showMyAddress(final Location location) {
//    Timer timer = new Timer();
//    timer.scheduleAtFixedRate(new TimerTask() {
//      @Override
//      public void run() {
//        new GetLocation().execute(location.getLatitude(), location.getLongitude());
//      }
//    }, 0L, 10000);
  }

  private double distance(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }

  private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }

  public class GetLocation extends AsyncTask<Double, Void, Void> {

    @Override
    protected Void doInBackground(Double... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      domParser.postLocation(params[0], params[1]);
      return null;
    }
  }

  private void buildAlertMessageNoGps() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
      .setCancelable(false)
      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
          startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
      })
      .setNegativeButton("No", new DialogInterface.OnClickListener() {
        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
          dialog.cancel();
        }
      });
    final AlertDialog alert = builder.create();
    alert.show();
  }


  @Override
  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.friends_layout:

        friendsListFragment = FriendsListFragment.getInstance(_feed);
        getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.detail_fragment, friendsListFragment)
          .addToBackStack(null)
          .commit();

        friendsLayout.setAlpha((float) 1);
        barcodeReader.setAlpha((float) 0.45);
        profileLayout.setAlpha((float) 0.45);

        break;

      case R.id.barcode_reader:

        barCodeGet = BarCodeGet.getInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", _feed);
        BarCodeGet barCodeGet = new BarCodeGet();
        barCodeGet.setArguments(bundle);
        getSupportFragmentManager()
          .beginTransaction()
          .addToBackStack(null)
          .replace(R.id.detail_fragment, barCodeGet)
          .commit();

        friendsLayout.setAlpha((float) 0.45);
        barcodeReader.setAlpha((float) 1);
        profileLayout.setAlpha((float) 0.45);
//        Log.e("clicked", "onClick: ");
//        startActivity(new Intent(MainActivity.this, SimpleScannerActivity.class).putExtra("contact",_feed));

        break;

      case R.id.profile_layout:
//        startActivity(new Intent(MainActivity.this , ProfileActivity.class));
        barCodeGet = BarCodeGet.getInstance();
        getSupportFragmentManager().beginTransaction().remove(barCodeGet).commit();
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.detail_fragment, profileFragment)
          .commit();
        friendsLayout.setAlpha((float) 0.45);
        barcodeReader.setAlpha((float) 0.45);
        profileLayout.setAlpha((float) 1);
        break;
      case R.id.rss_feed:
        startActivity(new Intent(this, RadarActivity.class));
        break;
//      case R.id.barcode_reader1:
//
//        barCodeGet = new BarCodeGet().getInstance();
//        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, barCodeGet).addToBackStack(null).commit();
//
//        friendsLayout.setAlpha((float) 0.45);
//        barcodeReader.setAlpha((float) 0.45);
//        barcodeGet.setAlpha((float)1);

//        break;

      default:
        break;


    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == 10) {
      description = data.getStringExtra("description");
      amount = data.getIntExtra("amount", 0);
      position = data.getIntExtra("pos", 0);
      friendsListFragment = FriendsListFragment.getInstance(_feed);
      Bundle bundle = new Bundle();
      bundle.putString("description", description);
      bundle.putInt("amount", amount);
      bundle.putInt("pos", position);
      friendsListFragment.setArguments(bundle);
      getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.detail_fragment, friendsListFragment)
        .addToBackStack(null)
        .commit();
    }
  }

  @Override
  public void onBackPressed() {
    Log.e("Finish", "onBackPressed: ");
    finish();

  }

}
