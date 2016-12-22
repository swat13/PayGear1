package magia.af.ezpay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;

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
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.MessageHandler;
import magia.af.ezpay.interfaces.OnClickHandler;


public class FriendsList extends Fragment implements OnClickHandler, MessageHandler {

    ChatListFeed _ChatList_feed;
    GroupItem groups;
    MembersFeed membersFeed;
    ArrayList<ChatListItem> contacts;
    RecyclerView recContacts, recUnCoctacts;
    FriendsList.ListAdapter adapter;
    FriendsList.UnListAdapter adapterUnContacts;
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
    public EditText srcTitle;
    ImageButton srcBtn;
    public ChatListFeed filteredChatList;
    public ChatListFeed filteredUnContacts;
    String titleTxt;
    ChatListFeed notContact;

    @Override
    public void onDestroyView() {

        titleTxt = srcTitle.getText().toString();
        getActivity().getSharedPreferences("EZpay", 0).edit().putString("srcTitle", titleTxt).apply();
        super.onDestroyView();

    }

    public void set_ChatList_feed(ChatListFeed _ChatList_feed) {
        this._ChatList_feed = _ChatList_feed;
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
        mobile = getActivity().getSharedPreferences("EZpay", 0).getString("mobile", "");
        MessagingService.mode = 3;
        recContacts = (RecyclerView) v.findViewById(R.id.contact_recycler);
        recUnCoctacts = (RecyclerView) v.findViewById(R.id.not_contacts);

        srcTitle = (EditText) v.findViewById(R.id.src_title);
        srcBtn = (ImageButton) v.findViewById(R.id.btn_search);

        titleTxt = getActivity().getSharedPreferences("EZpay", 0).getString("srcTitle", "");

        srcTitle.setText(titleTxt);
        if (titleTxt.length() > 0) {
            srcTitle.setVisibility(View.VISIBLE);
        }


//        filteredChatList = new ChatListFeed();
        filteredChatList = _ChatList_feed;

        try {
            for (int i = 0; i < _ChatList_feed.getItemCount(); i++) {
                if (_ChatList_feed.getItem(i).getGroupItem() != null) {
                    groups = _ChatList_feed.getItem(i).getGroupItem();
                } else {
                    contacts = _ChatList_feed.getItem(i).getContactMembers();
                    new LocalPersistence().writeObjectToFile(getActivity(), contacts, "AllContacts");
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
                intent.putExtra("chatListFeed", _ChatList_feed);
                getActivity().startActivity(intent);
            }
        });
        Bundle bundle = getArguments();
        if (bundle.getSerializable("notContacts") != null) {
            notContact = (ChatListFeed) bundle.getSerializable("notContacts");
            filteredUnContacts = notContact;
        }

//    if (bundle != null) {
//      comment = getArguments().getString("description");
//      amount = getArguments().getInt("amount");
//      position = getArguments().getInt("pos");
//      if (comment.length() > 0 && amount > 0) {
//        _ChatList_feed.getItem(position).setComment(comment);
//        _ChatList_feed.getItem(position).setLastChatAmount(amount);
//      }
//    }
        assert recContacts != null;
        adapter = new FriendsList.ListAdapter(this, filteredChatList);
        recContacts.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recContacts.setNestedScrollingEnabled(true);
        recContacts.setLayoutManager(llm);
        adapter.notifyDataSetChanged();

        //Adapter For Non Contacts
        assert recUnCoctacts != null;
        adapterUnContacts = new FriendsList.UnListAdapter(this, filteredUnContacts);
        recUnCoctacts.setAdapter(adapterUnContacts);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recUnCoctacts.setNestedScrollingEnabled(true);
        recUnCoctacts.setLayoutManager(llm2);
        adapterUnContacts.notifyDataSetChanged();

        srcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srcTitle.setVisibility(View.VISIBLE);
                srcTitle.requestFocus();

            }
        });

        srcTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(srcTitle, InputMethodManager.SHOW_IMPLICIT);

                } else {
                    srcTitle.setText("");
                    srcTitle.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(srcTitle.getWindowToken(), 0);
                }
            }
        });

        srcTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        v.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srcTitle.clearFocus();

            }
        });


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

        TextView contactName, description, pay;
        ImageView contactImage, contactStat;
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
            ChatListItem chatListItem = filteredChatList.getItem(getAdapterPosition());
            onClickHandler.onClick(chatListItem);

        }

    }


    public class UnContactFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView contactName, description;
        OnClickHandler onClickHandler;


        public UnContactFeedViewHolder(View v, OnClickHandler onClickHandler) {
            super(v);
            contactName = (TextView) v.findViewById(R.id.txt_contact_item_name);
            description = (TextView) v.findViewById(R.id.txt_contact_item_description);
            v.setOnClickListener(this);
            this.onClickHandler = onClickHandler;

        }


        @Override
        public void onClick(View v) {

        }
    }

    public class ListAdapter extends RecyclerView.Adapter<FriendsList.FeedViewHolder> implements Filterable {
        OnClickHandler onClickHandler;

        private doFilter mFilter;

        private ChatListFeed chatItems;

        public ListAdapter(OnClickHandler onClickHandler, ChatListFeed chatItems) {
            this.onClickHandler = onClickHandler;
            mFilter = new doFilter(ListAdapter.this);
            this.chatItems = chatItems;
        }

        @Override
        public int getItemCount() {
            return chatItems.getItemCount();
        }

        @Override
        public void onBindViewHolder(final FriendsList.FeedViewHolder FeedViewHolder, int position) {
            String contactName = "";
            ChatListItem fe = chatItems.getItem(position);
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


            } else {
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
                if (fe.getTitle().length() > 15) {
                    contactName = database.getNameFromNumber(fe.getTelNo());
                    FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
                } else {
                    FeedViewHolder.contactName.setText(database.getNameFromNumber(fe.getTelNo()));
                }
                if (FeedViewHolder.contactName.getText().toString().length() == 0 || FeedViewHolder.contactName.getText().toString().isEmpty()) {
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


        @Override
        public android.widget.Filter getFilter() {
            return mFilter;
        }

        public class doFilter extends android.widget.Filter {

            private ListAdapter lAdapter;

            private doFilter(ListAdapter listAdapter) {
                this.lAdapter = listAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                filteredChatList = new ChatListFeed();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    filteredChatList = _ChatList_feed;
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (int i = 0; i < _ChatList_feed.getItemCount(); i++) {
                        if (_ChatList_feed.getItem(i).getGroupItem() == null) {
                            if (_ChatList_feed.getItem(i).getTitle().toLowerCase().startsWith(filterPattern)) {
                                filteredChatList.addItem(_ChatList_feed.getItem(i));
                            }
                        } else {
                            if (_ChatList_feed.getItem(i).getGroupItem().getGroupTitle().toLowerCase().startsWith(filterPattern)) {
                                filteredChatList.addItem(_ChatList_feed.getItem(i));
                            }
                        }
                    }
                }

                results.values = filteredChatList;
                results.count = filteredChatList.getItemCount();
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                chatItems = (ChatListFeed) results.values;
                this.lAdapter.notifyDataSetChanged();

            }

        }


    }

    public class UnListAdapter extends RecyclerView.Adapter<FriendsList.UnContactFeedViewHolder> implements Filterable {
        OnClickHandler onClickHandler;
        private doFilter mFilter;
        private ChatListFeed unChatItems;

        public UnListAdapter(OnClickHandler onClickHandler, ChatListFeed chatItems) {
            this.onClickHandler = onClickHandler;
            mFilter = new doFilter(UnListAdapter.this);
            this.unChatItems = chatItems;
        }

        @Override
        public int getItemCount() {
            return unChatItems.getItemCount();
        }

        @Override
        public void onBindViewHolder(final FriendsList.UnContactFeedViewHolder FeedViewHolder, int position) {
            String contactName = "";
            ChatListItem fe = unChatItems.getItem(position);
            ContactDatabase database = new ContactDatabase(getActivity());

        }

        @Override
        public FriendsList.UnContactFeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.un_contact_item, viewGroup, false);
            return new UnContactFeedViewHolder(itemView, onClickHandler);
        }


        @Override
        public android.widget.Filter getFilter() {
            return mFilter;
        }

        public class doFilter extends android.widget.Filter {

            private UnListAdapter lAdapter;

            private doFilter(UnListAdapter listAdapter) {
                this.lAdapter = listAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                filteredUnContacts = new ChatListFeed();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    filteredUnContacts = unChatItems;
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (int i = 0; i < notContact.getItemCount(); i++) {
                        if (notContact.getItem(i).getGroupItem() == null) {
                            if (notContact.getItem(i).getTitle().toLowerCase().startsWith(filterPattern)) {
                                filteredUnContacts.addItem(notContact.getItem(i));
                            }
                        } else {
                            if (notContact.getItem(i).getGroupItem().getGroupTitle().toLowerCase().startsWith(filterPattern)) {
                                filteredUnContacts.addItem(notContact.getItem(i));
                            }
                        }
                    }

                }

                results.values = filteredUnContacts;
                results.count = filteredUnContacts.getItemCount();
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                unChatItems = (ChatListFeed) results.values;
                this.lAdapter.notifyDataSetChanged();

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
        adapter.notifyDataSetChanged();
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

