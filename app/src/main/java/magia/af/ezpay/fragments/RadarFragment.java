package magia.af.ezpay.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.R;


public class RadarFragment extends Fragment {
    private RelativeLayout relativeLayout;
    private ImageView[] circleImageView;
    RelativeLayout.LayoutParams[] params;
    public int measuredWidth;
    public int counter;
    public int halfDisplayWidth;
    public int halfDisplayHeight;
    public int imageViewWidth;
    public int imageViewHeight;
    public ImageView userAvatar;
    final int userAvatarWidth = 0;
    final int userAvatarHeight = 0;
    private RSSFeed rssFeed;
    public int i;
    private String imageUrl = "http://new.opaybot.ir";


    public static RadarFragment getInstance() {
        return new RadarFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.radar_view, container, false);
        final ImageView imageView = (ImageView) v.findViewById(R.id.radar_line);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        imageView.setAnimation(animation);
        animation.start();
        rssFeed = new RSSFeed();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new GetPeopleFromTheirLocation().execute();
            }
        }, 0L, 10000);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.container);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        userAvatar = (CircleImageView) v.findViewById(R.id.user_avatar);
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
            if (result != null && result.getItemCount() != 0) {
                Log.e("Count", "onPostExecute: " + result.getItemCount());
                Log.e("Image", "onPostExecute: " + result.getItem(0).getContactImg());
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

            int random = ((int) (Math.random() + 0.5) * 460) + (int) (Math.random() * 160);
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

}
