package magia.af.ezpay.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import magia.af.ezpay.ChatPageActivity;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.ContactDatabase;

/**
 * Created by erfan on 10/29/2016.
 */
/*ddd
* */
public class QRCodeDetails extends Fragment {

    Typeface typeface;
    static boolean type_m = false;
    boolean flag = false, commit = false;
    boolean isCommit = false;
    public RelativeLayout waitingDialog;
    public ImageView imageView;
    private String payamount = "", comment = "";
    String contactName;
    String contactName2;
    String phone;
    String contactImage;
    String contactImage2;
    Button btn_pay_and_get;
    private String imageUrl = "http://new.opaybot.ir";
    int pos;
    CircleImageView circleImageView;
    private volatile boolean isOnDestroyCalled = false;

    ChatPageActivity chatPageActivity;


    public static QRCodeDetails newInstance() {


        QRCodeDetails fragmentDemo = new QRCodeDetails();
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_code_result_layout, container, false);
        btn_pay_and_get = (Button) v.findViewById(R.id.btn_pay);
        circleImageView = (CircleImageView) v.findViewById(R.id.user_avatar);
        contactName = getArguments().getString("contactName");
        Log.e("sssss", contactName + "");
        phone = getArguments().getString("phone");
        ContactDatabase database = new ContactDatabase(getActivity());
        contactName2 = database.getNameFromNumber(phone);
        contactImage = imageUrl + getArguments().getString("image");
        contactImage2 = getArguments().getString("image");
        Glide.with(getActivity()).load(contactImage).into(circleImageView);
        pos = getArguments().getInt("pos");
        btn_pay_and_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("contactName", contactName2);
                intent.putExtra("image", contactImage2);
                startActivity(intent);
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(QRCodeDetails.this).commit();
                getActivity().finish();
            }
        });
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        show_dialog_get_phone(v);
//        ((ChatPageActivity) getActivity()).fragment_status = 3;
        return v;
    }

    public void show_dialog_get_phone(final View view) {

        waitingDialog = (RelativeLayout) view.findViewById(R.id.wait_layout);


        EditText edtUsername = (EditText) view.findViewById(R.id.edt_username);
        edtUsername.setText(contactName);

        view.findViewById(R.id.edge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(R.animator.exit_to_right2, R.animator.enter_from_right2).remove(QRCodeDetails.this).commit();
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
