package magia.af.ezpay;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Utilities.LocalPersistence;

public class ChatPageActivity extends BaseActivity {
  private String phone;
  private String contactName;
  private String imageUrl = "http://new.opaybot.ir";
  RecyclerView recyclerView;
  RSSFeed _feed;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_page_activity);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null){
      phone = bundle.getString("phone");
      contactName = bundle.getString("contactName");
      imageUrl = imageUrl + bundle.getString("image");
    }
    ImageView contactImage = (ImageView)findViewById(R.id.profile_image);
    Picasso.with(this).load(R.drawable.step_two_1).into(contactImage);
    TextView name = (TextView)findViewById(R.id.txt_user_name);
    name.setText(contactName);
    recyclerView = (RecyclerView)findViewById(R.id.pay_list_recycler);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);
    new fillContact().execute(phone);
  }
  private class fillContact extends AsyncTask<String, Void, RSSFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

    }

    @Override
    protected RSSFeed doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.payLogWithAnother(params[0]);
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        Log.i("PAY" , result.toString());
      }
    }
  }
}
