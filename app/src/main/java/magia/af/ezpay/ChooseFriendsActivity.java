package magia.af.ezpay;

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

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.helper.ContactDatabase;

public class ChooseFriendsActivity extends BaseActivity {
  RSSFeed rssFeed;
  EditText groupTitle;
  RecyclerView recyclerView;
  ImageView imageView;
  ArrayList<String> phone = new ArrayList<>();
  ContactDatabase database;
  RecyclerAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_friends);
    database = new ContactDatabase(this);
    rssFeed = database.getInNetworkUserName();
    groupTitle = (EditText) findViewById(R.id.edt_group_title);
    recyclerView = (RecyclerView) findViewById(R.id.contact_recycler);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);
    imageView = (ImageView) findViewById(R.id.btn_done);
    adapter = new RecyclerAdapter();
    recyclerView.setAdapter(adapter);
    groupTitle.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e("RRRRR", "afterTextChanged: " + s.toString());
        if (groupTitle.getText().toString().length() == 0 || groupTitle.getText().toString().isEmpty()) {
          rssFeed = database.getInNetworkUserName();
          adapter.notifyDataSetChanged();
        } else {
//          rssFeed = database.search(s.toString());
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
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.e("Phones", phone.toString());
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < phone.size(); i++) {
          try {
            jsonArray.put(i, phone.get(i));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        if (jsonArray.length() > 0) {
          Intent intent = new Intent(ChooseFriendsActivity.this, CreateGroupActivity.class);
          intent.putExtra("contact", rssFeed);
          intent.putExtra("json", jsonArray.toString());
          startActivity(intent);
          finish();
        }
        else {
          Toast.makeText(ChooseFriendsActivity.this, "حداقل یک نفر را انتخاب کنید", Toast.LENGTH_SHORT).show();
        }
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
      holder.txt_contact_item_name.setText(rssFeed.getItem(position).getContactName());
      holder.txt_contact_item_phone.setText(rssFeed.getItem(position).getTelNo());
      String imageUrl = "http://new.opaybot.ir";
      Glide.with(ChooseFriendsActivity.this)
        .load(imageUrl + rssFeed.getItem(position).getContactImg())
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
    }

    @Override
    public int getItemCount() {
      return rssFeed.getItemCount();
    }

    @Override
    public Filter getFilter() {

      return new Filter() {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          rssFeed = (RSSFeed) results.values;
          notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          rssFeed = database.getInNetworkUserName();

          FilterResults results = new FilterResults();
          RSSFeed FilteredArrayNames = new RSSFeed();

          constraint = constraint.toString().toLowerCase();
          for (int i = 0; i < rssFeed.getItemCount(); i++) {
//            String dataNames = rssFeed.getItem(i).getContactName();
            RSSItem rssItem = new RSSItem();
            rssItem.setContactName(rssFeed.getItem(i).getContactName());
            if (rssFeed.getItem(i).getContactName().toLowerCase().startsWith(constraint.toString())) {
              FilteredArrayNames.addItem(rssItem);
            }
          }

          results.count = FilteredArrayNames.getItemCount();
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
          phone.add(rssFeed.getItem(getAdapterPosition()).getTelNo());
        } else {
          status_circle.setVisibility(View.GONE);
          phone.remove(rssFeed.getItem(getAdapterPosition()).getTelNo());
        }
        Log.e("Phones", phone.toString());
      }
    }
  }

//  public class CreateGroup extends AsyncTask<String, Void, Boolean> {
//    String title;
//    JSONArray jsonArray;
//
//    public CreateGroup(String title, JSONArray jsonArray) {
//      this.title = title;
//      this.jsonArray = jsonArray;
//    }
//
//    @Override
//    protected Boolean doInBackground(String... params) {
//      DOMParser parser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
//      return parser.group(title, jsonArray);
//    }
//
//    @Override
//    protected void onPostExecute(Boolean b) {
//      if (b) {
////        Intent intent = new Intent(ChooseFriendsActivity.this, CreateGroupActivity.class);
////        intent.putExtra("contacts" , rssFeed);
////        startActivity(intent);
//        new fillContact().execute();
//      }
//      super.onPostExecute(b);
//    }
//  }

//  private class fillContact extends AsyncTask<Void, Void, RSSFeed> {
//
//    @Override
//    protected void onPreExecute() {
//      super.onPreExecute();
//    }
//
//    @Override
//    protected RSSFeed doInBackground(Void... params) {
//      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
//      return domParser.checkContactListWithGroup(new GetContact().getContact(ChooseFriendsActivity.this));
//
//    }
//
//    @Override
//    protected void onPostExecute(RSSFeed result) {
//      if (result != null) {
//        ChooseFriendsActivity.this.finish();
//        startActivity(new Intent(ChooseFriendsActivity.this, CreateGroupActivity.class).putExtra("contact", result));
//        finish();
//      } else
//        Toast.makeText(ChooseFriendsActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
//    }
//
//
//  }
}
