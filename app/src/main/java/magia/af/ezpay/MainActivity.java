package magia.af.ezpay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.fragments.BarCodeGet;
import magia.af.ezpay.fragments.FriendsListFragment;
import magia.af.ezpay.fragments.ProfileFragment;

/**
 * Created by erfan on 11/3/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {


    public RelativeLayout darkDialog, waitingDialog;
    public FriendsListFragment friendsListFragment;
    public BarCodeGet barCodeGet;
    public LinearLayout friendsLayout, barcodeReader, profileLayout;
    public int fragment_status = 0;
    RSSFeed _feed;
    public String description;
    public int amount;
    private int position;
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        waitingDialog = (RelativeLayout) findViewById(R.id.wait_layout);
        friendsLayout = (LinearLayout) findViewById(R.id.friends_layout);
        barcodeReader = (LinearLayout) findViewById(R.id.barcode_reader);
        profileLayout = (LinearLayout) findViewById(R.id.profile_layout);
        imageView = (ImageView) findViewById(R.id.image_view);


        _feed = (RSSFeed) getIntent().getSerializableExtra("contact");


        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            friendsListFragment = new FriendsListFragment().getInstance(_feed);
            fm.beginTransaction().replace(R.id.detail_fragment, friendsListFragment).addToBackStack(null).commit();
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.friends_layout:

                friendsListFragment = FriendsListFragment.getInstance(_feed);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, friendsListFragment)
                        .addToBackStack(null)
                        .commit();

                friendsLayout.setAlpha((float) 1);
                barcodeReader.setAlpha((float) 0.45);
                profileLayout.setAlpha((float) 0.45);

                break;

            case R.id.barcode_reader:

                barCodeGet = BarCodeGet.getInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", _feed);
                BarCodeGet barCodeGet = new BarCodeGet();
                barCodeGet.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.detail_fragment, barCodeGet)
                        .commit();

                friendsLayout.setAlpha((float) 0.45);
                barcodeReader.setAlpha((float) 1);
                profileLayout.setAlpha((float) 0.45);

                break;

            case R.id.profile_layout:
                barCodeGet = BarCodeGet.getInstance();
                getSupportFragmentManager().beginTransaction().remove(barCodeGet).commit();
                ProfileFragment profileFragment = new ProfileFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, profileFragment)
                        .commit();
                friendsLayout.setAlpha((float) 0.45);
                barcodeReader.setAlpha((float) 0.45);
                profileLayout.setAlpha((float) 1);
                break;
            case R.id.rss_feed:
                startActivity(new Intent(this , RadarActivity.class));

//      case R.id.barcode_reader1:
//
//        barCodeGet = new BarCodeGet().getInstance();
//        getFragmentManager().beginTransaction().replace(R.id.detail_fragment, barCodeGet).addToBackStack(null).commit();
//
//        friendsLayout.setAlpha((float) 0.45);
//        barcodeReader.setAlpha((float) 0.45);
//        barcodeGet.setAlpha((float)1);

//        break;

            default:
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10) {
                description = data.getStringExtra("description");
                amount = data.getIntExtra("amount", 0);
                position = data.getIntExtra("pos", 0);
                friendsListFragment = FriendsListFragment.getInstance(_feed);
                Bundle bundle = new Bundle();
                bundle.putString("description", description);
                bundle.putInt("amount", amount);
                bundle.putInt("pos", position);
                friendsListFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment, friendsListFragment)
                        .addToBackStack(null)
                        .commit();
            }
            Log.e("3333333333", "onActivityResult: "+requestCode);
            switch (requestCode) {
                case 0:
                    Log.e("#######", "onActivityResult: 0000"+data.getData() );
                    File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
//                    CallAsync(Uri.fromFile(file));
                    break;
                case 1:
//                    CallAsync(tempUri);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("Finish", "onBackPressed: ");
        finish();

    }

}
