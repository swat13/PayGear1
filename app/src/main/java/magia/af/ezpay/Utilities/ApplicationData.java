package magia.af.ezpay.Utilities;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.GroupItem;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.MembersItem;
import magia.af.ezpay.Parser.PayLogFeed;

/**
 * Created by pc on 12/18/2016.
 */

public class ApplicationData {
  private static boolean done;
  private static ChatListFeed chatListFeed;
  private static ChatListFeed outNetworkContact;
  private static PayLogFeed payLogFeed;
  private static String nameOfUser;

  public static String getNameOfUser() {
    return nameOfUser;
  }

  public static void setNameOfUser(String nameOfUser) {
    ApplicationData.nameOfUser = nameOfUser;
  }

  public static ChatListFeed getOutNetworkContact() {
    return outNetworkContact;
  }

  public static void setOutNetworkContact(ChatListFeed outNetworkContact) {
    ApplicationData.outNetworkContact = outNetworkContact;
  }

  public static ChatListFeed getChatListFeed() {
    return chatListFeed;
  }

  public static void setChatListFeed(ChatListFeed chatListFeed) {
    ApplicationData.chatListFeed = chatListFeed;
  }

  public static boolean isDone() {
    return done;
  }

  public static void setDone(boolean done) {
    ApplicationData.done = done;
  }

  public static ChatListFeed getContactListWithGroup(String s) {
    JSONArray jsonArray;
    ChatListFeed chatListFeed = null;
    try {
      jsonArray = new JSONArray(s);
      chatListFeed = new ChatListFeed();
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

          if (!contactObject.isNull("lastchat")) {
            JSONObject jsonObject = contactObject.getJSONObject("lastchat");
            rssChatListItem.setComment(jsonObject.getString("c"));
            rssChatListItem.setLastChatAmount(jsonObject.getInt("a"));
            rssChatListItem.setLastChatFrom(jsonObject.getString("f"));
            rssChatListItem.setLastChatTo(jsonObject.getString("t"));
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
                JSONObject jsonObjectf = lastChatGroupObject.getJSONObject("f");
                JSONObject jsonObjectt = lastChatGroupObject.getJSONObject("t");
                groupItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
                groupItem.setGroupLastChatFrom(jsonObjectf.getString("mobile"));
                groupItem.setGroupLastChatTo(jsonObjectt.getString("mobile"));
                groupItem.setGroupLastChatId(lastChatGroupObject.getInt("id"));
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
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return chatListFeed;
  }

  public static ChatListItem getAccount(String s) {

    ChatListItem chatListItem = null;
    try {
      JSONObject jsonObject = new JSONObject(s);
      chatListItem = new ChatListItem();
      chatListItem.setContactImg(jsonObject.getString("photo"));
      chatListItem.setTelNo(jsonObject.getString("mobile"));
      chatListItem.setUserId(jsonObject.getString("id"));
      chatListItem.setContactName(jsonObject.getString("title"));
      chatListItem.setCredit(jsonObject.getInt("credit"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return chatListItem;
  }

  public static ChatListFeed checkContactListWithGroup(final Context context) {
    JSONParser parser = JSONParser.connect(Constant.CHECK_CONTACT_LIST_WITH_GROUP);
    parser.setRequestMethod(JSONParser.POST);
    parser.setReadTimeOut(20000);
    parser.setConnectionTimeOut(20000);
    parser.setAuthorization(context.getSharedPreferences("EZpay", 0).getString("token", ""));
    parser.setJson("[]");
    parser.execute(new JSONParser.Execute() {

      @Override
      public void onPreExecute(){

      }

      @Override
      public void onPostExecute(String s) {
        Log.e("STRING S", "onPostExecute: " + s);
        if (s != null) {
          chatListFeed = ApplicationData.getContactListWithGroup(s);
        } else {
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(context, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
            }
          }, 3000);
        }
      }
    });

    return chatListFeed;
  }

  public static PayLogFeed getPayLogFeed() {
    return payLogFeed;
  }

  public static void setPayLogFeed(PayLogFeed payLogFeed) {
    ApplicationData.payLogFeed = payLogFeed;
  }

  public static String getCostAccountCredit(int amount , Context context){
    int currentAmount = context.getSharedPreferences("EZpay", 0).getInt("amount", 0);
    int costAmount = currentAmount - amount;
    context.getSharedPreferences("EZpay", 0).edit().putInt("amount",costAmount).apply();
    return String.valueOf(costAmount);
  }

  public static String getAddToAccountCredit(int amount , Context context){
    int currentAmount = context.getSharedPreferences("EZpay", 0).getInt("amount", 0);
    int costAmount = currentAmount + amount;
    context.getSharedPreferences("EZpay", 0).edit().putInt("amount",costAmount).apply();
    return String.valueOf(costAmount);
  }
}
