package magia.af.ezpay.Parser;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import magia.af.ezpay.Utilities.LocalPersistence;

public class Parser {

  public Parser(String token) {
    this.token = token;
    Log.e("TOKEN", "Parser: " + token);
  }

  public Parser() {
  }


  private String mainUrl = "https://paygear.org/";
  private String token = "";

/*kkkkkk*/

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

  public String getQR() {

    try {

      URL url = new URL(mainUrl + "api/QR");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Authorization", "JWT " + token);

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


      Log.e("$$$$$$$", "getQr " + sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      String js = jsonObject.getString("photo");

      return js;


    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ChatListItem getAccountId(String id) {

    try {

      URL url = new URL(mainUrl + "api/account/" + id);
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Authorization", "bearer " + token);

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


      Log.e("@@@@@@2222", sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      ChatListItem chatListItem = new ChatListItem();

      try {
        chatListItem.setContactImg(jsonObject.getString("photo"));
        chatListItem.setTelNo(jsonObject.getString("mobile"));
        chatListItem.setUserId(jsonObject.getString("id"));
        chatListItem.setContactName(jsonObject.getString("title"));
        Log.e("Name", chatListItem.getContactName());
      } catch (Exception e) {
        e.printStackTrace();

      }
      return chatListItem;


    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public ChatListItem getAccount() {

    try {

      URL url = new URL(mainUrl + "api/account");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode != 200) {
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

      Log.e("@@@@@@2222", sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      ChatListItem chatListItem = new ChatListItem();

      try {
        chatListItem.setContactImg(jsonObject.getString("photo"));
        chatListItem.setTelNo(jsonObject.getString("mobile"));
        chatListItem.setUserId(jsonObject.getString("id"));
        chatListItem.setContactName(jsonObject.getString("title"));
        chatListItem.setCredit(jsonObject.getInt("credit"));
        Log.e("Name", chatListItem.getContactName());
      } catch (Exception e) {
        e.printStackTrace();

      }
      return chatListItem;

    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public JSONObject getQRid(String id) {

    try {

      URL url = new URL(mainUrl + "api/QR/" + id + "/");
      Log.e("1111111", "doInBackground: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Authorization", "JWT " + token);

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


      Log.e("$$$$$$$", "getAdslInfo:aa " + sb.toString());
      Log.e("@@@@@@", sb.toString());
      JSONObject jsonObject = new JSONObject(sb.toString());
      String js = jsonObject.getString("errcode");
      int b = Integer.parseInt(js);
      if (b == 0) {

        try {
          JSONArray jsonArray = jsonObject.getJSONArray("errmsg");
          JSONObject jsonObject1 = jsonArray.getJSONObject(0);
          return jsonObject1;
        } catch (Exception e) {

        }
        return null;
      } else if (b == 1) {
        return null;

      }


    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param username
   * @param activeCode
   * @return
   **/
  public String[] Verify_Login_Activation_Code(String username, String activeCode, String newName) {

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

      JSONObject object = new JSONObject();
      object.put("mobile", username);
      object.put("code", activeCode);
      object.put("newName", newName);
//            String request = "{\n" +
//                    "\"mobile\" : \"" + username + "\",\n" +
//                    "\"code\" : " + activeCode + ",\n" +
//                    "\"newName\" : \"" + newName + "\"\n" +
//                    "}";

      Log.e("999999999", "activateSong: " + object.toString());
      writer.write(object.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("0000000", "doInBackground: " + resCode);
      if (resCode == 400) {
        return null;
      } else if (resCode == 500) {
        String[] Wrong = new String[2];
        Wrong[0] = "Wrong";
        Wrong[1] = "Wrong";
        return Wrong;
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
      String[] tokId = new String[2];
      String token = jsonObject.getString("access_token");
      String id = jsonObject.getString("id");
      Log.e("id", id);
      tokId[0] = token;
      tokId[1] = id;
      return tokId;

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return null;

  }

  /**
   * @return
   **/
  public ChatListFeed getContact(String json) {

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
      ChatListFeed chatListFeed = new ChatListFeed();


      for (int i = 0; i < jsonArray.length(); i++) {
        ChatListItem chatListItem = new ChatListItem();
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        try {
          chatListItem.setContactStatus(true);
          chatListItem.setTelNo(jsonObject.getString("mobile"));
          chatListItem.setContactName(getContactName(jsonObject.getString("mobile"), json));
          chatListItem.setContactImg(jsonObject.getString("photo"));
          if (!jsonObject.isNull("lastchat")) {
            JSONObject object = jsonObject.getJSONObject("lastchat");
            chatListItem.setLastChatId(object.getInt("id"));
            chatListItem.setLastChatFrom(object.getString("f"));
            chatListItem.setLastChatTo(object.getString("t"));
            chatListItem.setLastChatAmount(object.getInt("a"));
            chatListItem.setLastChatDate(object.getString("d"));
            chatListItem.setLastChatOrderByFromOrTo(object.getBoolean("o"));
            chatListItem.setComment(object.getString("c"));
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
        chatListFeed.addItem(chatListItem);
      }

      return chatListFeed;

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

  /*sss*/
  public ChatListFeed checkContactListWithGroup(String json) {

    try {

      URL url = new URL(mainUrl + "api/Account/CheckContactListWithGroups");
      Log.e("Parser", "Url: " + url);
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

      Log.e("Parser", "Output: " + json);
      writer.write(json);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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
      Log.e("Parser In", sb.toString());
      JSONArray jsonArray = new JSONArray(sb.toString());

      ChatListFeed chatListFeed = new ChatListFeed();
      ChatListItem rssChatListItem;

      ArrayList<ChatListItem> contactMembers = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++) {
        rssChatListItem = new ChatListItem();
        JSONObject contactObject = jsonArray.getJSONObject(i);
        if (contactObject.getString("$type").contains("FriendModel")) {
          rssChatListItem.setContactImg(contactObject.getString("photo"));
          rssChatListItem.setTelNo(contactObject.getString("mobile"));
          rssChatListItem.setUserId(contactObject.getString("id"));
          rssChatListItem.setTitle(contactObject.getString("title"));
          rssChatListItem.setContactName(contactObject.getString("title"));

          if (!contactObject.isNull("lastchat")) {
            JSONObject jsonObject = contactObject.getJSONObject("lastchat");
            rssChatListItem.setComment(jsonObject.getString("c"));
            rssChatListItem.setLastChatAmount(jsonObject.getInt("a"));
            rssChatListItem.setLastChatId(jsonObject.getInt("id"));
            rssChatListItem.setLastChatTo(jsonObject.getString("f"));
            rssChatListItem.setLastChatFrom(jsonObject.getString("t"));
            rssChatListItem.setLastChatOrderByFromOrTo(jsonObject.getBoolean("o"));
            rssChatListItem.setContactStatus(jsonObject.getBoolean("s"));
          }

          rssChatListItem.setContactCount(i);
          contactMembers.add(rssChatListItem);
        } else if (contactObject.getString("$type").contains("GroupModel")) {
          GroupItem groupItem = new GroupItem();
          groupItem.setGroupId(contactObject.getInt("id"));
          groupItem.setGroupPhoto(contactObject.getString("photo"));
          groupItem.setGroupTitle(contactObject.getString("title"));
          //Be Checked
          if (!contactObject.isNull("members")) {
            JSONArray groupsMemberObject = contactObject.getJSONArray("members");
            MembersFeed membersFeed = new MembersFeed();
            for (int j = 0; j < groupsMemberObject.length(); j++) {
              MembersItem membersItem = new MembersItem();
              JSONObject memberGroupObject = groupsMemberObject.getJSONObject(j);
              membersItem.setMemberId(memberGroupObject.getString("id"));
              membersItem.setMemberTitle(memberGroupObject.getString("title"));
              membersItem.setMemberPhoto(memberGroupObject.getString("photo"));
              membersItem.setMemberPhone(memberGroupObject.getString("mobile"));
              membersFeed.addMemberItem(membersItem);
            }
            if (!contactObject.isNull("lastChats")) {
              JSONArray groupLastChatArray = contactObject.getJSONArray("lastChats");
              for (int j = 0; j < groupLastChatArray.length(); j++) {
                JSONObject lastChatGroupObject = groupLastChatArray.getJSONObject(j);
                JSONObject from = lastChatGroupObject.getJSONObject("f");
                JSONObject to = lastChatGroupObject.getJSONObject("t");
                groupItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
                groupItem.setGroupLastChatId(lastChatGroupObject.getInt("id"));
                groupItem.setGroupLastChatFrom(from.getString("mobile"));
                groupItem.setGroupLastChatTo(to.getString("mobile"));
                groupItem.setGroupLastChatDate(lastChatGroupObject.getString("d"));
                groupItem.setGroupLastChatOrderPay(lastChatGroupObject.getBoolean("o"));
                groupItem.setGroupLastChatStatus(lastChatGroupObject.getBoolean("s"));
                groupItem.setGroupLastChatFromGroup(lastChatGroupObject.getBoolean("g"));
                groupItem.setGroupLastChatComment(lastChatGroupObject.getString("c"));
              }
            }
            groupItem.setMembersFeed(membersFeed);
          }
          rssChatListItem.setGroupItem(groupItem);
        }
        //Come Back Here!!!
        rssChatListItem.setContactMembers(contactMembers);
//                rssChatListItem.setGroupFeed();
        chatListFeed.addItem(rssChatListItem);
      }

      return chatListFeed;

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

  public PayLogFeed payLogWithAnother(String phone, String maxDate, String maxpage) {

    try {

      URL url = new URL(mainUrl + "api/payment/PayLogWithAnother?pagesize=" + maxpage + "&maxDate=" + maxDate);
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

      Log.e("Parser:", "Phone: " + phone);
      writer.write(request);
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("GetChatLog:", sb.toString());
      JSONArray jsonArray = new JSONArray(sb.toString());

      PayLogFeed payLogFeed = new PayLogFeed();

      for (int i = 0; i < jsonArray.length(); i++) {
        PayLogItem payLogItem1 = new PayLogItem();
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        //kk
        try {
          payLogItem1.setId(jsonObject.getInt("id"));
          payLogFeed.getHash().put(jsonObject.getInt("id"), i);
          payLogItem1.setfMobile(jsonObject.getString("f"));
          payLogItem1.settMobile(jsonObject.getString("t"));
          payLogItem1.setAmount(jsonObject.getInt("a"));
          payLogItem1.setDate(jsonObject.getString("d"));
          payLogItem1.setPaideBool(jsonObject.getBoolean("o"));
          payLogItem1.setStatus(jsonObject.getBoolean("s"));
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

  public PayLogItem RequestFromAnother(String phone, String Amount, String cm) {

    try {

      URL url = new URL(mainUrl + "api/payment/RequestFromAnother");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("amount", Integer.parseInt(Amount.replace(",", "")));
      jsonObject.put("comment", cm);

      Log.e("Parser", "Parser Out: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "Res Code: " + resCode);

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

      Log.e("Parser", "Parser Input: " + resCode);


      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
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

  public PayLogItem groupRequestFromAnother(String phone, String Amount, String cm, int groupId) {

    try {

      URL url = new URL(mainUrl + "api/payment/RequestFromAnother");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("amount", Integer.parseInt(Amount.replace(",", "")));
      jsonObject.put("comment", cm);
      jsonObject.put("groupId", groupId);

      Log.e("Parser", "Parser Out: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "Res Code: " + resCode);
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

      Log.e("Parser In", sb.toString());


      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        JSONObject object = jsonObject1.getJSONObject("f");
        payLogItem.setfPhoto("http://new.opaybot.ir" + object.getString("photo"));
        payLogItem.setfMobile(object.getString("mobile"));
        payLogItem.setfId(object.getString("id"));
        payLogItem.setfTitle(object.getString("title"));
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));
        payLogItem.setGroup(jsonObject1.getBoolean("g"));
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

  public PayLogItem accPaymentRequest(String id, String detail) {

    try {

      URL url = new URL(mainUrl + "api/payment/AcceptPaymentRequest");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("id", Integer.parseInt(id));
      jsonObject.put("paymentDetails", detail);

      Log.e("Parser", "Out: " + jsonObject);
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setComment(jsonObject1.getString("c"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));


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

  public String sendDeviceId(String pushToken) {

    try {

      URL url = new URL(mainUrl + "api/Device");
      Log.e("Parser", "Url " + url);
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
      jsonObject.put("IMEI", "5555");
      jsonObject.put("DeviceID", "5555");
      jsonObject.put("Platform", "1");
      jsonObject.put("OsVersion", "5555");
      jsonObject.put("AppVersion", "5555");
      jsonObject.put("PushToken", pushToken);

      Log.e("Parser", "Out: " + jsonObject);
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());
      return sb + "";

    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public boolean deletePayment(Integer id) {
    boolean state = false;
    try {

      URL url = new URL(mainUrl + "api/Payment/" + id);
      Log.e("Parser", "Url: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(false);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("DELETE");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Authorization", "bearer " + token);
      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
      if (resCode == 200) {
        state = true;
      } else state = false;

    } catch (IOException e) {
      e.printStackTrace();
    }

    return state;
  }

  public PayLogItem sendPaymentRequest(String phone, String detail, String comment, String amount) {

    try {

      URL url = new URL(mainUrl + "api/payment/PayToAnotherWithTF");
      Log.e("Parser", "Url:" + url);
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

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
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

      Log.e("ParserIn ", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));

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

  public String changeUserImage(String uploadFile1) throws IOException {

    URL url = new URL(mainUrl + "api/account/setPhotoBase64");
    Log.e("Parser", "Url: " + url);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setDoInput(true);
    httpConn.setAllowUserInteraction(false);
    httpConn.setRequestMethod("POST");
    httpConn.setConnectTimeout(30000);
    httpConn.setReadTimeout(30000);
    httpConn.setRequestProperty("Authorization", "bearer " + token);
    httpConn.setRequestProperty("Content-Type", "application/json");

    OutputStream os = httpConn.getOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


    //writer.write(base64);
    writer.write("{\"data\":\"" + uploadFile1 + "\"}");
    writer.flush();
    writer.close();
    os.close();
    int resCode = httpConn.getResponseCode();
    Log.e("Parser", "ResCode: " + resCode);
    if (resCode != 200) {
      return "Error";
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

    Log.e("Parser In", sb.toString());
    return sb.toString();


  }

  public String changeGroupImage(String uploadFile1, String id) throws IOException {


    URL url = new URL(mainUrl + "api/Group/" + id + "/SetPhotoBase64");
    Log.e("Parser", "Url: " + url);
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setDoInput(true);
    httpConn.setAllowUserInteraction(false);
    httpConn.setRequestMethod("POST");
    httpConn.setConnectTimeout(30000);
    httpConn.setReadTimeout(30000);
    httpConn.setRequestProperty("Authorization", "bearer " + token);
    httpConn.setRequestProperty("Content-Type", "application/json");

    OutputStream os = httpConn.getOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


    //writer.write(base64);
    writer.write("{\"data\":\"" + uploadFile1 + "\"}");
    writer.flush();
    writer.close();
    os.close();
    int resCode = httpConn.getResponseCode();
    Log.e("Parser", "ResCode: " + resCode);
    if (resCode != 200) {
      return "Error";
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

    Log.e("Parser In", sb.toString());
    return sb.toString();


  }

  public ChatListFeed getLocation() {

    try {

      URL url = new URL(mainUrl + "api/location");
      Log.e("Parser", "Url " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      ChatListFeed chatListFeed = new ChatListFeed();
      Log.e("Parser In", sb.toString());
      JSONArray jsonArray = new JSONArray(sb.toString());
      for (int i = 0; i < jsonArray.length(); i++) {
        ChatListItem chatListItem = new ChatListItem();
        try {
          chatListItem.setContactImg(jsonArray.getJSONObject(i).getString("photo"));
          chatListItem.setTelNo(jsonArray.getJSONObject(i).getString("mobile"));
          chatListItem.setUserId(jsonArray.getJSONObject(i).getString("id"));
          chatListItem.setContactName(jsonArray.getJSONObject(i).getString("title"));
        } catch (Exception e) {
          e.printStackTrace();

        }
        chatListFeed.addItem(chatListItem);
      }

      return chatListFeed;


    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void postLocation(double lat, double lng, double acc) {
    try {

      URL url = new URL(mainUrl + "api/location");
      Log.e("Parser", "Url: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("POST");
      httpConn.setConnectTimeout(13000);
      httpConn.setReadTimeout(13000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);
      OutputStream os = httpConn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("lat", lat);
      jsonObject.put("lng", lng);
      jsonObject.put("acc", acc);

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  public String addMemberToGroup(int id, ArrayList<String> memberPhone) {
    try {

      URL url = new URL(mainUrl + "AddMember");
      Log.e("Parser", "Url: " + url);
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
      JSONArray jsonArray = new JSONArray();
      jsonObject.put("id", id);
      for (int i = 0; i < memberPhone.size(); i++) {
        jsonArray.put(i, memberPhone.get(i));
      }
      jsonObject.put("membersMobiles", jsonArray);

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
      if (resCode != 400) {
        return null;
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public GroupItem group(String title, JSONArray jsonMemberPhone) {
    boolean state = false;
    GroupItem groupItem = null;
    try {

      URL url = new URL(mainUrl + "api/Group");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("title", title);
      jsonObject.put("membersMobiles", jsonMemberPhone);

      Log.e("Parser", "Output: " + jsonObject);
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
      if (resCode != 400) {
        state = true;
      } else {
        state = false;
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


      Log.e("Parser In", sb.toString());
      JSONObject object = new JSONObject(sb.toString());
      groupItem = new GroupItem();
      groupItem.setGroupId(object.getInt("id"));
      groupItem.setGroupTitle(object.getString("title"));
      groupItem.setGroupPhoto(object.getString("photo"));
      if (!object.isNull("members")) {
        JSONArray jsonArray = object.getJSONArray("members");
        MembersFeed membersFeed = new MembersFeed();
        for (int i = 0; i < jsonArray.length(); i++) {
          MembersItem membersItem = new MembersItem();
          JSONObject object1 = jsonArray.getJSONObject(i);
          membersItem.setMemberTitle(object1.getString("title"));
          membersItem.setMemberPhoto(object1.getString("photo"));
          membersItem.setMemberPhone(object1.getString("mobile"));
          membersItem.setMemberId(object1.getString("id"));
          membersFeed.addMemberItem(membersItem);
        }
        groupItem.setMembersFeed(membersFeed);
      }
      if (object.isNull("lastChats")) {
        JSONObject groupsLastChat = object.getJSONObject("lastchat");
        groupItem.setGroupLastChatId(groupsLastChat.getInt("id"));
        groupItem.setGroupLastChatFrom(groupsLastChat.getString("f"));
        groupItem.setGroupLastChatTo(groupsLastChat.getString("t"));
        groupItem.setGroupLastChatAmount(groupsLastChat.getInt("a"));
        groupItem.setGroupLastChatDate(groupsLastChat.getString("d"));
        groupItem.setGroupLastChatOrderPay(groupsLastChat.getBoolean("o"));
        groupItem.setGroupLastChatStatus(groupsLastChat.getBoolean("s"));
        groupItem.setGroupLastChatFromGroup(groupsLastChat.getBoolean("g"));
        groupItem.setGroupLastChatComment(groupsLastChat.getString("c"));
      }

      return groupItem;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public PayLogItem groupChat(int id, int pageSize, String maxDate, ArrayList<String> memberPhone) {
    try {

      URL url = new URL(mainUrl + "Chat?id=" + id + "&pagesize=" + pageSize + "&maxDate=" + maxDate);
      Log.e("Parser", "Url: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Content-Type", "application/json");
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
      if (resCode != 400) {
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

      Log.e("Parser In", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));

        payLogItem.setComment(jsonObject1.getString("c"));


      } catch (JSONException e) {
        e.printStackTrace();
      }

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public GroupItem groupWithChat(String id, int pageSize) {

    GroupItem groupItem = null;
    try {

      URL url = new URL(mainUrl + "api/group/" + id + "?pagesize=" + pageSize);
      Log.e("Parser", "Url: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(false);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
      if (resCode != 400) {
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

      Log.e("Parser In", sb.toString());
      JSONObject object = new JSONObject(sb.toString());
      groupItem = new GroupItem();
      groupItem.setGroupId(object.getInt("id"));
      groupItem.setGroupTitle(object.getString("title"));
      groupItem.setGroupPhoto(object.getString("photo"));
      if (!object.isNull("members")) {
        JSONArray jsonArray = object.getJSONArray("members");
        MembersFeed membersFeed = new MembersFeed();
        for (int i = 0; i < jsonArray.length(); i++) {
          MembersItem membersItem = new MembersItem();
          JSONObject object1 = jsonArray.getJSONObject(i);
          membersItem.setMemberTitle(object1.getString("title"));
          membersItem.setMemberPhoto(object1.getString("photo"));
          membersItem.setMemberPhone(object1.getString("mobile"));
          membersItem.setMemberId(object1.getString("id"));
          membersFeed.addMemberItem(membersItem);
        }
        groupItem.setMembersFeed(membersFeed);
      }
      if (object.isNull("lastChats")) {
        JSONObject groupsLastChat = object.getJSONObject("lastchat");
        groupItem.setGroupLastChatId(groupsLastChat.getInt("id"));
        groupItem.setGroupLastChatFrom(groupsLastChat.getString("f"));
        groupItem.setGroupLastChatTo(groupsLastChat.getString("t"));
        groupItem.setGroupLastChatAmount(groupsLastChat.getInt("a"));
        groupItem.setGroupLastChatDate(groupsLastChat.getString("d"));
        groupItem.setGroupLastChatOrderPay(groupsLastChat.getBoolean("o"));
        groupItem.setGroupLastChatStatus(groupsLastChat.getBoolean("s"));
        groupItem.setGroupLastChatFromGroup(groupsLastChat.getBoolean("g"));
        groupItem.setGroupLastChatComment(groupsLastChat.getString("c"));
      }

      return groupItem;

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    return null;

  }


  public PayLogItem payToAnotherWithTF(String anotherMobile, String paymentDetails, String Amount, String cm, int groupId) {

    try {

      URL url = new URL(mainUrl + "api/Payment/PayToAnotherWithTF");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("anotherMobile", anotherMobile);
      jsonObject.put("paymentDetails", paymentDetails);
      jsonObject.put("amount", Amount);
      jsonObject.put("comment", cm);
      jsonObject.put("groupId", groupId);

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());


      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
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

  public PayLogFeed getGroupChat(int id, int page, String date) {

    try {

      URL url = new URL(mainUrl + "api/Group/" + id + "/Chat?pagesize=" + page + "&maxDate=" + date);
      Log.e("Parser", "Url: " + url);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setDoOutput(false);
      httpConn.setDoInput(true);
      httpConn.setAllowUserInteraction(false);
      httpConn.setRequestMethod("GET");
      httpConn.setConnectTimeout(20000);
      httpConn.setReadTimeout(20000);
      httpConn.setRequestProperty("Authorization", "bearer " + token);

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());

      JSONArray jsonArray = new JSONArray(sb.toString());
      PayLogFeed feed = new PayLogFeed();

      for (int i = 0; i < jsonArray.length(); i++) {
        PayLogItem payLogItem = null;
        try {
          payLogItem = new PayLogItem();
          JSONObject object = jsonArray.getJSONObject(i);
          JSONObject jsonObjectf = object.getJSONObject("f");
          JSONObject jsonObjectt = object.getJSONObject("t");
          payLogItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
          payLogItem.setfMobile(jsonObjectf.getString("mobile"));
          payLogItem.setfId(jsonObjectf.getString("id"));
          payLogItem.setfTitle(jsonObjectf.getString("title"));
          payLogItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
          payLogItem.settMobile(jsonObjectt.getString("mobile"));
          payLogItem.settId(jsonObjectt.getString("id"));
          payLogItem.settTitle(jsonObjectt.getString("title"));
          payLogItem.setId(object.getInt("id"));
          payLogItem.setAmount(object.getInt("a"));
          payLogItem.setDate(object.getString("d"));
          payLogItem.setPaideBool(object.getBoolean("o"));
          payLogItem.setStatus(object.getBoolean("s"));
          payLogItem.setGroup(object.getBoolean("g"));
          payLogItem.setComment(object.getString("c"));
          feed.getHash().put(object.getInt("id"), i);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        feed.addItem(payLogItem);
      }
      return feed;

    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;

  }

  public PayLogItem sendGroupPaymentRequest(String phone, String detail, String comment, String amount, String id) {

    try {

      URL url = new URL(mainUrl + "api/payment/PayToAnotherWithTF");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("groupId", Integer.parseInt(id));

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        JSONObject jsonObjectf = jsonObject1.getJSONObject("f");
        JSONObject jsonObjectt = jsonObject1.getJSONObject("t");
        payLogItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
        payLogItem.setfMobile(jsonObjectf.getString("mobile"));
        payLogItem.setfId(jsonObjectf.getString("id"));
        payLogItem.setfTitle(jsonObjectf.getString("title"));
        payLogItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
        payLogItem.settMobile(jsonObjectt.getString("mobile"));
        payLogItem.settId(jsonObjectt.getString("id"));
        payLogItem.settTitle(jsonObjectt.getString("title"));
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));
        payLogItem.setGroup(jsonObject1.getBoolean("g"));
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

  public PayLogItem payToAnotherWithCredit(String anotherMobile, String Amount, String cm) {

    try {

      URL url = new URL(mainUrl + "api/Payment/PayToAnotherWithCredit");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("anotherMobile", anotherMobile);
      jsonObject.put("amount", Amount);
      jsonObject.put("comment", cm);

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());


      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
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

  public PayLogItem payToAnotherWithCreditInGroup(String anotherMobile, String Amount, String cm, String groupId) {

    try {

      URL url = new URL(mainUrl + "api/Payment/PayToAnotherWithCredit");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("anotherMobile", anotherMobile);
      jsonObject.put("amount", Amount);
      jsonObject.put("comment", cm);
      jsonObject.put("groupId", Integer.parseInt(groupId));

      Log.e("Parser", "Output: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        JSONObject jsonObjectf = jsonObject1.getJSONObject("f");
        JSONObject jsonObjectt = jsonObject1.getJSONObject("t");
        payLogItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
        payLogItem.setfMobile(jsonObjectf.getString("mobile"));
        payLogItem.setfId(jsonObjectf.getString("id"));
        payLogItem.setfTitle(jsonObjectf.getString("title"));
        payLogItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
        payLogItem.settMobile(jsonObjectt.getString("mobile"));
        payLogItem.settId(jsonObjectt.getString("id"));
        payLogItem.settTitle(jsonObjectt.getString("title"));
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));
        payLogItem.setGroup(jsonObject1.getBoolean("g"));
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

  public PayLogItem acceptPaymentWithCredit(int id) {

    try {

      URL url = new URL(mainUrl + "api/Payment/AcceptPaymentRequestWithCredit");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("id", id);
      Log.e("Parser", "Out: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());

      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setComment(jsonObject1.getString("c"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));


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

  public PayLogItem acceptPaymentWithCreditInGroup(int id) {

    try {

      URL url = new URL(mainUrl + "api/Payment/AcceptPaymentRequestWithCredit");
      Log.e("Parser", "Url: " + url);
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
      jsonObject.put("id", id);
      Log.e("Parser", "Out: " + jsonObject.toString());
      writer.write(jsonObject.toString());
      writer.flush();
      writer.close();
      os.close();

      int resCode = httpConn.getResponseCode();
      Log.e("Parser", "ResCode: " + resCode);
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

      Log.e("Parser In", sb.toString());
      PayLogItem payLogItem = new PayLogItem();
      JSONObject jsonObject1 = new JSONObject(sb.toString());
      try {
        payLogItem.setId(jsonObject1.getInt("id"));
        payLogItem.setfMobile(jsonObject1.getString("f"));
        payLogItem.settMobile(jsonObject1.getString("t"));
        payLogItem.setAmount(jsonObject1.getInt("a"));
        payLogItem.setDate(jsonObject1.getString("d"));
        payLogItem.setPaideBool(jsonObject1.getBoolean("o"));
        payLogItem.setComment(jsonObject1.getString("c"));
        payLogItem.setStatus(jsonObject1.getBoolean("s"));
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
