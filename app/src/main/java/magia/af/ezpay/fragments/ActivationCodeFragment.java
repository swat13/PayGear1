package magia.af.ezpay.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.R;
import magia.af.ezpay.Splash;
import magia.af.ezpay.helper.GetContact;
import magia.af.ezpay.interfaces.EventCallbackHandler;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class ActivationCodeFragment extends Fragment implements View.OnClickListener, EventCallbackHandler {
    private Button btn_send_activation_code_again;
    private ImageButton btn_send_activation_code;
    private EditText edtInputPhoneNumber, nameEditText;
    private TextView timerText;
    private String phone;
    private String activationCode;
    private String newName;
    public volatile boolean running = true;
    public fillContact ffillContact;


    public static ActivationCodeFragment getInstance() {

        return new ActivationCodeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_activation_code, null);
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
                new verifyActiveCode().execute(phone, edtInputPhoneNumber.getText().toString(), nameEditText.getText().toString());
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
            DOMParser domParser = new DOMParser();
            /*while (running) {
                if (isCancelled()) {
                    return null;

                }
            }*/
            return domParser.Verify_Login_Activation_Code(params[0], params[1], params[2]);
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

    private class fillContact extends AsyncTask<Void, Void, RSSFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected RSSFeed doInBackground(Void... params) {



            DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));

            /*while (running) {
                if (isCancelled()) {
                    return null;

                }
            }*/
            return domParser.checkContactListWithGroup(new GetContact().getContact(getActivity()));

        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            if (result != null) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra("contact", result));
                getActivity().finish();
            } else
                Toast.makeText(getActivity(), "problem in connection!", Toast.LENGTH_SHORT).show();

            ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);


        }


    }


    public void cancelling() {
        Log.e(TAG, "onCancelled: 6666666");
        ffillContact.cancel(true);
        running = false;
        ((LoginActivity) getActivity()).fragment_status = 0;
        ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
    }
}
