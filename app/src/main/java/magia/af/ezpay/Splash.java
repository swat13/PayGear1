package magia.af.ezpay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {

  /*ddfdf*/

    ContactDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No expl
            //
            // anation needed, we can request the permission.

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {

            // No expl
            //
            // anation needed, we can request the permission.

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {

            // No expl
            //
            // anation needed, we can request the permission.

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
        new Handler().postDelayed(new Runnable() {
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
        }, 1500);

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
            return domParser.getContact(params[0]);
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
