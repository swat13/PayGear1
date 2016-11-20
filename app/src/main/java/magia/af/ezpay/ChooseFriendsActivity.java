package magia.af.ezpay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;

public class ChooseFriendsActivity extends BaseActivity {
  RSSFeed rssFeed;
  EditText groupTitle;
  RecyclerView recyclerView;
  ImageView imageView;
  ArrayList<String> phone = new ArrayList<>();
  private String imageUrl = "http://new.opaybot.ir";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_friends);
    rssFeed = (RSSFeed) getIntent().getSerializableExtra("contact");
    groupTitle = (EditText) findViewById(R.id.edt_group_title);
    recyclerView = (RecyclerView) findViewById(R.id.contact_recycler);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);
    imageView = (ImageView) findViewById(R.id.btn_done);
    RecyclerAdapter adapter = new RecyclerAdapter();
    recyclerView.setAdapter(adapter);
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
        if (groupTitle.getText().toString().length() < 1) {
          Toast.makeText(ChooseFriendsActivity.this, "لطفا عنوان گروه را وارد کنید", Toast.LENGTH_SHORT).show();
        } else {
          Log.e("JSONArray", "onClick: " + jsonArray.toString());
          new CreateGroup(groupTitle.getText().toString(),jsonArray).execute();
        }
      }
    });
  }

  public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_contact_item, parent, false);
      return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
      holder.txt_contact_item_name.setText(rssFeed.getItem(position).getContactName());
      holder.txt_contact_item_phone.setText(rssFeed.getItem(position).getTelNo());
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      TextView txt_contact_item_name;
      TextView txt_contact_item_phone;
      ImageView contact_item_image;
      ImageView status_circle;

      public ViewHolder(View itemView) {
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

  public class CreateGroup extends AsyncTask<String, Void, Boolean> {
    String title;
    JSONArray jsonArray;
    public CreateGroup(String title ,JSONArray jsonArray){
      this.title = title;
      this.jsonArray = jsonArray;
    }

    @Override
    protected Boolean doInBackground(String... params) {
      DOMParser parser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.group(title, jsonArray);
    }

    @Override
    protected void onPostExecute(Boolean b) {
      if (b) {
        Intent intent = new Intent(ChooseFriendsActivity.this, GroupChatPageActivity.class);
        startActivity(intent);
        finish();
      }
      super.onPostExecute(b);
    }
  }
}
