package magia.af.ezpay;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class RadarActivity extends AppCompatActivity {
  private RelativeLayout relativeLayout;
  private CircleImageView circleImageView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.radar_view);
    ImageView imageView = (ImageView) findViewById(R.id.radar_line);
    Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
    imageView.setAnimation(animation);
    animation.start();
    relativeLayout = (RelativeLayout) findViewById(R.id.container);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    Random random = new Random();
    Display display = getWindowManager().getDefaultDisplay();
    params.leftMargin = random.nextInt(display.getWidth()-200);
    params.topMargin = random.nextInt(display.getHeight()/2);
//    relativeLayout.setLayoutParams(params);
    circleImageView = (CircleImageView)findViewById(R.id.user_avatar);
    circleImageView.setLayoutParams(params);
    circleImageView.getLayoutParams().width = 200;
    circleImageView.getLayoutParams().height = 200;
    circleImageView.setImageResource(R.drawable.ali);
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
//        relativeLayout.addView(circleImageView);
        circleImageView.setVisibility(View.VISIBLE);
      }
    }, 3000);
    //radar
  }
}
