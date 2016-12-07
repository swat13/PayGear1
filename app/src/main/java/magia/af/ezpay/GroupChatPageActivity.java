package magia.af.ezpay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

import magia.af.ezpay.Firebase.MyFirebaseMessagingService;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.CalendarConversion;
import magia.af.ezpay.helper.NumberTextWatcher;
import magia.af.ezpay.interfaces.MessageHandler;

public class GroupChatPageActivity extends BaseActivity implements MessageHandler {

  private int groupId;
  private String groupTitle;
  private String groupPhoto;
  private String phone;
  private ImageView groupImage;
  private TextView txtGroupTitle;
  private ArrayList<RSSItem> members;
  private RecyclerView payListRecycler;
  private Dialog dialog;
  private Dialog cardDialog;
  private Dialog payDialog;
  private Dialog requestDialog;
  PayLogFeed feed;
  GroupChatPageAdapter adapter;
  public int amount;
  private int position;
  int pos;
  String anotherMobile;
  boolean lastChecked;
  int lastCheckedPos;
  EditText edtAmount;
  EditText edtComment;
  EditText edtCardNumber;
  EditText edtCardPassword;
  String mAmount;
  String mComment;
  String mCardNumber;
  String mCardPassword;
  static MessageHandler mHandler;
  RSSItem rssItem;
  PayLogItem logItem;
  ArrayList<RSSItem> groupMembers;
  int newPos;
  public static final String TAG = GroupChatPageActivity.class.getSimpleName();
  private String date;

  private String lastChatAmount;
  private String comment;
  private boolean payState;
  private boolean requestState;
  private boolean cancelState;
  private String selfPhoneNumber;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat_page);
    MyFirebaseMessagingService.mode = 2;
    feed = new PayLogFeed();
    mHandler = this;
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      groupId = bundle.getInt("id");
      groupPhoto = bundle.getString("photo");
      groupTitle = bundle.getString("title");
      groupMembers = (ArrayList<RSSItem>) bundle.getSerializable("members");
    }
    Log.e(TAG, "onCreate: " + groupId);
    Log.e(TAG, "onCreate: " + groupTitle);
    phone = getSharedPreferences("EZpay", 0).getString("phoneNumber", "");

    date = "2050-01-01T00:00:00.000";

    members = (ArrayList<RSSItem>) getIntent().getSerializableExtra("members");
    for (int i = 0; i < members.size(); i++) {
      Log.e(TAG, "onCreate: " + members.get(i).getGroupMemberPhone());
      Log.e(TAG, "onCreate: " + members.get(i).getGroupMemberTitle());
      if (members.get(i).getGroupMemberPhone().equals(phone)) {
        members.remove(i);
      }
    }

    groupImage = (ImageView) findViewById(R.id.groupImage);
    groupImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(GroupChatPageActivity.this, GroupDetails.class);
        intent.putExtra("id", groupId);
        intent.putExtra("photo", groupPhoto);
        intent.putExtra("title", groupTitle);
        intent.putExtra("members", groupMembers);
        startActivity(intent);
      }
    });
    setGroupPhoto();
    txtGroupTitle = (TextView) findViewById(R.id.groupTitle);
    txtGroupTitle.setText(groupTitle);

    ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    adapter = new GroupChatPageAdapter();
    payListRecycler = (RecyclerView) findViewById(R.id.pay_list_recycler);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    manager.setReverseLayout(true);
    payListRecycler.setLayoutManager(manager);
    payListRecycler.setAdapter(adapter);

    new getChatLog(groupId, 100, date).execute();
    findViewById(R.id.btn_pey).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
        dialog.setContentView(R.layout.choose_contact_dialog);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.choose_contact_recycler);
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.confirm);
        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        GroupChatPageActivity.RecyclerAdapter adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        btnCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            cardDialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
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
            contactDetail.setText("پرداخت وجه به " + rssItem.getGroupMemberTitle());
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
                  Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (edtComment.getText().toString().length() == 0) {
                  Toast.makeText(GroupChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (!validate_number(edtAmount.getText().toString().replace(",", ""))) {
                  Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
                } else {
                  mAmount = edtAmount.getText().toString().replace(",", "");
                  mComment = edtComment.getText().toString();
                  hideKey(edtComment);
                  payDialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
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
                      Log.e(TAG, "onClick1: " + anotherMobile);
                      Log.e(TAG, "onClick2: " + edtCardNumber.getText().toString() + edtCardPassword.getText().toString());
                      Log.e(TAG, "onClick3: " + mComment);
                      Log.e(TAG, "onClick4: " + mAmount);
                      new sendPaymentRequest().execute(anotherMobile
                        , edtCardNumber.getText().toString().replace("-", "") + edtCardPassword.getText().toString()
                        , mComment, mAmount, String.valueOf(groupId));
                      payDialog.dismiss();
                    }
                  });
                  payDialog.show();
                  cardDialog.dismiss();
                }
              }
            });
            cardDialog.show();
            dialog.dismiss();
          }
        });
        dialog.show();
      }
    });

    findViewById(R.id.btn_receive).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
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
              Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (commment.getText().toString().length() == 0) {
              Toast.makeText(GroupChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
            } else if (!validate_number(payAmount.getText().toString().replace(",", ""))) {
              Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
            } else {
              hideKey(commment);
              new requestFromAnother(phone, payAmount.getText().toString().replace(",", ""), commment.getText().toString(), groupId).execute();
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

  public void setGroupPhoto() {
    Glide.with(this)
      .load(groupPhoto)
      .asBitmap()
      .centerCrop()
      .placeholder(R.drawable.pic_profile)
      .into(new BitmapImageViewTarget(groupImage) {
        @Override
        protected void setResource(Bitmap resource) {
          RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
          circularBitmapDrawable.setCornerRadius(700);
          groupImage.setImageDrawable(circularBitmapDrawable);
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
      Log.e("POOOOS", "onBindViewHolder: " + position);
      Log.e("Saeid TEST", "onBindViewHolder: " + members.get(position).getGroupMemberTitle());
      holder.txt_contact_item_name.setText(members.get(position).getGroupMemberTitle());
      holder.txt_contact_item_phone.setText(members.get(position).getGroupMemberPhone());
      String imageUrl = "http://new.opaybot.ir";
      Glide.with(GroupChatPageActivity.this)
        .load(imageUrl + members.get(position).getContactImg())
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
      return members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemClickListener {
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
          anotherMobile = members.get(getAdapterPosition()).getGroupMemberPhone();
          rssItem = members.get(getAdapterPosition());
        } else {
          status_circle.setVisibility(View.GONE);
        }
      }

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < parent.getCount(); i++) {

          View v = parent.getChildAt(i);
          ImageView imageView = (ImageView) v.findViewById(R.id.status_circle);
          imageView.setVisibility(View.GONE);

        }

        ImageView imageView = (ImageView) view.findViewById(R.id.status_circle);
        imageView.setVisibility(View.VISIBLE);

      }
    }
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

  private int correctNum(String number) {
    String result = number;
    for (int i = 0; i < number.length(); i++) {
      if (number.charAt(i) == ',') {
        result = number.substring(0, i) + number.substring(i + 1);
      }
    }
    return Integer.valueOf(number.replace(",", ""));
  }

  private class requestFromAnother extends AsyncTask<String, Void, PayLogItem> {
    String phone;
    String amount;
    String comment;
    int groupId;

    public requestFromAnother(String phone, String amount, String comment, int groupId) {
      this.phone = phone;
      this.amount = amount;
      this.comment = comment;
      this.groupId = groupId;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogItem doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.groupRequestFromAnother(phone, amount, comment, groupId);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.addItem(result, 0);
        lastChatAmount = getDividedToman((long) result.getAmount());
        comment = result.getComment();
        payState = true;
        selfPhoneNumber = result.getfMobile();
        new LocalPersistence().writeObjectToFile(GroupChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public class GroupChatPageAdapter extends RecyclerView.Adapter<GroupChatPageAdapter.ViewHolder> {
    public GroupChatPageAdapter() {

    }

    @Override
    public int getItemViewType(int position) {
      int viewType = 8;

      if (feed.getItem(position).getfMobile().equals(phone)) {
        if (feed.getItem(position).isStatus()) {
          Log.e(TAG, "getItemViewType: " + "state2");
          viewType = 0;//cancel from
        } else if (feed.getItem(position).isPaideBool()) {
          Log.e(TAG, "getItemViewType: " + "state3");
          viewType = 1;//payed from
        } else {
          Log.e(TAG, "getItemViewType: " + "state4");
          viewType = 2;//request to
        }
      } else if (feed.getItem(position).getfMobile().equals(feed.getItem(position).gettMobile())) {
        if (feed.getItem(position).isStatus()) {
          Log.e(TAG, "getItemViewType: " + "state6");
          viewType = 3;//cancel from other
        } else if (feed.getItem(position).isPaideBool()) {
          Log.e(TAG, "getItemViewType: " + "state7");
          viewType = 0;// Payed From Another which is impossible
        } else {
          Log.e(TAG, "getItemViewType: " + "state8");
          viewType = 5;//request from other
        }
      } else if (feed.getItem(position).gettMobile().equals(phone)) {
        if (feed.getItem(position).isPaideBool()) {
          Log.e(TAG, "getItemViewType: " + "state10");
          viewType = 4; //payed to from
        }
      } else {
        if (feed.getItem(position).isPaideBool()) {
          Log.e(TAG, "getItemViewType: " + "state11");
          viewType = 6; //done
        }
      }
      return viewType;
    }

    @Override
    public GroupChatPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView = null;
      if (viewType == 0) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_from, parent, false);
      } else if (viewType == 1) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_from, parent, false);
      } else if (viewType == 2) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_from, parent, false);
      } else if (viewType == 3) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_cancel_to, parent, false);
      } else if (viewType == 4) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_to, parent, false);
      } else if (viewType == 5) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_to, parent, false);
      } else if (viewType == 6) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_payed_to, parent, false);
      }
      return new GroupChatPageAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final GroupChatPageAdapter.ViewHolder holder, final int position) {
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
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
//        holder.payerName.setText(feed.getItem(pos).gettTitle());
//        Glide.with(GroupChatPageActivity.this)
//          .load(feed.getItem(pos).gettPhoto())
//          .asBitmap()
//          .centerCrop()
//          .placeholder(R.drawable.pic_profile)
//          .into(new BitmapImageViewTarget(holder.payToUserImage) {
//            @Override
//            protected void setResource(Bitmap resource) {
//              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
//              circularBitmapDrawable.setCornerRadius(700);
//              this.view.setImageDrawable(circularBitmapDrawable);
//            }
//          });
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
      } else if (holder.getItemViewType() == 1) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.payerName.setText(feed.getItem(pos).gettTitle());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.payToUserImage) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
            }
          });
      } else if (holder.getItemViewType() == 2) {
        Log.e("id", "0000000000: " + pos);
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
            new DeletePaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(holder.getAdapterPosition()).getId());
          }
        });
//        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
////            darkDialog.setVisibility(View.VISIBLE);
//            pos = holder.getAdapterPosition();
//            Log.e("id", "1111111111111: " + pos);
////            getCardFragment = GetCardFragment.newInstance(feed.getItem(pos).getId());
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
////            ft.add(android.R.id.content, getCardFragment).commit();
////            if (success) {
////              holder.btn_cancel.setVisibility(View.GONE);
////              holder.btn_accept.setVisibility(View.GONE);
////              holder.txt_status.setText("پرداخت شد");
////              adapter.notifyDataSetChanged();
////            }
////                        holder.removeAt(pos);
////                        adapter.notifyDataSetChanged();
////            removePosition = holder.getAdapterPosition();
//          }
//        });
      }
      if (holder.getItemViewType() == 3) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.cancelUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
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
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).getfPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.requesterUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
            }
          });
      } else if (holder.getItemViewType() == 5) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.nameOfApplicant.setText(feed.getItem(pos).getfTitle());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).getfPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.requesterUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
            }
          });
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            cardDialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
            cardDialog.setContentView(R.layout.group_payment_layout);
            edtAmount = (EditText) cardDialog.findViewById(R.id.payAmount);
            edtAmount.setText(getDividedToman(Long.valueOf(feed.getItem(holder.getAdapterPosition()).getAmount()+"")));
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
            contactDetail.setText("پرداخت وجه به " + feed.getItem(holder.getAdapterPosition()).gettTitle());
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
                  Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (edtComment.getText().toString().length() == 0) {
                  Toast.makeText(GroupChatPageActivity.this, "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (!validate_number(edtAmount.getText().toString().replace(",", ""))) {
                  Toast.makeText(GroupChatPageActivity.this, "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
                } else {
                  mAmount = edtAmount.getText().toString().replace(",", "");
                  mComment = edtComment.getText().toString();
                  hideKey(edtComment);
                  payDialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
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
                      Log.e(TAG, "onClick1: " + anotherMobile);
                      Log.e(TAG, "onClick2: " + edtCardNumber.getText().toString() + edtCardPassword.getText().toString());
                      Log.e(TAG, "onClick3: " + mComment);
                      Log.e(TAG, "onClick4: " + mAmount);
                      new sendPaymentRequest().execute(feed.getItem(holder.getAdapterPosition()).gettMobile()
                        , edtCardNumber.getText().toString().replace("-", "") + edtCardPassword.getText().toString()
                        , mComment, mAmount, String.valueOf(groupId));
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
      } else if (holder.getItemViewType() == 6) {
        pos = holder.getAdapterPosition();
        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.userReceiverName.setText(feed.getItem(pos).getfTitle());
        holder.payedToUserName.setText(feed.getItem(pos).gettTitle());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.payedToUserImage) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
            }
          });
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).getfPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.requesterUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              this.view.setImageDrawable(circularBitmapDrawable);
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
      TextView payerName;
      TextView nameOfApplicant;
      Button btn_cancel;
      Button btn_accept;
      ImageView requestUserAvatar;
      ImageView requesterUserAvatar;
      ImageView payerImage;
      ImageView payToUserImage;
      TextView userReceiverName;
      ImageView payedToUserImage;
      TextView payedToUserName;
      ImageView cancelUserAvatar;

      ViewHolder(View itemView) {
        super(itemView);
        txt_price = (TextView) itemView.findViewById(R.id.txt_priceFrom);
        txt_clock = (TextView) itemView.findViewById(R.id.txt_clock);
        txt_date = (TextView) itemView.findViewById(R.id.txt_date);
        txt_status = (TextView) itemView.findViewById(R.id.txt_status_payed_from);
        txt_description = (TextView) itemView.findViewById(R.id.txt_description_from);
        btn_cancel = (Button) itemView.findViewById(R.id.btn_cancel_pay);
        btn_accept = (Button) itemView.findViewById(R.id.btn_accept_pay);
//        requestUserAvatar = (ImageView) itemView.findViewById(R.id.requestUserAvatar);
        requesterUserAvatar = (ImageView) itemView.findViewById(R.id.requesterImage);
        payerImage = (ImageView) itemView.findViewById(R.id.payerProfileImage);
        payToUserImage = (ImageView) itemView.findViewById(R.id.payToUserProfile);
        payerName = (TextView) itemView.findViewById(R.id.payerName);
        nameOfApplicant = (TextView) itemView.findViewById(R.id.nameApplicant);
        userReceiverName = (TextView) itemView.findViewById(R.id.userReceiverName);
        payedToUserImage = (ImageView) itemView.findViewById(R.id.payedToUserImage);
        payedToUserName = (TextView) itemView.findViewById(R.id.payedToUserName);
        cancelUserAvatar = (ImageView) itemView.findViewById(R.id.cancelUserAvatar);
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

  private class getChatLog extends AsyncTask<String, Void, PayLogFeed> {
    int groupId;
    int page;
    String date;

    public getChatLog(int groupId, int page, String date) {
      this.groupId = groupId;
      this.page = page;
      this.date = date;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogFeed doInBackground(String... params) {
      PayLogFeed payLogFeed = null;
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      try {
        payLogFeed = domParser.getGroupChat(groupId, page, date);
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
          adapter = new GroupChatPageAdapter();
          payListRecycler.setAdapter(adapter);
          adapter.notifyDataSetChanged();
        } else {
          Log.e("222222222", "onPostExecute: ");
          feed = result;
          adapter.notifyDataSetChanged();
        }
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private class sendPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogItem doInBackground(String... params) {
      DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return domParser.sendGroupPaymentRequest(params[0], params[1], params[2], params[3], params[4]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
//        feed.removeItem(pos);
        Log.e("POS", pos + "");
        feed.addItem(result, 0);
        lastChatAmount = getDividedToman((long) result.getAmount());
        comment = result.getComment();
        payState = true;
        selfPhoneNumber = result.getfMobile();
        feed.getHash().put(result.getId(), 0);
        new LocalPersistence().writeObjectToFile(GroupChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
      } else
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
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

  public static MessageHandler informNotif() {
    return mHandler;
  }

  public static String tag() {
    return GroupChatPageActivity.class.getSimpleName();
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
        feed.addItem(result, 0);
        adapter.notifyDataSetChanged();
        new LocalPersistence().writeObjectToFile(GroupChatPageActivity.this, feed, "Payment_Chat_List");
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
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
        if (result) {
          cancelState = true;
          Log.e("TEst", "onClick: " + pos);
          feed.getItem(pos).setStatus(true);
          adapter.notifyDataSetChanged();
        }
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
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
        cancelState = true;
        Log.e("TEst", "onClick: " + result);
        if (result) {
          Log.e("TEst", "onClick: " + pos);
          feed.getItem(pos).setStatus(true);
          adapter.notifyDataSetChanged();
        }
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void handleMessage(final PayLogItem logItem, final boolean deleteState, final String chatMemberMobile) {
    Log.e(TAG, "handleMessage: " + logItem.getComment());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (deleteState) {
          Log.e("EQ2", "run: " + logItem.getCancelId());
          Log.e("EQ2", "run: " + feed.getHash());
          for (int i = 0; i < feed.getHash().size(); i++) {
          }
          try {
            newPos = feed.getHash().get(logItem.getCancelId());
          } catch (Exception e) {
            e.printStackTrace();
          }
          new GroupChatPageActivity.DeletePaymentRequestWithID(newPos).execute(logItem.getCancelId());
          adapter.notifyDataSetChanged();
        } else {
          Log.e("chatMemberMobile", "run: " + chatMemberMobile);
          Log.e("phone", "run: " + phone);
          Log.e("TTTT", "handleMessage pv: ");
          lastChatAmount = getDividedToman((long) logItem.getAmount());
          comment = logItem.getComment();
          payState = logItem.isPaideBool();
          selfPhoneNumber = logItem.getfMobile();
          feed.getHash().put(logItem.getId(), 0);
          feed.addItem(logItem, 0);
          adapter.notifyDataSetChanged();
        }
      }
    });
  }

  @Override
  public void onBackPressed() {

    new fillContact().execute("[]");
    super.onBackPressed();
//    Intent intent = new Intent(ChatPageActivity.this, MainActivity.class);
//    if (_feed != null && _feed.getItemCount() > 0 && _feed.getItemCount() != 0) {
//      Log.e("CHAT TRUE", "onBackPressed: ");
//      intent.putExtra("contact", _feed);
//    }
//    startActivity(intent);
    finish();
//    Intent intent = new Intent(GroupChatPageActivity.this , MainActivity.class);
//    startActivity(intent);
//    super.onBackPressed();
//    finish();
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
      } else {
        Toast.makeText(GroupChatPageActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(result);
    }

  }

}