package magia.af.ezpay;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.helper.GetContact;

public class FriendListActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);
    new DOMParser().getContact(new GetContact().getContact(FriendListActivity.this));

  }
}
