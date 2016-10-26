package magia.af.ezpay.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.CountdownTimerTextView;
import magia.af.ezpay.interfaces.EventCallbackHandler;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class ActivationCodeFragment extends Fragment implements View.OnClickListener,EventCallbackHandler {
  private Button btn_send_activation_code_again;
  private EditText edtInputPhoneNumber;
  private TextView timerText;
  public static ActivationCodeFragment getInstance(){
    return new ActivationCodeFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_activation_code , container , false);
    btn_send_activation_code_again = (Button)rootView.findViewById(R.id.btn_send_activation_code_again);
    edtInputPhoneNumber = (EditText)rootView.findViewById(R.id.edt_input_phone_number);
    timerText = (TextView)rootView.findViewById(R.id.timer_text);
    CountdownTimerTextView countdownTimerTextView = new CountdownTimerTextView(timerText , this);
    countdownTimerTextView.setSecond(30);
    countdownTimerTextView.setMinute(2);
    countdownTimerTextView.start();
    return rootView;
  }

  @Override
  public void onClick(View v) {
    //Handle All View Click Here
    switch (v.getId()){
      case R.id.btn_done:
        break;
    }
  }

  @Override
  public void callback() {
    //Handle Callback here
  }

  private class verifyActiveCode extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
//      findViewById(R.id.wait_layout).setVisibility(View.VISIBLE);
//      GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget((ImageView) findViewById(R.id.image_view));
//      Glide.with(Splash.this).load(R.drawable.gif_loading).into(imageViewTarget);
    }

    @Override
    protected String doInBackground(String... params) {
      DOMParser domParser = new DOMParser();
      return domParser.Verify_Login_Activation_Code(params[0],params[1]);
    }

    @Override
    protected void onPostExecute(String result) {
//      findViewById(R.id.wait_layout).setVisibility(View.GONE);
 Log.e("11111111111111", "onPostExecute: " + result);
      if (result.length() > 10) {
        Log.e("55555555555", "onPostExecute: " + result);
//        getSharedPreferences("TCT", 0).edit().putString("number", phone_number).commit();
//        getSharedPreferences("TCT", 0).edit().putString("token", result).commit();
//        startActivity(new Intent(Splash.this, MainActivity.class));
//        finish();
      } else if (result.equals("wrong")) {
        Toast.makeText(getActivity(), "کد فعال سازی صحیح نمی باشد!", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getActivity(), "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
      }
    }
  }



}
