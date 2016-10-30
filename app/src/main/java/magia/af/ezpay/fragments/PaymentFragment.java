package magia.af.ezpay.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;

/**
 * Created by erfan on 10/29/2016.
 */

public class PaymentFragment extends Fragment {

    Typeface typeface;
    static boolean type_m = false;
    boolean flag = false, commit = false;
    public RelativeLayout waitingDialog;
    public ImageView imageView;

    ChatPageActivity chatPageActivity;


    public static PaymentFragment newInstance(boolean type) {
        PaymentFragment fragmentDemo = new PaymentFragment();
        type_m = type;
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.payment_layout, null);
        show_dialog_get_phone(v);
        return v;
    }

    TextView texx;

    public void show_dialog_get_phone(final View view) {

        waitingDialog = (RelativeLayout) view.findViewById(R.id.wait_layout);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        texx = (TextView) view.findViewById(R.id.texx);

        texx.setText(new StringBuilder(((ChatPageActivity) getActivity()).contactName +" " + "پرداخت وجه به"));

//        chatPageActivity.contactName


        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(PaymentFragment.this).commit();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(PaymentFragment.this).commit();

            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        Log.e("((((((((111111111", "onDestroy: ");
        super.onDestroy();
        if (!type_m && commit) {
            Log.e("((((((((11111111", "onDestroy: ");
        }
//            else
//                ((MainActivity)getActivity()).adslLayoutFragment.sendPhoneNumber_Adsl(phone);
//        ((MainActivity) getActivity()).fragment_status = 0;
        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.GONE);
    }

}
