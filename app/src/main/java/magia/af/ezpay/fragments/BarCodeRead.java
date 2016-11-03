package magia.af.ezpay.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import magia.af.ezpay.R;
import magia.af.ezpay.zxing.IntentIntegrator;
import magia.af.ezpay.zxing.IntentResult;

/**
 * Created by erfan on 11/2/2016.
 */

public class BarCodeRead extends android.support.v4.app.Fragment {

    public static BarCodeRead getInstance() {
        BarCodeRead barcode=new BarCodeRead();

        return barcode;




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.barcode_scanner,container,false);

//        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
//        scanIntegrator.initiateScan();





        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);

        }



        return rootView;

    }


}
