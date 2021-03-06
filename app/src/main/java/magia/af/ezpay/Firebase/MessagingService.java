package magia.af.ezpay.Firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.GroupChatPageActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.fragments.FriendsList;
import magia.af.ezpay.interfaces.MessageHandler;

/**
 * Created by Alif on 10/5/2016.
 */


/**
 * Created by Belal on 5/27/2016.
 */

public class MessagingService extends FirebaseMessagingService {
  MessageHandler groupMessageHandler;
  MessageHandler chatMessageHandler;
  MessageHandler mainMessageHandler;
  public static int mode;
  int type;
  private static final String TAG = "MyFirebaseMsgService";


  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Map<String, String> data = remoteMessage.getData();
    String key = data.get("");
    Log.e(TAG, "Mode: " + mode);
    if (remoteMessage.getNotification() != null) {
      Log.e(TAG, "Notification Received: " + remoteMessage.getNotification().getBody());
      PayLogItem payLogItem = getChatItem(remoteMessage.getNotification().getBody());
      sendNotification(payLogItem);
      try {
        JSONObject jsonObject = new JSONObject(remoteMessage.getNotification().getBody());
        if (mode == 1) {//chat pv
          chatMessageHandler = ChatPageActivity.informNotif();
          chatMessageHandler.handleMessage(payLogItem, jsonObject.isNull("chatItem"), "1");
        } else if (mode == 2) {//group chat
          groupMessageHandler = GroupChatPageActivity.informNotif();
          groupMessageHandler.handleMessage(payLogItem, jsonObject.isNull("groupChatItem"), "2");
        } else if (mode == 3) {//update the chat list
          mainMessageHandler = FriendsList.informNotif();
          if (jsonObject.getString("type").contains("1")) {
            mainMessageHandler.handleMessage(payLogItem, false, "");
          } else {
            mainMessageHandler.handleMessageGp(jsonObject.getString("body"), jsonObject.getString("param1"));
          }


        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    } else {
      start();
      Log.e(TAG, "Notification Received: " + remoteMessage.getData());
      PayLogItem payLogItem = getChatItem(data.get("body"));
      sendNotification(payLogItem);
      try {
        JSONObject jsonObject = new JSONObject(data.get("body"));
        if (mode == 1) {//chat pv
          chatMessageHandler = ChatPageActivity.informNotif();
          chatMessageHandler.handleMessage(payLogItem, jsonObject.isNull("chatItem"), "1");
        } else if (mode == 2) {//group chat
          groupMessageHandler = GroupChatPageActivity.informNotif();
          groupMessageHandler.handleMessage(payLogItem, jsonObject.isNull("groupChatItem"), "2");
        } else if (mode == 3) {//update the chat list
          mainMessageHandler = FriendsList.informNotif();
          if (jsonObject.getString("type").contains("1")) {
            mainMessageHandler.handleMessage(payLogItem, false, "");
          } else {
            mainMessageHandler.handleMessageGp(jsonObject.getString("body"), jsonObject.getString("param1"));
          }


        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

  }

  private void sendNotification(PayLogItem messageBody) {

    Log.e(TAG, "sendNotification: " + messageBody.getNotifType());
    Log.e(TAG, "sendNotification: " + messageBody.getNotifBody());


    if (messageBody.getNotifBody() != null) {
      Log.e(TAG, "sendNotification: 0000000");
      String url = messageBody.getNotifParam1();
      Log.e(TAG, "sendNotification: " + url);
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
      Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      URL mUrl = null;
      Bitmap large_icon = null;
      Log.e(TAG, "sendNotification: " + messageBody.getfPhoto());
      try {
        mUrl = new URL("https://paygear.ir"+messageBody.getfPhoto());
      HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      large_icon = BitmapFactory.decodeStream(input);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setLargeIcon(large_icon)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentText(messageBody.getComment())
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      notificationManager.notify(0, notificationBuilder.build());
    }

  }

  public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;
    private String title, message, imageUrl;

    public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
      super();
      this.mContext = context;
      this.title = title;
      this.message = message;
      this.imageUrl = imageUrl;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
      InputStream in;
      try {
        URL url = new URL(this.imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        in = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(in);
        return myBitmap;
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap result) {
      super.onPostExecute(result);
      Intent intent = new Intent(mContext, MainActivity.class);
      intent.putExtra("key", "value");
      PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);

      NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
      Notification notif = new Notification.Builder(mContext)
        .setContentIntent(pendingIntent)
        .setContentTitle(title)
        .setContentText(message)
        .setLargeIcon(result)
        .setVibrate(new long[]{400, 400})
        .setStyle(new Notification.BigPictureStyle().bigPicture(result))
        .build();
      notif.flags |= Notification.FLAG_AUTO_CANCEL;
      notificationManager.notify(1, notif);
    }
  }

  public void start() {
    new fillContact().execute("[]");
  }

  private static PayLogItem getChatItem(String json) {
    PayLogItem rssItem = new PayLogItem();
    try {
      JSONObject jsonObject = new JSONObject(json);
      rssItem.setNotifBody(jsonObject.getString("body"));
      rssItem.setNotifType(jsonObject.getInt("type"));
      rssItem.setNotifParam1(jsonObject.getString("param1"));
      if (!jsonObject.isNull("param2")) {
        rssItem.setCancelId(Integer.parseInt(jsonObject.getString("param2")));
      }

      if (!jsonObject.isNull("chatItem")) {
        JSONObject lastChatObject = jsonObject.getJSONObject("chatItem");
        rssItem.setId(lastChatObject.getInt("id"));
        rssItem.setfMobile(lastChatObject.getString("f"));
        rssItem.settMobile(lastChatObject.getString("t"));
        rssItem.setAmount(lastChatObject.getInt("a"));
        rssItem.setDate(lastChatObject.getString("d"));
        rssItem.setPaideBool(lastChatObject.getBoolean("o"));
        rssItem.setStatus(lastChatObject.getBoolean("s"));
        rssItem.setGroup(lastChatObject.getBoolean("g"));
        rssItem.setComment(lastChatObject.getString("c"));
      } else if (!jsonObject.isNull("groupChatItem")) {
        JSONObject lastChatObject = jsonObject.getJSONObject("groupChatItem");
        JSONObject jsonObjectf = lastChatObject.getJSONObject("f");
        JSONObject jsonObjectt = lastChatObject.getJSONObject("t");
        rssItem.setfPhoto("https://paygear.ir" + jsonObjectf.getString("photo"));
        rssItem.setfMobile(jsonObjectf.getString("mobile"));
        rssItem.setfId(jsonObjectf.getString("id"));
        rssItem.setfTitle(jsonObjectf.getString("title"));
        rssItem.settPhoto("https://paygear.ir" + jsonObjectt.getString("photo"));
        rssItem.settMobile(jsonObjectt.getString("mobile"));
        rssItem.settId(jsonObjectt.getString("id"));
        rssItem.settTitle(jsonObjectt.getString("title"));
        rssItem.setId(lastChatObject.getInt("id"));
        rssItem.setAmount(lastChatObject.getInt("a"));
        rssItem.setDate(lastChatObject.getString("d"));
        rssItem.setPaideBool(lastChatObject.getBoolean("o"));
        rssItem.setStatus(lastChatObject.getBoolean("s"));
        rssItem.setGroup(lastChatObject.getBoolean("g"));
        rssItem.setComment(lastChatObject.getString("c"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return rssItem;
  }


  public class fillContact extends AsyncTask<String, Void, ChatListFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected ChatListFeed doInBackground(String... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.checkContactListWithGroup(params[0]);
    }

    @Override
    protected void onPostExecute(ChatListFeed result) {
      if (result != null) {
        if (ApplicationData.getOutNetworkContact() != null) {
          result.addAll(result.getItemCount(), ApplicationData.getOutNetworkContact());
          ApplicationData.setChatListFeed(result);
        }
      }
      super.onPostExecute(result);
    }

  }


}