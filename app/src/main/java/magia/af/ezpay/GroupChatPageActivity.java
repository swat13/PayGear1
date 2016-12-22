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

import java.util.HashMap;

import magia.af.ezpay.Firebase.MessagingService;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.MembersItem;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogFeed;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.Utilities.ApplicationData;
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
  MembersItem membersItem;
  PayLogItem payLogItem;
  ChatListFeed _ChatList_feed;
  MembersFeed groupMembers;
  int newPos;
  public static final String TAG = GroupChatPageActivity.class.getSimpleName();
  private String date;

  private String lastChatAmount;
  private String comment;
  private boolean payState;
  private boolean requestState;
  private boolean cancelState;
  HashMap<Integer, Integer> hashMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat_page);
    MessagingService.mode = 2;
    feed = new PayLogFeed();
    mHandler = this;
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      groupId = bundle.getInt("id");
      Log.e(TAG, "onCreate: id:" + groupId);
      groupPhoto = bundle.getString("photo");
      groupTitle = bundle.getString("title");
      groupMembers = (MembersFeed) bundle.getSerializable("members");
      _ChatList_feed = (ChatListFeed) bundle.getSerializable("contact");
    }
    phone = getSharedPreferences("EZpay", 0).getString("phoneNumber", "");

    date = "2050-01-01T00:00:00.000";

    for (int i = 0; i < groupMembers.memberItemCount(); i++) {
      if (groupMembers.getMember(i).getMemberPhone().equals(phone)) {
        groupMembers.removeMemberItem(i);
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
            edtAmount.requestFocus();
            edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
              @Override
              public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                  edtAmount.setGravity(Gravity.LEFT);
                  edtAmount.setHint("");
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
                if (s.length() == 0) {

                  edtAmount.setGravity(Gravity.CENTER);
                  edtAmount.setHint("مبلغ پرداختی");


                } else {
                  edtAmount.setGravity(Gravity.LEFT);
                  edtAmount.setHint("");
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
                } else if (edtComment.getText().length() == 0) {
                  edtComment.setGravity(Gravity.CENTER);
                  edtComment.setHint("مبلغ پرداختی");

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
            contactDetail.setText("پرداخت وجه به " + membersItem.getMemberTitle());
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
                  edtCardNumber.requestFocus();
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
      Log.e("Erfan Pos", "onBindViewHolder: " + position);
      Log.e("Erfan Test", "onBindViewHolder: " + groupMembers.getMember(position).getMemberPhoto());
      holder.txt_contact_item_name.setText(groupMembers.getMember(position).getMemberTitle());
      holder.txt_contact_item_phone.setText(groupMembers.getMember(position).getMemberPhone());
      String imageUrl = "http://new.opaybot.ir";
      Glide.with(GroupChatPageActivity.this)
        .load(imageUrl + groupMembers.getMember(position).getMemberPhoto())
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
      return groupMembers.memberItemCount();
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
          anotherMobile = groupMembers.getMember(getAdapterPosition()).getMemberPhone();
          membersItem = groupMembers.getMember(getAdapterPosition());
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.groupRequestFromAnother(phone, amount, comment, groupId);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.addItem(result, 0);
        new fillContact().execute("[]");
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
          viewType = 0;//cancel from
        } else if (feed.getItem(position).isPaideBool()) {
          viewType = 1;//payed from
        } else {
          viewType = 2;//request to
        }
      } else if (feed.getItem(position).getfMobile().equals(feed.getItem(position).gettMobile())) {
        if (feed.getItem(position).isStatus()) {
          viewType = 3;//cancel from other
        } else if (feed.getItem(position).isPaideBool()) {
          viewType = 0;// Payed From Another which is impossible
        } else {
          viewType = 5;//request from other
        }
      } else if (feed.getItem(position).gettMobile().equals(phone)) {
        if (feed.getItem(position).isPaideBool()) {
          viewType = 4; //payed to from
        }
      } else {
        if (feed.getItem(position).isPaideBool()) {
          viewType = 6; //done
        }
      }
      return viewType;
    }

    @Override
    public GroupChatPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View rootView = null;
      switch (viewType) {
        case 0:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_cancel_from, parent, false);
          break;
        case 1:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_from, parent, false);
          break;
        case 2:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_from, parent, false);
          break;
        case 3:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_cancel_to, parent, false);
          break;
        case 4:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_to, parent, false);
          break;
        case 5:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_pay_to, parent, false);
          break;
        case 6:
          rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_payed_to, parent, false);
          break;
      }
      return new GroupChatPageAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final GroupChatPageAdapter.ViewHolder holder, final int position) {
      int year;
      int month;
      int day;
      CalendarConversion conversion;
      pos = position;
      if (holder.getItemViewType() != 8) {

        year = Integer.parseInt(feed.getItem(pos).getDate().substring(0, 4));
        month = Integer.parseInt(feed.getItem(pos).getDate().substring(5, 7));
        day = Integer.parseInt(feed.getItem(pos).getDate().substring(8, 10));
        conversion = new CalendarConversion(year, month, day);
        holder.txt_price.setText(getDividedToman(Long.valueOf(feed.getItem(pos).getAmount() + "")));
        holder.txt_clock.setText(feed.getItem(pos).getDate().substring(11, 16) + "");
        holder.txt_date.setText(conversion.getIranianDate() + "");
        holder.txt_description.setText("توضیحات: " + feed.getItem(pos).getComment());

        switch (holder.getItemViewType()) {
          case 1:
            holder.payerName.setText(feed.getItem(pos).gettTitle());
            onBindViewGlide(holder.payToUserImage, true);
            break;

          case 2:
            holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                new DeletePaymentRequest(holder.getAdapterPosition()).execute(feed.getItem(holder.getAdapterPosition()).getId());
              }
            });
            break;

          case 3:
            onBindViewGlide(holder.cancelUserAvatar, true);
            break;
          case 4:
            onBindViewGlide(holder.requesterUserAvatar, false);
            break;

          case 5:

            holder.nameOfApplicant.setText(feed.getItem(pos).getfTitle());
            onBindViewGlide(holder.requesterUserAvatar, false);

            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                cardDialog = new Dialog(GroupChatPageActivity.this, R.style.PauseDialog);
                cardDialog.setContentView(R.layout.group_card_layout);
                edtCardNumber = (EditText) cardDialog.findViewById(R.id.payAmount);
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
                edtCardPassword = (EditText) cardDialog.findViewById(R.id.comments);
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
                    // Remove spacing char
                    if (s.length() > 0 && (s.length() % 5) == 0) {
                      final char c = s.charAt(s.length() - 1);
                      if (space == c) {
                        s.delete(s.length() - 1, s.length());
                      }
                    }
                    // Insert char where needed.
                    if (s.length() > 0 && (s.length() % 5) == 0) {
                      char c = s.charAt(s.length() - 1);
                      // Only if its a digit where there should be a space we insert a space
                      if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                      }
                    }
                  }
                });
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
                    new GroupChatPageActivity.accPaymentRequest(position).execute(feed.getItem(holder.getAdapterPosition()).getId() + "", edtCardNumber.getText().toString().replace("-", "") + edtCardPassword.getText().toString());
                    cardDialog.dismiss();
                  }
                });
                cardDialog.show();
              }
            });

            break;

          case 6:

            holder.userReceiverName.setText(feed.getItem(pos).getfTitle());
            holder.payedToUserName.setText(feed.getItem(pos).gettTitle());
            onBindViewGlide(holder.payedToUserImage, false);
            onBindViewGlide(holder.requesterUserAvatar, false);
            break;

        }
      }
    }

    private void onBindViewGlide(final ImageView imageView, boolean ToF) {

      if (ToF) {
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).gettPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              imageView.setImageDrawable(circularBitmapDrawable);
            }
          });

      } else {
        Glide.with(GroupChatPageActivity.this)
          .load(feed.getItem(pos).getfPhoto())
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              imageView.setImageDrawable(circularBitmapDrawable);
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

        requesterUserAvatar = (ImageView) itemView.findViewById(R.id.requesterImage);
        payerImage = (ImageView) itemView.findViewById(R.id.payerProfileImage);
        payToUserImage = (ImageView) itemView.findViewById(R.id.payToUserProfile);
        payerName = (TextView) itemView.findViewById(R.id.payerName);
        nameOfApplicant = (TextView) itemView.findViewById(R.id.nameApplicant);
        userReceiverName = (TextView) itemView.findViewById(R.id.userReceiverName);
        payedToUserImage = (ImageView) itemView.findViewById(R.id.payedToUserImage);
        payedToUserName = (TextView) itemView.findViewById(R.id.payedToUserName);
        cancelUserAvatar = (ImageView) itemView.findViewById(R.id.cancelUserAvatar);
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      try {
        payLogFeed = parser.getGroupChat(groupId, page, date);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return payLogFeed;
    }

    @Override
    protected void onPostExecute(PayLogFeed result) {
      if (result != null) {
        if (feed == null || feed.getItemCount() == 0) {
          feed = result;
          for (int i = 0; i < feed.getItemCount(); i++) {
            hashMap.put(result.getItem(i).getId(), i);
          }
          adapter = new GroupChatPageAdapter();
          payListRecycler.setAdapter(adapter);
          adapter.notifyDataSetChanged();
        } else {
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.sendGroupPaymentRequest(params[0], params[1], params[2], params[3], params[4]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
//        chatListFeed.removeItem(pos);
        feed.addItem(result, 0);
        new fillContact().execute("[]");
        hashMap.put(result.getId(), 0);
        new LocalPersistence().writeObjectToFile(GroupChatPageActivity.this, feed, "Payment_Chat_List");
        adapter.notifyDataSetChanged();
      } else
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
    }
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.accPaymentRequest(params[0], params[1]);

    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
        feed.addItem(result, 0);
        adapter.notifyDataSetChanged();
        new fillContact().execute("[]");
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
      Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.deletePayment(params[0]);

    }

    @Override
    protected void onPostExecute(Boolean result) {
      if (result != null) {
        Log.e("TEst", "onClick: " + result);
        if (result) {
          Log.e("TEst", "onClick: " + pos);
          feed.getItem(pos).setStatus(true);
          adapter.notifyDataSetChanged();
          new fillContact().execute("[]");
        }
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private class DeletePaymentRequestWithID extends AsyncTask<Integer, Boolean, Boolean> {
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
      if (result != null) {
        if (result) {
          feed.getItem(pos).setStatus(true);
          adapter.notifyDataSetChanged();
          new fillContact().execute("[]");
        }
      } else {
        Toast.makeText(GroupChatPageActivity.this, "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void handleMessage(final PayLogItem payLogItem, final boolean deleteState, final String chatMemberMobile) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (deleteState) {
          try {
            newPos = hashMap.get(payLogItem.getCancelId());
          } catch (Exception e) {
            e.printStackTrace();
          }
          new GroupChatPageActivity.DeletePaymentRequestWithID(newPos).execute(payLogItem.getCancelId());
          adapter.notifyDataSetChanged();
        } else if (payLogItem.getNotifParam1().equals(String.valueOf(groupId))) {
          Log.e(TAG, "run: ");
          adapter.notifyDataSetChanged();
          feed.addItem(payLogItem, 0);
          hashMap = shiftHashMap(payLogItem.getId());
        }
      }
    });
  }

  @Override
  public void handleMessageGp(String body, String chatMemberMobile) {

  }


  @Override
  public void onBackPressed() {
    MessagingService.mode = 3;
    super.onBackPressed();
    finish();
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
        ApplicationData.setChatListFeed(result);
      } else {
        Toast.makeText(GroupChatPageActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
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