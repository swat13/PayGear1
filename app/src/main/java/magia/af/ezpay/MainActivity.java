package magia.af.ezpay;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsListFragment;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    public RelativeLayout darkDialog, waitingDialog;
    public FriendsListFragment friendsListFragment;
    public BarCodeGet barCodeGet;
    public LinearLayout friendsLayout, barcodeGet, barcodeReader;
    public int fragment_status = 0;
    RSSFeed _feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        barcodeGet = (LinearLayout) findViewById(R.id.barcode_reader1);
        barcodeReader = (LinearLayout) findViewById(R.id.barcode_reader);

        _feed = (RSSFeed) getIntent().getSerializableExtra("contact");



        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            friendsListFragment = new FriendsListFragment().getInstance(_feed);
            fm.beginTransaction().replace(R.id.detail_fragment, friendsListFragment).addToBackStack(null).commit();
        }


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.friends_layout:

                friendsListFragment = new FriendsListFragment().getInstance(_feed);
                getFragmentManager().beginTransaction().replace(R.id.detail_fragment, friendsListFragment).addToBackStack(null).commit();

                friendsLayout.setAlpha((float) 1);
                barcodeReader.setAlpha((float) 0.45);
                barcodeGet.setAlpha((float) 0.45);

                break;

            case R.id.barcode_reader:

                startActivity(new Intent(MainActivity.this,SimpleScannerActivity.class));

                break;

            case R.id.barcode_reader1:

                barCodeGet = new BarCodeGet().getInstance();
                getFragmentManager().beginTransaction().replace(R.id.detail_fragment, barCodeGet).addToBackStack(null).commit();


                friendsLayout.setAlpha((float) 0.45);
                barcodeReader.setAlpha((float) 0.45);
                barcodeGet.setAlpha((float) 1);

                break;

            default:
                break;


        }
    }

}
