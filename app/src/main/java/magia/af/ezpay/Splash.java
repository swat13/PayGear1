package magia.af.ezpay;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.global.App;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {

  ContactDatabase database;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (getSharedPreferences("EZpay", 0).getString("token", "").length() > 10) {

          database = new ContactDatabase(Splash.this);
          GetContact getContact = new GetContact();

          RSSFeed databaseContact = database.getAllData();
          Log.e("000000000", "run: "+databaseContact.getItemCount() );
          RSSFeed phoneContact = getContact.getNewContact(Splash.this);
          Log.e("1111111", "run: "+phoneContact.getItemCount() );
          for (int i = 0; i < phoneContact.getItemCount(); i++) {
            for (int j = 0; j < databaseContact.getItemCount(); j++) {
              if (phoneContact.getItem(i).getTelNo().equals(databaseContact.getItem(j).getTelNo())) {
                Log.e("))))))))))", "run: $$$$$ "+i );
                phoneContact.removeItem(i);
                break;
              }
            }
          }
          Log.e("2222222222", "run: "+phoneContact.getItemCount() );
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

  public void compareContact(ContactDatabase database){

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
      return domParser.sendContact(params[0]);
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        startActivity(new Intent(Splash.this, FriendListActivity.class).putExtra("contact", result));
      } else
        Toast.makeText(Splash.this, "problem in connection!", Toast.LENGTH_SHORT).show();

    }

  }

}
