package magia.af.ezpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import magia.af.ezpay.Parser.RSSFeed;

public class GroupChatPageActivity extends AppCompatActivity {

  RSSFeed rssFeed;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat_page);
    rssFeed = (RSSFeed) getIntent().getSerializableExtra("contact");
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(this , MainActivity.class);
    intent.putExtra("contact",rssFeed);
    startActivity(intent);
    finish();
  }
}
