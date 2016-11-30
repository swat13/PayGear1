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
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Alif on 10/5/2016.
 */import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Splash;

import static magia.af.ezpay.ChatPageActivity.informNotif;


/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        PayLogItem payLogItem = getChatItem(remoteMessage.getNotification().getBody());

        Log.e(TAG, "onMessageReceived: "+ payLogItem);

        informNotif(payLogItem);

        //Calling method to generate notification
//        sendNotification(getChatItem(remoteMessage.getNotification().getBody()));
//        new
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(PayLogItem messageBody) {

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

    private PayLogItem getChatItem(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            PayLogItem rssItem = new PayLogItem();
            rssItem.setNotifBody(jsonObject.getString("body"));
            rssItem.setNotifType(jsonObject.getInt("type"));
            rssItem.setNotifParam1(jsonObject.getString("param1"));
            if (!jsonObject.isNull("chatItem")) {
                JSONObject lastChatObject = jsonObject.getJSONObject("chatItem");
                rssItem.setId(lastChatObject.getInt("id"));
                rssItem.setFrom(lastChatObject.getString("f"));
                rssItem.setTo(lastChatObject.getString("t"));
                rssItem.setAmount(lastChatObject.getInt("a"));
                rssItem.setDate(lastChatObject.getString("d"));
                rssItem.setPaideBool(lastChatObject.getBoolean("o"));
                rssItem.setStatus(lastChatObject.getBoolean("s"));
                rssItem.setGroup(lastChatObject.getBoolean("g"));
                rssItem.setComment(lastChatObject.getString("c"));
            } else if (!jsonObject.isNull("groupChatItem")) {
                JSONObject lastChatObject = jsonObject.getJSONObject("chatItem");
//                rssItem.setidGroupId(lastChatObject.getInt("id"));
//                rssItem.setGroupPhoto(lastChatObject.getString("photo"));
//                rssItem.setGroupTitle(lastChatObject.getString("title"));
//                rssItem.setGroup(true);
//                rssItem.setGroupStatus(true);
            }
            return rssItem;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}