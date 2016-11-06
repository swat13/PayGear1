package magia.af.ezpay.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.R;

/**
 * Created by erfan on 11/3/2016.
 */

public class BarCodeGet extends android.app.Fragment {

    ImageView imageView;


    public static BarCodeGet getInstance() {
        return new BarCodeGet();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.barcode_scanner, container, false);
        ((MainActivity) getActivity()).fragment_status = 4;
        imageView=(ImageView) v.findViewById(R.id.QrCode);
//        new getQr().execute();
        Glide.with(getActivity()).load("http://new.opaybot.ir/api/QR/"+getActivity().getSharedPreferences("EZpay", 0).getString("id", "")).into(imageView);
        return v;
    }

//
//    public class getQr extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
////            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((FriendListActivity) getActivity()).imageView);
////            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);
//
//        }
//
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
//            domParser.getQRid();
//
//        }
//
//        @Override
//        protected void onPostExecute(String jsons) {
////            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
//
//
//
//
//
//
//
//
//
//        }
//    }


}
