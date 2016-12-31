package magia.af.ezpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import magia.af.ezpay.Utilities.Constant;

public class IPGViewActivity extends AppCompatActivity {

  private String au = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ipgview);

    WebView webView = (WebView)findViewById(R.id.webView);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null){
      au = bundle.getString("au").replace("\"","");
    }

    webView.loadUrl(Constant.IPG_URL + au);
  }
}
