package magia.af.ezpay;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.fragments.GetCardFragment;
import magia.af.ezpay.fragments.PaymentFragment;
import magia.af.ezpay.fragments.RequestPaymentFragment;
import magia.af.ezpay.helper.CalendarConversion;

/*dd*/
public class ChatPageActivity extends BaseActivity {
    private String phone;
    public String contactName;
    private String imageUrl = "http://new.opaybot.ir";
    RecyclerView recyclerView;
    static PayLogFeed feed;
    static ChatPageAdapter adapter;
    static boolean isOpen = false;
    public RelativeLayout darkDialog;
    public GetCardFragment getCardFragment;
    public PaymentFragment paymentFragment;
    public RequestPaymentFragment requestPaymentFragment;
    public int fragment_status = 0;

    public String description;
    public int amount;
    private int position;
    int pos;
    boolean success = false;
    boolean getStatus;
    int removePosition;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int load = 10;
    LinearLayoutManager manager;
    String date;

    int reversePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("phone");
//      date = bundle.getString("date");
//      Log.e("bundle date", date);
            position = bundle.getInt("pos");
            Log.i("Current Phone", phone);
            contactName = bundle.getString("contactName");
            Log.e("ContactName", "contactName" + contactName);
            imageUrl = imageUrl + bundle.getString("image");
            Log.e("image", "image" + imageUrl);
        }

        date = "2050-01-01T00:00:00.000";

        final ImageView contactImage = (ImageView) findViewById(R.id.profile_image);
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.pic_profile)
                .into(new BitmapImageViewTarget(contactImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(700);
                        contactImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatPageActivity.this, ProfileActivity.class);
                intent.putExtra("contactName", contactName);
                intent.putExtra("phone", phone);
                intent.putExtra("image", imageUrl);
                startActivity(intent);
            }
        });
        TextView name = (TextView) findViewById(R.id.txt_user_name);
        name.setText(contactName);
        recyclerView = (RecyclerView) findViewById(R.id.pay_list_recycler);
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(false);
        recyclerView.setLayoutManager(manager);


        /*if (new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List") != null) {
            feed = (PayLogFeed) new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List");
            adapter = new ChatPageAdapter();
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }*/
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    String maxDate = df.format(Calendar.getInstance().getTime());
//    Log.e("MAX_DATE", "onCreate: " + maxDate);
        new getChatLog().execute(phone, date, "100");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.e("dy", "" + dy);
                if (dy == -10) //check for scroll down
                {
//          visibleItemCount = manager.getChildCount();
//          Log.e("ddddd", "" + visibleItemCount);
//          totalItemCount = manager.getItemCount();
//          Log.e("ggggg", "" + totalItemCount);
//          pastVisiblesItems = manager.findFirstVisibleItemPosition();
//          Log.e("hhhhh", "" + pastVisiblesItems);
                    Log.e("LOL", "onScrolled: " + load);
                    new getChatLog().execute(phone, date, String.valueOf(load));
                    load += 10;
//          DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//          String maxDate = df.format(Calendar.getInstance().getTime());

//          if (loading) {
//            if ((visibleItemCount - pastVisiblesItems) >= 1) {
//              loading = false;
//              Log.e("...", "Last Item Wow !");
//            }
//          }
//          return;
                }
            }
        });

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

        findViewById(R.id.btn_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Receive", "onClick: ");
                FragmentTransaction ft;
                darkDialog.setVisibility(View.VISIBLE);
                requestPaymentFragment = RequestPaymentFragment.newInstance();
                ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
                ft.add(android.R.id.content, requestPaymentFragment).commit();
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
//        feed.removeItem(pos);
                Log.e("POS", pos + "");
                feed.addItem(result, 0);
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
                new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
                adapter.notifyDataSetChanged();
            } else
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
        }
    }

    private class requestFromAnother extends AsyncTask<String, Void, PayLogItem> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PayLogItem doInBackground(String... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            Log.e("PARAMS1", "doInBackground: " + params[0]);
            Log.e("PARAMS2", "doInBackground: " + params[1]);
            Log.e("PARAMS3", "doInBackground: " + params[2]);
            return domParser.RequestFromAnother(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(PayLogItem result) {
            if (result != null) {
//        feed.removeItem(pos);
                Log.e("POS", pos + "");
                feed.addItem(result, 0);
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
                new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class accPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PayLogItem doInBackground(String... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            Log.e("0000", "accpayment0000: " + getSharedPreferences("EZpay", 0).getString("id", ""));
            return domParser.accPaymentRequest(params[0], params[1]);

        }

        @Override
        protected void onPostExecute(PayLogItem result) {
            if (result != null) {
//        feed.removeItem(pos);
                Log.e("POS", pos + "");
                feed.addItem(result, 0);
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
                description = result.getComment();
                amount = result.getAmount();
                new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
                success = true;
                Log.e("%%%%%%", "onPostExecute: accept" + success);
                if (success) {
                    Log.e("sssssssss", "onPostExecute: " + pos);
                    feed.removeItem(pos);
                    adapter.notifyDataSetChanged();
          /*adapter.notifyItemRemoved(pos);
          adapter.notifyItemRangeRemoved(removePosition, adapter.getItemCount());
          adapter.notifyItemRangeChanged(removePosition, adapter.getItemCount());
          adapter.notifyDataSetChanged();*/
                }
                Log.e("%%%%%%", "onPostExecute: accept");
//                recreate();
            } else {
                success = false;
                Log.e("%%%%%%", "onPostExecute: accept2" + success);
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeletePaymentRequest extends AsyncTask<Integer, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            Log.e("0000", "accpayment0000: " + getSharedPreferences("EZpay", 0).getString("id", ""));
            return domParser.deletePayment(params[0]);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null) {
                Log.e("TEst", "onClick: " + result);
                if (result) {
                    Log.e("TEst", "onClick: " + pos);
                    feed.getItem(pos).setStatus(true);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class getChatLog extends AsyncTask<String, Void, PayLogFeed> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PayLogFeed doInBackground(String... params) {
            PayLogFeed payLogFeed = null;
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            try {
                payLogFeed = domParser.payLogWithAnother(params[0], params[1], params[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return payLogFeed;
        }

        @Override
        protected void onPostExecute(PayLogFeed result) {
            if (result != null) {
                Log.e("0000000000", "onPostExecute: ");
                if (feed == null || feed.getItemCount() == 0) {
                    Log.e("1111111111", "onPostExecute: ");
                    feed = result;
                    Log.i("PAY", result.toString());
                    reversePosition = feed.getItemCount() - 1;
                    adapter = new ChatPageAdapter();
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("222222222", "onPostExecute: ");
                    feed = result;
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ChatPageAdapter extends RecyclerView.Adapter<ChatPageAdapter.ViewHolder> {
        public ChatPageAdapter() {

        }

        @Override
        public int getItemViewType(int position) {
            int viewType;
            if (feed.getItem(position).getFrom().equals(phone)) {
                if (feed.getItem(position).isStatus()) {
                    viewType = 1;
                } else if (feed.getItem(position).isPaideBool()) {
                    viewType = 5;

                } else
                    viewType = 2;
            } else {
                if (feed.getItem(position).isStatus()) {
                    viewType = 0;
                } else if (feed.getItem(position).isPaideBool()) {
                    viewType = 4;

                } else
                    viewType = 3;
            }

//      if (feed.getItem(pos).getFrom().equals(phone)) {
//        if (feed.getItem(pos).isPaideBool()) {
//          if (feed.getItem(pos).isStatus()) {
//            return 4;
//          }
//          return 0;
//        } else
//          return 2;
//      } else {
//        if (feed.getItem(pos).isPaideBool()) {
//          if (!feed.getItem(pos).isStatus()) {
//            return 5;
//          }
//          return 1;
//        } else
//          return 3;
//      }
            return viewType;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView;
            if (viewType == 0) {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_from, parent, false);
            } else if (viewType == 1) {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_to, parent, false);
            } else if (viewType == 2) {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_pay_from, parent, false);
            } else if (viewType == 3) {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_pay_to, parent, false);
            } else if (viewType == 4) {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from, parent, false);
            } else {
                rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_to, parent, false);
            }
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Log.e("ffffff", "onBindViewHolder: " + holder.getItemViewType());
            int year = 0;
            int month = 0;
            int day = 0;
            CalendarConversion conversion = null;
            pos = holder.getAdapterPosition();
            if (holder.getItemViewType() == 0) {
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                //holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (holder.getItemViewType() == 2) {
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");

                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = holder.getAdapterPosition();
                        Log.e("eeeeeeeee", "onClick: " + pos);
                        new DeletePaymentRequest().execute(feed.getItem(pos).getId());
                    }
                });
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (holder.getItemViewType() == 3) {
                Log.e("id", "0000000000: " + pos);
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");

//        holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        darkDialog.setVisibility(View.VISIBLE);
                        pos = holder.getAdapterPosition();
                        Log.e("id", "1111111111111: " + pos);
                        getCardFragment = GetCardFragment.newInstance(feed.getItem(pos).getId());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
                        ft.add(android.R.id.content, getCardFragment).commit();
//            if (success) {
//              holder.btn_cancel.setVisibility(View.GONE);
//              holder.btn_accept.setVisibility(View.GONE);
//              holder.txt_status.setText("پرداخت شد");
//              adapter.notifyDataSetChanged();
//            }
//                        holder.removeAt(pos);
//                        adapter.notifyDataSetChanged();
                        removePosition = holder.getAdapterPosition();
                    }
                });
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = holder.getAdapterPosition();
                        Log.e("eeeeeeeee", "onClick: " + pos);
                        new DeletePaymentRequest().execute(feed.getItem(pos).getId());
                    }
                });
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (holder.getItemViewType() == 4) {
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                // holder.txt_status.setText("پرداخت شد");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (holder.getItemViewType() == 5) {
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                // holder.txt_status.setText("لغو شد");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                pos = holder.getAdapterPosition();
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
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

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txt_status;
            TextView txt_description;
            ImageButton btn_replay;
            TextView txt_price;
            TextView txt_clock;
            TextView txt_date;
            Button btn_cancel;
            Button btn_accept;

            ViewHolder(View itemView) {
                super(itemView);
                txt_price = (TextView) itemView.findViewById(R.id.txt_priceFrom);
                txt_clock = (TextView) itemView.findViewById(R.id.txt_clock);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);
                txt_status = (TextView) itemView.findViewById(R.id.txt_status_payed_from);
                txt_description = (TextView) itemView.findViewById(R.id.txt_description_from);
                btn_replay = (ImageButton) itemView.findViewById(R.id.btn_replay_pay_from);
                btn_cancel = (Button) itemView.findViewById(R.id.btn_cancel_pay);
                btn_accept = (Button) itemView.findViewById(R.id.btn_accept_pay);
//        btn_cancel.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }

            public void removeAt(int position) {
                feed.removeItem(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, feed.getItemCount());
            }
        }

    }

    public static void informNotif(PayLogItem messageBody) {
        feed.addItem(messageBody);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void update(ChatPageAdapter adapter) {
        adapter.notifyDataSetChanged();
    }

    public void sendReqPay(int amount, String comment) {
        description = comment;
        this.amount = amount;
        new requestFromAnother().execute(phone, amount + "", comment);
    }

    public void sendPay(String detail, String comment, int amount) {
        description = comment;
        this.amount = amount;
        new sendPaymentRequest().execute(phone, detail, comment, String.valueOf(amount));
    }

    public void accPay(int id, String detail) {

        new accPaymentRequest().execute(id + "", detail);
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
        } else if (fragment_status == 3) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(requestPaymentFragment).commit();
        } else if (description != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("description", description);
            intent.putExtra("amount", amount);
            intent.putExtra("pos", position);
            setResult(10, intent);
            finish();
        } else
            finish();
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

}
