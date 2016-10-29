package magia.af.ezpay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.Parser.PullJSON;
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
