package magia.af.ezpay.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
}
