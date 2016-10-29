package magia.af.ezpay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.Parser.PayLogFeed;

public class ChatPageActivity extends BaseActivity {
  private String phone;
  private String contactName;
  private String imageUrl = "http://new.opaybot.ir";
  RecyclerView recyclerView;
  PayLogFeed feed;
  ChatPageAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_page_activity);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      phone = bundle.getString("phone");
      Log.i("#%^&@%^&@" , phone);
      Log.i("#%^&@%^&@" , phone);
      contactName = bundle.getString("contactName");
      imageUrl = imageUrl + bundle.getString("image");
    }
    ImageView contactImage = (ImageView) findViewById(R.id.profile_image);
    Glide.with(this).load(imageUrl).into(contactImage);
    TextView name = (TextView) findViewById(R.id.txt_user_name);
    name.setText(contactName);
    recyclerView = (RecyclerView) findViewById(R.id.pay_list_recycler);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);
    new fillContact().execute(phone);
  }

  private class fillContact extends AsyncTask<String, Void, PayLogFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();

    }

    @Override
    protected PayLogFeed doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.payLogWithAnother(params[0]);
    }

    @Override
    protected void onPostExecute(PayLogFeed result) {
      if (result != null) {
        if (feed == null || feed.getItemCount() == 0) {
          feed = result;
          Log.i("PAY", result.toString());
          adapter = new ChatPageAdapter();
          recyclerView.setAdapter(adapter);
          adapter.notifyDataSetChanged();
        } else {
          feed = null;
          adapter.notifyDataSetChanged();
        }
      } else
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
    }
  }

  public class ChatPageAdapter extends RecyclerView.Adapter<ChatPageAdapter.ViewHolder> {
    public ChatPageAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
      if (feed.getItem(position).getFrom().equals(phone)) {
        return 0;
      } else return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView;
      if (viewType == 0) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from, parent, false);
      } else {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_to, parent, false);
      }
      return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      if (holder.getItemViewType() == 0) {
//        holder.txt_price_from.setText(feed.getItem(position).getAmount());
        holder.txt_status_from.setText(feed.getItem(position).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
        holder.txt_description_from.setText(feed.getItem(position).getComment());
        holder.btn_replay_from.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(ChatPageActivity.this, ""+holder.getItemViewType(), Toast.LENGTH_SHORT).show();
          }
        });
      } else {
//        holder.txt_price_from.setText(feed.getItem(position).getAmount());
        holder.txt_status_from.setText(feed.getItem(position).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
        holder.txt_description_from.setText(feed.getItem(position).getComment());
        holder.btn_replay_from.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Toast.makeText(ChatPageActivity.this, ""+holder.getItemViewType(), Toast.LENGTH_SHORT).show();
          }
        });
      }
    }

    @Override
    public int getItemCount() {
      return feed.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
//      TextView txt_price_from;
      TextView txt_status_from;
      TextView txt_description_from;
      ImageButton btn_replay_from;
      TextView txt_price_to;
      TextView txt_status_to;
      TextView txt_description_to;
      ImageButton btn_replay_to;

      ViewHolder(View itemView) {
        super(itemView);
//        txt_price_from = (TextView) itemView.findViewById(R.id.txt_price_from);
        txt_status_from = (TextView) itemView.findViewById(R.id.txt_status_payed_from);
        txt_description_from = (TextView) itemView.findViewById(R.id.txt_description_from);
        btn_replay_from = (ImageButton) itemView.findViewById(R.id.btn_replay_pay_from);
      }
    }

  }
}
