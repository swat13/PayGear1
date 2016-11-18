package magia.af.ezpay.location;

import android.content.Context;
import android.os.AsyncTask;

import magia.af.ezpay.Parser.DOMParser;

/**
 * Created by Android Developer on 11/17/2016.
 */

public class PostLocation extends AsyncTask<Double,Void,Void> {
  Context context;
  public PostLocation(Context context){
    this.context = context;
  }
  @Override
  protected Void doInBackground(Double... params) {
    DOMParser parser = new DOMParser(context.getSharedPreferences("EZpay", 0).getString("token", ""));
    parser.postLocation(params[0],params[1]);
    return null;
  }
}
