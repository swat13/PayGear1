package magia.af.ezpay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.modules.ContactItem;

public class FriendListActivity extends BaseActivity {

    RSSFeed _feed;
    ListAdapter listAdapter;
    RecyclerView recBills;
    ListAdapter adapter;
    int pos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        recBills = (RecyclerView) findViewById(R.id.contact_recycler);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        new fillContact().execute();

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

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView contactName;
        ImageView contactImage;
        ImageView contactStat;

        public FeedViewHolder(View v) {
            super(v);
            contactName = (TextView) v.findViewById(R.id.name_text);
            contactImage = (ImageView) v.findViewById(R.id.contact_img);
            contactStat = (ImageView) v.findViewById(R.id.status_circle);

        }
    }

    public class ListAdapter extends RecyclerView.Adapter<FeedViewHolder> {
        ArrayList<ContactItem> contactItems;
        public ListAdapter(ArrayList<ContactItem> contactItems) {
            this.contactItems = contactItems;
        }

        @Override
        public int getItemCount() {
            return contactItems.size();
        }

        @Override
        public void onBindViewHolder(final FeedViewHolder FeedViewHolder, final int position) {

            FeedViewHolder.contactName.setText(contactItems.get(position).getContactName());

//            final RSSItem fe = _feed.getItem(position);

//            FeedViewHolder.contactName.setText(fe.getContactName());
//        FeedViewHolder.contactImage.setImageDrawable(fe.getContactImg());

            /*if (fe.isContactStatus()) {
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_online_stat);

            } else
                FeedViewHolder.contactStat.setImageResource(R.drawable.circle_bg_offline_stat);*/


        }

        @Override
        public FeedViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
            return new FeedViewHolder(itemView);
        }

    }

    private class fillContact extends AsyncTask<ArrayList<ContactItem>, Void, ArrayList<ContactItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<ContactItem> doInBackground(ArrayList<ContactItem>... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.getRegisteredContactFromServer(new GetContact().getContact(FriendListActivity.this));
        }

        @Override
        protected void onPostExecute(ArrayList<ContactItem> result) {
            if (result != null) {
                adapter = new ListAdapter(result);
                recBills.setAdapter(adapter);
//                if (_feed == null || _feed.getItemCount() == 0) {
//                    _feed = result;
////                    Log.e("000000000", "onPostExecute: " + result.getItem(2).getTelNo());
//                    adapter = new ListAdapter();
//                    recBills.setAdapter(adapter);
                    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recBills.setNestedScrollingEnabled(true);
                    recBills.setLayoutManager(llm);
                    adapter.notifyDataSetChanged();
//                } else {
//                    _feed = result;
//                    adapter.notifyDataSetChanged();
//                }
                new LocalPersistence().writeObjectToFile(getApplicationContext(), _feed, "Contact_List");
            } else {
                Toast.makeText(getApplicationContext(), "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
