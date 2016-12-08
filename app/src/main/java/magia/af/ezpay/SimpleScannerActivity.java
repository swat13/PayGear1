package magia.af.ezpay;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Parser.Feed;
import magia.af.ezpay.Parser.Item;
import magia.af.ezpay.fragments.QRCodeDetails;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    Feed feed;
    private String id;
    private boolean commit = false;
    int pos;
    public QRCodeDetails qrCodeDetails;
    static boolean isOpen = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Log.e("Created", "baaaar ");
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        // Set the scanner view as the content view

//        darkDialog = (RelativeLayout) findViewById(R.id.dark_dialog);
        feed = (Feed) getIntent().getSerializableExtra("contact");

        Log.e("Feeed", String.valueOf(feed));


    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the feed here
        Log.e("dddd", rawResult.getText()); // Prints scan results
        Log.e("eeeeeeeee", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if (rawResult.getBarcodeFormat().toString().contains("QR_CODE")) {

            if (rawResult.getText().contains("/")) {

                id = rawResult.getText().substring(rawResult.getText().lastIndexOf("/") + 1);
                Log.e("id", "iiiiiiiii: " + id);
                new getAccountId().execute(id);

                // Split it.
            } else {
                throw new IllegalArgumentException("String " + rawResult.getText() + " does not contain /");
            }


        }

        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
    }


    public class getAccountId extends AsyncTask<String, Void, Item> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((FriendListActivity) getActivity()).imageView);
//            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

        }


        @Override
        protected Item doInBackground(String... params) {

            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.getAccountId(params[0]);

        }

        @Override
        protected void onPostExecute(Item jsons) {
            Log.e("jsons", String.valueOf(jsons));

            if (jsons != null) {
                commit = true;

                Log.e("LastFeed", String.valueOf(feed));

                feed.addItem(jsons);
                pos = feed.getItemCount() - 1;
                openDialog();
//                finish();
            } else {
                Log.e(TAG, "onPostExecute: 1111111111");
                Toast.makeText(getApplicationContext(), "Json Is Null!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void openDialog() {
        if (commit) {
            FragmentTransaction ft;
//            darkDialog.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            Log.e("ssssssssssss", "openDialog: " + feed.getItem(pos).getContactName());
            bundle.putString("phone", feed.getItem(pos).getTelNo());
            bundle.putString("contactName", feed.getItem(pos).getContactName());
            bundle.putString("image", feed.getItem(pos).getContactImg());
            bundle.putInt("pos", pos);
            qrCodeDetails = QRCodeDetails.newInstance();
            qrCodeDetails.setArguments(bundle);
            ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
            ft.add(android.R.id.content, qrCodeDetails).commit();
            isOpen = true;
//            Intent goToChatPageActivity = new Intent(this, ChatPageActivity.class);
//            goToChatPageActivity.putExtra("phone", feed.getItem(pos).getTelNo());
//            goToChatPageActivity.putExtra("contactName", feed.getItem(pos).getContactName());
//            goToChatPageActivity.putExtra("image", feed.getItem(pos).getContactImg());
//            goToChatPageActivity.putExtra("pos", pos);
//            startActivityForResult(goToChatPageActivity, 10);


        } else
            finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
//        feed= (Feed) getIntent().getSerializableExtra("contact");
    }

    public boolean isDestroyed(boolean destroy) {
        return destroy;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
//        if (commit) {
//            FragmentTransaction ft;
////            darkDialog.setVisibility(View.VISIBLE);
//            qrCodeDetails = QRCodeDetails.newInstance();
//            ft = getFragmentManager().beginTransaction();
//            ft.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_right);
//            ft.add(android.R.id.content, qrCodeDetails).commit();
//            isOpen = false;
////            Intent goToChatPageActivity = new Intent(this, ChatPageActivity.class);
////            goToChatPageActivity.putExtra("phone", feed.getItem(pos).getTelNo());
////            goToChatPageActivity.putExtra("contactName", feed.getItem(pos).getContactName());
////            goToChatPageActivity.putExtra("image", feed.getItem(pos).getContactImg());
////            goToChatPageActivity.putExtra("pos", pos);
////            startActivityForResult(goToChatPageActivity, 10);
//
//
//        }else
//            finish();

    }
}