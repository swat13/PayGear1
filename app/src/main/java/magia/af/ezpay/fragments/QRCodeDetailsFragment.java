package magia.af.ezpay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;

/**
 * Created by erfan on 10/29/2016.
 */
/*ddd
* */
public class QRCodeDetailsFragment extends Fragment {

    Typeface typeface;
    static boolean type_m = false;
    boolean flag = false, commit = false;
    boolean isCommit = false;
    public RelativeLayout waitingDialog;
    public ImageView imageView;
    private String payamount = "", comment = "";
    String contactName;
    String phone;
    String contactImage;
    int pos;
    private volatile boolean isOnDestroyCalled = false;

    ChatPageActivity chatPageActivity;


    public static QRCodeDetailsFragment newInstance() {


        QRCodeDetailsFragment fragmentDemo = new QRCodeDetailsFragment();
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_code_result_layout, container , false);
        contactName = getArguments().getString("contactName");
        Log.e("sssss", contactName + "");
        phone = getArguments().getString("phone");
        contactImage = getArguments().getString("image");
        pos = getArguments().getInt("pos");
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        show_dialog_get_phone(v);
//        ((ChatPageActivity) getActivity()).fragment_status = 3;
        return v;
    }

    public void show_dialog_get_phone(final View view) {

        waitingDialog = (RelativeLayout) view.findViewById(R.id.wait_layout);

        EditText edtUsername = (EditText)view.findViewById(R.id.edt_username);
        edtUsername.setText(contactName);

        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(QRCodeDetailsFragment.this).commit();
                getActivity().finish();
            }
        });

    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.VISIBLE);
//    }



    public void hideKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


//    public boolean isDestroyed(){
//        return this.isOnDestroyCalled;
//    }

    //    @Override
//    public void onDestroy() {
//        Log.e("((((((((111111111", "onDestroy: ");
//        super.onDestroy();
//
//
//        if (isCommit) {
//
//            ((ChatPageActivity) getActivity()).sendReqPay(correctNum(payamount), comment);
//        }
//
//
//        ((ChatPageActivity) getActivity()).fragment_status = 0;
//        ((ChatPageActivity) getActivity()).darkDialog.setVisibility(View.GONE);
//
//
//    }

}
