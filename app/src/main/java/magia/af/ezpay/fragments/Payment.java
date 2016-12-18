package magia.af.ezpay.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.NumberTextWatcher;

/**
 * Created by erfan on 10/29/2016.
 */
/*ddd
* */
public class Payment extends Fragment {

    Typeface typeface;
    static boolean type_m = false;
    boolean flag = false, commit = false;
    boolean isCommit = false;
    public RelativeLayout waitingDialog;
    public ImageView imageView;
    public TextView texx;
    private String payamount = "", comment = "";

    ChatPageActivity chatPageActivity;


    public static Payment newInstance(boolean type) {


        Payment fragmentDemo = new Payment();
        type_m = type;
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.payment_layout, null);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        show_dialog_get_phone(v);
        ((ChatPageActivity) getActivity()).fragment_status = 1;
        return v;
    }

    public void show_dialog_get_phone(final View view) {

        waitingDialog = (RelativeLayout) view.findViewById(R.id.wait_layout);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        texx = (TextView) view.findViewById(R.id.texx);
        final EditText PayAmount = (EditText) view.findViewById(R.id.payAmount);
        final EditText Comments = (EditText) view.findViewById(R.id.comments);
        Comments.setImeOptions(EditorInfo.IME_ACTION_DONE);
        texx.setText(new StringBuilder("پرداخت وجه به" + " " + ((ChatPageActivity) getActivity()).contactName));

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


        PayAmount.addTextChangedListener(new TextWatcher() {
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

                    PayAmount.setGravity(Gravity.CENTER);
                    PayAmount.setHint("مبلغ پرداختی");



                } else {
                    PayAmount.setGravity(Gravity.LEFT);
                    if (s.length() > 0 && (s.length() % 4) == 0) {
                        final char c = s.charAt(s.length() - 3);
                        if (space == c) {
                            s.delete(s.length() -3, s.length()-2);
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
        PayAmount.addTextChangedListener(new NumberTextWatcher(PayAmount));

        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Payment.this).commit();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Payment.this).commit();

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
                    payamount = PayAmount.getText().toString();
                    comment = Comments.getText().toString();
                    hideKey(view);
//                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(Payment.this).commit();
//                    ((ChatPageActivity) getActivity()).payBill(correctNum(payamount), comment);

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
        return Integer.valueOf(number.replace("," , ""));
    }


    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        Log.e("((((((((111111111", "onDestroy: ");
        super.onDestroy();

        ((ChatPageActivity) getActivity()).fragment_status = 0;
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.GONE);


    }

}
