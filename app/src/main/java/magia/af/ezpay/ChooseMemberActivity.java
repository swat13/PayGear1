package magia.af.ezpay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.MembersItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.ContactDatabase;

public class ChooseMemberActivity extends BaseActivity {
    ArrayList<ChatListItem> rssFeed2 = new ArrayList<>();
    ArrayList<ChatListItem> rssFeed = new ArrayList<>();

    GroupDetailsActivity groupDetailsActivity;

    MembersFeed groupMembers;
    MembersFeed addedMembers = new MembersFeed();

    boolean isAddMember = false;

    TextView selectText;
    EditText groupTitle;
    ChatListFeed _ChatList_Feed;
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<String> phone = new ArrayList<>();
    ContactDatabase database;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends);

        selectText = (TextView) findViewById(R.id.select_text);
        groupTitle = (EditText) findViewById(R.id.edt_group_title);


//    database = new ContactDatabase(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("addMember")) {
            addGroupMember();
        } else {

            addGroup();
        }

        recyclerView = (RecyclerView) findViewById(R.id.contact_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        imageView = (ImageView) findViewById(R.id.btn_done);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < phone.size(); i++) {
                    try {
                        jsonArray.put(i, phone.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonArray.length() > 0) {
                    Intent intent;
                    if (!isAddMember) {
                        intent = new Intent(ChooseMemberActivity.this, CreateGroupActivity.class);
                        intent.putExtra("contact", rssFeed);
                        intent.putExtra("chatFeed", rssFeed2);
                        intent.putExtra("chatListFeed", _ChatList_Feed);
                        intent.putExtra("json", jsonArray.toString());
                        startActivity(intent);
                        finish();

                    } else {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("json", jsonArray.toString()).putExtra("contact", rssFeed);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }


                } else {
                    Toast.makeText(ChooseMemberActivity.this, "حداقل یک نفر را انتخاب کنید", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addGroup() {
        isAddMember = false;
        selectText.setText("انتخاب افراد گروه ");
        groupTitle.setHint("انتخاب افراد گروه ");
        _ChatList_Feed = (ChatListFeed) getIntent().getSerializableExtra("chatFeed");
        rssFeed2 = (ArrayList<ChatListItem>) getIntent().getSerializableExtra("contact");

        rssFeed = rssFeed2;

        groupTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (groupTitle.getText().toString().length() == 0 || groupTitle.getText().toString().isEmpty()) {
                    rssFeed = rssFeed2;
                    adapter.notifyDataSetChanged();
                } else {
//          chatListFeed = database.search(s.toString());
//          chatListFeed = (ArrayList<ChatListItem>) getIntent().getSerializableExtra("contact");
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

//        adapter.getFilter().filter(s.toString());
//        adapter.notifyDataSetChanged();

            }
        });


    }


    private void addGroupMember() {

        isAddMember = true;
        selectText.setText("اضافه کردن اعضا ");
        groupTitle.setHint("کسی که میخوای اضافه کنی رو انتخاب کن ");
        groupMembers = (MembersFeed) getIntent().getSerializableExtra("groupMembers");
        rssFeed2 = (ArrayList<ChatListItem>) new LocalPersistence().readObjectFromFile(getApplicationContext(), "AllContacts");
        if (groupMembers == null) {
            Log.e("00000000", "groupMembers is null");
        }
        if (rssFeed2 == null) {
            Log.e("11111111", "rssFeed2 is null");
        }

        for (int i = 0; i < groupMembers.memberItemCount(); i++) {
            for (int j = 0; j < rssFeed2.size(); j++) {


                if (groupMembers.getMember(i).getMemberId().equals(rssFeed2.get(j).getUserId())) {
                    rssFeed2.remove(j);
                    j--;
                }


            }
        }

        rssFeed = rssFeed2;


        groupTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (groupTitle.getText().toString().length() == 0 || groupTitle.getText().toString().isEmpty()) {
                    rssFeed = rssFeed2;
                    adapter.notifyDataSetChanged();
                } else {
//          chatListFeed = database.search(s.toString());
//          chatListFeed = (ArrayList<ChatListItem>) getIntent().getSerializableExtra("contact");
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

//        adapter.getFilter().filter(s.toString());
//        adapter.notifyDataSetChanged();

            }
        });


    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_contact_item, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
            if (rssFeed.get(position).getTitle().length() > 15) {
                holder.txt_contact_item_name.setText(rssFeed.get(position).getTitle().substring(0, 15) + "...");
            } else {
                holder.txt_contact_item_name.setText(rssFeed.get(position).getTitle());
            }
            holder.txt_contact_item_phone.setText(rssFeed.get(position).getTelNo());
            String imageUrl = "http://new.opaybot.ir";
            Glide.with(ChooseMemberActivity.this)
                    .load(imageUrl + rssFeed.get(position).getContactImg())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.pic_profile)
                    .into(new BitmapImageViewTarget(holder.contact_item_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(700);
                            holder.contact_item_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
            /*
            master's way
            * */
            if (phone.contains(rssFeed.get(position).getTelNo()))
                holder.status_circle.setVisibility(View.VISIBLE);
            else
                holder.status_circle.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return rssFeed.size();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    rssFeed = (ArrayList<ChatListItem>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    rssFeed = rssFeed2;

                    FilterResults results = new FilterResults();
                    ArrayList<ChatListItem> FilteredArrayNames = new ArrayList<>();

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < rssFeed.size(); i++) {
//            String dataNames = chatListFeed.getItem(i).getContactName();
                        ChatListItem chatListItem = new ChatListItem();
                        if (rssFeed.get(i).getTitle().toLowerCase().startsWith(constraint.toString())) {
                            chatListItem.setTitle(rssFeed.get(i).getTitle());
                            chatListItem.setTelNo(rssFeed.get(i).getTelNo());
                            chatListItem.setContactImg(rssFeed.get(i).getContactImg());
                            FilteredArrayNames.add(chatListItem);
                        }
                    }

                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                    Log.e("VALUES", results.values.toString());

                    return results;
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txt_contact_item_name;
            TextView txt_contact_item_phone;
            ImageView contact_item_image;
            ImageView status_circle;

            ViewHolder(View itemView) {
                super(itemView);
                txt_contact_item_name = (TextView) itemView.findViewById(R.id.txt_contact_item_name);
                txt_contact_item_phone = (TextView) itemView.findViewById(R.id.txt_contact_item_phone);
                contact_item_image = (ImageView) itemView.findViewById(R.id.contact_img);
                status_circle = (ImageView) itemView.findViewById(R.id.status_circle);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (status_circle.getVisibility() == View.GONE) {
                    status_circle.setVisibility(View.VISIBLE);
                    phone.add(rssFeed.get(getAdapterPosition()).getTelNo());
                } else {
                    status_circle.setVisibility(View.GONE);
                    phone.remove(rssFeed.get(getAdapterPosition()).getTelNo());
                }
            }
        }
    }


}
