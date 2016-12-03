package magia.af.ezpay;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
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

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
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

  private static final String TAG = GroupChatPageActivity.class.getSimpleName();
  private String date;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat_page);
    feed = new PayLogFeed();
    mHandler = this;
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      groupId = bundle.getInt("id");
      groupPhoto = bundle.getString("photo");
      groupTitle = bundle.getString("title");
    }
    phone = getSharedPreferences("EZpay", 0).getString("phoneNumber", "");

    date = "2050-01-01T00:00:00.000";


    members = (ArrayList<RSSItem>) getIntent().getSerializableExtra("members");
    for (int i = 0; i < members.size(); i++) {
      Log.e(TAG, "onCreate: " + members.get(i).getGroupMemberPhone());
      if (members.get(i).getGroupMemberPhone().equals(phone)) {
        members.remove(i);
      }
    }

    groupImage = (ImageView) findViewById(R.id.groupImage);
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
              new requestFromAnother(phone, payAmount.getText().toString().replace(",",""), commment.getText().toString(), groupId).execute();
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
    public GroupChatPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView;
      if (viewType == 0) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_from, parent, false);
      } else if (viewType == 1) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_to, parent, false);
      } else if (viewType == 2) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_from, parent, false);
      } else if (viewType == 3) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_to, parent, false);
      } else if (viewType == 4) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_from, parent, false);
      } else {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_to, parent, false);
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
//        holder.txt_status.setText("درخواست شده توسط " + feed.getItem(pos).getfTitle());
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            pos = holder.getAdapterPosition();
            Log.e("eeeeeeeee", "onClick: " + pos);
//            new ChatPageActivity.DeletePaymentRequest().execute(feed.getItem(pos).getId());
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
        holder.nameOfApplicant.setText(feed.getItem(pos).gettTitle());
        Log.e(TAG, "onBindViewHolder: " + feed.getItem(pos).gettPhoto());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.requesterUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              holder.requesterUserAvatar.setImageDrawable(circularBitmapDrawable);
            }
          });
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
//            darkDialog.setVisibility(View.VISIBLE);
            pos = holder.getAdapterPosition();
            Log.e("id", "1111111111111: " + pos);
//            getCardFragment = GetCardFragment.newInstance(feed.getItem(pos).getId());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//            ft.add(android.R.id.content, getCardFragment).commit();
//            if (success) {
//              holder.btn_cancel.setVisibility(View.GONE);
//              holder.btn_accept.setVisibility(View.GONE);
//              holder.txt_status.setText("پرداخت شد");
//              adapter.notifyDataSetChanged();
//            }
//                        holder.removeAt(pos);
//                        adapter.notifyDataSetChanged();
//            removePosition = holder.getAdapterPosition();
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
              holder.payToUserImage.setImageDrawable(circularBitmapDrawable);
            }
          });
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
        holder.userReceiverName.setText(feed.getItem(pos).gettTitle());
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(holder.requesterUserAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              holder.requesterUserAvatar.setImageDrawable(circularBitmapDrawable);
            }
          });
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
      TextView payerName;
      TextView nameOfApplicant;
      Button btn_cancel;
      Button btn_accept;
      ImageView requestUserAvatar;
      ImageView requesterUserAvatar;
      ImageView payerImage;
      ImageView payToUserImage;
      TextView userReceiverName;

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
//        adapter.notifyItemRangeChanged(pos,feed.getItemCount());
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


  @Override
  public void handleMessage(final PayLogItem logItem,boolean deleteState) {
    Log.e(TAG, "handleMessage: " + logItem.getComment());
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.e("TTTT", "handleMessage: ");
        feed.addItem(logItem, 0);
        adapter.notifyDataSetChanged();
      }
    });
  }
}
