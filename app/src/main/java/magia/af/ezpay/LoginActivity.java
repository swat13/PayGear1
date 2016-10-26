package magia.af.ezpay;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import magia.af.ezpay.fragments.LoginFragment;

/**
 * Created by erfan on 10/25/2016.
 */

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Load The Login Fragment
        loadFragment();
    }

    //Load The Login Fragment
    public void loadFragment(){
        LoginFragment loginFragment = LoginFragment.getInstance();
        getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container , loginFragment)
          .commit();
    }
}
