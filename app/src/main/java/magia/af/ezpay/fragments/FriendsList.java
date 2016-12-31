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

import java.util.ArrayList;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.ChooseMemberActivity;
import magia.af.ezpay.Firebase.MessagingService;
import magia.af.ezpay.GroupChatPageActivity;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.GroupItem;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.DialogMaker;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.MessageHandler;
import magia.af.ezpay.interfaces.OnClickHandler;


public class FriendsList extends Fragment implements OnClickHandler, MessageHandler {

  ChatListFeed _ChatList_feed;
  ChatListFeed outNetworkContact;
  ChatListFeed chatListFeed;
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
  String userID;

  public void set_ChatList_feed(ChatListFeed _ChatList_feed) {
    this._ChatList_feed = _ChatList_feed;
  }

  public void setOutNetworkContact(ChatListFeed outNetworkContact) {
    this.outNetworkContact = outNetworkContact;
  }

  public static MessageHandler informNotif() {
    return mHandler;
  }


  @Override
  public void onStart() {
    _ChatList_feed = ApplicationData.getChatListFeed();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
    super.onStart();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mHandler = this;
    View v = inflater.inflate(R.layout.activity_friend_list, container, false);
    Bundle bundle = getArguments();
    if (bundle != null) {
      userID = bundle.getString("id");
    }
    mobile = getActivity().getSharedPreferences("EZpay", 0).getString("mobile", "");
    MessagingService.mode = 3;
    recBills = (RecyclerView) v.findViewById(R.id.contact_recycler);
//    _ChatList_feed.addAll(_ChatList_feed.getItemCount(), outNetworkContact);
    if (_ChatList_feed != null) {
      Log.e(TAG, "onCreateView: " + "True");
    }
    try {
      for (int i = 0; i < _ChatList_feed.getItemCount(); i++) {
        if (_ChatList_feed.getItem(i).getGroupItem() != null) {
          groups = _ChatList_feed.getItem(i).getGroupItem();
        } else {
          Log.e(TAG, "onCreateView: " + _ChatList_feed.getItem(i).getContactMembers().size());
          contacts = _ChatList_feed.getItem(i).getContactMembers();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    if (_ChatList_feed != null && _ChatList_feed.getItemCount() != 0) {
//      final DividerDecoration divider = new DividerDecoration.Builder(this.getActivity())
//        .setHeight(R.dimen.default_divider_height)
//        .setPadding(R.dimen.default_divider_padding)
//        .setColorResource(R.color.default_header_color)
//        .build();
//
//      recBills.setHasFixedSize(true);
//      recBills.addItemDecoration(divider);
      adapter = new FriendsList.ListAdapter(this);
      StickyHeaderDecoration decoration = new StickyHeaderDecoration(adapter);
      LinearLayoutManager llm = new LinearLayoutManager(getActivity());
      llm.setOrientation(LinearLayoutManager.VERTICAL);
      recBills.setLayoutManager(llm);
      recBills.setNestedScrollingEnabled(true);
      recBills.setAdapter(adapter);
      recBills.addItemDecoration(decoration, 0);
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
      goToChatPageActivity.putExtra("contactName", chatListItem.getContactName());
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
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        new fillContact().execute("[]");
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
            new fillContact().execute("[]");
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
    ImageView groupSign;
    OnClickHandler onClickHandler;

    public FeedViewHolder(View v, OnClickHandler onClickHandler) {
      super(v);
      contactName = (TextView) v.findViewById(R.id.txt_contact_item_name);
      description = (TextView) v.findViewById(R.id.txt_contact_item_description);
      pay = (TextView) v.findViewById(R.id.txt_contact_item_pay);
      contactImage = (ImageView) v.findViewById(R.id.contact_img);
      contactStat = (ImageView) v.findViewById(R.id.status_circle);
      groupSign = (ImageView) v.findViewById(R.id.groupSign);
      v.setOnClickListener(this);
      this.onClickHandler = onClickHandler;
    }

    @Override
    public void onClick(View v) {
      ChatListItem chatListItem = _ChatList_feed.getItem(getAdapterPosition());
      onClickHandler.onClick(chatListItem);
    }
  }

  public class ListAdapter extends RecyclerView.Adapter<FriendsList.FeedViewHolder> implements StickyHeaderAdapter<ListAdapter.HeaderHolder> {
    OnClickHandler onClickHandler;

    public ListAdapter(OnClickHandler onClickHandler) {
      this.onClickHandler = onClickHandler;
    }

    @Override
    public int getItemCount() {
      return _ChatList_feed.getItemCount();
    }

    @Override
    public void onBindViewHolder(final FriendsList.FeedViewHolder FeedViewHolder, int position) {
      String contactName = "";
      ChatListItem fe = _ChatList_feed.getItem(position);
      ContactDatabase database = new ContactDatabase(getActivity());
//      if (fe.getGroupItem() != null) {
//        Log.e(TAG, "onBindViewHolder: " + fe.getGroupItem().getGroupLastChatAmount() + "     ===>" + position);
//      }
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
        FeedViewHolder.groupSign.setImageResource(R.drawable.ic_group_black);
      } else {
        FeedViewHolder.groupSign.setImageResource(0);
        if (fe.isLastChatOrderByFromOrTo() && fe.getLastChatTo().equals(mobile)) {
          FeedViewHolder.pay.setTextColor(Color.RED);
          FeedViewHolder.pay.setText("-" + getDividedToman((long) fe.getLastChatAmount()) + "");
        } else if (!fe.isLastChatOrderByFromOrTo()) {
          FeedViewHolder.pay.setTextColor(Color.GRAY);
          FeedViewHolder.pay.setText(getDividedToman((long) fe.getLastChatAmount()) + "");
        } else if (fe.isContactStatus()) {
          FeedViewHolder.pay.setTextColor(Color.GRAY);
          FeedViewHolder.pay.setText(getDividedToman((long) fe.getLastChatAmount()) + "");
        } else {
          FeedViewHolder.pay.setTextColor(Color.parseColor("#6db314"));
          FeedViewHolder.pay.setText("+" + getDividedToman((long) fe.getLastChatAmount()) + "");
        }
        FeedViewHolder.description.setText(fe.getComment());
        contactName = database.getNameFromNumber(fe.getTelNo());
        if (contactName.length() > 15) {
          FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
        } else {
          FeedViewHolder.contactName.setText(database.getNameFromNumber(fe.getTelNo()));
        }
        if (FeedViewHolder.contactName.getText().toString().length() == 0 || FeedViewHolder.contactName.getText().toString().isEmpty()) {
          FeedViewHolder.contactName.setText(fe.getTitle());
        }

        if (position > (_ChatList_feed.getItemCount() - outNetworkContact.getItemCount() - 1)) {
          FeedViewHolder.pay.setText("");
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

    @Override
    public long getHeaderId(int position) {
      if (position <= _ChatList_feed.getItemCount() - outNetworkContact.getItemCount() - 1) {
        return 0;
      } else if (position >= _ChatList_feed.getItemCount() - outNetworkContact.getItemCount()) {
        return 1;
      } else return -1;

    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticky_header, parent, false);
      return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
      if (position == 0) {
        viewholder.headerText.setText("دوستان داخل شبکه");
      } else {
        viewholder.headerText.setText("دوستان خارج شبکه");
      }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
      public TextView headerText;

      public HeaderHolder(View itemView) {
        super(itemView);
        headerText = (TextView) itemView.findViewById(R.id.txtHeader);
      }
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

  @Override
  public void onResume() {
    _ChatList_feed = ApplicationData.getChatListFeed();
    if (adapter != null) {
      adapter.notifyDataSetChanged();
    }
    super.onResume();
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
        ChatListFeed feed = ApplicationData.getOutNetworkContact();
        for (int i = 0; i < feed.getItemCount(); i++) {
          for (int j = 0; j < result.getItemCount(); j++) {
            if (feed.getItem(i).getTelNo().equals(result.getItem(j).getTelNo())){
              feed.removeItem(i);
              i--;
              break;
            }
          }
        }
        result.addAll(result.getItemCount(),feed);
        ApplicationData.setChatListFeed(result);
        adapter.notifyDataSetChanged();
        onResume();
      } else {
        Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }

}

