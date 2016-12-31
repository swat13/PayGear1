package magia.af.ezpay.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.R;
import magia.af.ezpay.Splash;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.Utilities.DialogMaker;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.interfaces.EventCallbackHandler;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class ActivationCode extends Fragment implements View.OnClickListener, EventCallbackHandler {
  private Button btn_send_activation_code_again;
  private ImageButton btn_send_activation_code;
  private EditText edtInputPhoneNumber, nameEditText;
  private TextView timerText;
  private String phone;
  private String activationCode;
  private String newName;
  private ChatListFeed chatListFeed;
  private JSONArray jsonArray;
  private ChatListItem chatListItem;
  public volatile boolean running = true;
  public fillContact ffillContact;
  String contact;
  private HashMap<String, Boolean> stringArrayMap = new HashMap<>();


  public static ActivationCode getInstance() {

    return new ActivationCode();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_activation_code, null);
    new PutContactInJsonArray(getActivity()).execute();
    ffillContact = new fillContact();
    ((LoginActivity) getActivity()).fragment_status = 2;
    btn_send_activation_code_again = (Button) rootView.findViewById(R.id.btn_send_activation_code_again);
    btn_send_activation_code = (ImageButton) rootView.findViewById(R.id.btn_send_activation_code);
    nameEditText = (EditText) rootView.findViewById(R.id.name_edittext);
    edtInputPhoneNumber = (EditText) rootView.findViewById(R.id.edt_input_activation_code);
    edtInputPhoneNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() == 4) {
          nameEditText.requestFocus();
          if (nameEditText.length() > 1) {
            btn_send_activation_code.setVisibility(View.VISIBLE);
          }
        } else {
          btn_send_activation_code.setVisibility(View.GONE);

        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    nameEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }


      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() > 1 && edtInputPhoneNumber.length() == 4) {
          btn_send_activation_code.setVisibility(View.VISIBLE);
        } else {
          btn_send_activation_code.setVisibility(View.GONE);

        }

      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    timerText = (TextView) rootView.findViewById(R.id.timer_text);
    btn_send_activation_code.setOnClickListener(this);
    new CountDownTimer(10000, 1000) {

      public void onTick(long millisUntilFinished) {
//                textview.setText((millisUntilFinished / 60000)+":"+(millisUntilFinished % 60000 / 1000));

        timerText.setText(String.format("%02d:%02d", (millisUntilFinished / 60000), (millisUntilFinished % 60000 / 1000)));
      }

      public void onFinish() {
        Log.i("Finish", "TEST");
//        finish();
      }

    }.start();


    phone = getArguments().getString("number");
    Log.i("Finish", "TEST");
    Log.i("Phone", phone + "");
    return rootView;
  }

  @Override
  public void onClick(View v) {
    //Handle All View Click Here
    switch (v.getId()) {
      case R.id.btn_send_activation_code:
//                ((LoginActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
        hideKey(v);
        activationCode = edtInputPhoneNumber.getText().toString();
        newName = nameEditText.getText().toString();
        Log.e("((((((((((", "onClick: 00000");
        JSONParser parser = JSONParser.connect(Constant.VERIFY_LOGIN);
        parser.setRequestMethod(JSONParser.POST);
        parser.setReadTimeOut(20000);
        parser.setConnectionTimeOut(20000);
        JSONObject object = new JSONObject();
        try {
          object.put("mobile", phone);
          object.put("code", activationCode);
          object.put("newName", newName);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        parser.setJson(object.toString());
        parser.execute(new JSONParser.Execute() {

          @Override
          public void onPreExecute(){

          }


          @Override
          public void onPostExecute(String s) {
            DialogMaker.disMissDialog();
            if (s != null) {
              Log.e("STRING S", "onPostExecute: " + s);
              try {
                JSONObject jsonObject = new JSONObject(s);
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("token", jsonObject.getString("access_token")).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("id", jsonObject.getString("id")).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("mobile", jsonObject.getString("mobile")).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("photo", jsonObject.getString("photo")).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("title", jsonObject.getString("title")).apply();
                JSONParser parser = JSONParser.connect(Constant.CHECK_CONTACT_LIST_WITH_GROUP);
                parser.setRequestMethod(JSONParser.POST);
                parser.setReadTimeOut(20000);
                parser.setConnectionTimeOut(20000);
                parser.setAuthorization(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
                parser.setJson(getContacts());
                parser.execute(new JSONParser.Execute() {

                  @Override
                  public void onPreExecute(){
                    DialogMaker.makeDialog(getActivity()).showDialog();
                  }


                  @Override
                  public void onPostExecute(final String s) {
                    Log.e("STRING S", "onPostExecute: " + s);
                    if (s != null) {
                      Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                          ContactDatabase contactDatabase = new ContactDatabase(getActivity());
                          for (int i = 0; i < chatListFeed.getItemCount(); i++) {
                            contactDatabase.setContactInNetwork(chatListFeed.getItem(i).getTelNo());
                          }
                          chatListFeed = ApplicationData.getContactListWithGroup(s);
                          ChatListFeed feed = contactDatabase.getAllData();
                          for (int i = 0; i < feed.getItemCount(); i++) {
                            for (int j = 0; j < chatListFeed.getItemCount(); j++) {
                              if (feed.getItem(i).getTelNo().equals(chatListFeed.getItem(j).getTelNo())) {
                                feed.removeItem(i);
                                i--;
                                break;
                              }
                            }
                          }
                          chatListFeed.addAll(chatListFeed.getItemCount(), feed);
                          ApplicationData.setChatListFeed(chatListFeed);
                          ApplicationData.setOutNetworkContact(feed);
                          startActivity(new Intent(getActivity(), MainActivity.class));
                          getActivity().finish();
                          getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              DialogMaker.disMissDialog();
                            }
                          });
                        }
                      });
                      thread.start();
                    }
                  }
                });
              } catch (JSONException e) {
                e.printStackTrace();
              }
//                            ((LoginActivity)getActivity()).loadActiveFragment(phone);
            }
          }
        });
//                new verifyActiveCode().execute(phone, edtInputPhoneNumber.getText().toString(), nameEditText.getText().toString());
        break;


    }
  }


  @Override
  public void callback() {
    //Handle Callback here
  }

  public void hideKey(View view) {
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }


  private class verifyActiveCode extends AsyncTask<String, Void, String[]> {


    @Override
    protected void onCancelled() {
      super.onCancelled();
    }


    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
      GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((LoginActivity) getActivity()).imageView);
      Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);
//            ((LoginActivity) getActivity()).fragment_status = 3;
    }

    @Override
    protected String[] doInBackground(String... params) {
      Parser parser = new Parser();
            /*while (running) {
                if (isCancelled()) {
                    return null;

                }
            }*/
      return parser.Verify_Login_Activation_Code(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(String[] result) {
//            Log.e("11111111111111", "onPostExecute: " + result);


      if (result != null && !result[1].contains("Wrong")) {
        String tok = result[0];
        String id = result[1];
        if (tok.length() > 10) {
          Log.e("55555555555", "onPostExecute: " + tok);
          getActivity().getSharedPreferences("EZpay", 0).edit().putString("token", tok).apply();
          getActivity().getSharedPreferences("EZpay", 0).edit().putString("id", id).apply();
          new fillContact().execute();
        }
      } else if (result != null && result[0].contains("Wrong")) {
        Toast.makeText(getActivity(), "کد فعال سازی صحیح نمی باشد!", Toast.LENGTH_SHORT).show();
        ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);

      } else {
        Toast.makeText(getActivity(), "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
        ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);

      }

    }
  }

  public class PutContactInJsonArray extends AsyncTask<Void, Void, String> {
    Context context;

    public PutContactInJsonArray(Context context) {
      this.context = context;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      DialogMaker.makeDialog(context).showDialog();
    }

    @Override
    protected String doInBackground(Void... params) {
      chatListFeed = new ChatListFeed();
      ContentResolver cr = context.getContentResolver();
      Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
        null, null, null, null);

      if (cur.getCount() > 0) {
        jsonArray = new JSONArray();
        int count = 0;
        while (cur.moveToNext()) {
          String id = cur.getString(
            cur.getColumnIndex(ContactsContract.Contacts._ID));
          String name = cur.getString(cur.getColumnIndex(
            ContactsContract.Contacts.DISPLAY_NAME));

          if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            Cursor pCur = cr.query(
              ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
              null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
              new String[]{id}, null);
            while (pCur.moveToNext()) {
              String phoneNo = pCur.getString(pCur.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER));
              if (phoneNo.contains(" ")) {
                phoneNo = phoneNo.replace(" ", "");
              }
              if (phoneNo.contains("+989")) {
                phoneNo = phoneNo.replace("+98", "0");
              }
              chatListItem = new ChatListItem();
              chatListItem.setTelNo(phoneNo);
              chatListItem.setContactName(name);
              chatListFeed.addItem(chatListItem);
              if (phoneNo.startsWith("09")) {

                try {
                  if (!stringArrayMap.containsKey(phoneNo)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("t", name);
                    jsonObject.put("m", phoneNo);
                    jsonArray.put(count, jsonObject);
                    stringArrayMap.put(phoneNo, true);
                    count++;
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }


              }


            }
            pCur.close();
          }
        }
      }
      ContactDatabase database = new ContactDatabase(context);
      for (int i = 0; i < chatListFeed.getItemCount(); i++) {
        database.createData(chatListFeed.getItem(i).getTelNo(), chatListFeed.getItem(i).getContactName(), false);
      }
      Log.i("JSON CONTACT", jsonArray.toString());
      writeToFile(jsonArray);
      return jsonArray.toString();
    }

    @Override
    protected void onPostExecute(String s) {
      if (s != null) {
        DialogMaker.disMissDialog();
        contacts(s);
      }
      super.onPostExecute(s);
    }
  }

  public String contacts(String s) {
    this.contact = s;
    return contact;
  }

  public String getContacts() {
    return contact;
  }

  private class fillContact extends AsyncTask<Void, Void, ChatListFeed> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();


    }

    @Override
    protected ChatListFeed doInBackground(Void... params) {
      Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
      return parser.checkContactListWithGroup(getContacts());

    }

    @Override
    protected void onPostExecute(ChatListFeed result) {
      if (result != null) {
        startActivity(new Intent(getActivity(), MainActivity.class).putExtra("contact", result));
        getActivity().finish();
      } else
        Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();

      ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);


    }


  }

  public void writeToFile(JSONArray json) {
    ChatListFeed chatListFeed = new ChatListFeed();
    for (int i = 0; i < json.length(); i++) {
      try {
        JSONObject jsonObject = json.getJSONObject(i);
        ChatListItem chatListItem = new ChatListItem();
        chatListItem.setTelNo(jsonObject.getString("m"));
        chatListFeed.addItem(chatListItem);
      } catch (JSONException e) {
        e.printStackTrace();
      }

    }
    new LocalPersistence().writeObjectToFile(getActivity(), chatListFeed, "All_Contact_List");
  }


  public void cancelling() {
    Log.e(TAG, "onCancelled: 6666666");
    ffillContact.cancel(true);
    running = false;
    ((LoginActivity) getActivity()).fragment_status = 0;
    ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
  }
}
