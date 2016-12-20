package magia.af.ezpay.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.ChooseMemberActivity;
import magia.af.ezpay.Firebase.MessagingService;
import magia.af.ezpay.GroupChatPageActivity;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.GroupItem;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.MembersItem;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.MessageHandler;
import magia.af.ezpay.interfaces.OnClickHandler;


public class FriendsList extends Fragment implements OnClickHandler, MessageHandler {

  ChatListFeed _ChatList_feed;
  GroupItem groups;
  MembersFeed membersFeed;
  ArrayList<ChatListItem> contacts;
  RecyclerView recBills;
  FriendsList.ListAdapter adapter;
  public String comment;
  public int amount;
  public int position;
  ContactDatabase database;
  JSONArray jsonArray;
  static Handler handler = new Handler();
  public static MessageHandler mHandler;
  ProgressDialog progressDialog;
  String mobile;
  private String TAG = "TEST";

  public FriendsList getInstance(ChatListFeed chatListFeed) {
    _ChatList_feed = chatListFeed;
    return new FriendsList();
  }

  public void set_ChatList_feed(ChatListFeed _ChatList_feed) {
    this._ChatList_feed = _ChatList_feed;
  }

  public static MessageHandler informNotif() {
    return mHandler;
  }


  @Override
  public void onStart() {
    super.onStart();
    _ChatList_feed = ApplicationData.getChatListFeed();
    adapter.notifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.e("TEST", "onCreateView: ");
    Log.e("TEST", "onCreateView: ");
    mHandler = this;
    View v = inflater.inflate(R.layout.activity_friend_list, container, false);
    Log.e("Phone", "onCreateView: " + getActivity().getSharedPreferences("EZpay", 0).getString("mobile", ""));
    mobile = getActivity().getSharedPreferences("EZpay", 0).getString("mobile", "");
    MessagingService.mode = 3;
    recBills = (RecyclerView) v.findViewById(R.id.contact_recycler);
    try {
      Log.e("Line 87", "onCreateView: " + _ChatList_feed.getItemCount());
      for (int i = 0; i < _ChatList_feed.getItemCount(); i++) {
        if (_ChatList_feed.getItem(i).getGroupItem() != null) {
          groups = _ChatList_feed.getItem(i).getGroupItem();
          Log.e(TAG, "onCreateView: " + groups.getGroupLastChatAmount());
        } else {
          contacts = _ChatList_feed.getItem(i).getContactMembers();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

//    new getAccount().execute();

    getAccount();
    CardView inviteFriends = (CardView) v.findViewById(R.id.invite_friends);
    inviteFriends.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ChooseMemberActivity.class);
        intent.putExtra("contact", contacts);
        intent.putExtra("contact2", _ChatList_feed);
        getActivity().startActivity(intent);
      }
    });
    Bundle bundle = getArguments();
//    if (bundle != null) {
//      comment = getArguments().getString("description");
//      amount = getArguments().getInt("amount");
//      position = getArguments().getInt("pos");
//      if (comment.length() > 0 && amount > 0) {
//        _ChatList_feed.getItem(position).setComment(comment);
//        _ChatList_feed.getItem(position).setLastChatAmount(amount);
//      }
//    }
    if (_ChatList_feed != null && _ChatList_feed.getItemCount() != 0) {
      adapter = new FriendsList.ListAdapter(this);
      recBills.setAdapter(adapter);
      LinearLayoutManager llm = new LinearLayoutManager(getActivity());
      llm.setOrientation(LinearLayoutManager.VERTICAL);
      recBills.setNestedScrollingEnabled(true);
      recBills.setLayoutManager(llm);
      adapter.notifyDataSetChanged();
    }

    return v;
  }

  @Override
  public void onClick(ChatListItem chatListItem) {

    if (chatListItem.getGroupItem() != null) {
      Intent goToChatPageActivity = new Intent(getActivity(), GroupChatPageActivity.class);
      GroupItem groupItem = chatListItem.getGroupItem();
      MembersFeed membersFeed = groupItem.getMembersFeed();
      goToChatPageActivity.putExtra("title", groupItem.getGroupTitle());
      goToChatPageActivity.putExtra("photo", "http://new.opaybot.ir" + groupItem.getGroupPhoto());
      goToChatPageActivity.putExtra("id", groupItem.getGroupId());
      goToChatPageActivity.putExtra("members", membersFeed);
      goToChatPageActivity.putExtra("contact", _ChatList_feed);
      startActivity(goToChatPageActivity);
//      getActivity().finish();
    } else {
      Intent goToChatPageActivity = new Intent(getActivity(), ChatPageActivity.class);
      goToChatPageActivity.putExtra("phone", chatListItem.getTelNo());
      goToChatPageActivity.putExtra("contactName", chatListItem.getTitle());
      goToChatPageActivity.putExtra("image", chatListItem.getContactImg());
      goToChatPageActivity.putExtra("pos", chatListItem.getPosition());
      goToChatPageActivity.putExtra("date", chatListItem.getLastChatDate());
      goToChatPageActivity.putExtra("contact", _ChatList_feed);
      startActivity(goToChatPageActivity);
//      getActivity().finish();
    }
  }

  @Override
  public void handleMessage(final PayLogItem payLogItem, final boolean deleteState, final String chatMemberMobile) {

    Log.e("handleMessage", "In Pv");
//    new fillContact().execute("[]");
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        JSONParser parser = JSONParser.connect(Constant.CHECK_CONTACT_LIST_WITH_GROUP);
        parser.setRequestMethod(JSONParser.POST);
        parser.setReadTimeOut(20000);
        parser.setConnectionTimeOut(20000);
        parser.setAuthorization(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
        parser.setJson("[]");
        parser.execute(new JSONParser.Execute() {
          @Override
          public void onPreExecute() {

          }

          @Override
          public void onPostExecute(String s) {
            Log.e("STRING S", "onPostExecute: " + s);
            if (s != null) {
              JSONArray jsonArray;
              try {
                jsonArray = new JSONArray(s);
                _ChatList_feed = new ChatListFeed();
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
                          groupItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
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
                  _ChatList_feed.addItem(rssChatListItem);
                  adapter.notifyDataSetChanged();
                }
                ApplicationData.setChatListFeed(_ChatList_feed);
              } catch (JSONException e) {
                e.printStackTrace();
              }
            } else {
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(getActivity(), "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                }
              }, 3000);
            }
          }
        });
      }
    });

  }

  @Override
  public void handleMessageGp(final String body, final String chatMemberMobile) {


    Log.e("handleMessage", "In Gp");

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            JSONParser parser = JSONParser.connect(Constant.CHECK_CONTACT_LIST_WITH_GROUP);
            parser.setRequestMethod(JSONParser.POST);
            parser.setReadTimeOut(20000);
            parser.setConnectionTimeOut(20000);
            parser.setAuthorization(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            parser.setJson("[]");
            parser.execute(new JSONParser.Execute() {
              @Override
              public void onPreExecute() {

              }

              @Override
              public void onPostExecute(String s) {
                Log.e("STRING S", "onPostExecute: " + s);
                if (s != null) {
                  JSONArray jsonArray;
                  try {
                    jsonArray = new JSONArray(s);
                    _ChatList_feed = new ChatListFeed();
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
                              groupItem.setGroupLastChatAmount(lastChatGroupObject.getInt("a"));
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
                      _ChatList_feed.addItem(rssChatListItem);
                      adapter.notifyDataSetChanged();
                    }
                    ApplicationData.setChatListFeed(_ChatList_feed);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                } else {
                  Handler handler = new Handler();
                  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      Toast.makeText(getActivity(), "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                    }
                  }, 3000);
                }
              }
            });
          }
        });
      }
    });


  }

  public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView contactName;
    TextView description;
    TextView pay;
    ImageView contactImage;
    ImageView contactStat;
    OnClickHandler onClickHandler;

    public FeedViewHolder(View v, OnClickHandler onClickHandler) {
      super(v);
      contactName = (TextView) v.findViewById(R.id.txt_contact_item_name);
      description = (TextView) v.findViewById(R.id.txt_contact_item_description);
      pay = (TextView) v.findViewById(R.id.txt_contact_item_pay);
      contactImage = (ImageView) v.findViewById(R.id.contact_img);
      contactStat = (ImageView) v.findViewById(R.id.status_circle);
      v.setOnClickListener(this);
      this.onClickHandler = onClickHandler;
    }

    @Override
    public void onClick(View v) {
      ChatListItem chatListItem = _ChatList_feed.getItem(getAdapterPosition());
      onClickHandler.onClick(chatListItem);
    }
  }

  public class ListAdapter extends RecyclerView.Adapter<FriendsList.FeedViewHolder> {
    OnClickHandler onClickHandler;

    public ListAdapter(OnClickHandler onClickHandler) {
      this.onClickHandler = onClickHandler;
    }

    @Override
    public int getItemCount() {
      return _ChatList_feed.getItemCount();
    }

    @Override
    public void onBindViewHolder(final FriendsList.FeedViewHolder FeedViewHolder, final int position) {
      String contactName = "";
      ChatListItem fe = _ChatList_feed.getItem(position);
      if (fe.getGroupItem() != null) {
        Log.e(TAG, "onBindViewHolder: " + fe.getGroupItem().getGroupLastChatAmount());
      }
      if (fe.getGroupItem() != null) {
        if (fe.getGroupItem().getGroupLastChatFrom() != null) {
          if (fe.getGroupItem().isGroupLastChatOrderPay() && fe.getGroupItem().getGroupLastChatFrom().equals(mobile)) {
            FeedViewHolder.pay.setTextColor(Color.RED);
            FeedViewHolder.pay.setText("-" + getDividedToman((long) fe.getGroupItem().getGroupLastChatAmount()) + "");
          } else if (!fe.getGroupItem().isGroupLastChatOrderPay()) {
            FeedViewHolder.pay.setTextColor(Color.GRAY);
            FeedViewHolder.pay.setText(getDividedToman((long) fe.getGroupItem().getGroupLastChatAmount()) + "");
          } else if (fe.getGroupItem().isGroupLastChatOrderPay() && fe.getGroupItem().getGroupLastChatTo().equals(mobile)) {
            FeedViewHolder.pay.setTextColor(Color.parseColor("#6db314"));
            FeedViewHolder.pay.setText("+" + getDividedToman((long) fe.getGroupItem().getGroupLastChatAmount()) + "");
          } else {
            FeedViewHolder.pay.setTextColor(Color.GRAY);
            FeedViewHolder.pay.setText(getDividedToman((long) fe.getGroupItem().getGroupLastChatAmount()) + "");
          }
        }
        FeedViewHolder.description.setText(fe.getGroupItem().getGroupLastChatComment());
        if (fe.getGroupItem().getGroupTitle().length() > 15) {
          contactName = fe.getGroupItem().getGroupTitle();
          FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
        } else {
          FeedViewHolder.contactName.setText(fe.getGroupItem().getGroupTitle());
        }
//          FeedViewHolder.description.setText(fe.getGroupLastChatComment());
        Glide.with(getActivity())
          .load("http://new.opaybot.ir" + fe.getGroupItem().getGroupPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(FeedViewHolder.contactImage) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              FeedViewHolder.contactImage.setImageDrawable(circularBitmapDrawable);
            }
          });

        fe.setPosition(position);


      } else {
        if (fe.isLastChatOrderByFromOrTo() && fe.getLastChatFrom().equals(mobile)) {
          FeedViewHolder.pay.setTextColor(Color.RED);
          FeedViewHolder.pay.setText("-" + getDividedToman((long) fe.getLastChatAmount()) + "");
        } else if (!fe.isLastChatOrderByFromOrTo()) {
          FeedViewHolder.pay.setTextColor(Color.GRAY);
          FeedViewHolder.pay.setText(getDividedToman((long) fe.getLastChatAmount()) + "");
        } else {
          FeedViewHolder.pay.setTextColor(Color.parseColor("#6db314"));
          FeedViewHolder.pay.setText("+" + getDividedToman((long) fe.getLastChatAmount()) + "");
        }
        if (fe.isContactStatus()) {
          FeedViewHolder.pay.setTextColor(Color.GRAY);
          FeedViewHolder.pay.setText(getDividedToman((long) fe.getLastChatAmount()) + "");
        }
        FeedViewHolder.description.setText(fe.getComment());
        if (fe.getTitle().length() > 15) {
          contactName = fe.getTitle();
          FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
        } else {
          FeedViewHolder.contactName.setText(fe.getTitle());
        }

        Glide.with(getActivity())
          .load("http://new.opaybot.ir" + fe.getContactImg())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(FeedViewHolder.contactImage) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              FeedViewHolder.contactImage.setImageDrawable(circularBitmapDrawable);
            }
          });

      }
    }

    @Override
    public FriendsList.FeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
      View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
      return new FeedViewHolder(itemView, onClickHandler);
    }

  }

  private String getDividedToman(Long price) {
    if (price == 0) {
      return price + "";
    } else if (price < 1000) {
      return price + "";
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < price.toString().length(); i += 3) {
        try {
          if (i == 0)
            stringBuilder.insert(0, price.toString().substring(price.toString().length() - 3 - i, price.toString().length() - i));
          else
            stringBuilder.insert(0, price.toString().substring(price.toString().length() - 3 - i, price.toString().length() - i) + ",");
        } catch (Exception e) {
          try {
            stringBuilder.insert(0, price.toString().substring(price.toString().length() - 2 - i, price.toString().length() - i) + ",");
          } catch (Exception e1) {
            stringBuilder.insert(0, price.toString().substring(price.toString().length() - 1 - i, price.toString().length() - i) + ",");
          }
        }

      }
      return stringBuilder.toString();
    }
  }

  public void getAccount() {
    JSONParser parser = JSONParser.connect(Constant.GET_ACCOUNT);
    parser.setRequestMethod(JSONParser.GET);
    parser.setReadTimeOut(20000);
    parser.setConnectionTimeOut(20000);
    parser.setAuthorization(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
    parser.execute(new JSONParser.Execute() {
      @Override
      public void onPreExecute() {

      }

      @Override
      public void onPostExecute(String s) {
        if (s != null) {
          ChatListItem chatListItem = ApplicationData.getAccount(s);
          getActivity().getSharedPreferences("EZpay", 0).edit().putString("phoneNumber", chatListItem.getTelNo()).apply();
        } else {
          Toast.makeText(getActivity(), "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  public class fillContact extends AsyncTask<String, Void, ChatListFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected ChatListFeed doInBackground(String... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.checkContactListWithGroup(params[0]);
    }

    @Override
    protected void onPostExecute(ChatListFeed result) {
      if (result != null) {
        _ChatList_feed = result;
        ApplicationData.setChatListFeed(result);
        adapter.notifyDataSetChanged();
      } else {
        Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }

}

