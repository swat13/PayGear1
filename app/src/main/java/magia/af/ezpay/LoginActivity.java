package magia.af.ezpay;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.fragments.LoginFragment;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.modules.ContactItem;

/**
 * Created by Saeid Yazdany on 10/25/2016.
 */

public class LoginActivity extends BaseActivity {

  private static final String TAG = "TAG";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    //Load The Login Fragment
    loadFragment();
    new GetContact().getContact(this);
    DOMParser parser = new DOMParser();
    ArrayList<ContactItem> contactItems = parser.getAllContactInfo(new GetContact().getContact(this));
    for (int i = 0 ; i< contactItems.size() ; i++) {
      Log.i("Contact Info", "phone "+i+" :" + contactItems.get(i).getPhoneNumber());
    }
//    GetContact getContact = new GetContact();
//    getContact.getContact(this);
  }

  //Load The Login Fragment
  public void loadFragment() {
    LoginFragment loginFragment = LoginFragment.getInstance();
    getSupportFragmentManager()
      .beginTransaction()
      .add(R.id.container, loginFragment)
      .commit();
  }
}
