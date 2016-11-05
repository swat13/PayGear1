package magia.af.ezpay.global;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import magia.af.ezpay.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Saeid yazdany on 10/26/2016.
 */

public class App extends Application {
  public static SharedPreferences preferences;
  public static SharedPreferences.Editor editor;
  @Override
  public void onCreate() {
    super.onCreate();
    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
      .setDefaultFontPath("fonts/IRANSansMobile(FaNum).ttf")
      .setFontAttrId(R.attr.fontPath)
      .build()
    );
    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }

  public static int getVersion(){
    return preferences.getInt("version",0);
  }

  public static void setVersion(int version){
    editor = preferences.edit();
    editor.putInt("version",version);
    editor.apply();
  }
}
