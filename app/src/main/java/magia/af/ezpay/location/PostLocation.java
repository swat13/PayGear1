package magia.af.ezpay.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import magia.af.ezpay.Parser.Parser;

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
    SharedPreferences preferences =context.getSharedPreferences("EZpay", 0);

    Parser parser = new Parser(preferences.getString("token", ""));


    float x = preferences.getFloat("Lat",0);
    float y = preferences.getFloat("Lng",0);
    float a = preferences.getFloat("Acc",0);

    Log.e("Location" ,""+(Math.pow (x-params[0],2)+Math.pow (y-params[1],2)));
  // if(Math.pow (x-params[0],2)+Math.pow (y-params[1],2) >   0.0000015)
    parser.postLocation(params[0],params[1],params[2]);
    //SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(context);

    SharedPreferences.Editor editor = preferences.edit();

    editor.putFloat("Lat",(float)(double) params[0]);
    editor.putFloat("Lng",(float)(double)params[1]);
    editor.putFloat("Acc",(float)(double)params[2]);
    editor.apply();
    return null;
  }
}
