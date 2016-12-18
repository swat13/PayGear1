package magia.af.ezpay.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import magia.af.ezpay.LoginActivity;
import magia.af.ezpay.MainActivity;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.R;
import magia.af.ezpay.helper.ExifUtils;
import magia.af.ezpay.helper.ImageMaker;


/**
 * Created by pc on 11/9/2016.
 */

public class Profile extends Fragment {
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    private ImageView userAvatar, imageBtn, imageSignOut;
    private TextView contactName, amount, phoneNumber;

    private static final int PICK_IMAGE = 1;
    private Button upload;
    private EditText caption;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    String Qpath;
    String Ipath;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile_backup, container, false);

        userAvatar = (ImageView) rootView.findViewById(R.id.user_avatar);
        imageBtn = (ImageView) rootView.findViewById(R.id.imageButton);
        imageSignOut = (ImageView) rootView.findViewById(R.id.log_out);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }

        contactName = (TextView) rootView.findViewById(R.id.txt_user_name);
        amount = (TextView) rootView.findViewById(R.id.txt_account_availability);
        phoneNumber = (TextView) rootView.findViewById(R.id.txt_phone_number);

        if (getActivity().getSharedPreferences("EZpay", 0).getString("contactName", "").isEmpty()) {
            Log.e("2222222", "Executed");
            new getAccount().execute();
        } else {
            contactName.setText(getActivity().getSharedPreferences("EZpay", 0).getString("contactName", ""));
            amount.setText(String.valueOf(getActivity().getSharedPreferences("EZpay", 0).getInt("amount", 0)));
            phoneNumber.setText(getActivity().getSharedPreferences("EZpay", 0).getString("phoneNumber", ""));
            Ipath = getActivity().getSharedPreferences("EZpay", 0).getString("Ipath", "");

            Log.e("ImageString", "http://new.opaybot.ir" + Ipath.replace("\"", ""));

            Glide.with(getActivity())
                    .load("http://new.opaybot.ir" + Ipath.replace("\"", ""))
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.pic_profile)
                    .into(new BitmapImageViewTarget(userAvatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(700);
                            userAvatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });


        }


        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Get photo from gallery or take picture ?");
//                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Import from gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);//
                                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                            }
                        });

                builder1.setNegativeButton(
                        "Take picture",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, 0);
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });


        imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSharedPreferences("EZpay", 0).edit().remove("token").apply();
                Log.e("Token :", getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();

            }
        });

        return rootView;
    }

    public void checkPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 0);
    }

    Bitmap thumbnail;

    private String onCaptureImageResult(Intent data) {
        if (data!=null) {
            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] byteArray = bytes.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return encoded;
        }
        else
            return null;
    }

    @SuppressWarnings("deprecation")
    private String onSelectFromGalleryResult(Intent data) {


        thumbnail = null;
        if (data != null) {

            ImageMaker imageMaker=new ImageMaker(getActivity().getApplicationContext(),data);
            return imageMaker.onSelectFromGalleryResult();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("11111111", "onActivityResult: 0000" + resultCode);
        Log.e("22222222", "onActivityResult: 1111" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult: "+imageReturnedIntent.getData() );
//            Uri tempUri = getImageUri(getActivity(), bitmap);
            switch (requestCode) {
                case 0:
                    new AsyncInsertUserImage().execute(onCaptureImageResult(data));
                    break;
                case 1:
                    new AsyncInsertUserImage().execute(onSelectFromGalleryResult(data));
                    break;
                default:
                    break;
            }
        }
    }

    private class AsyncInsertUserImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((MainActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((MainActivity) getActivity()).imageView);
            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

        }

        @Override
        protected String doInBackground(String... params) {
            Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            try {
                return parser.changeUserImage(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Glide.with(getActivity())
                        .load("http://new.opaybot.ir" + result.replace("\"", ""))
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.pic_profile)
                        .into(new BitmapImageViewTarget(userAvatar) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCornerRadius(700);
                                userAvatar.setImageDrawable(circularBitmapDrawable);
                            }
                        });

                getActivity().getSharedPreferences("EZpay", 0).edit().putString("Ipath", result).apply();
            } else {
                Toast.makeText(getActivity(), "Error In Connection", Toast.LENGTH_SHORT).show();
            }

            ((MainActivity) getActivity()).waitingDialog.setVisibility(View.GONE);
        }
    }

    public class getAccount extends AsyncTask<Void, Void, ChatListItem> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((FriendListActivity) getActivity()).imageView);
//            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

        }

        @Override
        protected ChatListItem doInBackground(Void... params) {
            Parser parser = new Parser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.getAccount();
        }

        @Override
        protected void onPostExecute(ChatListItem result) {
            Log.e("jsons", String.valueOf(result));


            if (result != null) {
                Ipath = result.getContactImg();
                Glide.with(getActivity())
                        .load("http://new.opaybot.ir" + Ipath.replace("\"", ""))
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.pic_profile)
                        .into(new BitmapImageViewTarget(userAvatar) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCornerRadius(700);
                                userAvatar.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                contactName.setText(result.getContactName());
                amount.setText(String.valueOf(result.getCredit()));
                phoneNumber.setText(result.getTelNo());
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("contactName", result.getContactName()).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putInt("amount", result.getCredit()).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("phoneNumber", result.getTelNo()).apply();
                getActivity().getSharedPreferences("EZpay", 0).edit().putString("Ipath", result.getContactImg()).apply();


//                finish();
            } else {

                Toast.makeText(getActivity(), "Json Is Null!", Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}



