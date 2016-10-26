package magia.af.ezpay;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;

public class Splash extends AppCompatActivity {

    String phone_number = "", pass = "";
    boolean backPress = false;
    boolean flag=false;
    Typeface typeface;
    TextView payfriendPer,payfriendEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        payfriendEn=(TextView) findViewById(R.id.payfriends_en);


        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobile(FaNum)_Bold.ttf");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
            }
        }, 3200);
    }



    @Override
    public void onBackPressed() {
        if (backPress) {
            backPress = false;
//            show_login_page();
        } else
            finish();

    }

    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private class verifyActiveCode extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.wait_layout).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            DOMParser domParser = new DOMParser();
            return domParser.Verify_Login_Activation_Code(phone_number, params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.wait_layout).setVisibility(View.GONE);
            if (result.length() > 10) {
                Log.e("55555555555", "onPostExecute: " + result);
                getSharedPreferences("TCT", 0).edit().putString("number", phone_number).commit();
                getSharedPreferences("TCT", 0).edit().putString("token", result).commit();
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            } else if (result.equals("wrong")) {
                Toast.makeText(Splash.this, "کد فعال سازی صحیح نمی باشد!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Splash.this, "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class resendSMS extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.wait_layout).setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            DOMParser domParser = new DOMParser();
            return domParser.resendSMS(phone_number);
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.wait_layout).setVisibility(View.GONE);
            Log.e("^^^^^^^^^", "onPostExecute: " + result);
            if (result.equals("ok")) {
                showCodePage();
            }else {
                Toast.makeText(Splash.this, "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void checkPermissions() {
        ActivityCompat.requestPermissions(Splash.this,
                new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
    }

    boolean hasPermission = true;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            // for each permission check if the user grantet/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            if (ContextCompat.checkSelfPermission(Splash.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, Manifest.permission.READ_PHONE_STATE)) {
                    Log.e("2222222222", "onRequestPermissionsResult: ");
                    hasPermission = false;
                }
                Log.e("11111111", "onRequestPermissionsResult: ");
                hasPermission = false;
            }
            Log.e("00000", "onRequestPermissionsResult: ");
            if (hasPermission) {
//                new registration().execute();
            }
        }
    }

    private void showCodePage() {
        flag=false;
        setContentView(R.layout.registeration2);
        final EditText phone_ed = (EditText) findViewById(R.id.code_Edittext);
        final Button submitLayout = (Button) findViewById(R.id.phone_btn);
        final TextView conTextView=(TextView) findViewById(R.id.continue_button1);
        conTextView.setText("تایید");
        phone_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.hint_text2).setVisibility(View.INVISIBLE);
                    phone_ed.setGravity(Gravity.LEFT | Gravity.CENTER);
                }
            }
        });

        phone_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone_ed.getText().length() == 4) {
                    findViewById(R.id.phone_btn).setBackgroundResource(R.drawable.register_bg_ed5);
                    findViewById(R.id.phone_btn).setAlpha(1);
                    hideKey(phone_ed);
                    submitLayout.performClick();

                } else if (phone_ed.getText().length() < 5) {
                    findViewById(R.id.phone_btn).setBackgroundResource(R.drawable.register_bg_ed4);
                    findViewById(R.id.phone_btn).setAlpha(0.26f);
                }
                if (phone_ed.getText().length() > 0) {
                    findViewById(R.id.hint_text2).setVisibility(View.INVISIBLE);
                    phone_ed.setGravity(Gravity.LEFT | Gravity.CENTER);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final TextView textview = (TextView) findViewById(R.id.time_label);
        final RelativeLayout timeLabelLayout = (RelativeLayout) findViewById(R.id.time_label_layout);
        timeLabelLayout.setVisibility(View.VISIBLE);


        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
//                textview.setText((millisUntilFinished / 60000)+":"+(millisUntilFinished % 60000 / 1000));

                textview.setText(String.format("%02d:%02d", (millisUntilFinished / 60000), (millisUntilFinished % 60000 / 1000)));
            }

            public void onFinish() {

                flag=true;
                timeLabelLayout.setVisibility(View.GONE);
                conTextView.setText("ارسال مجدد sms");

            }

        }.start();
        submitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag)
                {
                    new resendSMS().execute(phone_number);
                }
                else {
                    String active_code = phone_ed.getText().toString();
                    new verifyActiveCode().execute(active_code);
                }
            }
        });


    }

    private boolean validate_number(String number) {
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) != '0' && number.charAt(i) != '1' && number.charAt(i) != '2' && number.charAt(i) != '3'
                    && number.charAt(i) != '4' && number.charAt(i) != '5' && number.charAt(i) != '6' && number.charAt(i) != '7'
                    && number.charAt(i) != '8' && number.charAt(i) != '9') {
                return false;
            }
        }
        return true;
    }

    private void show_registration_page() {
        setContentView(R.layout.registration1);
//        final RelativeLayout activeSubLayout = (RelativeLayout) findViewById(R.id.active_submit_layout);
        final EditText phone_ed = (EditText) findViewById(R.id.phone_Edittext);
        final EditText pass_ed = (EditText) findViewById(R.id.phone_Edittext2);

        phone_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.hint_text).setVisibility(View.INVISIBLE);
                    ((EditText) findViewById(R.id.phone_Edittext)).setGravity(Gravity.LEFT | Gravity.CENTER);
                    findViewById(R.id.pre_text).setVisibility(View.VISIBLE);
                }
            }
        });

        phone_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("8888888", "onTextChanged: ");
                if (phone_ed.getText().length() == 9 ) {

                    findViewById(R.id.phone_btn).setBackgroundResource(R.drawable.register_bg_ed5);
                    findViewById(R.id.phone_btn).setAlpha(1);
                    hideKey(phone_ed);

                } else if (phone_ed.getText().length() < 9) {
                    findViewById(R.id.phone_btn).setBackgroundResource(R.drawable.register_bg_ed4);
                    findViewById(R.id.phone_btn).setAlpha(0.26f);
                }
                if (phone_ed.getText().length() > 0) {
                    findViewById(R.id.hint_text).setVisibility(View.INVISIBLE);
                    ((EditText) findViewById(R.id.phone_Edittext)).setGravity(Gravity.LEFT | Gravity.CENTER);
                    findViewById(R.id.pre_text).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.phone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCodePage();

            }
        });








    }




}
