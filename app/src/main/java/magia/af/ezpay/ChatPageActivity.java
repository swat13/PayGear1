package magia.af.ezpay;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.fragments.GetCardFragment;
import magia.af.ezpay.fragments.PaymentFragment;
import magia.af.ezpay.fragments.RequestPaymentFragment;
import magia.af.ezpay.helper.CalendarConversion;

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
    public RequestPaymentFragment requestPaymentFragment;
    public int fragment_status = 0;

    public String description;
    public int amount;
    private int position;
    int pos;
    boolean success = false;
    boolean getStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("phone");
            position = bundle.getInt("pos");
            Log.i("#%^&@%^&@", phone);
            contactName = bundle.getString("contactName");
            Log.e("ContactName", "contactName" + contactName);
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

        /*if (new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List") != null) {
            feed = (PayLogFeed) new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List");
            adapter = new ChatPageAdapter();
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }*/

        new getChatLog().execute(phone);

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
        feed.addItem(result);
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
      return domParser.RequestFromAnother(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.addItem(result);
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
        getStatus = true;
      } else
        getStatus = false;
      Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
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
        feed.addItem(result);
        description = result.getComment();
        amount = result.getAmount();
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
        success = true;
        Log.e("%%%%%%", "onPostExecute: accept" + success);
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
        adapter.notifyDataSetChanged();
      } else
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
    }
  }

  private class getChatLog extends AsyncTask<String, Void, PayLogFeed> {

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
        Log.e("0000000000", "onPostExecute: ");
        if (feed == null || feed.getItemCount() == 0) {
          Log.e("1111111111", "onPostExecute: ");
          feed = result;
          Log.i("PAY", result.toString());
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
      int pos = feed.getItemCount() - position - 1;
      if (feed.getItem(pos).getFrom().equals(phone)) {
        if (feed.getItem(pos).isPaideBool()) {
          if (feed.getItem(pos).isDeleted()) {
            return 4;
          }
          return 0;
        } else
          return 2;

      } else {
        if (feed.getItem(pos).isPaideBool()) {
          if (feed.getItem(pos).isDeleted()) {
            return 5;
          }
          return 1;
        } else
          return 3;
      }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView;
      if (viewType == 0) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_to, parent, false);
      } else if (viewType == 1) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from, parent, false);
      } else if (viewType == 2) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_pay_from, parent, false);
      } else if (viewType == 4) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from, parent, false);
      } else if (viewType == 5) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_to, parent, false);
      } else {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_pay_to, parent, false);
      }
      return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
      int year = 0;
      int month = 0;
      int day = 0;
      CalendarConversion conversion = null;
      pos = feed.getItemCount() - holder.getAdapterPosition() - 1;
      if (holder.getItemViewType() == 0) {
        pos = feed.getItemCount() - position - 1;
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
        holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
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
        pos = feed.getItemCount() - holder.getAdapterPosition() - 1;
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
        if (getStatus) {
          holder.txt_status.setText("درخواست داده شده");
        }
        if (success) {
          holder.txt_status.setText("پرداخت شد");
        } else {
          holder.txt_status.setText("درخواست داده شده");
        }
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText(feed.getItem(pos).getComment());
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            new DeletePaymentRequest().execute(feed.getItem(pos).getId());
            holder.btn_cancel.setVisibility(View.GONE);
            holder.txt_status.setText("پراخت نشد");
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
        pos = feed.getItemCount() - holder.getAdapterPosition() - 1;
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
        if (getStatus) {
          holder.txt_status.setText("درخواست داده شده");
        }
        if (success) {
          holder.txt_status.setText("پرداخت شد");
        } else {
          holder.txt_status.setText("درخواست داده شده");
        }
//        holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
                holder.txt_description.setText(feed.getItem(pos).getComment());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        darkDialog.setVisibility(View.VISIBLE);
                        pos = feed.getItemCount() - holder.getAdapterPosition() - 1;
                        Log.e("id", "1111111111111: " + pos);
                        getCardFragment = GetCardFragment.newInstance(feed.getItem(pos).getId());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
                        ft.add(android.R.id.content, getCardFragment).commit();
                        if (success) {
                            holder.btn_cancel.setVisibility(View.GONE);
                            holder.btn_accept.setVisibility(View.GONE);
                            holder.txt_status.setText("پرداخت شد");
                        }
                        holder.removeAt(pos);
                        adapter.notifyDataSetChanged();
                    }
                });
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.btn_cancel.setVisibility(View.GONE);
                        holder.btn_accept.setVisibility(View.GONE);
                    }
                });
                holder.btn_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ChatPageActivity.this, "" + holder.getItemViewType(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (holder.getItemViewType() == 5) {
                pos = feed.getItemCount() - position - 1;
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                if (feed.getItem(pos).isDeleted()) {
                    holder.txt_status.setText("پرداخت نشد");
                } else {
                    holder.txt_status.setText("پرداخت شد");
                }
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
            if (holder.getItemViewType() == 4) {
                pos = feed.getItemCount() - position - 1;
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                if (feed.getItem(pos).isDeleted()) {
                    holder.txt_status.setText("پرداخت نشد");
                } else {
                    holder.txt_status.setText("پرداخت شد");
                }
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
                pos = feed.getItemCount() - holder.getAdapterPosition() - 1;
                year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
                month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
                day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
                conversion = new CalendarConversion(year, month, day);
                holder.txt_price.setText(feed.getItem(pos).getAmount() + "");
                holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
                holder.txt_date.setText(conversion.getIranianDate() + "");
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
