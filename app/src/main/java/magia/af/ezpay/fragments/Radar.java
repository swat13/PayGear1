package magia.af.ezpay.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.R;


public class Radar extends Fragment {
  private RelativeLayout relativeLayout;
  private ArrayList<ImageView> circleImageView = new ArrayList<ImageView>();
  ArrayList<RelativeLayout.LayoutParams> params = new ArrayList<RelativeLayout.LayoutParams>();
  public int halfDisplayWidth;
  public int halfDisplayHeight;
  public ImageView userAvatar;
  final int userAvatarWidth = 0;
  final int userAvatarHeight = 0;
  private ChatListFeed chatListFeed;
  public int i;
  public int k;
  private String imageUrl = "http://new.opaybot.ir";
  Handler handler;
  Timer timer;
  String Ipath;


  public static Radar getInstance() {
    return new Radar();
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.radar_view, container, false);
    final ImageView imageView = (ImageView) v.findViewById(R.id.radar_line);
    Ipath = getActivity().getSharedPreferences("EZpay", 0).getString("Ipath", "");
    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
    imageView.setAnimation(animation);
    animation.start();
    chatListFeed = new ChatListFeed();
    handler = new Handler(Looper.getMainLooper());
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {

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

  public class GetPeopleFromTheirLocation extends AsyncTask<ChatListFeed, Void, ChatListFeed> {

    @Override
    protected ChatListFeed doInBackground(ChatListFeed... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.getLocation();
    }

    @Override
    protected void onPostExecute(ChatListFeed result) {
      if (result != null) {
        chatListFeed = result;
        generateImageViews();
      }
      super.onPostExecute(chatListFeed);
    }
  }

  public void generateImageViews() {
    // params = new RelativeLayout.LayoutParams[chatListFeed.getItemCount()];
    if (circleImageView == null)
      circleImageView = new ArrayList<ImageView>();

    for (int j = 0; j < circleImageView.size(); j++) {
      boolean isDuplicate = false;
      for (i = 0; i < chatListFeed.getItemCount(); i++) {
        if (((ChatListItem) circleImageView.get(j).getTag(R.string.Amir)).getUserId().equals(chatListFeed.getItem(i).getUserId())) {
          isDuplicate = true;
          break;
        }
      }
      if (!isDuplicate) {
        relativeLayout.removeView(circleImageView.get(i));
        circleImageView.remove(i);
      }

    }

    for (i = 0; i < chatListFeed.getItemCount(); i++) {

      boolean isDuplicate = false;
      for (int j = 0; j < circleImageView.size(); j++) {
        if (((ChatListItem) circleImageView.get(j).getTag(R.string.Amir)).getUserId().equals(chatListFeed.getItem(i).getUserId())) {
          isDuplicate = true;
          break;
        }
      }
      if (isDuplicate)
        continue;

      Display display = getActivity().getWindowManager().getDefaultDisplay();
      Log.e("width", "generateImageViews: " + display.getWidth());
      Log.e("height", "generateImageViews: " + display.getHeight());

      final ImageView newView = new ImageView(getActivity());
      RelativeLayout.LayoutParams newParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      Log.e("Amir", chatListFeed.getItem(i).getContactImg());
      newView.setTag(R.string.Amir, chatListFeed.getItem(i));
      int random = ((int) (Math.random() + 0.5) * ((display.getWidth() / 2) + 100)) + (int) (Math.random() * ((display.getWidth() / 2) - 200));
      int randomH = ((int) (Math.random() + 0.5) * ((display.getHeight() / 2) + 100)) + (int) (Math.random() * ((display.getHeight() / 2) - 200));
      double randomHH = Math.random() * 360;
      Log.e("Random", randInt(200, 540) + "");
      newParam.leftMargin = random;
      Log.e("leftMargin", "generateImageViews: " + newParam.leftMargin);
      for (int l = 540; l <= 740; l++) {
        if (randomH == l) {
          Log.e("HAAAAS", "TRUE");
          break;
        } else {
          newParam.topMargin = randInt(300, 540);
        }
      }
//      params[i].bottomMargin = random.nextInt(display.getHeight()/4);
      Log.e("topMargin", "generateImageViews: " + newParam.topMargin);
      newView.setLayoutParams(newParam);
      newView.getLayoutParams().width = 100;
      newView.getLayoutParams().height = 100;
      Log.e("image", "generateImageViews: " + imageUrl + chatListFeed.getItem(i).getContactImg());

      Glide.with(this).load(imageUrl + chatListFeed.getItem(i).getContactImg()).into(newView);
      Log.e("SSS", "generateImageViews: " + " " + i);
      Glide.with(this)
        .load(imageUrl + chatListFeed.getItem(i).getContactImg())
        .asBitmap()
        .centerCrop()
        .placeholder(R.drawable.pic_profile)
        .into(new BitmapImageViewTarget(newView) {
          @Override
          protected void setResource(Bitmap resource) {
            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
            circularBitmapDrawable.setCornerRadius(700);
            Log.e("SSS2", "generateImageViews: " + " " + i);
            ((ImageView) this.view).setImageDrawable(circularBitmapDrawable);
            //newView.setImageDrawable(circularBitmapDrawable);
          }
        });
      Log.e("SSS3", "generateImageViews: " + " " + i);
//      newView.setImageResource(R.drawable.ali);
      boolean flag = true;
//          relativeLayout.addView(newView);
      if (calculateDistanceOfTwoPoint(xDot(newParam), yDot(newParam), halfDisplayWidth, halfDisplayHeight) <= 200) {
        break;
      }
      for (int j = 0; j < params.size(); j++) {
        Log.e("Distance" + i + "  " + j, "generateImageViews: " + calculateDistanceOfTwoPoint(xDot(newParam), yDot(newParam), xDot(params.get(j)), yDot(params.get(j))));
//        Log.e("diameter", "generateImageViews: " + calculateDiameter(newView));
        if (i != j) {
          if (calculateDistanceOfTwoPoint(xDot(newParam), yDot(newParam), xDot(params.get(j)), yDot(params.get(j))) <= calculateDiameter(newView)) {
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

        relativeLayout.addView(newView);
        newView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final ChatListItem chatListItem = (ChatListItem) newView.getTag(R.string.Amir);
            final Dialog dialog = new Dialog(getActivity(), R.style.PauseDialog);
            dialog.setContentView(R.layout.radar_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            ImageView userAvatar = (ImageView) dialog.findViewById(R.id.user_avatar);
            Glide.with(getActivity())
              .load(imageUrl + chatListItem.getContactImg())
              .asBitmap()
              .centerCrop()
              .placeholder(R.drawable.pic_profile)
              .into(new BitmapImageViewTarget(userAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                  RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                  circularBitmapDrawable.setCornerRadius(700);
                  ((ImageView) this.view).setImageDrawable(circularBitmapDrawable);
                }
              });
            TextView edtUserName = (TextView) dialog.findViewById(R.id.edt_username);
            edtUserName.setText(chatListItem.getContactName());
            Button btnGoToChat = (Button)dialog.findViewById(R.id.btn_pay);
            btnGoToChat.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                intent.putExtra("phone",chatListItem.getTelNo());
                intent.putExtra("contactName",chatListItem.getContactName());
                intent.putExtra("image",chatListItem.getContactImg());
                getActivity().startActivity(intent);
              }
            });
            dialog.show();
          }
        });
        params.add(newParam);
        circleImageView.add(newView);
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
