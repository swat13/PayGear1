package magia.af.ezpay;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import magia.af.ezpay.fragments.ActivationCodeFragment;
import magia.af.ezpay.fragments.LoginFragment;

/**
 * Created by Saeid Yazdany on 10/25/2016.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "TAG";
    public RelativeLayout waitingDialog;
    public ImageView imageView;
    public int fragment_status = 0;
    public ActivationCodeFragment activationCodeFragment;

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


    public void loadActiveFragment(String phone) {
        Bundle bundle = new Bundle();
        bundle.putString("number", phone);
        Log.i("Input phone", phone);
        activationCodeFragment = ActivationCodeFragment.getInstance();
        activationCodeFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, activationCodeFragment)
                .commit();
    }


    private void checkPermissions() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA}, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("onBackPressed: ", fragment_status+"" );
        if (fragment_status == 3) {
            Log.e("backkkk", "onBackPressed: " );
            activationCodeFragment.cancelling();


        } else if (fragment_status == 2) {


        } else
            finish();
    }


}
