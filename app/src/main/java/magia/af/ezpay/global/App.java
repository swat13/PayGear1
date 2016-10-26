package magia.af.ezpay.global;

import android.app.Application;
import android.content.Context;

import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.FontsOverride;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Saeid yazdany on 10/26/2016.
 */

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
      .setDefaultFontPath("fonts/IRANSansMobile(FaNum).ttf")
      .setFontAttrId(R.attr.fontPath)
      .build()
    );
  }
}
