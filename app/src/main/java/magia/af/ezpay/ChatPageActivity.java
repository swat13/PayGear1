package magia.af.ezpay;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsIntent;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import magia.af.ezpay.Firebase.MessagingService;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.DialogMaker;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.CalendarConversion;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.NumberTextWatcher;
import magia.af.ezpay.interfaces.MessageHandler;

/*dd*/
public class ChatPageActivity extends BaseActivity implements MessageHandler {
  private String phone;
  public String contactName;
  private String imageUrl = "http://new.opaybot.ir";
  RecyclerView recyclerView;
  PayLogFeed feed;
  ChatListFeed _ChatList_feed;
  PayLogItem item;
  ChatPageAdapter adapter;
  static boolean isOpen = false;
  public RelativeLayout darkDialog;
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

  PayLogItem payLogItem;
  EditText edtAmount;
  EditText edtComment;
  EditText edtCardNumber;
  EditText edtCardPassword;
  String mAmount;
  String mComment;
  private Dialog dialog;
  private Dialog firstDialog;
  private Dialog cardDialog;
  private Dialog payDialog;
  private Dialog requestDialog;
  private String selfNumber;
  HashMap<Integer, Integer> hashMap = new HashMap<>();

  private ProgressBar loadingProgressBar;

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
    MessagingService.mode = 1;
//    _ChatList_feed = new ChatListFeed();
//        service.setMessageHandler(this);
    mHandler = this;
    Bundle bundle = getIntent().getExtras();
    loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
    if (bundle != null) {
      phone = bundle.getString("phone");
      position = bundle.getInt("pos");
      contactName = bundle.getString("contactName");
      imageUrl = imageUrl + bundle.getString("image");
      _ChatList_feed = (ChatListFeed) bundle.getSerializable("contact");
    }
    selfNumber = getSharedPreferences("EZpay", 0).getString("mobile", "");
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
    ContactDatabase database = new ContactDatabase(this);
    name.setText(database.getNameFromNumber(phone));
    if (name.getText().toString().length() == 0 || name.getText().toString().isEmpty()) {
      name.setText(contactName);
    }
    recyclerView = (RecyclerView) findViewById(R.id.pay_list_recycler);
    darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
    manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    manager.setReverseLayout(true);
    manager.setStackFromEnd(false);
    recyclerView.setLayoutManager(manager);


        /*if (new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List") != null) {
            chatListFeed = (PayLogFeed) new LocalPersistence().readObjectFromFile(ChatPageActivity.this, "Payment_Chat_List");
            adapter = new ChatPageAdapter();
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }*/
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    String maxDate = df.format(Calendar.getInstance().getTime());
    new getChatLog().execute(phone, date, "100");

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy == -10) //check for scroll down
        {
//          visibleItemCount = manager.getChildCount();
//          Log.e("ddddd", "" + visibleItemCount);
//          totalItemCount = manager.getItemCount();
//          Log.e("ggggg", "" + totalItemCount);
//          pastVisiblesItems = manager.findFirstVisibleItemPosition();
//          Log.e("hhhhh", "" + pastVisiblesItems);
//          new getChatLog().execute(phone, date, String.valueOf(load));
//          load += 10;
//          DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//          String maxDate = df.format(Calendar.getInstance().getTime());

//          if (loading) {
//            if ((visibleItemCount - pastVisiblesItems) >= 1) {
//              loading = false;
//              Log.e("...", "Last ChatListItem Wow !");
//            }
//          }
//          return;
        }
      }
    });

    findViewById(R.id.btn_pey).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        cardDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
        cardDialog.setContentView(R.layout.group_payment_layout);
        edtAmount = (EditText) cardDialog.findViewById(R.id.payAmount);
        edtAmount.requestFocus();
        edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              edtAmount.setHint("");
              edtAmount.setGravity(Gravity.LEFT);


            } else if (edtAmount.getText().length() == 0) {

              edtAmount.setGravity(Gravity.CENTER);
              edtAmount.setHint("مبلغ پرداختی");


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
            if (edtAmount.getText().length() == 0) {

              edtAmount.setGravity(Gravity.CENTER);
              edtAmount.setHint("مبلغ پرداختی");

            } else {
              edtAmount.setHint("");
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
                edtComment.setHint("توضیحات");
              }
            }
          }
        });
        edtComment.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (edtComment.getText().length() == 0) {
              edtComment.setGravity(Gravity.CENTER);
              edtComment.setHint("توضیحات");
            } else {
              edtComment.setGravity(Gravity.RIGHT);
              edtComment.setHint("");
            }

          }

          @Override
          public void afterTextChanged(Editable s) {

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
            mAmount = edtAmount.getText().toString().replace(",", "");
            mComment = edtComment.getText().toString();
            if (edtAmount.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (edtComment.getText().toString().length() == 0) {
              Toast.makeText(ChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (!validate_number(edtAmount.getText().toString().replace(",", ""))) {
              Toast.makeText(ChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
            } else if (edtAmount.getText().toString().length() < 4 && Integer.parseInt(edtAmount.getText().toString()) < 1000) {
              Toast.makeText(ChatPageActivity.this, "حداقل مبلغ باید 1000 ریال باشد", Toast.LENGTH_SHORT).show();
            } else if (Long.valueOf(mAmount) > getSharedPreferences("EZpay", 0).getInt("amount", 0)) {
              mAmount = edtAmount.getText().toString().replace(",", "");
              mComment = edtComment.getText().toString();
              firstDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
              firstDialog.setContentView(R.layout.choose_payment_dialog);
              Button btnPayWithCard = (Button) firstDialog.findViewById(R.id.btn_payWithCard);
              Button btnPayWithCredit = (Button) firstDialog.findViewById(R.id.btnPayWithCredit);
              btnPayWithCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  JSONParser parser = JSONParser.connect(Constant.PAY_TO_ANOTHER_WITH_IPG);
                  parser.setRequestMethod(JSONParser.POST);
                  parser.setReadTimeOut(20000);
                  parser.setConnectionTimeOut(20000);
                  parser.setAuthorization(Constant.token(ChatPageActivity.this));
                  JSONObject object = new JSONObject();
                  try {
                    object.put("anotherMobile", phone);
                    object.put("amount", mAmount);
                    object.put("comment", mComment);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  parser.setJson(object.toString());
                  parser.execute(new JSONParser.Execute() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(String s) {
                      if (s != null) {
                        DialogMaker.disMissDialog();
                        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                        intent.launchUrl(ChatPageActivity.this, Uri.parse(Constant.IPG_URL + s.replace("\"", "")));
                      }
                    }
                  });
                }
              });
              btnPayWithCredit.setTextColor(Color.GRAY);
              btnPayWithCredit.setClickable(false);
              firstDialog.show();
              cardDialog.dismiss();
            } else {
              mAmount = edtAmount.getText().toString().replace(",", "");
              mComment = edtComment.getText().toString();
              hideKey(edtComment);
              firstDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
              firstDialog.setContentView(R.layout.choose_payment_dialog);
              Button btnPayWithCard = (Button) firstDialog.findViewById(R.id.btn_payWithCard);
              Button btnPayWithCredit = (Button) firstDialog.findViewById(R.id.btnPayWithCredit);
              btnPayWithCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  JSONParser parser = JSONParser.connect(Constant.PAY_TO_ANOTHER_WITH_IPG);
                  parser.setRequestMethod(JSONParser.POST);
                  parser.setReadTimeOut(20000);
                  parser.setConnectionTimeOut(20000);
                  parser.setAuthorization(Constant.token(ChatPageActivity.this));
                  JSONObject object = new JSONObject();
                  try {
                    object.put("anotherMobile", phone);
                    object.put("amount", mAmount);
                    object.put("comment", mComment);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  parser.setJson(object.toString());
                  parser.execute(new JSONParser.Execute() {
                    @Override
                    public void onPreExecute() {
                      DialogMaker.makeDialog(ChatPageActivity.this).showDialog();
                    }

                    @Override
                    public void onPostExecute(String s) {
                      if (s != null) {
                        DialogMaker.disMissDialog();
                        firstDialog.dismiss();
                        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                        intent.launchUrl(ChatPageActivity.this, Uri.parse(Constant.IPG_URL + s.replace("\"", "")));
                      }
                    }
                  });
                }
              });
              btnPayWithCredit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  firstDialog.dismiss();
                  final Dialog getPassword = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
                  getPassword.setContentView(R.layout.get_password_dialog);
                  final EditText edtGetPassWord = (EditText) getPassword.findViewById(R.id.edtGetPassword);
                  Button btnConfirm = (Button) getPassword.findViewById(R.id.btnConfirm);
                  btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (edtGetPassWord.getText().toString().equals(getSharedPreferences("password", 0).getString("password", ""))) {
                        new PayToAnotherWithCredit().execute(phone, mAmount, mComment);
                        getPassword.dismiss();
                      }
                      else {
                        Toast.makeText(ChatPageActivity.this, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                  getPassword.show();
                }
              });
              firstDialog.show();
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
//        request = Request.newInstance();
//        ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//        ft.add(android.R.id.content, request).commit();
//        isOpen = false;

        dialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
        dialog.setContentView(R.layout.group_request_payment_layout);
        final EditText payAmount = (EditText) dialog.findViewById(R.id.payAmount);
        final EditText commment = (EditText) dialog.findViewById(R.id.comments);
        payAmount.requestFocus();
        payAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {

              payAmount.setGravity(Gravity.LEFT);
              payAmount.setHint("");

            } else if (payAmount.getText().length() == 0) {
              payAmount.setGravity(Gravity.CENTER);
              payAmount.setHint("مبلغ پرداختی");
            }

          }
        });

        payAmount.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (payAmount.getText().length() == 0) {
              payAmount.setGravity(Gravity.CENTER);
              payAmount.setHint("مبلغ پرداختی");
            } else {

              payAmount.setGravity(Gravity.LEFT);
              payAmount.setHint("");

            }


          }

          @Override
          public void afterTextChanged(Editable s) {

          }
        });


        commment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
              commment.setGravity(Gravity.RIGHT);
              commment.setHint("");
            } else if (commment.getText().length() == 0) {
              commment.setGravity(Gravity.CENTER);
              commment.setHint("توضیحات");

            }
          }
        });

        commment.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (commment.getText().length() == 0) {
              commment.setGravity(Gravity.CENTER);
              commment.setHint("توضیحات");
            } else {
              commment.setGravity(Gravity.RIGHT);
              commment.setHint("");

            }

          }

          @Override
          public void afterTextChanged(Editable s) {

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

//    @Override
//    public void handleMessage(PayLogItem logItem, boolean deleteState, String chatMemberMobile) {
//
//    }


  private class sendPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogItem doInBackground(String... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.sendPaymentRequest(params[0], params[1], params[2], params[3]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {

        hashMap.put(result.getId(), 0);
        new fillContact().execute("[]");
        feed.addItem(result, 0);
//        adapter.notifyItemRangeChanged(pos,chatListFeed.getItemCount());
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        ApplicationData.getCostAccountCredit(result.getAmount(), ChatPageActivity.this);
        adapter.notifyDataSetChanged();
      } else
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
    }
  }

  private class PayToAnotherWithCredit extends AsyncTask<String, Void, PayLogItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      DialogMaker.makeDialog(ChatPageActivity.this).showDialog();
    }

    @Override
    protected PayLogItem doInBackground(String... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.payToAnotherWithCredit(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        DialogMaker.disMissDialog();
        hashMap.put(result.getId(), 0);
        new fillContact().execute("[]");
        feed.addItem(result, 0);
        new LocalPersistence().writeObjectToFile(ChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
        ApplicationData.getCostAccountCredit(result.getAmount(), ChatPageActivity.this);
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.RequestFromAnother(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
//        chatListFeed.removeItem(pos);
        hashMap.put(result.getId(), 0);
        feed.addItem(result, 0);
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
//        adapter.notifyItemRangeChanged(pos,chatListFeed.getItemCount());
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.accPaymentRequest(params[0], params[1]);

    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.removeItem(pos);
        adapter.notifyDataSetChanged();
        feed.addItem(result, 0);
      } else {
        success = false;
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private class AcceptPaymentRequest extends AsyncTask<Integer, Void, PayLogItem> {

    int pos;

    public AcceptPaymentRequest(int pos) {
      this.pos = pos;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogItem doInBackground(Integer... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.acceptPaymentWithCredit(params[0]);

    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.removeItem(pos);
        adapter.notifyDataSetChanged();
        feed.addItem(result, 0);
        new fillContact().execute("[]");
        ApplicationData.getCostAccountCredit(result.getAmount(), ChatPageActivity.this);
      } else {
        success = false;
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.deletePayment(params[0]);

    }

    @Override
    protected void onPostExecute(Boolean result) {
      if (result != null) {
        JSONArray array = new JSONArray();
        new fillContact().execute(array.toString());
        if (result) {
          feed.getItem(pos).setStatus(true);
          adapter.notifyDataSetChanged();
        }
      } else {
        Toast.makeText(ChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private class DeletePaymentRequestWithID extends AsyncTask<Integer, Void, Boolean> {

    int pos;

    public DeletePaymentRequestWithID(int pos) {
      this.pos = pos;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.deletePayment(params[0]);

    }

    @Override
    protected void onPostExecute(Boolean result) {
      if (result) {
        feed.getItem(pos).setStatus(true);
        adapter.notifyDataSetChanged();

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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      try {
        payLogFeed = parser.payLogWithAnother(params[0], params[1], params[2]);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return payLogFeed;
    }

    @Override
    protected void onPostExecute(PayLogFeed result) {
      if (result != null) {
        loadingProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        feed = result;
        for (int i = 0; i < feed.getItemCount(); i++) {
          Log.e("ID", "onPostExecute: " + feed.getItem(i).getId() + "   ==>   " + i);
          hashMap.put(feed.getItem(i).getId(), i);
        }
        for (int i = 0; i < hashMap.size(); i++) {
          Log.e("ID", "*********: " + feed.getItem(i).getId() + "   ==>   " + hashMap.get(feed.getItem(i).getId()));
        }
        adapter = new ChatPageAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

//      if (chatListFeed.getItem(pos).getFrom().equals(phone)) {
//        if (chatListFeed.getItem(pos).isPaideBool()) {
//          if (chatListFeed.getItem(pos).isStatus()) {
//            return 4;
//          }
//          return 0;
//        } else
//          return 2;
//      } else {
//        if (chatListFeed.getItem(pos).isPaideBool()) {
//          if (!chatListFeed.getItem(pos).isStatus()) {
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
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        //holder.txt_status.setText(chatListFeed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
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
            new DeletePaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(pos).getId());
          }
        });
      } else if (holder.getItemViewType() == 3) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));

//        holder.txt_status.setText(chatListFeed.getItem(pos).isPaideBool() ? "پرداخت شد" : "پرداخت نشد");
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (feed.getItem(holder.getAdapterPosition()).getAmount() > getSharedPreferences("EZpay", 0).getInt("amount", 0)) {
              firstDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
              firstDialog.setContentView(R.layout.choose_payment_dialog);
              Button btnPayWithCard = (Button) firstDialog.findViewById(R.id.btn_payWithCard);
              Button btnPayWithCredit = (Button) firstDialog.findViewById(R.id.btnPayWithCredit);
              btnPayWithCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  JSONParser parser = JSONParser.connect(Constant.PAY_TO_ANOTHER_WITH_IPG);
                  parser.setRequestMethod(JSONParser.POST);
                  parser.setReadTimeOut(20000);
                  parser.setConnectionTimeOut(20000);
                  parser.setAuthorization(Constant.token(ChatPageActivity.this));
                  JSONObject object = new JSONObject();
                  try {
                    object.put("anotherMobile", phone);
                    object.put("amount", feed.getItem(holder.getAdapterPosition()).getAmount());
                    object.put("comment", feed.getItem(holder.getAdapterPosition()).getComment());
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  parser.setJson(object.toString());
                  parser.execute(new JSONParser.Execute() {

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(String s) {
                      if (s != null) {
                        firstDialog.dismiss();
                        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                        intent.launchUrl(ChatPageActivity.this, Uri.parse(Constant.IPG_URL + s.replace("\"", "")));
                      }
//
//                             Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.IPG_URL + s.replace("\"","")));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setPackage("com.android.chrome");
//                            try {
//                              startActivity(intent);
//                            } catch (ActivityNotFoundException ex) {
//                              // Chrome browser presumably not installed so allow user to choose instead
//                              intent.setPackage(null);
//                              startActivity(intent);
//                            }
//                          }
                    }
                  });
                }
              });
              btnPayWithCredit.setClickable(false);
              btnPayWithCredit.setTextColor(Color.GRAY);
              firstDialog.show();
            } else {
              firstDialog = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
              firstDialog.setContentView(R.layout.choose_payment_dialog);
              Button btnPayWithCard = (Button) firstDialog.findViewById(R.id.btn_payWithCard);
              Button btnPayWithCredit = (Button) firstDialog.findViewById(R.id.btnPayWithCredit);
              btnPayWithCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  JSONParser parser = JSONParser.connect(Constant.PAY_TO_ANOTHER_WITH_IPG);
                  parser.setRequestMethod(JSONParser.POST);
                  parser.setReadTimeOut(20000);
                  parser.setConnectionTimeOut(20000);
                  parser.setAuthorization(Constant.token(ChatPageActivity.this));
                  JSONObject object = new JSONObject();
                  try {
                    object.put("anotherMobile", phone);
                    object.put("amount", feed.getItem(holder.getAdapterPosition()).getAmount());
                    object.put("comment", feed.getItem(holder.getAdapterPosition()).getComment());
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  parser.setJson(object.toString());
                  parser.execute(new JSONParser.Execute() {
                    @Override
                    public void onPreExecute() {

                    }


                    @Override
                    public void onPostExecute(String s) {
                      if (s != null) {
                        firstDialog.dismiss();
                        DialogMaker.disMissDialog();
                        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                        intent.launchUrl(ChatPageActivity.this, Uri.parse(Constant.IPG_URL + s.replace("\"", "")));
                      }
//
//                             Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.IPG_URL + s.replace("\"","")));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setPackage("com.android.chrome");
//                            try {
//                              startActivity(intent);
//                            } catch (ActivityNotFoundException ex) {
//                              // Chrome browser presumably not installed so allow user to choose instead
//                              intent.setPackage(null);
//                              startActivity(intent);
//                            }
//                          }
                    }
                  });
                }
              });
              btnPayWithCredit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  firstDialog.dismiss();
                  final Dialog getPassword = new Dialog(ChatPageActivity.this, R.style.PauseDialog);
                  getPassword.setContentView(R.layout.get_password_dialog);
                  final EditText edtGetPassWord = (EditText) getPassword.findViewById(R.id.edtGetPassword);
                  Button btnConfirm = (Button) getPassword.findViewById(R.id.btnConfirm);
                  btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (edtGetPassWord.getText().toString().equals(getSharedPreferences("password", 0).getString("password", ""))) {
                        new AcceptPaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(holder.getAdapterPosition()).getId());
                        getPassword.dismiss();
                      }
                      else {
                        Toast.makeText(ChatPageActivity.this, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
                      }
                    }
                  });
                  firstDialog.cancel();
                  getPassword.show();
                }
              });
              firstDialog.show();
            }
          }
        });
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            pos = holder.getAdapterPosition();
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

//    public void payBill(int amount, String comment) {
//        darkDialog.setVisibility(View.VISIBLE);
//        getCard = GetCard.newInstance(amount, comment);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//        ft.add(android.R.id.content, getCard).commit();
//    }

//  @Override
//  public void onBackPressed() {
//    if (fragment_status == 1) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(payment).commit();
//    } else if (fragment_status == 2) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(getCard).commit();
//    } else if (fragment_status == 3) {
//      getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(request).commit();
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
    MessagingService.mode = 3;
    super.onBackPressed();
    finish();
  }

  private String getDividedToman(Long price) {
    if (price == 0) {
      return price + "";
    } else if (price < 1000) {
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
  public void handleMessage(final PayLogItem payLogItem, final boolean deleteState, final String chatMemberMobile) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.e("TAG Run", "run: " + payLogItem.getId());
        Log.e("TAG Run", "run: " + payLogItem.getCancelId());
        Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getId()));
        Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getCancelId()));
        if (deleteState) {
          for (int i = 0; i < hashMap.size(); i++) {
            Log.e("ID", "*********: " + feed.getItem(i).getId() + "   ==>   " + hashMap.get(feed.getItem(i).getId()));
          }
          try {
            newPos = hashMap.get(payLogItem.getCancelId());
          } catch (Exception e) {
            e.printStackTrace();
          }
          new DeletePaymentRequestWithID(newPos).execute(payLogItem.getCancelId());
          adapter.notifyDataSetChanged();

        } else if (payLogItem.getfMobile().equals(selfNumber) && payLogItem.getCancelId() != 0) {
          Log.e("TEST 1", "run: ");
          feed.addItem(payLogItem, 0);
          adapter.notifyDataSetChanged();
          hashMap.put(payLogItem.getId(), 0);
          Log.e("TAG Run", "run: " + payLogItem.getId());
          Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getId()));
          Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getCancelId()));
        } else if (payLogItem.gettMobile().equals(phone)) {
          Log.e("TEST 2", "run: ");
          feed.addItem(payLogItem, 0);
          adapter.notifyDataSetChanged();
          hashMap = shiftHashMap(payLogItem.getId());
          Log.e("TAG Run", "run: " + payLogItem.getId());
          Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getId()));
          Log.e("TAG Run", "run: " + hashMap.get(payLogItem.getCancelId()));
          for (int i = 0; i < hashMap.size(); i++) {
            Log.e("ID", "*********: " + feed.getItem(i).getId() + "   ==>   " + hashMap.get(feed.getItem(i).getId()));
          }
        } else if (payLogItem.getfMobile().equals(phone) && payLogItem.getCancelId() != 0) {
          Log.e("TEST 3", "run: ");
          feed.addItem(payLogItem, 0);
          adapter.notifyDataSetChanged();
          hashMap.put(payLogItem.getId(), 0);
        } else if (payLogItem.gettMobile().equals(selfNumber) && payLogItem.getfMobile().equals(phone) && payLogItem.isPaideBool()) {
          Log.e("TEST 4", "run: ");
          feed.set(hashMap.get(payLogItem.getId()), payLogItem);
          adapter.notifyDataSetChanged();
          hashMap.put(payLogItem.getId(), 0);
        }
      }
    });
  }

  @Override
  public void handleMessageGp(String body, String chatMemberMobile) {

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

  public class fillContact extends AsyncTask<String, Void, ChatListFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected ChatListFeed doInBackground(String... params) {
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.checkContactListWithGroup(params[0]);
    }

    @Override
    protected void onPostExecute(ChatListFeed result) {
      if (result != null) {
        _ChatList_feed = result;
        ChatListFeed feed = ApplicationData.getOutNetworkContact();
        for (int i = 0; i < feed.getItemCount(); i++) {
          for (int j = 0; j < result.getItemCount(); j++) {
            if (feed.getItem(i).getTelNo().equals(result.getItem(j).getTelNo())) {
              feed.removeItem(i);
            }
          }
        }
        result.addAll(result.getItemCount(), feed);
        ApplicationData.setChatListFeed(result);
        adapter.notifyDataSetChanged();

      } else {
        Toast.makeText(ChatPageActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }

  public HashMap<Integer, Integer> shiftHashMap(int key) {
    HashMap<Integer, Integer> temp = new HashMap<>();
//    temp.put(key, 0);
    for (int i = 0; i < feed.getItemCount(); i++) {
      temp.put(feed.getItem(i).getId(), i);
    }
    return temp;
  }


}
