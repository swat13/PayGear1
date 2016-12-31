package magia.af.ezpay.fragments;



import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.DrawItem;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.PayLogItem;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.DialogMaker;
import magia.af.ezpay.helper.ImageMaker;
import magia.af.ezpay.helper.NumberTextWatcher;
import magia.af.ezpay.interfaces.OnClickListener;


/**
 * Created by pc on 11/9/2016.
 */

public class Profile extends Fragment {
  Toolbar toolbar;
  AppBarLayout appBarLayout;
  private ImageView userAvatar, imageBtn, imageSignOut;
  private TextView contactName, amount, phoneNumber;

  private static final int PICK_IMAGE = 1;
  private Button upload;
  private EditText caption;
  private Bitmap bitmap;
  String Qpath;
  String Ipath;
  private Button btnEditProfile;
  private Button btnAdd;
  private Button btnWithDraw;

  private Dialog dialog;
  private Dialog firstDialog;
  private Dialog cardDialog;
  private Dialog payDialog;
  private Dialog requestDialog;
  private Dialog addDialog;

  EditText edtCardNumber;
  EditText edtCardPassword;
  private Button btnChangePass;

  ArrayList<DrawItem> drawItems = new ArrayList<>(25);
  String[] bankName = {"پارسیان", "اقتصاد نوین", "شهر", "ملت", "ملی", "صادرات", "سامان", "پاسارگاد", "کشاورزی", "مسکن", "رفاه", "سپه", "آینده", "تجارت", "سرمایه", "صنعت و معدن", "توسعه صادرات", "پست بانک", "توسعه تعاون", "کارآفرین", "سینا", "انصار", "دی", "ایران زمین", "خاورمیانه"};
  int[] bankId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
  int[] bankPic = {R.drawable.parsian, R.drawable.bank_en, R.drawable.bank_shahr_d, R.drawable.bank_mellat_2_, R.drawable.bank_melli,
    R.drawable.bank_saderat, R.drawable.saman, R.drawable.bank_pasargad, R.drawable.agribank, R.drawable.bank_maskan
    , R.drawable.refah_bank, R.drawable.bank_sepah, R.drawable.ayandeh, R.drawable.bank_tejarat
    , R.drawable.sarmaye_bank, R.drawable.bank_sanat_madan, R.drawable.tose_e_saderat, R.drawable.post_bank, R.drawable.tose_e_bank
    , R.drawable.bank_karafarin, R.drawable.sina_bank, R.drawable.bank_ansar, R.drawable.bank_dey, R.drawable.bnk_iranzamin_d
    , R.drawable.bnk_khavarmianeh_d};

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_profile_backup, container, false);

    userAvatar = (ImageView) rootView.findViewById(R.id.user_avatar);
    imageBtn = (ImageView) rootView.findViewById(R.id.imageButton);
    imageSignOut = (ImageView) rootView.findViewById(R.id.log_out);

    btnEditProfile = (Button) rootView.findViewById(R.id.btnEditProfile);
    btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
    btnWithDraw = (Button) rootView.findViewById(R.id.btnWithDraw);
    btnChangePass = (Button) rootView.findViewById(R.id.btnChangePass);
    if (getActivity().getSharedPreferences("password", 0).getString("password", "").isEmpty()) {
      btnChangePass.setText("اعمال رمز");
      btnChangePass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final Dialog dialog = new Dialog(getActivity(), R.style.PauseDialog);
          dialog.setContentView(R.layout.set_password_dialog);
          final EditText edtFirstPass = (EditText) dialog.findViewById(R.id.edtFirstPass);
          final EditText edtConfirmPass = (EditText) dialog.findViewById(R.id.edtConfirmPass);
          Button btnSet = (Button) dialog.findViewById(R.id.btnSet);
          btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (edtFirstPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                getActivity().getSharedPreferences("password", 0).edit().putString("password", edtConfirmPass.getText().toString()).apply();
                Toast.makeText(getActivity(), "رمز عبور با موفقیت اعمال شد", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                btnChangePass.setText("تغییر رمز عبور");
              } else {
                Toast.makeText(getActivity(), "رمز عبور را به دقت وارد کنید", Toast.LENGTH_SHORT).show();
              }
            }
          });
          dialog.show();
        }
      });
    } else {
      btnChangePass.setText("تغییر رمز عبور");
      btnChangePass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final Dialog dialog = new Dialog(getActivity(), R.style.PauseDialog);
          dialog.setContentView(R.layout.change_password_dialog);
          final EditText edtCurrentPass = (EditText) dialog.findViewById(R.id.edtCurrentPass);
          final EditText edtFirstPass = (EditText) dialog.findViewById(R.id.edtFirstPass);
          final EditText edtConfirmPass = (EditText) dialog.findViewById(R.id.edtConfirmPass);
          Button btnSet = (Button) dialog.findViewById(R.id.btnSet);
          btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (edtCurrentPass.getText().toString().equals(getActivity().getSharedPreferences("password", 0).getString("password", ""))) {
                if (edtFirstPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                  getActivity().getSharedPreferences("password", 0).edit().putString("password", edtConfirmPass.getText().toString()).apply();
                  Toast.makeText(getActivity(), "رمز عبور با موفقیت تغییر کرد", Toast.LENGTH_SHORT).show();
                  dialog.dismiss();
                } else {
                  Toast.makeText(getActivity(), "لطفا رمز عبور خود را با دقت وارد کنید", Toast.LENGTH_SHORT).show();
                }
              }
              else {
                Toast.makeText(getActivity(), "رمز عبور فعلی با رمز وارد شده یکی نمی باشد!", Toast.LENGTH_SHORT).show();
              }
            }
          });
          dialog.show();
        }
      });
    }
    btnEditProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_profile_dialog);
        final EditText edtInputUsername = (EditText) dialog.findViewById(R.id.edtInputUserName);
        edtInputUsername.setText(getActivity().getSharedPreferences("EZpay", 0).getString("contactName", ""));
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (edtInputUsername.getText().toString().length() < 2) {
              Toast.makeText(getActivity(), "لطفا اسم خود را وارد کنید", Toast.LENGTH_SHORT).show();
            } else {
              JSONParser parser = JSONParser.connect(Constant.SET_TITLE);
              parser.setRequestMethod(JSONParser.PUT);
              parser.setReadTimeOut(20000);
              parser.setConnectionTimeOut(20000);
              parser.setAuthorization(Constant.token(getActivity()));
              JSONObject object = new JSONObject();
              try {
                object.put("title", edtInputUsername.getText().toString());
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
                    new getAccount().execute();

                  }
                }
              });
              dialog.dismiss();
              dialog.cancel();
            }
          }
        });
        dialog.show();
      }
    });
    btnAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addDialog = new Dialog(getActivity(), R.style.PauseDialog);
        addDialog.setContentView(R.layout.add_to_account_dialog);
        Button btnCancel = (Button) addDialog.findViewById(R.id.cancel);
        final EditText edtInputNumber = (EditText) addDialog.findViewById(R.id.edtInputNumber);
        edtInputNumber.addTextChangedListener(new NumberTextWatcher(edtInputNumber));
        btnCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            addDialog.dismiss();
          }
        });
        Button btnConfirm = (Button) addDialog.findViewById(R.id.confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.e("AM", "onClick: " + Integer.parseInt(edtInputNumber.getText().toString().replace(",", "")));
            Log.e("Phone", "onClick: " + getActivity().getSharedPreferences("EZpay", 0).getString("phoneNumber", ""));
            JSONParser parser = JSONParser.connect(Constant.PAY_TO_ANOTHER_WITH_IPG);
            parser.setRequestMethod(JSONParser.POST);
            parser.setReadTimeOut(20000);
            parser.setConnectionTimeOut(20000);
            parser.setAuthorization(Constant.token(getActivity()));
            JSONObject object = new JSONObject();
            try {
              object.put("anotherMobile", getActivity().getSharedPreferences("EZpay", 0).getString("phoneNumber", ""));
              object.put("amount", Integer.parseInt(edtInputNumber.getText().toString().replace(",", "")));
              object.put("comment", "شارژ حساب");
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
                  addDialog.dismiss();
                  CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                  intent.launchUrl(getActivity(), Uri.parse(Constant.IPG_URL + s.replace("\"", "")));
                }
              }
            });
          }
        });
        addDialog.show();
      }
    });
    for (int i = 0; i < bankName.length; i++) {
      DrawItem drawItem = new DrawItem();
      drawItem.setBankName(bankName[i]);
      drawItem.setBankId(bankId[i]);
      drawItem.setBandDrawableRes(bankPic[i]);
      drawItems.add(drawItem);
    }
    btnWithDraw.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        firstDialog = new Dialog(getActivity(), R.style.PauseDialog);
        firstDialog.setContentView(R.layout.choose_draw_dialog);
        Button btnFastDraw = (Button) firstDialog.findViewById(R.id.btnFastDraw);
        btnFastDraw.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            firstDialog.dismiss();
            payDialog = new Dialog(getActivity(), R.style.PauseDialog);
            payDialog.setContentView(R.layout.with_draw_dialog);
            edtCardNumber = (EditText) payDialog.findViewById(R.id.payAmount);
            edtCardNumber.requestFocus();
            edtCardNumber.addTextChangedListener(new NumberTextWatcher(edtCardNumber));
            edtCardPassword = (EditText) payDialog.findViewById(R.id.comments);
            edtCardPassword.setCursorVisible(false);
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
                JSONParser parser = JSONParser.connect(Constant.WITH_DRAW_FAST);
                parser.setRequestMethod(JSONParser.POST);
                parser.setReadTimeOut(20000);
                parser.setConnectionTimeOut(20000);
                parser.setAuthorization(Constant.token(getActivity()));
                JSONObject object = new JSONObject();
                try {
                  object.put("amount", Integer.parseInt(edtCardNumber.getText().toString().replace(",", "")));
                  object.put("withdrawTo", edtCardPassword.getText().toString());
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
                      payDialog.dismiss();
                    }
                  }
                });
              }
            });
            payDialog.show();
          }
        });
        Button btnNormalDraw = (Button) firstDialog.findViewById(R.id.btnNormalDraw);
        btnNormalDraw.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            firstDialog.dismiss();
            final Dialog chooseBankDialog = new Dialog(getActivity(), R.style.PauseDialog);
            chooseBankDialog.setContentView(R.layout.bank_dialog);
            RecyclerView bankListRecycler = (RecyclerView) chooseBankDialog.findViewById(R.id.bankListRecyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            bankListRecycler.setLayoutManager(manager);
            BankListAdapter adapter = new BankListAdapter(new OnClickListener() {
              @Override
              public void onClick(final DrawItem drawItem) {
                chooseBankDialog.dismiss();
                payDialog = new Dialog(getActivity(), R.style.PauseDialog);
                payDialog.setContentView(R.layout.with_draw_dialog);
                edtCardNumber = (EditText) payDialog.findViewById(R.id.payAmount);
                edtCardNumber.requestFocus();
                edtCardNumber.addTextChangedListener(new NumberTextWatcher(edtCardNumber));
                edtCardPassword = (EditText) payDialog.findViewById(R.id.comments);
                edtCardPassword.setCursorVisible(false);
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
                    JSONParser parser = JSONParser.connect(Constant.WITH_DRAW_NORMAL);
                    parser.setRequestMethod(JSONParser.POST);
                    parser.setReadTimeOut(20000);
                    parser.setConnectionTimeOut(20000);
                    parser.setAuthorization(Constant.token(getActivity()));
                    JSONObject object = new JSONObject();
                    try {
                      object.put("amount", Integer.parseInt(edtCardNumber.getText().toString().replace(",", "")));
                      object.put("withdrawTo", edtCardPassword.getText().toString());
                      object.put("bankId", drawItem.getBankId());
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
                          payDialog.dismiss();
                        }
                      }
                    });
                  }
                });
                payDialog.show();
              }
            });
            bankListRecycler.setAdapter(adapter);
            Button btnCancel = (Button) chooseBankDialog.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                chooseBankDialog.dismiss();
              }
            });
            chooseBankDialog.show();
          }
        });
        firstDialog.show();
      }
    });
    if (Build.VERSION.SDK_INT >= 23) {
      checkPermissions();
    }

    contactName = (TextView) rootView.findViewById(R.id.txt_user_name);
    amount = (TextView) rootView.findViewById(R.id.txt_account_availability);
    phoneNumber = (TextView) rootView.findViewById(R.id.txt_phone_number);

    if (getActivity().getSharedPreferences("EZpay", 0).getString("contactName", "").isEmpty()) {
      Log.e("2222222", "Executed");
      new getAccount().execute();
    } else {
      contactName.setText(getActivity().getSharedPreferences("EZpay", 0).getString("contactName", ""));
      amount.setText(getDividedToman((long) getActivity().getSharedPreferences("EZpay", 0).getInt("amount", 0)));
      phoneNumber.setText(getActivity().getSharedPreferences("EZpay", 0).getString("phoneNumber", ""));
      Ipath = getActivity().getSharedPreferences("EZpay", 0).getString("Ipath", "");

      Log.e("ImageString", "http://new.opaybot.ir" + Ipath.replace("\"", ""));

      Glide.with(getActivity())
        .load("http://new.opaybot.ir" + Ipath.replace("\"", ""))
        .asBitmap()
        .centerCrop()
        .placeholder(R.drawable.pic_profile)
        .into(new BitmapImageViewTarget(userAvatar) {
          @Override
          protected void setResource(Bitmap resource) {
            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
            circularBitmapDrawable.setCornerRadius(700);
            userAvatar.setImageDrawable(circularBitmapDrawable);
          }
        });


    }


    imageBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Get photo from gallery or take picture ?");
//                builder1.setCancelable(true);

        builder1.setPositiveButton(
          "Import from gallery",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              Intent intent = new Intent();
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);//
              startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
            }
          });

        builder1.setNegativeButton(
          "Take picture",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent, 0);
            }
          });

        AlertDialog alert11 = builder1.create();
        alert11.show();
      }
    });


    imageSignOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        getActivity().getSharedPreferences("EZpay", 0).edit().remove("token").apply();
        Log.e("Token :", getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();

      }
    });

    return rootView;
  }

  public void checkPermissions() {
    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 0);
  }

  Bitmap thumbnail;

  private String onCaptureImageResult(Intent data) {
    if (data != null) {
      thumbnail = (Bitmap) data.getExtras().get("data");
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
      byte[] byteArray = bytes.toByteArray();
      String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
      return encoded;
    } else
      return null;
  }

  @SuppressWarnings("deprecation")
  private String onSelectFromGalleryResult(Intent data) {


    thumbnail = null;
    if (data != null) {

      ImageMaker imageMaker = new ImageMaker(getActivity().getApplicationContext(), data);
      return imageMaker.onSelectFromGalleryResult();
    }
    return null;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
    Log.e("11111111", "onActivityResult: 0000" + resultCode);
    Log.e("22222222", "onActivityResult: 1111" + requestCode);
    if (resultCode == Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult: "+imageReturnedIntent.getData() );
//            Uri tempUri = getImageUri(getActivity(), bitmap);
      switch (requestCode) {
        case 0:
          new AsyncInsertUserImage().execute(onCaptureImageResult(data));
          break;
        case 1:
          new AsyncInsertUserImage().execute(onSelectFromGalleryResult(data));
          break;
        default:
          break;
      }
    }
  }

  private class AsyncInsertUserImage extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      ((MainActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
      GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((MainActivity) getActivity()).imageView);
      Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

    }

    @Override
    protected String doInBackground(String... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      try {
        return parser.changeUserImage(params[0]);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return "Error";
    }

    @Override
    protected void onPostExecute(String result) {
      if (!result.equals("Error")) {
        Glide.with(getActivity())
          .load("http://new.opaybot.ir" + result.replace("\"", ""))
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(userAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              userAvatar.setImageDrawable(circularBitmapDrawable);
            }
          });

        getActivity().getSharedPreferences("EZpay", 0).edit().putString("Ipath", result).apply();
      } else {
        Toast.makeText(getActivity(), "Error In Connection", Toast.LENGTH_SHORT).show();
      }

      ((MainActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
    }
  }

  public class getAccount extends AsyncTask<Void, Void, ChatListItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
//            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((FriendListActivity) getActivity()).imageView);
//            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

    }

    @Override
    protected ChatListItem doInBackground(Void... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.getAccount();
    }

    @Override
    protected void onPostExecute(ChatListItem result) {
      Log.e("jsons", String.valueOf(result));


      if (result != null) {
        Ipath = result.getContactImg();
        Glide.with(getActivity())
          .load("http://new.opaybot.ir" + Ipath.replace("\"", ""))
          .asBitmap()
          .centerCrop()
          .placeholder(R.drawable.pic_profile)
          .into(new BitmapImageViewTarget(userAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
              RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
              circularBitmapDrawable.setCornerRadius(700);
              userAvatar.setImageDrawable(circularBitmapDrawable);
            }
          });
        contactName.setText(result.getContactName());
        amount.setText(String.valueOf(result.getCredit()));
        phoneNumber.setText(result.getTelNo());
        getActivity().getSharedPreferences("EZpay", 0).edit().putString("contactName", result.getContactName()).apply();
        getActivity().getSharedPreferences("EZpay", 0).edit().putInt("amount", result.getCredit()).apply();
        getActivity().getSharedPreferences("EZpay", 0).edit().putString("phoneNumber", result.getTelNo()).apply();
        getActivity().getSharedPreferences("EZpay", 0).edit().putString("Ipath", result.getContactImg()).apply();


//                finish();
      } else {

        Toast.makeText(getActivity(), "Json Is Null!", Toast.LENGTH_SHORT).show();

      }


    }
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }


  private class sendPaymentRequest extends AsyncTask<String, Void, PayLogItem> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected PayLogItem doInBackground(String... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.sendPaymentRequest(params[0], params[1], params[2], params[3]);
    }

    @Override
    protected void onPostExecute(PayLogItem result) {
      if (result != null) {
//                ApplicationData.getAddToAccountCredit(result.getAmount(),getActivity());
        amount.setText("" + ApplicationData.getAddToAccountCredit(result.getAmount(), getActivity()));
      } else
        Toast.makeText(getActivity(), "مشکل در برقراری ارتباط", Toast.LENGTH_SHORT).show();
    }
  }

  public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder> {

    OnClickListener clickListener;

    public BankListAdapter(OnClickListener clickListener) {
      this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_item, parent, false);
      return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.bankImage.setImageResource(drawItems.get(position).getBandDrawableRes());
      holder.txtNameOfBank.setText(drawItems.get(position).getBankName());
    }

    @Override
    public int getItemCount() {
      return drawItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
      TextView txtNameOfBank;
      ImageView bankImage;
      OnClickListener clickListener;

      ViewHolder(View itemView, OnClickListener clickListener) {
        super(itemView);
        txtNameOfBank = (TextView) itemView.findViewById(R.id.txtNameOfBank);
        bankImage = (ImageView) itemView.findViewById(R.id.bank_image_placeHolder);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
      }

      @Override
      public void onClick(View v) {
        DrawItem drawItem = drawItems.get(getAdapterPosition());
        clickListener.onClick(drawItem);
      }
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
          stringBuilder.insert(0, price.toString().substring(
            price.toString().length() - 2 - i, price.toString().length() - i) + ",");
        } catch (Exception e1) {
          stringBuilder.insert(0, price.toString().substring(price.toString().length() - 1 - i, price.toString().length() - i) + ",");
        }
      }

    }
    return stringBuilder.toString();
  }
}



