package magia.af.ezpay;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;

import magia.af.ezpay.Firebase.MyFirebaseMessagingService;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.fragments.GetCardFragment;
import magia.af.ezpay.fragments.PaymentFragment;
import magia.af.ezpay.fragments.RequestPaymentFragment;
import magia.af.ezpay.helper.CalendarConversion;
import magia.af.ezpay.helper.NumberTextWatcher;
import magia.af.ezpay.interfaces.MessageHandler;

/*dd*/
public class ChatPageActivity extends BaseActivity implements MessageHandler {
  private String phone;
  public String contactName;
  private String imageUrl = "http://new.opaybot.ir";
  RecyclerView recyclerView;
  PayLogFeed feed;
  RSSFeed _feed;
  PayLogItem item;
  ChatPageAdapter adapter;
  static boolean isOpen = false;
  public RelativeLayout darkDialog;
  public GetCardFragment getCardFragment;
  public PaymentFragment paymentFragment;
  public RequestPaymentFragment requestPaymentFragment;
  public int fragment_status = 0;
  int newPos;

  public String description;
  public int amount;
  private int position;
  int pos;
  boolean success = false;
  boolean getStatus;
  int removePosition;
  static MessageHandler mHandler;

  private boolean loading = true;
  int pastVisiblesItems, visibleItemCount, totalItemCount;
  int load = 10;
  LinearLayoutManager manager;
  String date;

  int reversePosition;
  static Handler handler;

  PayLogItem logItem;
  EditText edtAmount;
  EditText edtComment;
  EditText edtCardNumber;
  EditText edtCardPassword;
  String mAmount;
  String mComment;
  private Dialog dialog;
  private Dialog cardDialog;
  private Dialog payDialog;
  private Dialog requestDialog;

  static {
    handler = new Handler(Looper.getMainLooper());
  }

  public static void runOnUI(Runnable runnable) {
    handler.post(runnable);
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_page_activity);
    MyFirebaseMessagingService.mode = 1;
//    _feed = new RSSFeed();
//        service.setMessageHandler(this);
    mHandler = this;
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
      _feed = (RSSFeed) bundle.getSerializable("contact");
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
//        FragmentTransaction ft;
//        darkDialog.setVisibility(View.VISIBLE);
//        paymentFragment = PaymentFragment.newInstance(false);
//        ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//        ft.add(android.R.id.content, paymentFragment).commit();
//        isOpen = false;

        cardDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
        cardDialog.setContentView(R.layout.group_payment_layout);
        edtAmount = (EditText) cardDialog.findViewById(R.id.payAmount);
        edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              edtAmount.setGravity(Gravity.LEFT);
              edtAmount.setHint("");
            } else {
              if (edtAmount.getText().length() == 0) {
                edtAmount.setGravity(Gravity.CENTER);
                edtAmount.setHint("مبلغ پرداختی");
              }
            }
          }
        });
        edtAmount.addTextChangedListener(new TextWatcher() {
          private static final char space = ',';

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

          }

          @Override
          public void afterTextChanged(Editable s) {
            if (s.length() == 0) {

              edtAmount.setGravity(Gravity.CENTER);
              edtAmount.setHint("مبلغ پرداختی");


            } else {
              edtAmount.setGravity(Gravity.LEFT);
              if (s.length() > 0 && (s.length() % 4) == 0) {
                final char c = s.charAt(s.length() - 3);
                if (space == c) {
                  s.delete(s.length() - 3, s.length() - 2);
                }
              }
//                    && TextUtils.split(s.toString(), String.valueOf(space)).length <= 5

              if (s.length() > 0 && (s.length() % 4) == 0) {
                char c = s.charAt(s.length() - 3);
                // Only if its a digit where there should be a space we insert a space
                Log.e("iffffffffff", "afterTextChanged: ");
                if (Character.isDigit(c)) {
                  s.insert(s.length() - 3, String.valueOf(space));
                }
              }


            }
          }
        });
        edtAmount.addTextChangedListener(new NumberTextWatcher(edtAmount));
        edtComment = (EditText) cardDialog.findViewById(R.id.comments);
        edtComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              edtComment.setGravity(Gravity.RIGHT);
              edtComment.setHint("");
            } else {
              if (edtComment.getText().length() == 0) {
                edtComment.setGravity(Gravity.CENTER);
                edtComment.setHint("مبلغ پرداختی");
              }
            }
          }
        });
        TextView contactDetail = (TextView) cardDialog.findViewById(R.id.texx);
        contactDetail.setText("پرداخت وجه به " + contactName);
        Button btnCancel = (Button) cardDialog.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            cardDialog.dismiss();
          }
        });
        Button btnConfirm = (Button) cardDialog.findViewById(R.id.confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (edtAmount.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (edtComment.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (!validate_number(edtAmount.getText().toString().replace(",", ""))) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
            } else {
              mAmount = edtAmount.getText().toString().replace(",", "");
              mComment = edtComment.getText().toString();
              hideKey(edtComment);
              payDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
              payDialog.setContentView(R.layout.group_card_layout);
              edtCardNumber = (EditText) payDialog.findViewById(R.id.payAmount);
              edtCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                  if (hasFocus) {
                    edtCardNumber.setGravity(Gravity.LEFT);
                    edtCardNumber.setHint("");
                  } else {
                    if (edtCardNumber.getText().length() == 0) {
                      edtCardNumber.setGravity(Gravity.CENTER);
                      edtCardNumber.setHint("شماره کارت");
                    }
                  }
                }
              });
              edtCardNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  edtCardNumber.setSelection(edtCardNumber.getText().length());
                }
              });
              edtCardPassword = (EditText) payDialog.findViewById(R.id.comments);
              edtCardPassword.setCursorVisible(false);
              edtCardPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                  if (hasFocus) {
                    edtCardPassword.setGravity(Gravity.LEFT);
                    edtCardPassword.setHint("");
                  } else {
                    if (edtCardPassword.getText().length() == 0) {
                      edtCardPassword.setGravity(Gravity.CENTER);
                      edtCardPassword.setHint("رمز دوم");
                    }
                  }
                }
              });

              edtCardPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                  if (s.length() == 0) {
                    edtCardPassword.setGravity(Gravity.CENTER);
                    edtCardPassword.setHint("رمز دوم");
                  } else {
                    edtCardPassword.setGravity(Gravity.LEFT);
                    edtCardPassword.setHint("");
                  }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
              });
              edtCardNumber.addTextChangedListener(new TextWatcher() {
                private static final char space = '-';

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                  if (s.length() == 19) {
                    edtCardPassword.requestFocus();
                  }
                  if (s.length() == 0) {

                    edtCardNumber.setGravity(Gravity.RIGHT);
                    edtCardNumber.setHint("شماره کارت");

                  } else {
                    edtCardNumber.setGravity(Gravity.LEFT);
                    if (s.length() > 0 && (s.length() % 5) == 0) {
                      final char c = s.charAt(s.length() - 1);
                      if (space == c) {
                        s.delete(s.length() - 1, s.length());
                      }
                    }
                    if (s.length() > 0 && (s.length() % 5) == 0) {
                      char c = s.charAt(s.length() - 1);
                      // Only if its a digit where there should be a space we insert a space
                      if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                      }
                    }
                  }
                }
              });
              Button btnCancel = (Button) payDialog.findViewById(R.id.cancel);
              btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  payDialog.dismiss();
                }
              });
              Button btnConfirm = (Button) payDialog.findViewById(R.id.confirm);
              btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  new ChatPageActivity.sendPaymentRequest().execute(phone
                    , edtCardNumber.getText().toString().replace("-", "") + edtCardPassword.getText().toString()
                    , mComment, mAmount);
                  payDialog.dismiss();
                }
              });
              payDialog.show();
              cardDialog.dismiss();
            }
          }
        });
        cardDialog.show();

      }
    });

    findViewById(R.id.btn_receive).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

//        Log.e("Receive", "onClick: ");
//        FragmentTransaction ft;
//        darkDialog.setVisibility(View.VISIBLE);
//        requestPaymentFragment = RequestPaymentFragment.newInstance();
//        ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//        ft.add(android.R.id.content, requestPaymentFragment).commit();
//        isOpen = false;

        dialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
        dialog.setContentView(R.layout.group_request_payment_layout);
        final EditText payAmount = (EditText) dialog.findViewById(R.id.payAmount);
        final EditText commment = (EditText) dialog.findViewById(R.id.comments);
        payAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              payAmount.setGravity(Gravity.LEFT);
              payAmount.setHint("");
            } else {
              if (payAmount.getText().length() == 0) {
                payAmount.setGravity(Gravity.CENTER);
                payAmount.setHint("مبلغ پرداختی");
              }
            }
          }
        });


        commment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              commment.setGravity(Gravity.RIGHT);
              commment.setHint("");
            } else {
              if (commment.getText().length() == 0) {
                commment.setGravity(Gravity.CENTER);
                commment.setHint("توضیحات");
              }
            }
          }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.confirm);
        payAmount.addTextChangedListener(new NumberTextWatcher(payAmount));
        btnCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (payAmount.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (commment.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (!validate_number(payAmount.getText().toString().replace(",", ""))) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
            } else {
              hideKey(commment);
              new ChatPageActivity.requestFromAnother().execute(phone, payAmount.getText().toString(), commment.getText().toString());
              dialog.dismiss();
            }
          }
        });
        dialog.show();

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
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
        Log.e("POS", pos + "");
        feed.addItem(result, 0);
        feed.getHash().put(result.getId(), 0);
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
        Log.e("********", "onPostExecute: " + result.getId());
        feed.getHash().put(result.getId(), 0);
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
      } else {
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();

      }
    }
  }

  private class accPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

    int pos;

    public accPaymentRequest(int pos) {
      this.pos = pos;
    }

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
        feed.removeItem(pos);
        adapter.notifyDataSetChanged();
        Log.e("POS", pos + "");
        feed.addItem(result, 0);
        adapter.notifyDataSetChanged();
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
        description = result.getComment();
        amount = result.getAmount();
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        success = true;
        Log.e("%%%%%%", "onPostExecute: accept" + success);
        if (success) {
          Log.e("sssssssss", "onPostExecute: " + pos);
//          feed.removeItem(pos);
//          adapter.notifyDataSetChanged();
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
    int pos;

    public DeletePaymentRequest(int pos) {
      this.pos = pos;
    }

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
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
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

  private class DeletePaymentRequestWithID extends AsyncTask<Integer, Boolean, Boolean> {
    int pos;

    public DeletePaymentRequestWithID(int pos) {
      Log.e("(((((((", "DeletePaymentRequestWithID: " + pos);
      this.pos = pos;
    }

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
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
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
      Log.e("TT", "getItemViewType: " + feed.getItem(position).getfMobile());
      if (feed.getItem(position).getfMobile().equals(phone)) {
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
      Log.e("EQ", "onBindViewHolder: " + feed.getItem(0).getId());
      pos = holder.getAdapterPosition();
      Log.e("************((", "onBindViewHolder: " + feed.getItem(holder.getAdapterPosition()).getId());
      if (holder.getItemViewType() == 0) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        //holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
      } else if (holder.getItemViewType() == 2) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));

        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            pos = holder.getAdapterPosition();
            Log.e("eeeeeeeee", "onClick: " + pos);
            new DeletePaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(pos).getId());
          }
        });
      } else if (holder.getItemViewType() == 3) {
        Log.e("id", "0000000000: " + pos);
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));

//        holder.txt_status.setText(feed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
//            darkDialog.setVisibility(View.VISIBLE);
//            pos = holder.getAdapterPosition();
//            Log.e("id", "1111111111111: " + pos);
//            getCardFragment = GetCardFragment.newInstance(feed.getItem(pos).getId());
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//            ft.add(android.R.id.content, getCardFragment).commit();
////            if (success) {
////              holder.btn_cancel.setVisibility(View.GONE);
////              holder.btn_accept.setVisibility(View.GONE);
////              holder.txt_status.setText("پرداخت شد");
////              adapter.notifyDataSetChanged();
////            }
////                        holder.removeAt(pos);
////                        adapter.notifyDataSetChanged();
//            removePosition = holder.getAdapterPosition();
            payDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
            payDialog.setContentView(R.layout.group_card_layout);
            edtCardNumber = (EditText) payDialog.findViewById(R.id.payAmount);
            edtCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
              @Override
              public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                  edtCardNumber.setGravity(Gravity.LEFT);
                  edtCardNumber.setHint("");
                } else {
                  if (edtCardNumber.getText().length() == 0) {
                    edtCardNumber.setGravity(Gravity.CENTER);
                    edtCardNumber.setHint("شماره کارت");
                  }
                }
              }
            });
            edtCardNumber.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                edtCardNumber.setSelection(edtCardNumber.getText().length());
              }
            });
            edtCardPassword = (EditText) payDialog.findViewById(R.id.comments);
            edtCardPassword.setCursorVisible(false);
            edtCardPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
              @Override
              public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                  edtCardPassword.setGravity(Gravity.LEFT);
                  edtCardPassword.setHint("");
                } else {
                  if (edtCardPassword.getText().length() == 0) {
                    edtCardPassword.setGravity(Gravity.CENTER);
                    edtCardPassword.setHint("رمز دوم");
                  }
                }
              }
            });

            edtCardPassword.addTextChangedListener(new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 0) {
                  edtCardPassword.setGravity(Gravity.CENTER);
                  edtCardPassword.setHint("رمز دوم");
                } else {
                  edtCardPassword.setGravity(Gravity.LEFT);
                  edtCardPassword.setHint("");
                }
              }

              @Override
              public void afterTextChanged(Editable s) {
              }
            });
            edtCardNumber.addTextChangedListener(new TextWatcher() {
              private static final char space = '-';

              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {
              }

              @Override
              public void afterTextChanged(Editable s) {
                if (s.length() == 19) {
                  edtCardPassword.requestFocus();
                }
                if (s.length() == 0) {

                  edtCardNumber.setGravity(Gravity.RIGHT);
                  edtCardNumber.setHint("شماره کارت");

                } else {
                  edtCardNumber.setGravity(Gravity.LEFT);
                  if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                      s.delete(s.length() - 1, s.length());
                    }
                  }
                  if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                      s.insert(s.length() - 1, String.valueOf(space));
                    }
                  }
                }
              }
            });
            Button btnCancel = (Button) payDialog.findViewById(R.id.cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                payDialog.dismiss();
              }
            });
            Button btnConfirm = (Button) payDialog.findViewById(R.id.confirm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                pos = holder.getAdapterPosition();
                Log.e("POOOOS", "onClick: " + holder.getAdapterPosition());
                new ChatPageActivity.accPaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(holder.getAdapterPosition()).getId() + "", edtCardNumber.getText().toString() + edtCardPassword.getText().toString());
                payDialog.dismiss();
              }
            });
            payDialog.show();
          }
        });
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            pos = holder.getAdapterPosition();
            Log.e("eeeeeeeee", "onClick: " + pos);
            new DeletePaymentRequest(pos).execute(feed.getItem(pos).getId());
          }
        });

      }
      if (holder.getItemViewType() == 4) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        // holder.txt_status.setText("پرداخت شد");
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
      }
      if (holder.getItemViewType() == 5) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        // holder.txt_status.setText("لغو شد");
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
      } else {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
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

  public static MessageHandler informNotif() {
    return mHandler;
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

//  public void accPay(int id, String detail) {
//
//    new accPaymentRequest().execute(id + "", detail);
//  }

  public void payBill(int amount, String comment) {
    darkDialog.setVisibility(View.VISIBLE);
    getCardFragment = GetCardFragment.newInstance(amount, comment);
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
    ft.add(android.R.id.content, getCardFragment).commit();
  }

//  @Override
//  public void onBackPressed() {
//    if (fragment_status == 1) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(paymentFragment).commit();
//    } else if (fragment_status == 2) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(getCardFragment).commit();
//    } else if (fragment_status == 3) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(requestPaymentFragment).commit();
//    } else if (description != null) {
//      Intent intent = new Intent(this, MainActivity.class);
//      intent.putExtra("description", description);
//      intent.putExtra("amount", amount);
//      intent.putExtra("pos", position);
//      setResult(10, intent);
//      finish();
//    } else
//      finish();
//  }


  @Override
  public void onBackPressed() {
//    new fillContact().execute("[]");
    Intent intent = new Intent(ChatPageActivity.this, MainActivity.class);
    intent.putExtra("contact", _feed);
    startActivity(intent);
    finish();
    super.onBackPressed();
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
          stringBuilder.insert(0, price.toString().substring(
            price.toString().length() - 2 - i, price.toString().length() - i) + ",");
        } catch (Exception e1) {
          stringBuilder.insert(0, price.toString().substring(price.toString().length() - 1 - i, price.toString().length() - i) + ",");
        }
      }

    }
    return stringBuilder.toString();
  }

  public static String tag() {
    return ChatPageActivity.class.getSimpleName();
  }

  @Override
  public void handleMessage(final PayLogItem logItem, final boolean deleteState, final String chatMemberMobile) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (deleteState) {
          Log.e("EQ2", "run: " + logItem.getCancelId());
          Log.e("EQ2", "run: " + feed.getHash());
          for (int i = 0; i < feed.getHash().size(); i++) {
//            Log.e("++++++++++", "run: " + feed.getItem(i).getId());
//            Log.e("***********", "run: " + feed.getHash().get(feed.getItem(i).getId()));
          }
          try {
            newPos = feed.getHash().get(logItem.getCancelId());
          } catch (Exception e) {
            e.printStackTrace();
          }
          new DeletePaymentRequestWithID(newPos).execute(logItem.getCancelId());
          adapter.notifyDataSetChanged();
        } else {
          Log.e("chatMemberMobile", "run: " + chatMemberMobile);
          Log.e("phone", "run: " + phone);
          Log.e("TTTT", "handleMessage pv: ");
          feed.getHash().put(logItem.getId(), 0);
          feed.addItem(logItem, 0);
          JSONArray array = new JSONArray();
          new fillContact().execute(array.toString());
          //Update
          adapter.notifyDataSetChanged();
        }
      }
    });
  }

  public void hideKey(View view) {
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private boolean validate_number(String number) {
    for (int i = 0; i < number.length(); i++) {
      if (number.charAt(i) != '0' && number.charAt(i) != '1' && number.charAt(i) != '2' && number.charAt(i) != '3'
        && number.charAt(i) != '4' && number.charAt(i) != '5' && number.charAt(i) != '6' && number.charAt(i) != '7'
        && number.charAt(i) != '8' && number.charAt(i) != '9' && number.charAt(i) != '-') {
        return false;
      }
    }
    return true;
  }

  public class fillContact extends AsyncTask<String, Void, RSSFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected RSSFeed doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.checkContactListWithGroup(params[0]);
    }

    @Override
    protected void onPostExecute(RSSFeed result) {
      if (result != null) {
        _feed = result;
      } else {
        Toast.makeText(ChatPageActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }
}
