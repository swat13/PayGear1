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

public class Parser {

    public Parser(String token) {
        this.token = token;
        Log.e("TOKEN", "Parser: " + token);
    }

    public Parser() {
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

    public Item getAccountId(String id) {

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
            Item item = new Item();

            try {
                item.setContactImg(jsonObject.getString("photo"));
                item.setTelNo(jsonObject.getString("mobile"));
                item.setUserId(jsonObject.getString("id"));
                item.setContactName(jsonObject.getString("title"));
                Log.e("Name", item.getContactName());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return item;


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Item getAccount() {

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
            Item item = new Item();

            try {
                item.setContactImg(jsonObject.getString("photo"));
                item.setTelNo(jsonObject.getString("mobile"));
                item.setUserId(jsonObject.getString("id"));
                item.setContactName(jsonObject.getString("title"));
                item.setCredit(jsonObject.getInt("credit"));
                Log.e("Name", item.getContactName());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return item;

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
    public Feed getContact(String json) {

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
            Feed feed = new Feed();


            for (int i = 0; i < jsonArray.length(); i++) {
                Item item = new Item();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                try {
                    item.setContactStatus(true);
                    item.setTelNo(jsonObject.getString("mobile"));
                    item.setContactName(getContactName(jsonObject.getString("mobile"), json));
                    item.setContactImg(jsonObject.getString("photo"));
                    if (!jsonObject.isNull("lastchat")) {
                        JSONObject object = jsonObject.getJSONObject("lastchat");
                        item.setLastChatId(object.getInt("id"));
                        item.setLastChatFrom(object.getString("f"));
                        item.setLastChatTo(object.getString("t"));
                        item.setLastChatAmount(object.getInt("a"));
                        item.setLastChatDate(object.getString("d"));
                        item.setLastChatOrderByFromOrTo(object.getBoolean("o"));
                        item.setComment(object.getString("c"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feed.addItem(item);
            }

            return feed;

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
    public Feed checkContactListWithGroup(String json) {

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

            Feed feed = new Feed();
            Item rssItem;

            Log.e("Test0000", "checkContactListWithGroup: ");
            ArrayList<Item> contactMembers = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                rssItem = new Item();
                JSONObject contactObject = jsonArray.getJSONObject(i);
                if (contactObject.getString("$type").contains("FriendModel")) {
                    rssItem.setContactImg(contactObject.getString("photo"));
                    rssItem.setTelNo(contactObject.getString("mobile"));
                    rssItem.setUserId(contactObject.getString("id"));
                    rssItem.setTitle(contactObject.getString("title"));
                    rssItem.setContactCount(i);
                    contactMembers.add(rssItem);
                } else if (contactObject.getString("$type").contains("GroupModel")) {
                    GroupItem groupItem = new GroupItem();
                    groupItem.setGroupId(contactObject.getInt("id"));
                    groupItem.setGroupPhoto(contactObject.getString("photo"));
                    Log.e("))))))))))))((((((((((", "checkContactListWithGroup: " + groupItem.getGroupPhoto());
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
                                groupItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
                                groupItem.setGroupLastChatId(lastChatGroupObject.getInt("id"));
//                rssItem.setGroupLastChatFrom(lastChatGroupObject.getString("f"));
//                rssItem.setGroupLastChatTo(lastChatGroupObject.getString("t"));
                                groupItem.setGroupLastChatDate(lastChatGroupObject.getString("d"));
                                groupItem.setGroupLastChatOrderPay(lastChatGroupObject.getBoolean("o"));
                                groupItem.setGroupLastChatStatus(lastChatGroupObject.getBoolean("s"));
                                groupItem.setGroupLastChatFromGroup(lastChatGroupObject.getBoolean("g"));
                                groupItem.setGroupLastChatComment(lastChatGroupObject.getString("c"));
                            }

                        }
                        groupItem.setMembersFeed(membersFeed);
                    }
                    rssItem.setGroupItem(groupItem);
                }
                //Come Back Here!!!
                rssItem.setContactMembers(contactMembers);
//                rssItem.setGroupFeed();
                feed.addItem(rssItem);
            }

            return feed;

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

    public LogFeed payLogWithAnother(String phone, String maxDate, String maxpage) {

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

            LogFeed logFeed = new LogFeed();

            for (int i = 0; i < jsonArray.length(); i++) {
                LogItem logItem1 = new LogItem();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //kk
                try {
                    logItem1.setId(jsonObject.getInt("id"));
                    logItem1.setfMobile(jsonObject.getString("f"));
                    logItem1.settMobile(jsonObject.getString("t"));
                    logItem1.setAmount(jsonObject.getInt("a"));
                    logItem1.setDate(jsonObject.getString("d"));
                    logItem1.setPaideBool(jsonObject.getBoolean("o"));
                    logItem1.setStatus(jsonObject.getBoolean("s"));
                    logItem1.setComment(jsonObject.getString("c"));
                    Log.e("++++++++++++", "payLogWithAnother: " + jsonObject.getInt("id"));
                    Log.e("++++++++++++", "payLogWithAnother: " + i);
                    logFeed.getHash().put(jsonObject.getInt("id"), i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                logFeed.addItem(logItem1);
            }

            return logFeed;

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

    public LogItem RequestFromAnother(String phone, String Amount, String cm) {

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


            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setfMobile(jsonObject1.getString("f"));
                logItem.settMobile(jsonObject1.getString("t"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setComment(jsonObject1.getString("c"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LogItem groupRequestFromAnother(String phone, String Amount, String cm, int groupId) {

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


            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                JSONObject object = jsonObject1.getJSONObject("f");
                logItem.setfPhoto("http://new.opaybot.ir" + object.getString("photo"));
                logItem.setfMobile(object.getString("mobile"));
                logItem.setfId(object.getString("id"));
                logItem.setfTitle(object.getString("title"));
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setStatus(jsonObject1.getBoolean("s"));
                logItem.setGroup(jsonObject1.getBoolean("g"));
                logItem.setComment(jsonObject1.getString("c"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LogItem accPaymentRequest(String id, String detail) {

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

            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setfMobile(jsonObject1.getString("f"));
                logItem.settMobile(jsonObject1.getString("t"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setComment(jsonObject1.getString("c"));
                logItem.setStatus(jsonObject1.getBoolean("s"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

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

    public LogItem sendPaymentRequest(String phone, String detail, String comment, String amount) {

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

            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setfMobile(jsonObject1.getString("f"));
                logItem.settMobile(jsonObject1.getString("t"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setStatus(jsonObject1.getBoolean("s"));

                logItem.setComment(jsonObject1.getString("c"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

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


        URL url = new URL(mainUrl + "api/GroupItem/" + id + "/SetPhotoBase64");
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

    public Feed getLocation() {

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

            Feed feed = new Feed();
            Log.e("@@@@@@2222", sb.toString());
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                Item item = new Item();
                try {
                    item.setContactImg(jsonArray.getJSONObject(i).getString("photo"));
                    Log.e("CIMage", item.getContactImg());
                    item.setTelNo(jsonArray.getJSONObject(i).getString("mobile"));
                    item.setUserId(jsonArray.getJSONObject(i).getString("id"));
                    item.setContactName(jsonArray.getJSONObject(i).getString("title"));
                    Log.e("Name", item.getContactName());
                } catch (Exception e) {
                    e.printStackTrace();

                }
                feed.addItem(item);
            }

            return feed;


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

    public GroupItem group(String title, JSONArray jsonMemberPhone) {
/*ddd*/
        boolean state = false;
        GroupItem groupItem = null;
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


            Log.e("@@@@@@2222", sb.toString());
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

    public LogItem groupChat(int id, int pageSize, String maxDate, ArrayList<String> memberPhone) {
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

            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setfMobile(jsonObject1.getString("f"));
                logItem.settMobile(jsonObject1.getString("t"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setStatus(jsonObject1.getBoolean("s"));

                logItem.setComment(jsonObject1.getString("c"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LogItem payToAnotherWithTF(String anotherMobile, String paymentDetails, String Amount, String cm, int groupId) {

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


            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setfMobile(jsonObject1.getString("f"));
                logItem.settMobile(jsonObject1.getString("t"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setComment(jsonObject1.getString("c"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LogFeed getGroupChat(int id, int page, String date) {

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
            LogFeed feed = new LogFeed();

            for (int i = 0; i < jsonArray.length(); i++) {
                LogItem logItem = null;
                try {
                    logItem = new LogItem();
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONObject jsonObjectf = object.getJSONObject("f");
                    JSONObject jsonObjectt = object.getJSONObject("t");
                    logItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
                    logItem.setfMobile(jsonObjectf.getString("mobile"));
                    logItem.setfId(jsonObjectf.getString("id"));
                    logItem.setfTitle(jsonObjectf.getString("title"));
                    logItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
                    logItem.settMobile(jsonObjectt.getString("mobile"));
                    logItem.settId(jsonObjectt.getString("id"));
                    logItem.settTitle(jsonObjectt.getString("title"));
                    logItem.setId(object.getInt("id"));
                    logItem.setAmount(object.getInt("a"));
                    logItem.setDate(object.getString("d"));
                    logItem.setPaideBool(object.getBoolean("o"));
                    logItem.setStatus(object.getBoolean("s"));
                    logItem.setGroup(object.getBoolean("g"));
                    logItem.setComment(object.getString("c"));
                    feed.getHash().put(object.getInt("id"), i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feed.addItem(logItem);
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

    public LogItem sendGroupPaymentRequest(String phone, String detail, String comment, String amount, String id) {

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

            LogItem logItem = new LogItem();
            JSONObject jsonObject1 = new JSONObject(sb.toString());
            try {
                JSONObject jsonObjectf = jsonObject1.getJSONObject("f");
                JSONObject jsonObjectt = jsonObject1.getJSONObject("t");
                logItem.setfPhoto("http://new.opaybot.ir" + jsonObjectf.getString("photo"));
                logItem.setfMobile(jsonObjectf.getString("mobile"));
                logItem.setfId(jsonObjectf.getString("id"));
                logItem.setfTitle(jsonObjectf.getString("title"));
                logItem.settPhoto("http://new.opaybot.ir" + jsonObjectt.getString("photo"));
                logItem.settMobile(jsonObjectt.getString("mobile"));
                logItem.settId(jsonObjectt.getString("id"));
                logItem.settTitle(jsonObjectt.getString("title"));
                logItem.setId(jsonObject1.getInt("id"));
                logItem.setAmount(jsonObject1.getInt("a"));
                logItem.setDate(jsonObject1.getString("d"));
                logItem.setPaideBool(jsonObject1.getBoolean("o"));
                logItem.setStatus(jsonObject1.getBoolean("s"));
                logItem.setGroup(jsonObject1.getBoolean("g"));
                logItem.setComment(jsonObject1.getString("c"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return logItem;

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
