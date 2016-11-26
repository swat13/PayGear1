package magia.af.ezpay.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.ChooseFriendsActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.OnClickHandler;

/**
 * Created by erfan on 11/3/2016.
 */

public class FriendsListFragment extends Fragment implements OnClickHandler {

    static RSSFeed _feed;
    RecyclerView recBills;
    FriendsListFragment.ListAdapter adapter;
    public String comment;
    public int amount;
    public int position;

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
        CardView inviteFriends = (CardView)v.findViewById(R.id.invite_friends);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseFriendsActivity.class);
                intent.putExtra("contact" , _feed);
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
        Intent goToChatPageActivity = new Intent(getActivity(), ChatPageActivity.class);
        goToChatPageActivity.putExtra("phone", rssFeed.getTelNo());
        goToChatPageActivity.putExtra("contactName", rssFeed.getContactName());
        goToChatPageActivity.putExtra("image", rssFeed.getContactImg());
        goToChatPageActivity.putExtra("pos", rssFeed.getPosition());
        goToChatPageActivity.putExtra("date", rssFeed.getLastChatDate());
        startActivityForResult(goToChatPageActivity, 10);
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
            ContactDatabase database = new ContactDatabase(getActivity());
            fe.setContactName(database.getNameFromNumber(fe.getTelNo()));
            Log.e("sssssssss", "onBindViewHolder: " + fe.getTelNo());
            Log.e("(((((((((((", "onBindViewHolder: " + fe.getTelNo());
            Log.e("Test", "onBindViewHolder: " + fe.getGroupTitle());
            Log.e("Boolean", "onBindViewHolder: " + fe.isGroup());
            if (fe.getContactName().length() > 15) {
                contactName = fe.getContactName();
                FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
            } else {
                FeedViewHolder.contactName.setText(fe.getContactName());
            }
            if (fe.isGroupStatus()){
                if (fe.getGroupTitle().length() > 15) {
                    contactName = fe.getGroupTitle();
                    FeedViewHolder.contactName.setText(contactName.substring(0, 15) + "...");
                } else {
                    FeedViewHolder.contactName.setText(fe.getGroupTitle());
                }
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
            }
            Log.e("#######", "onBindViewHolder: "+ "http://new.opaybot.ir" + fe.getContactImg());
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

    public static void UpdateUi(){

    }
}
