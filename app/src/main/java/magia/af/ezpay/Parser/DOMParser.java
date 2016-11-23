package magia.af.ezpay.Parser;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.util.Base64;
import android.util.JsonReader;
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

import magia.af.ezpay.R;

public class DOMParser {

    public DOMParser(String token) {
        this.token = token;
        Log.e("TOKEN", "DOMParser: " + token);
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


    public RSSItem getAccountId(String id) {

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
            RSSItem rssItem = new RSSItem();

            try {
                rssItem.setContactImg(jsonObject.getString("photo"));
                rssItem.setTelNo(jsonObject.getString("mobile"));
                rssItem.setUserId(jsonObject.getString("id"));
                rssItem.setContactName(jsonObject.getString("title"));
                Log.e("Name", rssItem.getContactName());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return rssItem;


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RSSItem getAccount() {

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
            RSSItem rssItem = new RSSItem();

            try {
                rssItem.setContactImg(jsonObject.getString("photo"));
                rssItem.setTelNo(jsonObject.getString("mobile"));
                rssItem.setUserId(jsonObject.getString("id"));
                rssItem.setContactName(jsonObject.getString("title"));
                rssItem.setCredit(jsonObject.getInt("credit"));
                Log.e("Name", rssItem.getContactName());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return rssItem;


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
                    rssItem.setContactStatus(true);
                    rssItem.setTelNo(jsonObject.getString("mobile"));
                    rssItem.setContactName(getContactName(jsonObject.getString("mobile"), json));
                    rssItem.setContactImg(jsonObject.getString("photo"));
                    if (!jsonObject.isNull("lastchat")) {
                        JSONObject object = jsonObject.getJSONObject("lastchat");
                        rssItem.setLastChatId(object.getInt("id"));
                        rssItem.setLastChatFrom(object.getString("f"));
                        rssItem.setLastChatTo(object.getString("t"));
                        rssItem.setLastChatAmount(object.getInt("a"));
                        rssItem.setLastChatDate(object.getString("d"));
                        rssItem.setLastChatOrderByFromOrTo(object.getBoolean("o"));
                        rssItem.setComment(object.getString("c"));
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
/*sss*/
    public RSSFeed checkContactListWithGroup(String json) {

        try {

            URL url = new URL(mainUrl + "api/Account/CheckContactListWithGroups");
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
            RSSItem rssItem = null;
            Log.e("Test0000", "checkContactListWithGroup: ");
            for (int i = 0; i < jsonArray.length(); i++) {
                rssItem = new RSSItem();
                JSONObject contactObject = jsonArray.getJSONObject(i);
                if (contactObject.getString("$type").contains("FriendModel")){
                    Log.e("Test11111", "checkContactListWithGroup: " + contactObject.toString());
                    rssItem.setContactImg(contactObject.getString("photo"));
                    rssItem.setTelNo(contactObject.getString("mobile"));
                    rssItem.setUserId(contactObject.getString("id"));
                    rssItem.setTitle(contactObject.getString("title"));
                    rssItem.setContactCount(i);
                    Log.e("Test123123", "checkContactListWithGroup: " + contactObject.toString());
                    if (!contactObject.isNull("lastchat")){
                        JSONObject lastChatObject = contactObject.getJSONObject("lastchat");
                        Log.e("Test2222", "checkContactListWithGroup: " + lastChatObject.toString());
                        rssItem.setLastChatId(lastChatObject.getInt("id"));
                        rssItem.setLastChatFrom(lastChatObject.getString("f"));
                        rssItem.setLastChatTo(lastChatObject.getString("t"));
                        rssItem.setLastChatAmount(lastChatObject.getInt("a"));
                        rssItem.setLastChatDate(lastChatObject.getString("d"));
                        rssItem.setLastChatOrderByFromOrTo(lastChatObject.getBoolean("o"));
                        rssItem.setContactStatus(lastChatObject.getBoolean("s"));
                        rssItem.setGroup(lastChatObject.getBoolean("g"));
                        rssItem.setComment(lastChatObject.getString("c"));
                    }
                }
                else {
                    rssItem.setGroupId(contactObject.getInt("id"));
                    rssItem.setGroupPhoto(contactObject.getString("photo"));
                    rssItem.setGroupTitle(contactObject.getString("title"));
                    rssItem.setGroup(true);
                    if (!contactObject.isNull("members")) {
                        JSONArray groupsMemberObject = contactObject.getJSONArray("members");
                        for (int j = 0; j < groupsMemberObject.length(); j++) {
                            JSONObject memberGroupObject = groupsMemberObject.getJSONObject(j);
                            Log.e("Test444444", "checkContactListWithGroup: " + memberGroupObject.toString());
                            rssItem.setGroupMemberId(memberGroupObject.getString("id"));
                            rssItem.setGroupMemberTitle(memberGroupObject.getString("title"));
                            rssItem.setGroupMemberPhoto(memberGroupObject.getString("photo"));
                            rssItem.setGroupMemberPhone(memberGroupObject.getString("mobile"));
                            if (!memberGroupObject.isNull("lastchat")) {
                                JSONObject groupsMembersLastChat = memberGroupObject.getJSONObject("lastchat");
                                Log.e("Test55555", "checkContactListWithGroup: " + groupsMembersLastChat.toString());
                                rssItem.setGroupMemberLastChatId(groupsMembersLastChat.getInt("id"));
                                rssItem.setGroupMemberLastChatFrom(groupsMembersLastChat.getString("f"));
                                rssItem.setGroupMemberLastChatTo(groupsMembersLastChat.getString("t"));
                                rssItem.setGroupMemberLastChatAmount(groupsMembersLastChat.getInt("a"));
                                rssItem.setGroupMemberLastChatDate(groupsMembersLastChat.getString("d"));
                                rssItem.setGroupMemberLastChatOrderPay(groupsMembersLastChat.getBoolean("o"));
                                rssItem.setGroupMemberLastChatStatus(groupsMembersLastChat.getBoolean("s"));
                                rssItem.setGroupMemberLastChatFromGroup(groupsMembersLastChat.getBoolean("g"));
                                rssItem.setGroupMemberLastChatComment(groupsMembersLastChat.getString("c"));
                            }
                        }
                        if (!contactObject.isNull("lastchat")) {
                            JSONArray groupLastChatArray = contactObject.getJSONArray("lastchat");
                            for (int j = 0; j < groupLastChatArray.length(); j++) {
                                JSONObject lastChatGroupObject = groupLastChatArray.getJSONObject(j);
                                Log.e("Test6666666666", "checkContactListWithGroup: " + lastChatGroupObject.toString());
                                rssItem.setGroupLastChatId(lastChatGroupObject.getInt("id"));
                                rssItem.setGroupLastChatFrom(lastChatGroupObject.getString("f"));
                                rssItem.setGroupLastChatTo(lastChatGroupObject.getString("t"));
                                rssItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
                                rssItem.setGroupLastChatDate(lastChatGroupObject.getString("d"));
                                rssItem.setGroupLastChatOrderPay(lastChatGroupObject.getBoolean("o"));
                                rssItem.setGroupLastChatStatus(lastChatGroupObject.getBoolean("s"));
                                rssItem.setGroupLastChatFromGroup(lastChatGroupObject.getBoolean("g"));
                                rssItem.setGroupLastChatComment(lastChatGroupObject.getString("c"));
                            }
                        }
                        }
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

            Log.e("getchatlog@@@@@@", sb.toString());
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
                    "\"anotherMobile\" : \"" + phone + "\",\n" +
                    "\"amount\" : " + Amount + ",\n" +
                    "\"comment\" : \"" + cm + "\"\n" +
                    "}";

            Log.e("RR999999999", "activateSong: " + request);
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

            Log.e("R@@@@@@", sb.toString());


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

    public PayLogItem accPaymentRequest(String id, String detail) {

        try {

            URL url = new URL(mainUrl + "api/payment/AcceptPaymentRequest");
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
            jsonObject.put("id", Integer.parseInt(id));
            jsonObject.put("paymentDetails", detail);

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

            Log.e("accpay111111", sb.toString());

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
            jsonObject.put("IMEI", "5555");
            jsonObject.put("DeviceID", "5555");
            jsonObject.put("Platform", "5555");
            jsonObject.put("OsVersion", "5555");
            jsonObject.put("AppVersion", "5555");
            jsonObject.put("PushToken", pushToken);

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

            Log.e("accpay111111", sb.toString());
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

    public boolean deletePayment(int id) {
        boolean state = false;
        try {

            URL url = new URL(mainUrl + "api/Payment/" + id);
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("DELETE");
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Authorization", "bearer " + token);

            Log.e("Auth", "deletePayment: " + token);
            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
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

    public String changeUserImage(File uploadFile1) throws IOException {

        int size = (int) uploadFile1.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(uploadFile1));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);


        URL url = new URL(mainUrl + "api/account/setPhotoBase64");
        Log.e("1111111", "doInBackground: " + url);
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
        writer.write("{\"data\":\"" + base64 + "\"}");
        writer.flush();
        writer.close();
        os.close();
        int resCode = httpConn.getResponseCode();
        Log.e("0000000", "doInBackground: " + resCode);
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

        Log.e("@@@@@@", sb.toString());
        return sb.toString();


    }

    public RSSFeed getLocation() {

        try {

            URL url = new URL(mainUrl + "api/location");
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(13000);
            httpConn.setReadTimeout(13000);
            httpConn.setRequestProperty("Content-Type", "application/json");
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

            RSSFeed rssFeed = new RSSFeed();
            Log.e("@@@@@@2222", sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                RSSItem rssItem = new RSSItem();
                try {
                    rssItem.setContactImg(jsonArray.getJSONObject(i).getString("photo"));
                    Log.e("CIMage", rssItem.getContactImg());
                    rssItem.setTelNo(jsonArray.getJSONObject(i).getString("mobile"));
                    rssItem.setUserId(jsonArray.getJSONObject(i).getString("id"));
                    rssItem.setContactName(jsonArray.getJSONObject(i).getString("title"));
                    Log.e("Name", rssItem.getContactName());
                } catch (Exception e) {
                    e.printStackTrace();

                }
                rssFeed.addItem(rssItem);
            }

            return rssFeed;


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void postLocation(double lat, double lng) {
        try {
            URL url = new URL(mainUrl + "api/location");
            Log.e("1111111", "doInBackground: " + url);
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

            Log.e("999999999", "activateSong: " + jsonObject.toString());
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String addMemberToGroup(int id, ArrayList<String> memberPhone) {
/*ddd*/
        try {

            URL url = new URL(mainUrl + "AddMember");
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
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("id", id);
            for (int i = 0; i < memberPhone.size(); i++) {
                jsonArray.put(i, memberPhone.get(i));
            }
            jsonObject.put("membersMobiles", jsonArray);

            Log.e("999999999", "activateSong: " + jsonObject.toString());
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("0000000", "doInBackground: " + resCode);
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

    public boolean group(String title, JSONArray jsonMemberPhone) {
/*ddd*/
        boolean state = false;
        try {

            URL url = new URL(mainUrl + "api/Group");
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

            Log.e("TOKEN", "group: " + "bearer " + token);

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("membersMobiles", jsonMemberPhone);

            Log.e("group999999999", "activateSong: " + jsonObject);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("group0000000", "doInBackground: " + resCode);
            if (resCode != 400) {
                state = true;
            } else {
                state = false;
                Log.e("ERROR", "group: " + "Error Happened");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return state;
    }

    public PayLogItem groupChat(int id, int pageSize, String maxDate, ArrayList<String> memberPhone) {
/*ddd*/
        try {

            URL url = new URL(mainUrl + "Chat?id=" + id + "&pagesize=" + pageSize + "&maxDate=" + maxDate);
            Log.e("1111111", "doInBackground: " + url);
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
            Log.e("0000000", "doInBackground: " + resCode);
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
}
