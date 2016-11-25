package magia.af.ezpay.fragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.R;


public class RadarFragment extends Fragment {
  private RelativeLayout relativeLayout;
  private ImageView[] circleImageView;
  RelativeLayout.LayoutParams[] params;
  public int halfDisplayWidth;
  public int halfDisplayHeight;
  public ImageView userAvatar;
  final int userAvatarWidth = 0;
  final int userAvatarHeight = 0;
  private RSSFeed rssFeed;
  public int i;
  public int k;
  private String imageUrl = "http://new.opaybot.ir";
  Handler handler;
  Timer timer;
  String Ipath;


  public static RadarFragment getInstance() {
    return new RadarFragment();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.radar_view, container, false);
    final ImageView imageView = (ImageView) v.findViewById(R.id.radar_line);
    Ipath = getActivity().getSharedPreferences("EZpay", 0).getString("Ipath", "");
    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
    imageView.setAnimation(animation);
    animation.start();
    rssFeed = new RSSFeed();
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (rssFeed != null && rssFeed.getItemCount() != 0 && circleImageView.length != 0) {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              for (int j = 0; j < circleImageView.length; j++) {
                relativeLayout.removeView(circleImageView[j]);
              }
            }
          });
        }
        new GetPeopleFromTheirLocation().execute();
      }
    }, 0L, 10000);
    relativeLayout = (RelativeLayout) v.findViewById(R.id.container);
    Display display = getActivity().getWindowManager().getDefaultDisplay();
    userAvatar = (ImageView) v.findViewById(R.id.user_avatar);
    Glide.with(getActivity())
      .load(imageUrl + Ipath.replace("\"", ""))
      .asBitmap()
      .centerCrop()
      .placeholder(R.drawable.pic_profile)
      .into(new BitmapImageViewTarget(userAvatar) {
        @Override
        protected void setResource(Bitmap resource) {
          RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
          circularBitmapDrawable.setCornerRadius(700);
          userAvatar.setImageDrawable(circularBitmapDrawable);
        }
      });
    halfDisplayWidth = display.getWidth() / 2;
    halfDisplayHeight = display.getHeight() / 2;
    return v;
  }

  public class GetPeopleFromTheirLocation extends AsyncTask<RSSFeed, Void, RSSFeed> {

    @Override
    protected RSSFeed doInBackground(RSSFeed... params) {
      DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.getLocation();
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        rssFeed = result;
        params = new RelativeLayout.LayoutParams[result.getItemCount()];
        circleImageView = new ImageView[result.getItemCount()];
        generateImageViews();
      }
      super.onPostExecute(rssFeed);
    }
  }

  public void generateImageViews() {
    for (i = 0; i < rssFeed.getItemCount(); i++) {
      Display display = getActivity().getWindowManager().getDefaultDisplay();
      Log.e("width", "generateImageViews: " + display.getWidth());
      Log.e("height", "generateImageViews: " + display.getHeight());
      params[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      circleImageView[i] = new ImageView(getActivity());

      int random = ((int) (Math.random() + 0.5) * ((display.getWidth() / 2) + 100)) + (int) (Math.random() * ((display.getWidth() / 2) - 200));
      int randomH = ((int) (Math.random() + 0.5) * ((display.getHeight() / 2) + 100)) + (int) (Math.random() * ((display.getHeight() / 2) - 200));
      double randomHH = Math.random() * 360;
      Log.e("Random", randInt(200, 540) + "");
      params[i].leftMargin = random;
      Log.e("leftMargin", "generateImageViews: " + params[i].leftMargin);
      for (int l = 540; l <= 740; l++) {
        if (randomH == l) {
          Log.e("HAAAAS", "TRUE");
          break;
        } else {
          params[i].topMargin = randInt(300, 540);
        }
      }
//      params[i].bottomMargin = random.nextInt(display.getHeight()/4);
      Log.e("topMargin", "generateImageViews: " + params[i].topMargin);
      circleImageView[i].setLayoutParams(params[i]);
      circleImageView[i].getLayoutParams().width = 100;
      circleImageView[i].getLayoutParams().height = 100;
      Log.e("image", "generateImageViews: " + imageUrl + rssFeed.getItem(i).getContactImg());
//      Glide.with(this).load(imageUrl + rssFeed.getItem(i).getContactImg()).into(circleImageView[i]);
      if (this != null) {
        Glide.with(this)
          .load(imageUrl + rssFeed.getItem(i).getContactImg())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(circleImageView[i]) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              circleImageView[i].setImageDrawable(circularBitmapDrawable);
            }
          });
      } else {
        Glide.clear(circleImageView[i]);
      }
//      circleImageView[i].setImageResource(R.drawable.ali);
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
//            Toast.makeText(RadarFragment.this, "clicked" + v.getTag(), Toast.LENGTH_SHORT).show();
          }
        });
      }

    }
//      Log.e("Diameter", "generateImageViews: " + calculateDiameter(circleImageView[i]));
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

  public int calculateDiameter(ImageView circleImageView) {
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

  public static int randInt(int min, int max) {
    Random rand = new Random();

    return rand.nextInt((max - min) + 1) + min;
  }


  @Override
  public void onDestroy() {
    timer.cancel();
    super.onDestroy();
  }
}
