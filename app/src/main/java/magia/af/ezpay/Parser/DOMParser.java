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

public class DOMParser {

    public DOMParser(String token) {
        this.token = token;
        Log.e("TOKEN", "DOMParser: " + token);
    }

    public DOMParser() {
    }


    private String mainUrl = "http://new.opaybot.ir/";
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
            RSSFeed rssFeed2 = new RSSFeed();
            RSSFeed rssFeed3 = new RSSFeed();
            RSSItem rssItem = null;
            RSSItem rssItem2 = null;
            Log.e("Test0000", "checkContactListWithGroup: ");
            ArrayList<RSSItem> contactMembers = new ArrayList<>();
            ArrayList<RSSItem> gMembers = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                rssItem = new RSSItem();
                JSONObject contactObject = jsonArray.getJSONObject(i);
                if (contactObject.getString("$type").contains("FriendModel")) {
                    Log.e("Test11111", "checkContactListWithGroup: " + contactObject.toString());
                    rssItem.setContactImg(contactObject.getString("photo"));
                    rssItem.setTelNo(contactObject.getString("mobile"));
                    rssItem.setUserId(contactObject.getString("id"));
                    rssItem.setTitle(contactObject.getString("title"));
                    rssItem.setContactCount(i);
                    Log.e("Test123123", "checkContactListWithGroup: " + contactObject.toString());
                    if (!contactObject.isNull("lastchat")) {
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
                } else {
                    continue;
                }
                contactMembers.add(rssItem);
                rssItem.setContactMembers(contactMembers);
                rssFeed.addItem(rssItem);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                Log.e("%%%%%%%", "checkContactListWithGroup: " + i);
                rssItem2 = new RSSItem();
                JSONObject contactObject = jsonArray.getJSONObject(i);
                if (contactObject.getString("$type").contains("GroupModel")) {
                    Log.e("%%%%%%%2", "checkContactListWithGroup: " + i);
                    rssItem2.setGroupId(contactObject.getInt("id"));
                    rssItem2.setGroupPhoto(contactObject.getString("photo"));
                    rssItem2.setGroupTitle(contactObject.getString("title"));
                    rssItem2.setGroup(true);
                    rssItem2.setGroupStatus(true);
                    if (!contactObject.isNull("members")) {
                        JSONArray groupsMemberObject = contactObject.getJSONArray("members");
                        ArrayList<RSSItem> groupMembers = new ArrayList<>();
                        for (int j = 0; j < groupsMemberObject.length(); j++) {
                            RSSItem item = new RSSItem();
                            JSONObject memberGroupObject = groupsMemberObject.getJSONObject(j);
                            Log.e("Test444444", "checkContactListWithGroup: " + memberGroupObject.toString());
                            item.setGroupMemberId(memberGroupObject.getString("id"));
                            item.setGroupMemberTitle(memberGroupObject.getString("title"));
                            item.setGroupMemberPhoto(memberGroupObject.getString("photo"));
                            item.setGroupMemberPhone(memberGroupObject.getString("mobile"));
                            if (!memberGroupObject.isNull("lastchat")) {
                                JSONObject groupsMembersLastChat = memberGroupObject.getJSONObject("lastchat");
                                Log.e("Test55555", "checkContactListWithGroup: " + groupsMembersLastChat.toString());
                                item.setGroupMemberLastChatId(groupsMembersLastChat.getInt("id"));
                                item.setGroupMemberLastChatFrom(groupsMembersLastChat.getString("f"));
                                item.setGroupMemberLastChatTo(groupsMembersLastChat.getString("t"));
                                item.setGroupMemberLastChatAmount(groupsMembersLastChat.getInt("a"));
                                item.setGroupMemberLastChatDate(groupsMembersLastChat.getString("d"));
                                item.setGroupMemberLastChatOrderPay(groupsMembersLastChat.getBoolean("o"));
                                item.setGroupMemberLastChatStatus(groupsMembersLastChat.getBoolean("s"));
                                item.setGroupMemberLastChatFromGroup(groupsMembersLastChat.getBoolean("g"));
                                item.setGroupMemberLastChatComment(groupsMembersLastChat.getString("c"));
                            }
                            groupMembers.add(item);
                            rssItem2.setGroupMembers(groupMembers);
                        }
                        if (!contactObject.isNull("lastChats")) {
                            Log.e("THIS LINE 743", "checkContactListWithGroup: ");
                            JSONArray groupLastChatArray = contactObject.getJSONArray("lastChats");
                            for (int j = 0; j < groupLastChatArray.length(); j++) {
                                Log.e("THIS LINE 745", "checkContactListWithGroup: ");
                                JSONObject lastChatGroupObject = groupLastChatArray.getJSONObject(j);
                                Log.e("Test6666666666", "checkContactListWithGroup: " + lastChatGroupObject.getInt("a"));
                                rssItem2.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
                                rssItem2.setGroupLastChatId(lastChatGroupObject.getInt("id"));
//                rssItem2.setGroupLastChatFrom(lastChatGroupObject.getString("f"));
//                rssItem2.setGroupLastChatTo(lastChatGroupObject.getString("t"));
                                rssItem2.setGroupLastChatDate(lastChatGroupObject.getString("d"));
                                rssItem2.setGroupLastChatOrderPay(lastChatGroupObject.getBoolean("o"));
                                rssItem2.setGroupLastChatStatus(lastChatGroupObject.getBoolean("s"));
                                rssItem2.setGroupLastChatFromGroup(lastChatGroupObject.getBoolean("g"));
                                rssItem2.setGroupLastChatComment(lastChatGroupObject.getString("c"));
                            }
                        }
                    }
                } else {
                    continue;
                }
                gMembers.add(rssItem2);
                rssItem2.setGroupArray(gMembers);
                rssFeed2.addItem(rssItem2);
            }
            Log.e("Rss 2", "checkContactListWithGroup: " + rssFeed2.getItemCount());
            Log.e("gMember", "checkContactListWithGroup: " + gMembers.size());

//      for (int i = 0; i < gMembers.size(); i++) {
//        ArrayList<RSSItem> rssItems = gMembers.get(i).getGroupMembers();
//        for (int j = 0; j < rssItems.size(); j++) {
//          Log.e("gStar", "onCreateView: " + rssItems.get(j).getGroupLastChatAmount());
//        }
//      }

            for (int i = 0; i < rssFeed2.getItemCount(); i++) {
                Log.e("gStar", "onCreateView: " + rssFeed2.getItem(i).getGroupLastChatAmount());
            }
            rssFeed3.addAll(0, rssFeed);
            rssFeed3.addAll(1, rssFeed2);

            return rssFeed3;

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
                    payLogItem1.setfMobile(jsonObject.getString("f"));
                    payLogItem1.settMobile(jsonObject.getString("t"));
                    payLogItem1.setAmount(jsonObject.getInt("a"));
                    payLogItem1.setDate(jsonObject.getString("d"));
                    payLogItem1.setPaideBool(jsonObject.getBoolean("o"));
                    payLogItem1.setStatus(jsonObject.getBoolean("s"));
                    payLogItem1.setComment(jsonObject.getString("c"));
                    Log.e("++++++++++++", "payLogWithAnother: " + jsonObject.getInt("id"));
                    Log.e("++++++++++++", "payLogWithAnother: " + i);
                    payLogFeed.getHash().put(jsonObject.getInt("id"), i);
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

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("anotherMobile", phone);
            jsonObject.put("amount", Integer.parseInt(Amount.replace(",", "")));
            jsonObject.put("comment", cm);
//            String request = "{\n" +
//                    "\"anotherMobile\" : \"" + phone + "\",\n" +
//                    "\"amount\" : " + Amount + ",\n" +
//                    "\"comment\" : \"" + cm + "\"\n" +
//                    "}";

            Log.e("RR999999999", "activateSong: " + jsonObject.toString());
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

            Log.e("R@@@@@@", sb.toString());


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
            jsonObject.put("amount", Integer.parseInt(Amount.replace(",", "")));
            jsonObject.put("comment", cm);
            jsonObject.put("groupId", groupId);
//            String request = "{\n" +
//                    "\"anotherMobile\" : \"" + phone + "\",\n" +
//                    "\"amount\" : " + Amount + ",\n" +
//                    "\"comment\" : \"" + cm + "\"\n" +
//                    "}";

            Log.e("RR999999999", "activateSong: " + jsonObject.toString());
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

            Log.e("R@@@@@@", sb.toString());


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
            Log.e("1111111", "Send push RegID: " + url);
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

            Log.e("123456789", "activateSong: " + jsonObject.toString());
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("44444444444", "doInBackground: " + resCode);
            if (resCode == 400) {
                Log.e("55555555", "doInBackground: " + resCode);
                return null;
            }

            Log.e("@@@@@@222222", "sfgsdfg");
            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

            Log.e("@@@@@@333", "sfgsdfg");
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

    public String changeGroupImage(File uploadFile1, int id) throws IOException {

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


        URL url = new URL(mainUrl + "api/Group/" + id + "/SetPhotoBase64");
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
            Log.e("1111111", "doInBackground: Get " + url);
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

    public void postLocation(double lat, double lng, double acc) {
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
            jsonObject.put("acc", acc);

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

    public RSSFeed group(String title, JSONArray jsonMemberPhone) {
/*ddd*/
        boolean state = false;
        RSSFeed rssFeed = null;
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

            rssFeed = new RSSFeed();
            Log.e("@@@@@@2222", sb.toString());
            JSONObject object = new JSONObject(sb.toString());
            RSSItem rssItem = new RSSItem();
            rssItem.setGroupId(object.getInt("id"));
            rssItem.setGroupTitle(object.getString("title"));
            rssItem.setGroupPhoto(object.getString("photo"));
            if (!object.isNull("members")) {
                JSONArray jsonArray = object.getJSONArray("members");
                ArrayList<RSSItem> rssItems = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    RSSItem item = new RSSItem();
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    item.setGroupMemberTitle(object1.getString("photo"));
                    item.setGroupMemberPhoto(object1.getString("photo"));
                    item.setGroupMemberPhone(object1.getString("mobile"));
                    item.setGroupMemberId(object1.getString("id"));
                    if (!object1.isNull("lastchat")) {
                        JSONObject groupsMembersLastChat = object1.getJSONObject("lastchat");
                        Log.e("Test55555", "checkContactListWithGroup: " + groupsMembersLastChat.toString());
                        item.setGroupMemberLastChatId(groupsMembersLastChat.getInt("id"));
                        item.setGroupMemberLastChatFrom(groupsMembersLastChat.getString("f"));
                        item.setGroupMemberLastChatTo(groupsMembersLastChat.getString("t"));
                        item.setGroupMemberLastChatAmount(groupsMembersLastChat.getInt("a"));
                        item.setGroupMemberLastChatDate(groupsMembersLastChat.getString("d"));
                        item.setGroupMemberLastChatOrderPay(groupsMembersLastChat.getBoolean("o"));
                        item.setGroupMemberLastChatStatus(groupsMembersLastChat.getBoolean("s"));
                        item.setGroupMemberLastChatFromGroup(groupsMembersLastChat.getBoolean("g"));
                        item.setGroupMemberLastChatComment(groupsMembersLastChat.getString("c"));
                    }
                    rssItems.add(item);
                }
                rssItem.setGroupMembers(rssItems);
            }
            if (object.isNull("lastChats")) {
                JSONObject groupsLastChat = object.getJSONObject("lastchat");
                rssItem.setGroupLastChatId(groupsLastChat.getInt("id"));
                rssItem.setGroupLastChatFrom(groupsLastChat.getString("f"));
                rssItem.setGroupLastChatTo(groupsLastChat.getString("t"));
                rssItem.setGroupLastChatAmount(groupsLastChat.getInt("a"));
                rssItem.setGroupLastChatDate(groupsLastChat.getString("d"));
                rssItem.setGroupLastChatOrderPay(groupsLastChat.getBoolean("o"));
                rssItem.setGroupLastChatStatus(groupsLastChat.getBoolean("s"));
                rssItem.setGroupLastChatFromGroup(groupsLastChat.getBoolean("g"));
                rssItem.setGroupLastChatComment(groupsLastChat.getString("c"));
            }

            rssFeed.addItem(rssItem);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rssFeed;
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

    public PayLogItem payToAnotherWithTF(String anotherMobile, String paymentDetails, String Amount, String cm, int groupId) {

        try {

            URL url = new URL(mainUrl + "api/Payment/PayToAnotherWithTF");
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
            jsonObject.put("anotherMobile", anotherMobile);
            jsonObject.put("paymentDetails", paymentDetails);
            jsonObject.put("amount", Amount);
            jsonObject.put("comment", cm);
            jsonObject.put("groupId", groupId);
//            String request = "{\n" +
//                    "\"anotherMobile\" : \"" + phone + "\",\n" +
//                    "\"amount\" : " + Amount + ",\n" +
//                    "\"comment\" : \"" + cm + "\"\n" +
//                    "}";

            Log.e("RR999999999", "activateSong: " + jsonObject.toString());
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

            Log.e("R@@@@@@", sb.toString());


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
            Log.e("1111111", "doInBackground: " + url);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(false);
            httpConn.setDoInput(true);
            httpConn.setAllowUserInteraction(false);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
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

            Log.e("R@@@@@@", sb.toString());

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
            jsonObject.put("groupId", Integer.parseInt(id));

            Log.e("123456789", "activateSong: " + jsonObject.toString());
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            int resCode = httpConn.getResponseCode();
            Log.e("44444444444", "doInBackground: " + resCode);
            if (resCode == 400) {
                Log.e("55555555", "doInBackground: " + resCode);
                return null;
            }

            Log.e("@@@@@@222222", "sfgsdfg");
            InputStream in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();

            Log.e("@@@@@@333", "sfgsdfg");
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
}
