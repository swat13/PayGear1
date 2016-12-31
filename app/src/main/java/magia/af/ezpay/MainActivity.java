package magia.af.ezpay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.DialogMaker;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsList;
import magia.af.ezpay.fragments.Profile;
import magia.af.ezpay.fragments.Radar;
import magia.af.ezpay.location.LocationService;

public class MainActivity extends BaseActivity implements View.OnClickListener {


  public RelativeLayout darkDialog, waitingDialog;
  FriendsList friendsList;
  public magia.af.ezpay.fragments.BarCodeGet barCodeGet;
  public LinearLayout friendsLayout, barcodeReader, profileLayout, radarLayout;
  ChatListFeed _ChatList_feed;
  ChatListFeed outNetworkContact;
  public String description;
  public magia.af.ezpay.fragments.Radar radar;
  public int amount;
  public ImageView imageView;
  String id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _ChatList_feed = ApplicationData.getChatListFeed();
    outNetworkContact = ApplicationData.getOutNetworkContact();
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

//    for (int i = 0; i < _ChatList_feed.getItemCount(); i++) {
//      if (_ChatList_feed.getItem(i).getGroupItem() != null) {
//        Log.e("GROUP", "onPostExecute: " + _ChatList_feed.getItem(i).getGroupItem().getGroupTitle());
//      } else {
//        Log.e("Contact", "onPostExecute: " + _ChatList_feed.getItem(i).getTitle());
//      }
//    }
//    if (_ChatList_feed==null){
//      JSONParser parser = JSONParser.connect(Constant.CHECK_CONTACT_LIST_WITH_GROUP);
//      parser.setRequestMethod(JSONParser.POST);
//      parser.setReadTimeOut(20000);
//      parser.setConnectionTimeOut(20000);
//      parser.setAuthorization(getSharedPreferences("EZpay", 0).getString("token", ""));
//      parser.setJson("[]");
//      parser.execute(new JSONParser.Execute() {
//        @Override
//        public void onPreExecute() {
//        }
//
//        @Override
//        public void onPostExecute(String s) {
//          Log.e("STRING S", "onPostExecute: " + s);
//          if (s != null) {
//            FragmentManager fm = getSupportFragmentManager();
//            if (fm != null) {
//              ChatListFeed chatListFeed = ApplicationData.getContactListWithGroup(s);
//              ApplicationData.setChatListFeed(chatListFeed);
//              friendsList = new FriendsList().getInstance(chatListFeed);
//              fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
//            }
//          } else {
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//              @Override
//              public void run() {
//                Toast.makeText(MainActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
//              }
//            }, 3000);
//          }
//        }
//      });
//    }
    FragmentManager fm = getSupportFragmentManager();
    if (fm != null) {
      Log.e("Ok", "In Fm");
      Bundle bundle = new Bundle();
      bundle.putString("id", id);
      friendsList = new FriendsList();
      friendsList.setArguments(bundle);
      friendsList.set_ChatList_feed(_ChatList_feed);
      friendsList.setOutNetworkContact(outNetworkContact);
      fm.beginTransaction().replace(R.id.detail_fragment, friendsList).addToBackStack(null).commit();
    }
    startService(new Intent(this, LocationService.class));

    if (getSharedPreferences("EZpay", 0).contains("push")) {
      JSONParser parser = JSONParser.connect(Constant.SEND_DEVICE_ID);
      parser.setRequestMethod(JSONParser.POST);
      parser.setReadTimeOut(20000);
      parser.setConnectionTimeOut(20000);
      parser.setAuthorization(getSharedPreferences("EZpay", 0).getString("token", ""));
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("IMEI", "5555");
        jsonObject.put("DeviceID", "5555");
        jsonObject.put("Platform", "1");
        jsonObject.put("OsVersion", "5555");
        jsonObject.put("AppVersion", "5555");
        jsonObject.put("PushToken", getSharedPreferences("EZpay", 0).getString("push", ""));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      parser.setJson(jsonObject.toString());
      parser.execute(new JSONParser.Execute() {

        @Override
        public void onPreExecute(){

        }


        @Override
        public void onPostExecute(String s) {
          DialogMaker.disMissDialog();
          getSharedPreferences("EZpay", 0).edit().remove("push").apply();
        }
      });
    }
  }

  @Override
  protected void onDestroy() {
    startService(new Intent(this, LocationService.class));
    super.onDestroy();
  }

  public void checkPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.friends_layout:
        friendsList = new FriendsList();
        friendsList.set_ChatList_feed(_ChatList_feed);
        friendsList.setOutNetworkContact(outNetworkContact);
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
        friendsList = new FriendsList();
        friendsList.set_ChatList_feed(_ChatList_feed);
        friendsList.setOutNetworkContact(outNetworkContact);
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
//                    File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
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
}
