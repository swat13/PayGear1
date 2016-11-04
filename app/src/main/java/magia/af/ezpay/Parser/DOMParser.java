package magia.af.ezpay.Parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DOMParser {

  public DOMParser(String token) {
    this.token = token;
  }

  public DOMParser() {
  }


  private String mainUrl = "http://new.opaybot.ir/";
  private String token = "";


  /**
   * @param phoneNumber
   * @return
   **/
  public boolean register(String phoneNumber) {

    try {

      URL url = new URL(mainUrl + "api/Account/RegisterByMobile");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(30000);
      httpConn.setReadTimeout(30000);
      httpConn.setRequestProperty("Content-Type", "application/json");

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      String request = "{\n" +
        "\"mobile\" : \"" + phoneNumber + "\"\n" +
        "}";

      Log.e("999999999", "activateSong: " + request);
      writer.write(request);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 200) {
        return true;
      } else
        return false;


    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;

  }

  public String resendSMS(String phoneNumber) {

    try {

      URL url = new URL(mainUrl + "accounts/user/activation/resend/09" + phoneNumber);
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("PUT");
      httpConn.setConnectTimeout(10000);
      httpConn.setReadTimeout(10000);
      httpConn.setRequestProperty("Content-Type", "application/json");


      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 500) {
        return "NI";
      }

      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      Log.e("@@@@@@", sb.toString());
      return "ok";

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "NI";

  }

  /**
   * @param username
   * @param activeCode
   * @return
   **/
  public String Verify_Login_Activation_Code(String username, String activeCode) {

    try {

      URL url = new URL(mainUrl + "api/Account/VerifySMSCode");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(10000);
      httpConn.setReadTimeout(10000);
      httpConn.setRequestProperty("Content-Type", "application/json");

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      String request = "{\n" +
        "\"mobile\" : \"" + username + "\",\n" +
        "\"code\" : " + activeCode + "\n" +
        "}";

      Log.e("999999999", "activateSong: " + request);
      writer.write(request);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 400) {
        return "wrong";
      }

      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      Log.e("@@@@@@", sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      String token = jsonObject.getString("access_token");
      return token;

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return "NI";

  }

  /**
   * @return
   **/
  public RSSFeed sendContact(String json) {

    try {

      URL url = new URL(mainUrl + "api/Account/CheckContactList");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      Log.e("999999999", "activateSong: " + json);
      writer.write(json);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 400) {
        return null;
      }

      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      Log.e("@@@@@@", sb.toString());
      JSONArray jsonArray = new JSONArray(sb.toString());
      RSSFeed rssFeed = new RSSFeed();


      for (int i = 0; i < jsonArray.length(); i++) {
        RSSItem rssItem = new RSSItem();
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        try {
          rssItem.setContactStatus(true);
          rssItem.setTelNo(jsonObject.getString("mobile"));
          rssItem.setContactName(getContactName(jsonObject.getString("mobile"), json));
          rssItem.setContactImg(jsonObject.getString("photo"));
          JSONObject object = jsonObject.getJSONObject("lastchat");
          if (object != null) {
            rssItem.setLastChatId(object.getInt("id"));
            rssItem.setLastChatFrom(object.getString("f"));
            rssItem.setLastChatTo(object.getString("t"));
            rssItem.setLastChatAmount(object.getInt("a"));
            rssItem.setLastChatDate(object.getString("d"));
            rssItem.setLastChatOrderByFromOrTo(object.getBoolean("o"));
            rssItem.setComment(object.getString("c"));
          }
          else {
            break;
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
        rssFeed.addItem(rssItem);
      }

      return rssFeed;

      /**
       * TODO: check if activated then return the token to Splash class
       *
       * */


    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public String getContactName(String phoneNumber, String jsons) {
    try {
      JSONArray jsonArray = new JSONArray(jsons);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        if (jsonObject.getString("m").equals(phoneNumber)) {
          return jsonObject.getString("t");
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;


  }

  public PayLogFeed payLogWithAnother(String phone) {

    try {

      URL url = new URL(mainUrl + "api/payment/PayLogWithAnother");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      String request = "{\n" +
        "\"anotherMobile\" : \"" + phone + "\"\n" +
        "}";

      Log.e("999999999", "activateSong: " + phone);
      writer.write(request);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 400) {
        return null;
      }

      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      Log.e("@@@@@@", sb.toString());
      JSONArray jsonArray = new JSONArray(sb.toString());

      PayLogFeed payLogFeed = new PayLogFeed();

      for (int i = 0; i < jsonArray.length(); i++) {
        PayLogItem payLogItem1 = new PayLogItem();
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        //kk
        try {
          payLogItem1.setId(jsonObject.getInt("id"));
          payLogItem1.setFrom(jsonObject.getString("f"));
          payLogItem1.setTo(jsonObject.getString("t"));
          payLogItem1.setAmount(jsonObject.getInt("a"));
          payLogItem1.setDate(jsonObject.getString("d"));
          payLogItem1.setPaideBool(jsonObject.getBoolean("o"));
          payLogItem1.setComment(jsonObject.getString("c"));

        } catch (JSONException e) {
          e.printStackTrace();
        }
        payLogFeed.addItem(payLogItem1);
      }

      return payLogFeed;

      /**
       * TODO: check if activated then return the token to Splash class
       *
       * */


    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public PayLogItem sendPaymentRequest(String phone, String detail, String comment, String amount) {

    try {

      URL url = new URL(mainUrl + "api/payment/PayToAnotherWithTF");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("anotherMobile", phone);
      jsonObject.put("paymentDetails", detail);
      jsonObject.put("amount", Integer.parseInt(amount));
      jsonObject.put("comment", comment);

      Log.e("999999999", "activateSong: " + jsonObject);
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 400) {
        return null;
      }

      InputStream in = httpConn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();

      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      Log.e("@@@@@@", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setFrom(jsonObject1.getString("f"));
        payLogItem.setTo(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setComment(jsonObject1.getString("c"));


      } catch (JSONException e) {
        e.printStackTrace();
      }

      return payLogItem;

    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

}
