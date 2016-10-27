package magia.af.ezpay.helper;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import magia.af.ezpay.interfaces.EventCallbackHandler;

/**
 * Created by pc on 10/26/2016.
 */

public class CountdownTimerTextView {
  private int second = 0;
  private int minute = 0;
  private TextView textView;
  private EventCallbackHandler callbackHandler;

  public CountdownTimerTextView(TextView textView , EventCallbackHandler callbackHandler){
    this.callbackHandler= callbackHandler;
    this.textView = textView;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public void setSecond(int second) {
    this.second = second;
  }

  private int calculateTime(){
    int min = minute * 60;
    return (min + second) * 1000;
  }

  public CountDownTimer start(){
    return new CountDownTimer(calculateTime(), 1000) {

      public void onTick(long millisUntilFinished) {

        textView.setText(String.format("%02d:%02d", (millisUntilFinished / 60000), (millisUntilFinished % 60000 / 1000)));
      }

      public void onFinish() {
        textView.setText("00:00");
        Log.i("Finish" , "TEST");
        callbackHandler.callback();
      }

    }.start();
  }

}
