package magia.af.ezpay;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.global.App;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    int version;
    ContactDatabase database;

    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);
    if (Build.VERSION.SDK_INT >= 23) {
      checkPermissions();
    }

    database = new ContactDatabase(this);
    GetContact getContact = new GetContact();
    if (App.getVersion() == 1) {
      App.setVersion(App.getVersion() + 1);
      ArrayList<String> strings = new ArrayList<>();
      strings.add("09122849191");
      strings.add("09122849192");
      strings.add("09122849193");
      strings.add("09122849194");
      strings.add("09122849195");
      database = new ContactDatabase(this);

      Log.e("string size ", strings.size()+"" );
      for (int i = 0; i < strings.size(); i++) {
        database.createData("123456789");
        Log.i("DATABASE", database.getAllData().toString());
      }
    } else if (App.getVersion() > 1) {
      Log.i("DATABASE", database.getAllData().toString());
      App.setVersion(App.getVersion() + 1);
      ArrayList<String> databaseContact = database.getAllData();
      ArrayList<String> phoneContact = getContact.allContacts(this);
      for (int i = 0; i < phoneContact.size(); i++) {
        for (int j = 0; j < databaseContact.size(); j++) {
          if (phoneContact.get(i).equals(databaseContact.get(j))) {
            Log.i("TEEE", "TRUE");
          }
        }
      }
    }
    Log.i("Time of run", App.getVersion() + "");
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (getSharedPreferences("EZpay", 0).getString("token", "").length() > 10) {
          new fillContact().execute();
        } else {
          startActivity(new Intent(Splash.this, LoginActivity.class));
          finish();
        }
      }
    }, 1500);

  }

  private class fillContact extends AsyncTask<Void, Void, RSSFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected RSSFeed doInBackground(Void... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.getContact(new GetContact().getContact(Splash.this, (RSSFeed) new LocalPersistence().readObjectFromFile(Splash.this, "All_Contact_List")));
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        new LocalPersistence().writeObjectToFile(Splash.this, result, "Contact_List");
        startActivity(new Intent(Splash.this, FriendListActivity.class).putExtra("contact", result));
        finish();
      } else
        Toast.makeText(Splash.this, "problem in connection!", Toast.LENGTH_SHORT).show();

    }

  }

  private void checkPermissions() {
    ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
  }

}
