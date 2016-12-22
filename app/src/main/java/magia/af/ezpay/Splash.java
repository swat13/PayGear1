package magia.af.ezpay;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {

    ContactDatabase database;
    JSONArray jsonArray;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    ChatListFeed newChatList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

//    keyguardManager =
//      (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      fingerprintManager =
//        (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//    }
//
//    if (!keyguardManager.isKeyguardSecure()) {
//
////            Toast.makeText(this,
////              "Lock screen security not enabled in Settings",
////              Toast.LENGTH_LONG).show();
//    }
//
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//      // TODO: Consider calling
//      //    ActivityCompat#requestPermissions
//      // here to request the missing permissions, and then overriding
//      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//      //                                          int[] grantResults)
//      // to handle the case where the user grants the permission. See the documentation
//      // for ActivityCompat#requestPermissions for more details.
//      return;
//    }
//        if (!fingerprintManager.hasEnrolledFingerprints()) {
//
//            // This happens when no fingerprints are registered.
//            Toast.makeText(this,
//              "Register at least one fingerprint in Settings",
//              Toast.LENGTH_LONG).show();
//        }


        if (!getSharedPreferences("EZpay", 0).getString("token", "").isEmpty()) {
            new ComparingContactWithDatabase().execute();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                }
            }, 2500);
        }

    }


    public String newContact(JSONArray jsonArray) {
        return jsonArray.toString();
    }

    public class ComparingContactWithDatabase extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {
            database = new ContactDatabase(Splash.this);
            GetContact getContact = new GetContact();

            ChatListFeed databaseContact = database.getAllData();
            ChatListFeed phoneContact = getContact.getNewContact(Splash.this);
            newChatList=new ChatListFeed();

            for (int i = 0; i < phoneContact.getItemCount(); i++) {
                for (int j = 0; j < databaseContact.getItemCount(); j++) {
                    if (phoneContact.getItem(i).getTelNo().equals(databaseContact.getItem(j).getTelNo())) {
                        newChatList.addItem(phoneContact.getItem(i));
                        phoneContact.removeItem(i);
                        break;
                    }
                }
            }
            jsonArray = new JSONArray();
            for (int i = 0; i < phoneContact.getItemCount()
                    ; i++) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("m", phoneContact.getItem(i).getTelNo());
                    jsonObject.put("t", phoneContact.getItem(i).getContactName());
                    jsonArray.put(i, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                database.createData(phoneContact.getItem(i).getTelNo(), phoneContact.getItem(i).getContactName());
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                new fillContact().execute(jsonArray.toString());
            }
            super.onPostExecute(jsonArray);
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
                ApplicationData.setChatListFeed(result);
                startActivity(new Intent(Splash.this, MainActivity.class).putExtra("contact", result).putExtra("notContacts",newChatList));
                finish();
            } else {
                Toast.makeText(Splash.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

}
