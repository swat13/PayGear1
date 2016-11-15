package magia.af.ezpay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class RadarActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.radar_view);
    ImageView imageView = (ImageView) findViewById(R.id.radar_line);
    Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate);
    imageView.setAnimation(animation);
    animation.start();

  }
}
