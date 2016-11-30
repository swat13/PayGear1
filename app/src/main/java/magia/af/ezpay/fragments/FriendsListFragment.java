package magia.af.ezpay.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import magia.af.ezpay.ChooseFriendsActivity;
import magia.af.ezpay.GroupChatPageActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.interfaces.OnClickHandler;

/**
 * Created by erfan on 11/3/2016.
 */

public class FriendsListFragment extends Fragment implements OnClickHandler {

  static RSSFeed _feed;
  ArrayList<RSSItem> contacts;
  ArrayList<RSSItem> groups;
  RecyclerView recBills;
  FriendsListFragment.ListAdapter adapter;
  public String comment;
  public int amount;
  public int position;
  ContactDatabase database;
  JSONArray jsonArray;
  static Handler handler = new Handler();

  public static FriendsListFragment getInstance(RSSFeed rssFeed) {
    _feed = rssFeed;
    return new FriendsListFragment();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.activity_friend_list, container, false);
    ((MainActivity) getActivity()).fragment_status = 2;
    recBills = (RecyclerView) v.findViewById(R.id.contact_recycler);
    _feed = (RSSFeed) getActivity().getIntent().getSerializableExtra("contact");
    Log.e("Size", "onCreateView: " + _feed.getItemCount());
    for (int i = 0; i < _feed.getItemCount(); i++) {
      Log.e("SS", "onCreateView: " + _feed.getItem(i).getTitle());
      contacts = _feed.getItem(i).getContactMembers();
      groups = _feed.getItem(i).getGroupArray();
    }

    new getAccount().execute();


//    for (int i = 0; i < _feed.getItemCount(); i++) {
//      Log.e("Size", "onCreateView: " + _feed.getItem(i).getContactMembers().toString());
//    }
//    contacts = _feed.getItem(_feed.getItemCount()-1).getContactMembers();
//    groups = _feed.getItem(37).getGroupArray();
    for (int i = 0; i < contacts.size(); i++) {
      Log.e("Size", "onCreateView: " + contacts.get(i).getTitle());
    }
//    for (int i = 0; i < groups.size(); i++) {
//      Log.e("Size2", "onCreateView: " + groups.get(i).getGroupTitle());
//    }
    CardView inviteFriends = (CardView) v.findViewById(R.id.invite_friends);
    inviteFriends.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ChooseFriendsActivity.class);
        intent.putExtra("contact", contacts);
        getActivity().startActivity(intent);
      }
    });
    Bundle bundle = getArguments();
    if (bundle != null) {
      comment = getArguments().getString("description");
      amount = getArguments().getInt("amount");
      position = getArguments().getInt("pos");
      if (comment.length() > 0 && amount > 0) {
        _feed.getItem(position).setComment(comment);
        _feed.getItem(position).setLastChatAmount(amount);
      }
    }
    if (_feed != null && _feed.getItemCount() != 0) {
      adapter = new FriendsListFragment.ListAdapter(this);
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
  public void onClick(RSSItem rssFeed) {
    if (rssFeed.isGroupStatus()) {
      Intent goToChatPageActivity = new Intent(getActivity(), GroupChatPageActivity.class);
      goToChatPageActivity.putExtra("title", rssFeed.getGroupTitle());
      goToChatPageActivity.putExtra("photo", "http://new.opaybot.ir" + rssFeed.getGroupPhoto());
      goToChatPageActivity.putExtra("id", rssFeed.getGroupId());
      goToChatPageActivity.putExtra("members", rssFeed.getGroupMembers());
      startActivityForResult(goToChatPageActivity, 10);
    } else {
      Intent goToChatPageActivity = new Intent(getActivity(), ChatPageActivity.class);
      goToChatPageActivity.putExtra("phone", rssFeed.getTelNo());
      goToChatPageActivity.putExtra("contactName", rssFeed.getContactName());
      goToChatPageActivity.putExtra("image", rssFeed.getContactImg());
      goToChatPageActivity.putExtra("pos", rssFeed.getPosition());
      goToChatPageActivity.putExtra("date", rssFeed.getLastChatDate());
      startActivityForResult(goToChatPageActivity, 10);
    }
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
      RSSItem rssItem = _feed.getItem(getAdapterPosition());
      onClickHandler.onClick(rssItem);
    }
  }

  public class ListAdapter extends RecyclerView.Adapter<FriendsListFragment.FeedViewHolder> {
    OnClickHandler onClickHandler;

    public ListAdapter(OnClickHandler onClickHandler) {
      this.onClickHandler = onClickHandler;
    }

    @Override
    public int getItemCount() {
      return _feed.getItemCount();
    }

    @Override
    public void onBindViewHolder(final FriendsListFragment.FeedViewHolder FeedViewHolder, final int position) {
      String contactName = "";
      RSSItem fe = _feed.getItem(position);
//      ContactDatabase database = new ContactDatabase(getActivity());
//      fe.setContactName(database.getNameFromNumber(fe.getTelNo()));
      Log.e("sssssssss", "onBindViewHolder: " + fe.getTelNo());
      Log.e("(((((((((((", "onBindViewHolder: " + fe.getTelNo());
      Log.e("Test", "onBindViewHolder: " + fe.getGroupTitle());
      Log.e("Boolean", "onBindViewHolder: " + fe.isGroup());
      if (!fe.isGroupStatus()) {
        if (fe.getTitle().length() > 15) {
          contactName = fe.getTitle();
          FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
        } else {
          FeedViewHolder.contactName.setText(fe.getTitle());
        }
      }
      if (fe.isGroupStatus()) {
        if (fe.getGroupTitle().length() > 15) {
          contactName = fe.getGroupTitle();
          FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
        } else {
          FeedViewHolder.contactName.setText(fe.getGroupTitle());
        }
        Log.e("DDDD", "onBindViewHolder: " + "http://new.opaybot.ir" + fe.getGroupPhoto());
        Glide.with(getActivity())
          .load("http://new.opaybot.ir" + fe.getGroupPhoto())
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
      } else {
        Log.e("#######", "onBindViewHolder: " + "http://new.opaybot.ir" + fe.getContactImg());
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

      FeedViewHolder.description.setText(fe.getComment());
      FeedViewHolder.pay.setText(getDividedToman((long) fe.getLastChatAmount()) + "");
      fe.setPosition(position);

//        FeedViewHolder.contactImage.setImageDrawable(fe.getContactImg());

            /*if (fe.isContactStatus()) {
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_online_stat);

            } else
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_offline_stat);*/

    }

    @Override
    public FriendsListFragment.FeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
      View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
      return new FeedViewHolder(itemView, onClickHandler);
    }

  }


  private String getDividedToman(Long price) {
    if (price == 0) {
      return price + "";
    }
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

//  @Override
//  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//    new ComparingContactWithDatabase().execute();
//    if (adapter != null) {
//      adapter.notifyDataSetChanged();
//    }
//    super.onViewCreated(view, savedInstanceState);
//  }

//  @Override
//  public void onAttach(Context context) {
//    new ComparingContactWithDatabase().execute();
//    if (adapter != null) {
//      adapter.notifyDataSetChanged();
//    }
//    super.onAttach(context);
//  }

//  @Override
//  public void onCreate(@Nullable Bundle savedInstanceState) {
//    new ComparingContactWithDatabase().execute();
//    if (adapter != null) {
//      adapter.notifyDataSetChanged();
//    }
//    super.onCreate(savedInstanceState);
//  }

//  @Override
//  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//    new ComparingContactWithDatabase().execute();
//    if (adapter != null) {
//      adapter.notifyDataSetChanged();
//    }
//    super.onActivityCreated(savedInstanceState);
//  }

//  @Override
//  public void onResume() {
//    new ComparingContactWithDatabase().execute();
//    adapter.notifyDataSetChanged();
//    super.onResume();
//  }


  public String newContact(JSONArray jsonArray) {
    return jsonArray.toString();
  }

  public class ComparingContactWithDatabase extends AsyncTask<Void, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(Void... params) {
      database = new ContactDatabase(getActivity());
      GetContact getContact = new GetContact();

      RSSFeed databaseContact = database.getAllData();
      RSSFeed phoneContact = getContact.getNewContact(getActivity());
      for (int i = 0; i < phoneContact.getItemCount(); i++) {
//        Log.e("(((", "doInBackground i: " + i);
        for (int j = 0; j < databaseContact.getItemCount(); j++) {
//          Log.e("(((", "doInBackground j: " + j);
          if (phoneContact.getItem(i).getTelNo().equals(databaseContact.getItem(j).getTelNo())
            && phoneContact.getItem(i).getContactName().equals(databaseContact.getItem(j).getContactName())) {
            phoneContact.removeItem(i);
          }
        }
      }
      jsonArray = new JSONArray();
      for (int i = 0; i < phoneContact.getItemCount(); i++) {
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("m", phoneContact.getItem(i).getTelNo());
          jsonObject.put("t", phoneContact.getItem(i).getContactName());
          jsonArray.put(i, jsonObject);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        database.createData(phoneContact.getItem(i).getTelNo(), phoneContact.getItem(i).getContactName());
      }
      return jsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
      if (jsonArray != null) {
        new fillContact().execute(newContact(jsonArray));
      }
      super.onPostExecute(jsonArray);
    }
  }

  private class fillContact extends AsyncTask<String, Void, RSSFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected RSSFeed doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.checkContactListWithGroup(params[0]);
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        _feed = result;
        adapter.notifyDataSetChanged();
      } else {
        Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }

  public class getAccount extends AsyncTask<Void, Void, RSSItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected RSSItem doInBackground(Void... params) {
      DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.getAccount();
    }

    @Override
    protected void onPostExecute(RSSItem result) {
      Log.e("jsons", String.valueOf(result));


      if (result != null) {
        getActivity().getSharedPreferences("EZpay", 0).edit().putString("phoneNumber", result.getTelNo()).apply();
      } else {
        Toast.makeText(getActivity(), "Json Is Null!", Toast.LENGTH_SHORT).show();
      }


    }
  }
}
