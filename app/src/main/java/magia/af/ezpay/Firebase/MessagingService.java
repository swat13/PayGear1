package magia.af.ezpay.Firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.GroupChatPageActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.LogItem;
import magia.af.ezpay.R;
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
    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: " + mode);
        //Displaying data in log
        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        LogItem logItem = getChatItem(remoteMessage.getNotification().getBody());
        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getNotification().getBody());
            if (mode == 1) {
                chatMessageHandler = ChatPageActivity.informNotif();
                chatMessageHandler.handleMessage(logItem, jsonObject.isNull("chatItem"), "1");
            } else if (mode == 2) {
                groupMessageHandler = GroupChatPageActivity.informNotif();
                groupMessageHandler.handleMessage(logItem, jsonObject.isNull("groupChatItem"), "2");
            } else if (mode == 3) {
                mainMessageHandler = FriendsList.informNotif();
                mainMessageHandler.handleMessage(logItem, false, "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onMessageReceived: " + logItem);

    }

    private void sendNotification(LogItem messageBody) {

        Log.e(TAG, "sendNotification: " + messageBody.getNotifType());
        Log.e(TAG, "sendNotification: " + messageBody.getNotifBody());
//        Toast.makeText(getApplicationContext(), "salam, " + messageBody.getNotifType(), Toast.LENGTH_SHORT).show();
        /*if (messageBody.getNotifType() == 4) {
            Log.e(TAG, "sendNotification: 0000000");
            String url = messageBody.getNotifParam1();
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ali)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(messageBody.getNotifBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        } else {
            Log.e(TAG, "sendNotification: 111111111");
            Intent intent = new Intent(this, Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }*/

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
                    .setSmallIcon(R.mipmap.phone)
                    .setLargeIcon(result)
                    .setVibrate(new long[]{400, 400})
                    .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                    .build();
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notif);
        }
    }

    private static LogItem getChatItem(String json) {
        LogItem rssItem = new LogItem();
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
                rssItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
                rssItem.setfMobile(jsonObjectf.getString("mobile"));
                rssItem.setfId(jsonObjectf.getString("id"));
                rssItem.setfTitle(jsonObjectf.getString("title"));
                rssItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
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

}