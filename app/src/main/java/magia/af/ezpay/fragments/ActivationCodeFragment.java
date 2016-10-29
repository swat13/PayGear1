package magia.af.ezpay.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import magia.af.ezpay.FriendListActivity;
import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.LocalPersistence;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.interfaces.EventCallbackHandler;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class ActivationCodeFragment extends Fragment implements View.OnClickListener, EventCallbackHandler {
    private Button btn_send_activation_code_again;
    private ImageButton btn_send_activation_code;
    private EditText edtInputPhoneNumber;
    private TextView timerText;
    private String phone;
    private String activationCode;

    public static ActivationCodeFragment getInstance() {

        return new ActivationCodeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activation_code, null);
        btn_send_activation_code_again = (Button) rootView.findViewById(R.id.btn_send_activation_code_again);
        btn_send_activation_code = (ImageButton) rootView.findViewById(R.id.btn_send_activation_code);
        edtInputPhoneNumber = (EditText) rootView.findViewById(R.id.edt_input_activation_code);
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
                activationCode = edtInputPhoneNumber.getText().toString();
                Log.e("((((((((((", "onClick: 00000");
                new verifyActiveCode().execute(phone, edtInputPhoneNumber.getText().toString());
                break;


        }
    }

    @Override
    public void callback() {
        //Handle Callback here
    }

    private class verifyActiveCode extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((LoginActivity) getActivity()).imageView);
            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);
        }

        @Override
        protected String doInBackground(String... params) {
            DOMParser domParser = new DOMParser();
//      JSONObject object = new JSONObject();
//      PullJSON pullJSON;
//      try {
//        object.put("mobile",phone);
//        object.put("code",activationCode);
//        pullJSON = new PullJSON("http://new.opaybot.ir/api/Account/VerifySMSCode" , object.toString() , "POST",null);
//        pullJSON.getResponse();
//        //Log.i("TOKEN", object.toString());
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
            return domParser.Verify_Login_Activation_Code(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("11111111111111", "onPostExecute: " + result);
            if (result.length() > 10) {
                Log.e("55555555555", "onPostExecute: " + result);
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("token", result).apply();
                new fillContact().execute();
            } else if (result.equals("wrong")) {
                Toast.makeText(getActivity(), "کد فعال سازی صحیح نمی باشد!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class fillContact extends AsyncTask<Void, Void, RSSFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RSSFeed doInBackground(Void... params) {
            DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.getContact(new GetContact().getContact(getActivity()));
        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
            if (result != null) {
                startActivity(new Intent(getActivity(), FriendListActivity.class).putExtra("contact", result));
            } else
                Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();

        }

    }



}
