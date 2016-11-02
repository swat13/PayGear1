package magia.af.ezpay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.fragments.BarCodeRead;
import magia.af.ezpay.interfaces.OnClickHandler;
import magia.af.ezpay.zxing.IntentIntegrator;
import magia.af.ezpay.zxing.IntentResult;

public class FriendListActivity extends BaseActivity implements OnClickHandler {

    RSSFeed _feed;
    RecyclerView recBills;
    ListAdapter adapter;


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
        }

        findViewById(R.id.barcode_reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(FriendListActivity.this,SimpleScannerActivity.class));

            }
        });



    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
//        if (scanningResult != null) {
//
////            String scanContent = scanningResult.getContents();
////            String scanFormat = scanningResult.getFormatName();
////
////            Log.e("sssscccaaaaaaaaaaannn", scanContent + "eeeee" + scanFormat );
//
//
//
//
//
//
//
//
//
//
//
//
//
////we have a result
//        }
//
//        else{
//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "No scan data received!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//    }

    @Override
    public void onClick(RSSItem rssFeed) {
        Intent goToChatPageActivity = new Intent(this, ChatPageActivity.class);
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

        public FeedViewHolder(View v, OnClickHandler onClickHandler) {
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
            Glide.with(FriendListActivity.this).load("http://new.opaybot.ir" + fe.getContactImg()).into(FeedViewHolder.contactImage);
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

}
