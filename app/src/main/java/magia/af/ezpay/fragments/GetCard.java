package magia.af.ezpay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.Toast;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;


public class GetCard extends Fragment {

    Typeface typeface;
    boolean isCommit = false;
    private String card = "";
    static boolean type_m = false;
    static String comment;
    static int amount;
    static int id;

    public static GetCard newInstance(int Amount, String Comment) {
        GetCard fragmentDemo = new GetCard();
        type_m = false;
        amount = Amount;
        comment = Comment;
        return fragmentDemo;
    }

    public static GetCard newInstance(int Id) {
        GetCard fragmentDemo = new GetCard();
        type_m = true;
        id = Id;
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        typeface = Typeface.createFromAsset(getActivity().getAssets(), "IRANSansMobile(FaNum)_Bold.ttf");
        View v = inflater.inflate(R.layout.card_layout, null);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        ((ChatPageActivity) getActivity()).fragment_status = 2;
        show_dialog_get_pay_card(v);
        return v;
    }

    public void show_dialog_get_pay_card(final View view) {

        final EditText card_ed = (EditText) view.findViewById(R.id.payAmount);
        card_ed.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        final EditText pin_ed = (EditText) view.findViewById(R.id.comments);
//        card_ed.setTypeface(typeface);

        card_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    card_ed.setGravity(Gravity.LEFT);
                    card_ed.setHint("");
                } else {
                    if (card_ed.getText().length() == 0) {
                        card_ed.setGravity(Gravity.CENTER);
                        card_ed.setHint("شماره کارت");
                    }
                }
            }
        });
        card_ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_ed.setSelection(card_ed.getText().length());
            }
        });


        pin_ed.setCursorVisible(false);
        pin_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pin_ed.setGravity(Gravity.LEFT);
                    pin_ed.setHint("");
                } else {
                    if (pin_ed.getText().length() == 0) {
                        pin_ed.setGravity(Gravity.CENTER);
                        pin_ed.setHint("رمز دوم");
                    }
                }
            }
        });

        pin_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 0) {
                    pin_ed.setGravity(Gravity.CENTER);
                    pin_ed.setHint("رمز دوم");
                } else {
                    pin_ed.setGravity(Gravity.LEFT);
                    pin_ed.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        card_ed.setCursorVisible(false);
        card_ed.addTextChangedListener(new TextWatcher() {
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
                    pin_ed.requestFocus();
                }
                if (s.length() == 0) {

                    card_ed.setGravity(Gravity.RIGHT);
                    card_ed.setHint("شماره کارت");

                } else {
                    card_ed.setGravity(Gravity.LEFT);
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

        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKey(view);
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(GetCard.this).commit();
//                onDestroy();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKey(view);
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(GetCard.this).commit();
//                onDestroy();
            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card_ed.getText().toString().length() != 19 || pin_ed.getText().toString().length() < 3) {
                    Toast.makeText(getActivity(), "شماره کارت یا رمز کامل نمی باشد!", Toast.LENGTH_SHORT).show();
                } else if (!validate_number(pin_ed.getText().toString()) || !validate_number(card_ed.getText().toString())) {
                    Toast.makeText(getActivity(), "شماره کارت و یا رمز صحیح نمی باشد!", Toast.LENGTH_SHORT).show();
                } else {
                    isCommit = true;
                    card = card_ed.getText().toString() + pin_ed.getText().toString();
                    hideKey(view);
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(GetCard.this).commit();
                }
//                onDestroy();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
    }

    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("00000000", "onDestroy: ))))(((((");
        if (!type_m ) {
            ((ChatPageActivity) getActivity()).fragment_status = 1;
            if (isCommit) {
                ((ChatPageActivity) getActivity()).sendPay(card, comment, amount);
                getActivity().onBackPressed();
            }
        } else {
            ((ChatPageActivity) getActivity()).fragment_status = 0;
            ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.GONE);
            if (isCommit) {
//                ((ChatPageActivity) getActivity()).accPay(id, card);
            }


        }


    }
}
