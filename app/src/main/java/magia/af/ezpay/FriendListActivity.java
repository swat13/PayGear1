package magia.af.ezpay;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.interfaces.OnClickHandler;

public class FriendListActivity extends BaseActivity implements OnClickHandler{

  RSSFeed _feed;
  RecyclerView recBills;
  ListAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friend_list);
    recBills = (RecyclerView) findViewById(R.id.contact_recycler);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    StrictMode.setThreadPolicy(policy);
    new fillContact().execute(new GetContact().getContact(FriendListActivity.this));

//        if (_feed != null) {
//            adapter = new ListAdapter();
//            recBills.setAdapter(adapter);
//            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
//            llm.setOrientation(LinearLayoutManager.VERTICAL);
//            recBills.setNestedScrollingEnabled(true);
//            recBills.setLayoutManager(llm);
//            adapter.notifyDataSetChanged();
//        }

  }

  @Override
  public void onClick(RSSItem rssFeed) {
    Intent goToChatPageActivity = new Intent(this , ChatPageActivity.class);
    goToChatPageActivity.putExtra("phone", rssFeed.getTelNo());
    goToChatPageActivity.putExtra("contactName", rssFeed.getContactName());
    goToChatPageActivity.putExtra("image", rssFeed.getContactImg());
    startActivity(goToChatPageActivity);
  }

  public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView contactName;
    ImageView contactImage;
    ImageView contactStat;
    OnClickHandler onClickHandler;
    RSSFeed rssFeed;
    public FeedViewHolder(View v , OnClickHandler onClickHandler) {
      super(v);
      contactName = (TextView) v.findViewById(R.id.name_text);
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
    public void onBindViewHolder(final FeedViewHolder FeedViewHolder, final int position) {

      final RSSItem fe = _feed.getItem(position);

      FeedViewHolder.contactName.setText(fe.getContactName());
//        FeedViewHolder.contactImage.setImageDrawable(fe.getContactImg());

            /*if (fe.isContactStatus()) {
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_online_stat);

            } else
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_offline_stat);*/


    }

    @Override
    public FeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
      View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
      return new FeedViewHolder(itemView , onClickHandler);
    }

  }

  private class fillContact extends AsyncTask<String, Void, RSSFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

    }

    @Override
    protected RSSFeed doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.getContact(params[0]);
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        if (_feed == null || _feed.getItemCount() == 0) {
          _feed = result;
//                    Log.e("000000000", "onPostExecute: " + result.getItem(2).getTelNo());
          adapter = new ListAdapter(FriendListActivity.this);
          recBills.setAdapter(adapter);
          LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
          llm.setOrientation(LinearLayoutManager.VERTICAL);
          recBills.setNestedScrollingEnabled(true);
          recBills.setLayoutManager(llm);
          adapter.notifyDataSetChanged();
        } else {
          _feed = result;
          adapter.notifyDataSetChanged();
        }
        new LocalPersistence().writeObjectToFile(getApplicationContext(), _feed, "Contact_List");
      } else
        Toast.makeText(FriendListActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();

    }

  }
}
