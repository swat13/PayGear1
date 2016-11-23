package magia.af.ezpay.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.R;


/**
 * Created by pc on 11/9/2016.
 */

public class ProfileFragment extends Fragment {
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

            Log.e("ImageString","http://new.opaybot.ir" + Ipath.replace("\"","") );

            Glide.with(getActivity())
                    .load("http://new.opaybot.ir" + Ipath.replace("\"",""))
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

    Bitmap thumbnail;

    private File onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }

    @SuppressWarnings("deprecation")
    private File onSelectFromGalleryResult(Intent data) {
        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return destination;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private class AsyncInsertUserImage extends AsyncTask<File, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((MainActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((MainActivity) getActivity()).imageView);
            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

        }

        @Override
        protected String doInBackground(File... params) {
            DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            try {
                return domParser.changeUserImage(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Glide.with(getActivity())
                        .load("http://new.opaybot.ir" + result.replace("\"",""))
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

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 300;
        int targetHeight = 300;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public class getAccount extends AsyncTask<Void, Void, RSSItem> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ((FriendListActivity) getActivity()).waitingDialog.setVisibility(View.VISIBLE);
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((FriendListActivity) getActivity()).imageView);
//            Glide.with(getActivity()).load(R.drawable.gif_loading).into(imageViewTarget);

        }

        @Override
        protected RSSItem doInBackground(Void... params) {
            DOMParser domParser = new DOMParser(getActivity().getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.getAccount();
        }

        @Override
        protected void onPostExecute(RSSItem result) {
            Log.e("jsons", String.valueOf(result));


            if (result != null) {
                Ipath = result.getContactImg();
                Glide.with(getActivity())
                        .load("http://new.opaybot.ir" + Ipath.replace("\"",""))
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
