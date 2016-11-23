package magia.af.ezpay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {

  /*ddfdf*/

    ContactDatabase database;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (!getSharedPreferences("EZpay", 0).getString("token", "").isEmpty()) {

                    database = new ContactDatabase(Splash.this);
                    GetContact getContact = new GetContact();

                    RSSFeed databaseContact = database.getAllData();
                    RSSFeed phoneContact = getContact.getNewContact(Splash.this);
                    for (int i = 0; i < phoneContact.getItemCount(); i++) {
                        for (int j = 0; j < databaseContact.getItemCount(); j++) {
                            if (phoneContact.getItem(i).getTelNo().equals(databaseContact.getItem(j).getTelNo())
                              && phoneContact.getItem(i).getContactName().equals(databaseContact.getItem(j).getContactName())) {
                                phoneContact.removeItem(i);
                            }
                        }
                    }
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < phoneContact.getItemCount(); i++) {
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

                    new fillContact().execute(newContact(jsonArray));

                } else {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                }
            }
        });
        thread.start();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 1500);

    }


    public String newContact(JSONArray jsonArray) {
        return jsonArray.toString();
    }


    private class fillContact extends AsyncTask<String, Void, RSSFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RSSFeed doInBackground(String... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.checkContactListWithGroup(params[0]);
        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            if (result != null) {
                startActivity(new Intent(Splash.this, MainActivity.class).putExtra("contact", result));
                finish();
            } else
                Toast.makeText(Splash.this, "problem in connection!", Toast.LENGTH_SHORT).show();

        }

    }

}
