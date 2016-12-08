package magia.af.ezpay;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;

import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.Feed;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsList;
import magia.af.ezpay.fragments.Profile;
import magia.af.ezpay.fragments.Radar;
import magia.af.ezpay.location.LocationService;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {


    public RelativeLayout darkDialog, waitingDialog;
    public FriendsList friendsList;
    public BarCodeGet barCodeGet;
    public LinearLayout friendsLayout, barcodeReader, profileLayout, radarLayout;
    public int fragment_status = 0;
    Feed _feed;
    public String description;
    public Radar radar;
    public int amount;
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        barcodeReader = (LinearLayout) findViewById(R.id.barcode_reader);
        profileLayout = (LinearLayout) findViewById(R.id.profile_layout);
        radarLayout = (LinearLayout) findViewById(R.id.radar_layout);
        imageView = (ImageView) findViewById(R.id.image_view);


//    ContactDatabase database = new ContactDatabase(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            _feed = (Feed) bundle.getSerializable("contact");

            FragmentManager fm = getSupportFragmentManager();
            if (fm != null) {
                friendsList = new FriendsList().getInstance(_feed);
                fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
            }
        } else {
            JSONArray array = new JSONArray();
            new fillContact().execute(array.toString());
        }
//    for (int i = 0; i < _feed.getItemCount(); i++) {
//      Log.e("MAin", "onCreate: " + _feed.getItem(i).getTitle());
//    }
//    Log.e("Main", "Before calling locationservice");
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
            friendsList = new FriendsList().getInstance(_feed);
            fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
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

    public void checkPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends_layout:
                friendsList = FriendsList.getInstance(_feed);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, friendsList)
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
                Profile profile = new Profile();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, profile)
                        .commit();
                friendsLayout.setAlpha((float) 0.45);
                barcodeReader.setAlpha((float) 0.45);
                profileLayout.setAlpha((float) 1);
                radarLayout.setAlpha((float) 0.45);
                break;
            case R.id.radar_layout:
                radar = Radar.getInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, radar)
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
                friendsList = FriendsList.getInstance(_feed);
                Bundle bundle = new Bundle();
                bundle.putString("description", description);
                bundle.putInt("amount", amount);
                bundle.putInt("pos", position);
                friendsList.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, friendsList)
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
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.sendDeviceId(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                getSharedPreferences("EZpay", 0).edit().remove("push");
            } else {
            }
        }
    }

    private class fillContact extends AsyncTask<String, Void, Feed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Feed doInBackground(String... params) {
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.checkContactListWithGroup(params[0]);
        }

        @Override
        protected void onPostExecute(Feed result) {
            if (result != null) {
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null) {
                    friendsList = new FriendsList().getInstance(result);
                    fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
                }
            } else {
                Toast.makeText(MainActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

}
