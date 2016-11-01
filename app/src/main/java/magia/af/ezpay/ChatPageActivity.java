package magia.af.ezpay;

import android.app.FragmentTransaction;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.fragments.GetCardFragment;
import magia.af.ezpay.fragments.PaymentFragment;

public class ChatPageActivity extends BaseActivity {
    private String phone;
    public String contactName;
    private String imageUrl = "http://new.opaybot.ir";
    RecyclerView recyclerView;
    PayLogFeed feed;
    ChatPageAdapter adapter;
    static boolean isOpen = false;
    public RelativeLayout darkDialog;
    public GetCardFragment getCardFragment;
    public PaymentFragment paymentFragment;
    public int fragment_status = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("phone");
            Log.i("#%^&@%^&@", phone);
            contactName = bundle.getString("contactName");
            imageUrl = imageUrl + bundle.getString("image");
        }
        ImageView contactImage = (ImageView) findViewById(R.id.profile_image);
        Glide.with(this).load(imageUrl).into(contactImage);
        TextView name = (TextView) findViewById(R.id.txt_user_name);
        name.setText(contactName);
        recyclerView = (RecyclerView) findViewById(R.id.pay_list_recycler);
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(false);
        recyclerView.setLayoutManager(manager);
        new fillContact().execute(phone);

        findViewById(R.id.btn_pey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft;
                darkDialog.setVisibility(View.VISIBLE);
                paymentFragment = PaymentFragment.newInstance(false);
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
                ft.add(android.R.id.content, paymentFragment).commit();
                isOpen = false;

            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private class sendPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PayLogItem doInBackground(String... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.sendPaymentRequest(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(PayLogItem result) {
            if (result != null) {
                feed.addItem(result);
                adapter.notifyDataSetChanged();
            } else
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
        }
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
            int pos = feed.getItemCount() - position - 1;
            if (feed.getItem(pos).getFrom().equals(phone)) {
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
            int pos = feed.getItemCount() - position - 1;
            if (holder.getItemViewType() == 0) {
                holder.txt_price.setText(feed.getItem(pos).getAmount()+"");
                holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                holder.txt_price.setText(feed.getItem(pos).getAmount()+"");
                holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return feed.getItemCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_status;
            TextView txt_description;
            ImageButton btn_replay;
            TextView txt_price;

            ViewHolder(View itemView) {
                super(itemView);
                txt_price = (TextView) itemView.findViewById(R.id.txt_priceFrom);
                txt_status = (TextView) itemView.findViewById(R.id.txt_status_payed_from);
                txt_description = (TextView) itemView.findViewById(R.id.txt_description_from);
                btn_replay = (ImageButton) itemView.findViewById(R.id.btn_replay_pay_from);
            }
        }

    }

    public void sendPay(String detail, String comment, int amount) {
        new sendPaymentRequest().execute(phone, detail, comment, String.valueOf(amount));
    }

    public void payBill(int amount, String comment) {
        darkDialog.setVisibility(View.VISIBLE);
        getCardFragment = GetCardFragment.newInstance(amount, comment);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
        ft.add(android.R.id.content, getCardFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment_status == 1) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(paymentFragment).commit();
        } else if (fragment_status == 2) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(getCardFragment).commit();
        }
        else
            finish();

    }
}
