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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import magia.af.ezpay.modules.ContactItem;

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
      httpConn.setConnectTimeout(10000);
      httpConn.setReadTimeout(10000);
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

//            OutputStream os = httpConn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//
//            String request = "{" +
//                    "\"user\":" +
//                    "{\n" +
//                    "\"Number\" : \"09" + phoneNumber + "\",\n" +
//                    "\"password\" : \"" + pass + "\"\n" +
//                    "}}";
//
//            Log.e("999999999", "activateSong: " + request);
//            writer.write(request);
//            writer.flush();
//            writer.close();
//            os.close();

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
  public RSSFeed getContact(String json) {

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
          rssItem.setTelNo(jsonObject.getString("mobile"));
          rssItem.setContactName(getContactName(jsonObject.getString("mobile"), json));

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
          Log.i("C name", jsonObject.getString("t"));
          return jsonObject.getString("t");
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;


  }

  public String getContactPhone(String name, String jsons) {
    try {
      JSONArray jsonArray = new JSONArray(jsons);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        if (jsonObject.getString("t").equals(name)) {
          Log.i("C name", jsonObject.getString("m"));
          return jsonObject.getString("m");
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList<ContactItem> getAllContactInfo(String json) {
    ArrayList<ContactItem> contactItems = new ArrayList<>();
    ContactItem contactItem = null;
    try {
      JSONArray jsonArray = new JSONArray(json);
      for (int i = 0; i < jsonArray.length(); i++) {
        contactItem = new ContactItem();
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        contactItem.setContactName(jsonObject.getString("t"));
        contactItem.setPhoneNumber(jsonObject.getString("m"));
        contactItems.add(contactItem);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return contactItems;
  }

}
