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

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Fragments.BarCodeGet;
import magia.af.ezpay.Fragments.FriendsList;
import magia.af.ezpay.Fragments.Profile;
import magia.af.ezpay.Fragments.Radar;
import magia.af.ezpay.location.LocationService;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {


    public RelativeLayout darkDialog, waitingDialog;
    FriendsList friendsList;
    public BarCodeGet barCodeGet;
    public LinearLayout friendsLayout, barcodeReader, profileLayout, radarLayout;
    public int fragment_status = 0;
    ChatListFeed _ChatList_feed;
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


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.e("Ok","In Bundle");
            _ChatList_feed = (ChatListFeed) bundle.getSerializable("contact");//not null at this line

            FragmentManager fm = getSupportFragmentManager();
            if (fm != null) {
                Log.e("Ok","In Fm");

                friendsList = new FriendsList().getInstance(_ChatList_feed);
                fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
            }
        } else {
            JSONArray array = new JSONArray();
            new fillContact().execute(array.toString());
        }
        startService(new Intent(this, LocationService.class));

        if (getSharedPreferences("EZpay", 0).contains("push"))
            new AsyncPushToken().execute(getSharedPreferences("EZpay", 0).getString("push", ""));

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            friendsList = new FriendsList().getInstance(_ChatList_feed);
            fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
        }

    }

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
                friendsList = FriendsList.getInstance(_ChatList_feed);
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
                bundle.putSerializable("contact", _ChatList_feed);
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
                friendsList = FriendsList.getInstance(_ChatList_feed);
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

    private class fillContact extends AsyncTask<String, Void, ChatListFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ChatListFeed doInBackground(String... params) {
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.checkContactListWithGroup(params[0]);
        }

        @Override
        protected void onPostExecute(ChatListFeed result) {
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
