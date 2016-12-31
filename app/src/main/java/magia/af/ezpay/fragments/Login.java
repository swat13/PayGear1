package magia.af.ezpay.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.Parser.JSONParser;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.R;
import magia.af.ezpay.Utilities.Constant;
import magia.af.ezpay.Utilities.DialogMaker;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class Login extends Fragment implements View.OnClickListener {
    private ImageButton btn_done;
    private String phone;
    private Dialog dialog;
    private EditText edtInputPhoneNumber;
//تت

    public static Login getInstance() {
        return new Login();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);
        Log.e("77777777777", "onCreateView: " );
        ((LoginActivity) getActivity()).fragment_status = 1;
        btn_done = (ImageButton) rootView.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
        edtInputPhoneNumber = (EditText) rootView.findViewById(R.id.edt_input_phone_number);
        edtInputPhoneNumber = (EditText) rootView.findViewById(R.id.edt_input_phone_number);

        edtInputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (edtInputPhoneNumber.getText().length() == 11) {
                    btn_done.setVisibility(View.VISIBLE);
                    hideKey(edtInputPhoneNumber);
                } else
                    btn_done.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }

    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onClick(View v) {
        //Handle All View Click Here
        switch (v.getId()) {
            case R.id.btn_done:
//                ((LoginActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
                phone = edtInputPhoneNumber.getText().toString();
                JSONParser parser = JSONParser.connect(Constant.REGISTER);
                parser.setRequestMethod(JSONParser.POST);
                parser.setReadTimeOut(20000);
                parser.setConnectionTimeOut(20000);
                JSONObject object = new JSONObject();
                try {
                    object.put("mobile",phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                parser.setJson(object.toString());
                parser.execute(new JSONParser.Execute() {

                    @Override
                    public void onPreExecute(){
                        DialogMaker.makeDialog(getActivity()).showDialog();
                    }


                    @Override
                    public void onPostExecute(String s) {
                        Log.e("STRING S", "onPostExecute: " + s);
                        if (s!=null){
                            DialogMaker.disMissDialog();
                            ((LoginActivity)getActivity()).loadActiveFragment(phone);
                        }
                        else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DialogMaker.disMissDialog();
                                    Toast.makeText(getActivity(), "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                                }
                            }, 3000);
                        }
                    }
                });
//                new registration().execute(edtInputPhoneNumber.getText().toString());
                break;
        }

    }

    private class registration extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Parser parser = new Parser();
            return parser.register(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((LoginActivity) getActivity()).imageView);
            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ((LoginActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
            Log.e("^^^^^^^^^", "onPostExecute: " + result);
            if (result) {
//                Bundle bundle = new Bundle();
//                bundle.putString("number", phone);
//                Log.i("Input phone", phone);
//                ActivationCode activationCode = ActivationCode.getInstance();
//                activationCode.setArguments(bundle);
//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.container, activationCode).addToBackStack(null)
//                        .commit();

                ((LoginActivity)getActivity()).loadActiveFragment(phone);


            } else {
                Toast.makeText(getActivity(), "مشکل در برقراری ارتباط!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
