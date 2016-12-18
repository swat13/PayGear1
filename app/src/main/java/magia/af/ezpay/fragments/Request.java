package magia.af.ezpay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.NumberTextWatcher;

/**
 * Created by erfan on 10/29/2016.
 */
/*ddd
* */
public class Request extends Fragment {

    Typeface typeface;
    static boolean type_m = false;
    boolean flag = false, commit = false;
    boolean isCommit = false;
    public RelativeLayout waitingDialog;
    public ImageView imageView;
    private String payamount = "", comment = "";

    ChatPageActivity chatPageActivity;


    public static Request newInstance() {


        Request fragmentDemo = new Request();
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_payment_layout, null);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        show_dialog_get_phone(v);
        ((ChatPageActivity) getActivity()).fragment_status = 3;
        return v;
    }

    public void show_dialog_get_phone(final View view) {

        waitingDialog = (RelativeLayout) view.findViewById(R.id.wait_layout);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        final EditText PayAmount = (EditText) view.findViewById(R.id.payAmount);
        final EditText Comments = (EditText) view.findViewById(R.id.comments);


        PayAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    PayAmount.setGravity(Gravity.LEFT);
                    PayAmount.setHint("");
                } else {
                    if (PayAmount.getText().length() == 0) {
                        PayAmount.setGravity(Gravity.CENTER);
                        PayAmount.setHint("مبلغ پرداختی");
                    }
                }
            }
        });


        Comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Comments.setGravity(Gravity.RIGHT);
                    Comments.setHint("");
                } else {
                    if (Comments.getText().length() == 0) {
                        Comments.setGravity(Gravity.CENTER);
                        Comments.setHint("توضیحات");
                    }
                }
            }
        });


        PayAmount.addTextChangedListener(new NumberTextWatcher(PayAmount));

        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Request.this).commit();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Request.this).commit();

            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("PayAmount", PayAmount.getText().toString());
                Log.e("Comments", Comments.getText().toString());


                if (PayAmount.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "مبلغ وارد شده نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (Comments.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "توضیحات نمیتواند خالی باشد!", Toast.LENGTH_SHORT).show();
                } else if (!validate_number(PayAmount.getText().toString())) {
                    Toast.makeText(getActivity(), "مبلغ وارد شده صحیح نمیباشد!", Toast.LENGTH_SHORT).show();
                } else {
                    isCommit = true;
                    payamount = PayAmount.getText().toString();
                    comment = Comments.getText().toString();
                    hideKey(view);
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Request.this).commit();


                }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
    }


    private boolean validate_number(String number) {
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) != '0' && number.charAt(i) != '1' && number.charAt(i) != '2' && number.charAt(i) != '3'
                    && number.charAt(i) != '4' && number.charAt(i) != '5' && number.charAt(i) != '6' && number.charAt(i) != '7'
                    && number.charAt(i) != '8' && number.charAt(i) != '9' && number.charAt(i) != ',') {
                return false;
            }
        }
        return true;
    }

    private int correctNum(String number) {
        /*String result = number;
            if (number.charAt(i) == ',') {
                result = number.substring(0, i) + number.substring(i + 1);
            }
        }*/
        return Integer.valueOf(number.replace(",", ""));
    }


    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();


        if (isCommit) {

            ((ChatPageActivity) getActivity()).sendReqPay(correctNum(payamount), comment);
        }


        ((ChatPageActivity) getActivity()).fragment_status = 0;
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.GONE);


    }

}
