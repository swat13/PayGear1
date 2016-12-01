package magia.af.ezpay;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsListFragment;
import magia.af.ezpay.fragments.ProfileFragment;
import magia.af.ezpay.fragments.RadarFragment;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.location.LocationService;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {


    public RelativeLayout darkDialog, waitingDialog;
    public FriendsListFragment friendsListFragment;
    public BarCodeGet barCodeGet;
    public LinearLayout friendsLayout, barcodeReader, profileLayout, radarLayout;
    public int fragment_status = 0;
    RSSFeed _feed;
    public String description;
    public RadarFragment radarFragment;
    public int amount;
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        barcodeReader = (LinearLayout) findViewById(R.id.barcode_reader);
        profileLayout = (LinearLayout) findViewById(R.id.profile_layout);
        radarLayout = (LinearLayout) findViewById(R.id.radar_layout);
        imageView = (ImageView) findViewById(R.id.image_view);


//    ContactDatabase database = new ContactDatabase(this);
        _feed = (RSSFeed) getIntent().getSerializableExtra("contact");
        for (int i = 0; i < _feed.getItemCount(); i++) {
            Log.e("MAin", "onCreate: " + _feed.getItem(i).getTitle());
        }
        Log.e("Main", "Before calling locationservice");
//        startService(new Intent(this, LocationService.class));


//    manager = (LocationManager) getSystemService(LOCATION_SERVICE);
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//      // TODO: Consider calling
//      //    ActivityCompat#requestPermissions
//      // here to request the missing permissions, and then overriding
//      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//      //                                          int[] grantResults)
//      // to handle the case where the user grants the permission. See the documentation
//      // for ActivityCompat#requestPermissions for more details.
//      return;
//    }
//    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//    if (location == null) {
//      location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//      Log.e("TestNN", "showMyLocation: ");
//    }
////
//    if (location != null) {
//      Log.e("TestN", "showMyLocation: ");
//      showMyLocation(location);
//    } else {
//      Log.e("TestN2", "showMyLocation: ");
//    }
//    LocationListener locationListener = new LocationListener() {
//      @Override
//      public void onLocationChanged(Location location) {
//        Log.e("TestLL", "showMyLocation: ");
//        showMyLocation(location);
//        Log.e("TestDD", "showMyLocation: ");
//      }
//
//      @Override
//      public void onStatusChanged(String provider, int status, Bundle extras) {
//
//      }
//
//      @Override
//      public void onProviderEnabled(String provider) {
//
//      }
//
//      @Override
//      public void onProviderDisabled(String provider) {
//
//      }
//    };
//    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        if (getSharedPreferences("EZpay", 0).contains("push"))
            new AsyncPushToken().execute(getSharedPreferences("EZpay", 0).getString("push", ""));

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            friendsListFragment = new FriendsListFragment().getInstance(_feed);
            fm.beginTransaction().replace(R.id.detail_fragment, friendsListFragment).addToBackStack(null).commit();
        }

    }

//  private void showMyLocation(final Location location) {
//    Log.e("Test", "showMyLocation: ");
//    Timer timer = new Timer();
//    timer.scheduleAtFixedRate(new TimerTask() {
//      @Override
//      public void run() {
//        new PostLocation(MainActivity.this).execute(location.getLatitude(), location.getLongitude());
//      }
//    }, 0L, 10000);
//  }


    @Override
    protected void onDestroy() {
        startService(new Intent(this, LocationService.class));
        super.onDestroy();
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
                radarLayout.setAlpha((float) 0.45);
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
                radarLayout.setAlpha((float) 0.45);
                break;
            case R.id.profile_layout:
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
                radarLayout.setAlpha((float) 0.45);
                break;
            case R.id.radar_layout:
                radarFragment = RadarFragment.getInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, radarFragment)
                        .addToBackStack(null)
                        .commit();
                friendsLayout.setAlpha((float) 0.45);
                barcodeReader.setAlpha((float) 0.45);
                profileLayout.setAlpha((float) 0.45);
                radarLayout.setAlpha((float) 1);
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                description = data.getStringExtra("description");
                amount = data.getIntExtra("amount", 0);
                int position = data.getIntExtra("pos", 0);
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
            Log.e("3333333333", "onActivityResult: " + requestCode);
            switch (requestCode) {
                case 0:
                    Log.e("#######", "onActivityResult: 0000" + data.getData());
                    File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
//                    CallAsync(Uri.fromFile(file));
                    break;
                case 1:
//                    CallAsync(tempUri);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("Finish", "onBackPressed: ");
        finish();

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
            if (result != null) {
                getSharedPreferences("EZpay", 0).edit().remove("push");
            } else {
            }
        }
    }

}
