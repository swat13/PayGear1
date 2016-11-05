package magia.af.ezpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.OnClickHandler;

public class FriendListActivity extends BaseActivity implements OnClickHandler {

  RSSFeed _feed;
  RecyclerView recBills;
  ListAdapter adapter;
  String description;
  int amount;
  int pos;
  Bundle bundle;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);
    recBills = (RecyclerView) findViewById(R.id.contact_recycler);
//    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//    StrictMode.setThreadPolicy(policy);
    _feed = (RSSFeed) getIntent().getSerializableExtra("contact");
    if (_feed != null && _feed.getItemCount() != 0) {
      adapter = new ListAdapter(FriendListActivity.this);
      recBills.setAdapter(adapter);
      LinearLayoutManager llm = new LinearLayoutManager(FriendListActivity.this);
      llm.setOrientation(LinearLayoutManager.VERTICAL);
      recBills.setNestedScrollingEnabled(true);
      recBills.setLayoutManager(llm);
      adapter.notifyDataSetChanged();
    } else {
      adapter = new ListAdapter(FriendListActivity.this);
      recBills.setAdapter(adapter);
      LinearLayoutManager llm = new LinearLayoutManager(FriendListActivity.this);
      llm.setOrientation(LinearLayoutManager.VERTICAL);
      recBills.setNestedScrollingEnabled(true);
      recBills.setLayoutManager(llm);
      adapter.notifyDataSetChanged();
    }


  }

  @Override
  public void onClick(RSSItem rssFeed) {
    Intent goToChatPageActivity = new Intent(this, ChatPageActivity.class);
    goToChatPageActivity.putExtra("phone", rssFeed.getTelNo());
    goToChatPageActivity.putExtra("contactName", rssFeed.getContactName());
    goToChatPageActivity.putExtra("image", rssFeed.getContactImg());
    goToChatPageActivity.putExtra("pos", rssFeed.getPosition());
    startActivityForResult(goToChatPageActivity,10);
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

  public class ListAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    OnClickHandler onClickHandler;

    public ListAdapter(OnClickHandler onClickHandler) {
      this.onClickHandler = onClickHandler;
    }

    @Override
    public int getItemCount() {
      return _feed.getItemCount();
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder FeedViewHolder,final int position) {

      RSSItem fe = _feed.getItem(position);
      ContactDatabase database = new ContactDatabase(FriendListActivity.this);
      fe.setContactName(database.getNameFromNumber(fe.getTelNo()));
      FeedViewHolder.contactName.setText(fe.getContactName());
      Glide.with(FriendListActivity.this).load("http://new.opaybot.ir" + fe.getContactImg()).into(FeedViewHolder.contactImage);
      FeedViewHolder.description.setText(fe.getComment());
      Log.e("sssssssss", "onBindViewHolder: " + fe.getComment());
      Log.e("(((((((((((", "onBindViewHolder: " + fe.getTelNo());
      FeedViewHolder.pay.setText(fe.getLastChatAmount() + "");
      fe.setPosition(position);

//        FeedViewHolder.contactImage.setImageDrawable(fe.getContactImg());

            /*if (fe.isContactStatus()) {
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_online_stat);

            } else
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_offline_stat);*/


    }

    @Override
    public FeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
      View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
      return new FeedViewHolder(itemView, onClickHandler);
    }

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == 10){
      bundle = getIntent().getExtras();
      if (bundle != null) {
        Log.e("$$$$$$$$$", "onCreate:00000 "+data.getStringExtra("description"));
        description = data.getStringExtra("description");
        amount = data.getIntExtra("amount" , 0);
        pos = data.getIntExtra("pos" , 0);
        _feed.getItem(pos).setComment(description);
        _feed.getItem(pos).setLastChatAmount(amount);
        adapter.notifyDataSetChanged();
      }
    }
  }
}
