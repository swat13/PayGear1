package magia.af.ezpay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import magia.af.ezpay.Parser.RSSFeed;

public class ProfileActivity extends BaseActivity {

  RSSFeed rssFeed;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_backup);
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.e("Test1", "onOffsetChange: " + verticalOffset);
        toolbar.setBackgroundColor(Color.parseColor("#b07d79"));
      }
    });
  }
}
