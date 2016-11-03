package magia.af.ezpay;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import magia.af.ezpay.fragments.LoginFragment;

/**
 * Created by Saeid Yazdany on 10/25/2016.
 */

public class LoginActivity extends BaseActivity {

  private static final String TAG = "TAG";
  public RelativeLayout waitingDialog;
  public ImageView imageView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
    imageView = (ImageView) findViewById(R.id.image_view);

    //Load The Login Fragment
    loadFragment();
    if (Build.VERSION.SDK_INT >= 23) {
      checkPermissions();
    }
  }

  //Load The Login Fragment
  public void loadFragment() {
    LoginFragment loginFragment = LoginFragment.getInstance();
    getSupportFragmentManager()
      .beginTransaction()
      .add(R.id.container, loginFragment)
      .commit();
  }


  private void checkPermissions() {
    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
  }
}
