package magia.af.ezpay;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.helper.GetContact;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

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
        }, 3200);
    }

    private class fillContact extends AsyncTask<Void, Void, RSSFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RSSFeed doInBackground(Void... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.getContact(new GetContact().getContact(Splash.this));
        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            if (result != null) {
                startActivity(new Intent(Splash.this, FriendListActivity.class).putExtra("contact", result));
                finish();
            } else
                Toast.makeText(Splash.this, "problem in connection!", Toast.LENGTH_SHORT).show();

        }

    }

}
