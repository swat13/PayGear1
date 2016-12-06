package magia.af.ezpay;

import android.app.Activity;
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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import magia.af.ezpay.Parser.DOMParser;
import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.GetContact;

public class CreateGroupActivity extends BaseActivity {

    ArrayList<RSSItem> rssFeed;
    RSSFeed databaseRssFeed;
    EditText groupTitle;
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<String> phone = new ArrayList<>();
    private String imageUrl = "http://new.opaybot.ir";
    ContactDatabase database;
    RecyclerAdapter adapter;
    JSONArray jsonArray;
    String json;
    RSSFeed groupMember;
    ImageView groupAvatar;
    String TAG = "TAG";
    Bitmap thumbnail;
    int flag;
    Intent mData;
    RSSFeed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        rssFeed = (ArrayList<RSSItem>) getIntent().getSerializableExtra("contact");
        for (int i = 0; i < rssFeed.size(); i++) {
            Log.e(TAG, "onCreate: " + rssFeed.get(i).getTitle());
            Log.e(TAG, "onCreate: " + rssFeed.get(i).getTelNo());
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            json = bundle.getString("json");
        }
        Log.e(TAG, "onCreate: " + json);
//    database = new ContactDatabase(this);
//    databaseRssFeed = database.getInNetworkUserName();
        groupMember = new RSSFeed();

        try {
            jsonArray = new JSONArray(json);
            Log.e(TAG, "onCreate: " + jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                RSSItem rssItem = new RSSItem();
                for (int j = 0; j < rssFeed.size(); j++) {
                    if (jsonArray.getString(i).equals(rssFeed.get(j).getTelNo())) {
                        Log.e(TAG, "onCreate: " + imageUrl + rssFeed.get(j).getContactImg());
                        rssItem.setContactName(rssFeed.get(j).getTitle());
                        rssItem.setTelNo(rssFeed.get(j).getTelNo());
                        rssItem.setContactImg(rssFeed.get(j).getContactImg());
                    }
                }
                groupMember.addItem(rssItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        groupTitle = (EditText) findViewById(R.id.edt_group_title);
        recyclerView = (RecyclerView) findViewById(R.id.contact_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        imageView = (ImageView) findViewById(R.id.btn_done);
        groupAvatar = (ImageView) findViewById(R.id.groupAvatar);
        groupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(CreateGroupActivity.this);
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
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupTitle.getText().toString().length() < 1) {
                    Toast.makeText(CreateGroupActivity.this, "لطفا عنوان گروه را وارد کنید", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("JSONArray", "onClick: " + jsonArray.toString());
                    new CreateGroup(groupTitle.getText().toString(), jsonArray).execute();
                }
            }
        });
    }

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
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
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
                    this.mData = data;
                    Glide.with(CreateGroupActivity.this)
                            .load(onCaptureImageResult(data))
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.drawable.pic_profile)
                            .into(new BitmapImageViewTarget(groupAvatar) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCornerRadius(700);
                                    groupAvatar.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                    flag = 0;
//          new AsyncInsertUserImage().execute(onCaptureImageResult(data));
//          new CreateGroup(groupTitle.getText().toString(),jsonArray).execute();
                    break;
                case 1:
                    this.mData = data;
                    flag = 1;
//          new CreateGroup(groupTitle.getText().toString(),jsonArray).execute();
//          new AsyncInsertUserImage().execute(onSelectFromGalleryResult(data));
                    Glide.with(CreateGroupActivity.this)
                            .load(onSelectFromGalleryResult(data))
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.drawable.pic_profile)
                            .into(new BitmapImageViewTarget(groupAvatar) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCornerRadius(700);
                                    groupAvatar.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ChooseFriendsActivity.class);
        startActivity(intent);
        finish();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_contact_item, parent, false);
            return new RecyclerAdapter.ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
            Log.e(TAG, "onBindViewHolder: " + groupMember.getItem(position).getContactName());
            holder.txt_contact_item_name.setText(groupMember.getItem(position).getContactName());
            holder.txt_contact_item_phone.setText(groupMember.getItem(position).getTelNo());
            Glide.with(CreateGroupActivity.this)
                    .load(imageUrl + groupMember.getItem(position).getContactImg())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.pic_profile)
                    .into(new BitmapImageViewTarget(holder.contact_item_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(700);
                            holder.contact_item_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return groupMember.getItemCount();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_contact_item_name;
            TextView txt_contact_item_phone;
            ImageView contact_item_image;
            ImageView status_circle;

            public ViewHolder(View itemView) {
                super(itemView);
                txt_contact_item_name = (TextView) itemView.findViewById(R.id.txt_contact_item_name);
                txt_contact_item_phone = (TextView) itemView.findViewById(R.id.txt_contact_item_phone);
                contact_item_image = (ImageView) itemView.findViewById(R.id.contact_img);
                status_circle = (ImageView) itemView.findViewById(R.id.status_circle);
            }
        }
    }

    public class CreateGroup extends AsyncTask<String, Void, RSSFeed> {
        String title;
        JSONArray jsonArray;

        public CreateGroup(String title, JSONArray jsonArray) {
            this.title = title;
            this.jsonArray = jsonArray;
        }

        @Override
        protected RSSFeed doInBackground(String... params) {
            DOMParser parser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.group(title, jsonArray);
        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            if (result != null) {
                CreateGroupActivity.this.feed = result;
                Log.e(TAG, "onPostExecute: " + feed.getItemCount());
                Log.e(TAG, "onPostExecute: " + feed.getItem(0).getGroupId());
                Log.e(TAG, "onPostExecute: " + feed.getItem(0).getGroupTitle());
                Log.e(TAG, "onPostExecute: " + feed.getItem(0).getGroupPhoto());
                if (flag == 0) {
                    if (mData != null) {
                        new AsyncInsertUserImage(onCaptureImageResult(mData), result.getItem(0).getGroupId()).execute();
                    } else {
                        Intent intent = new Intent(CreateGroupActivity.this, GroupChatPageActivity.class);
                        intent.putExtra("title", feed.getItem(0).getGroupTitle());
                        intent.putExtra("photo", "http://new.opaybot.ir" + feed.getItem(0).getGroupPhoto().replace("\"", ""));
                        intent.putExtra("id", feed.getItem(0).getGroupId());
                        intent.putExtra("members", feed.getItem(0).getGroupMembers());
                        startActivity(intent);
                        finish();
                    }
                } else if (flag == 1) {
                    if (mData != null) {
                        new AsyncInsertUserImage(onSelectFromGalleryResult(mData), result.getItem(0).getGroupId()).execute();
                    } else {
                        Intent intent = new Intent(CreateGroupActivity.this, GroupChatPageActivity.class);
                        intent.putExtra("title", feed.getItem(0).getGroupTitle());
                        intent.putExtra("photo", "http://new.opaybot.ir" + feed.getItem(0).getGroupPhoto().replace("\"", ""));
                        intent.putExtra("id", feed.getItem(0).getGroupId());
                        intent.putExtra("members", feed.getItem(0).getGroupMembers());
                        startActivity(intent);
                        finish();
                    }
                }
            } else
                Toast.makeText(CreateGroupActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }
    }

    private class fillContact extends AsyncTask<Void, Void, RSSFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected RSSFeed doInBackground(Void... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return domParser.checkContactListWithGroup(new GetContact().getContact(CreateGroupActivity.this));

        }

        @Override
        protected void onPostExecute(RSSFeed result) {
            if (result != null) {
                if (flag == 0) {
                    new AsyncInsertUserImage(onCaptureImageResult(mData), result.getItem(result.getItemCount() - 1).getGroupId()).execute();
                } else if (flag == 1) {
                    new AsyncInsertUserImage(onSelectFromGalleryResult(mData), result.getItem(result.getItemCount() - 1).getGroupId()).execute();
                }
                startActivity(new Intent(CreateGroupActivity.this, CreateGroupActivity.class).putExtra("contact", result));
                finish();
            } else
                Toast.makeText(CreateGroupActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
        }

    }

    public class AsyncInsertUserImage extends AsyncTask<File, Void, String> {

        File file;
        int id;

        public AsyncInsertUserImage(File file, int id) {
            this.file = file;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(File... params) {
            DOMParser domParser = new DOMParser(getSharedPreferences("EZpay", 0).getString("token", ""));
            try {
                return domParser.changeGroupImage(file, id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("Error")) {
                Glide.with(CreateGroupActivity.this)
                        .load("http://new.opaybot.ir" + result.replace("\"", ""))
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.pic_profile)
                        .into(new BitmapImageViewTarget(groupAvatar) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCornerRadius(700);
                                groupAvatar.setImageDrawable(circularBitmapDrawable);
                            }
                        });

                getSharedPreferences("EZpay", 0).edit().putString("Ipath", result).apply();
                Intent intent = new Intent(CreateGroupActivity.this, GroupChatPageActivity.class);
                intent.putExtra("title", feed.getItem(0).getGroupTitle());
                intent.putExtra("photo", "http://new.opaybot.ir" + result.replace("\"", ""));
                intent.putExtra("id", feed.getItem(0).getGroupId());
                intent.putExtra("members", feed.getItem(0).getGroupMembers());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CreateGroupActivity.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
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

    public void checkPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
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
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
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
}
