package magia.af.ezpay;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;

public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
            }
        }, 3200);
    }






}
