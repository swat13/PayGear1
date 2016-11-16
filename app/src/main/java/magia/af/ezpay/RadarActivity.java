package magia.af.ezpay;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class

RadarActivity extends AppCompatActivity {
  private RelativeLayout relativeLayout;
  private CircleImageView[] circleImageView;
  RelativeLayout.LayoutParams[] params;
  public int measuredWidth;
  public int counter;
  public int halfDisplayWidth;
  public int halfDisplayHeight;
  public int imageViewWidth;
  public int imageViewHeight;
  public CircleImageView userAvatar;
  final int userAvatarWidth = 0;
  final int userAvatarHeight = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.radar_view);
    final ImageView imageView = (ImageView) findViewById(R.id.radar_line);
    Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
    imageView.setAnimation(animation);
    animation.start();
    Random random = new Random();
    int nextNumber = random.nextInt(15);
    relativeLayout = (RelativeLayout) findViewById(R.id.container);
    Display display = getWindowManager().getDefaultDisplay();
    userAvatar = (CircleImageView) findViewById(R.id.user_avatar);
    halfDisplayWidth = display.getWidth() / 2;
    halfDisplayHeight = display.getHeight() / 2;
//    imageViewWidth = imageView.getWidth();
//    imageViewHeight = imageView.getHeight();
    ViewTreeObserver vto = userAvatar.getViewTreeObserver();
    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      public boolean onPreDraw() {
        // Remove after the first run so it doesn't fire forever
        setUserAvatarWidth(userAvatar.getMeasuredWidth());
        setUserAvatarHeight(userAvatar.getMeasuredHeight());
        Log.e("imageViewHeight", "onCreate: " + userAvatar.getMeasuredHeight());
        Log.e("imageViewWidth", "onCreate: " + getUserAvatarHeight());
        userAvatar.getViewTreeObserver().removeOnPreDrawListener(this);
        return true;
      }
    });
    params = new RelativeLayout.LayoutParams[10];
    circleImageView = new CircleImageView[10];
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        generateImageViews();
      }
    }, 1000);
  }


  public void generateImageViews() {
    for (int i = 0; i < circleImageView.length; i++) {
      Display display = getWindowManager().getDefaultDisplay();
      Log.e("width", "generateImageViews: " + display.getWidth());
      Log.e("height", "generateImageViews: " + display.getHeight());
      params[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      circleImageView[i] = new CircleImageView(RadarActivity.this);

      int random = ((int) (Math.random()+0.5) * 460) + (int) (Math.random() * 160);
      int randomH = (int) (Math.random() * display.getHeight());
//      for (int k = 200; k <= 600; k++) {
//        if (random == k) {
//          Log.e("HAAAAS" , "TRUE");
//          break;
//        } else {
//        }
//      }
      params[i].leftMargin = random;
//      params[i].rightMargin = random.nextInt(20);
      Log.e("leftMargin", "generateImageViews: " + params[i].leftMargin);
      for (int l = 400; l <= 740; l++) {
        if (randomH == l) {
          Log.e("HAAAAS", "TRUE");
          break;
        } else {
          params[i].topMargin = randomH;
//          params[i].leftMargin = random;
        }
      }
//      params[i].bottomMargin = random.nextInt(display.getHeight()/4);
      Log.e("topMargin", "generateImageViews: " + params[i].topMargin);
      circleImageView[i].setLayoutParams(params[i]);
      circleImageView[i].getLayoutParams().width = 100;
      circleImageView[i].getLayoutParams().height = 100;
      circleImageView[i].setBorderWidth(3);
      circleImageView[i].setTag(i);
      circleImageView[i].setBorderColor(Color.parseColor("#aab77c78"));
      circleImageView[i].setImageResource(R.drawable.ali);
      boolean flag = true;
//          relativeLayout.addView(circleImageView[i]);
      if (calculateDistanceOfTwoPoint(xDot(params[i]), yDot(params[i]), halfDisplayWidth, halfDisplayHeight) <= 200) {
        break;
      }
      for (int j = 0; j <= i; j++) {
        Log.e("Distance" + i + "  " + j, "generateImageViews: " + calculateDistanceOfTwoPoint(xDot(params[i]), yDot(params[i]), xDot(params[j]), yDot(params[j])));
//        Log.e("diameter", "generateImageViews: " + calculateDiameter(circleImageView[i]));
        if (i != j) {
          if (calculateDistanceOfTwoPoint(xDot(params[i]), yDot(params[i]), xDot(params[j]), yDot(params[j])) <= calculateDiameter(circleImageView[i])) {
            Log.e("FOOOR", "generateImageViews: ");
            if (i != 0) {
              i--;
            }
            flag = false;
            break;
          }
        }
      }

      if (flag) {
        Log.e("TTTTTTTT", "AAAAADDDDD:(((((((( " + i);
        relativeLayout.addView(circleImageView[i]);
        circleImageView[i].setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(RadarActivity.this, "clicked" + v.getTag(), Toast.LENGTH_SHORT).show();
          }
        });

      }
//      Log.e("Diameter", "generateImageViews: " + calculateDiameter(circleImageView[i]));
    }
  }

  public int setUserAvatarWidth(int width) {
    return width;
  }

  public int setUserAvatarHeight(int height) {
    return height;
  }

  public int getUserAvatarHeight() {
    return userAvatarHeight;
  }

  public int getUserAvatarWidth() {
    return userAvatarWidth;
  }

  public int calculateDiameter(CircleImageView circleImageView) {
    return circleImageView.getLayoutParams().width;
  }

  public int xDot(RelativeLayout.LayoutParams params) {
    return params.leftMargin;
  }

  public int yDot(RelativeLayout.LayoutParams params) {
    return params.topMargin;
  }

  public int calculateDistanceOfTwoPoint(int x1, int y1, int x2, int y2) {
    return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
  }
}
