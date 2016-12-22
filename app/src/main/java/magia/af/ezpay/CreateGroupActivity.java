package magia.af.ezpay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
import java.io.IOException;
import java.util.ArrayList;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.GroupItem;
import magia.af.ezpay.Parser.MembersItem;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.Utilities.ApplicationData;
import magia.af.ezpay.helper.ContactDatabase;
import magia.af.ezpay.helper.ExifUtils;
import magia.af.ezpay.helper.ImageMaker;

public class CreateGroupActivity extends BaseActivity {

    ArrayList<ChatListItem> rssFeed;
    ArrayList<ChatListItem> rssFeed2;
    ChatListFeed databaseChatListFeed;
    EditText groupTitle;
    ChatListFeed _ChatList_Feed;
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<String> phone = new ArrayList<>();
    private String imageUrl = "http://new.opaybot.ir";
    ContactDatabase database;
    RecyclerAdapter adapter;
    JSONArray jsonArray;
    String json;
    ChatListFeed groupMember;
    MembersItem membersItem;
    ImageView groupAvatar;
    String TAG = "TAG";
    Bitmap thumbnail;
    int flag;
    Intent mData;
    GroupItem groupItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        rssFeed = (ArrayList<ChatListItem>) getIntent().getSerializableExtra("contact");
        rssFeed2 = (ArrayList<ChatListItem>) getIntent().getSerializableExtra("contact2");
        _ChatList_Feed = (ChatListFeed) getIntent().getSerializableExtra("contact3");
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
//    databaseChatListFeed = database.getInNetworkUserName();
        groupMember = new ChatListFeed();
        membersItem = new MembersItem();
        try {
            jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ChatListItem chatListItem = new ChatListItem();
                for (int j = 0; j < rssFeed.size(); j++) {
                    if (jsonArray.getString(i).equals(rssFeed.get(j).getTelNo())) {
                        chatListItem.setContactName(rssFeed.get(j).getTitle());
                        chatListItem.setTelNo(rssFeed.get(j).getTelNo());
                        chatListItem.setContactImg(rssFeed.get(j).getContactImg());
                    }
                }
                groupMember.addItem(chatListItem);
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
                    imageView.setEnabled(false);
                    new CreateGroup(groupTitle.getText().toString(), jsonArray).execute();
                }
            }
        });
    }

    private String onCaptureImageResult(Intent data) {
        if (data != null) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


            byte[] byteArray = bytes.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return encoded;

        }
        return null;

    }



    @SuppressWarnings("deprecation")
    private String onSelectFromGalleryResult(Intent data) {
        thumbnail = null;
        if (data != null) {

            ImageMaker imageMaker=new ImageMaker(getApplicationContext(),data);
            return imageMaker.onSelectFromGalleryResult();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("11111111", "onActivityResult: 0000" + resultCode);
        Log.e("22222222", "onActivityResult: 1111" + requestCode);
        if (resultCode == Activity.RESULT_OK) {

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
                    break;
                case 1:
                    this.mData = data;
                    flag = 1;
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
        Intent intent = new Intent(this, ChooseMemberActivity.class);
        intent.putExtra("contact", rssFeed2);
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

    public class CreateGroup extends AsyncTask<String, Void, GroupItem> {
        String title;
        JSONArray jsonArray;

        public CreateGroup(String title, JSONArray jsonArray) {
            this.title = title;
            this.jsonArray = jsonArray;
        }

        @Override
        protected GroupItem doInBackground(String... params) {
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.group(title, jsonArray);
        }

        @Override
        protected void onPostExecute(GroupItem result) {
            if (result != null) {
                new fillContact().execute("[]");
                groupItem = result;
                if (flag == 0) {
                    if (mData != null) {
                        new AsyncInsertGpImage().execute(onCaptureImageResult(mData), result.getGroupId()+"");
                    } else {
                        Intent intent = new Intent(CreateGroupActivity.this, GroupChatPageActivity.class);
                        intent.putExtra("title", groupItem.getGroupTitle());
                        intent.putExtra("photo", "http://new.opaybot.ir" + groupItem.getGroupPhoto().replace("\"", ""));
                        intent.putExtra("id", groupItem.getGroupId());
                        intent.putExtra("members", groupItem.getMembersFeed());
                        intent.putExtra("contact", _ChatList_Feed);
                        startActivity(intent);
                        finish();
                    }
                } else if (flag == 1) {
                    if (mData != null) {
                        new AsyncInsertGpImage().execute(onSelectFromGalleryResult(mData), result.getGroupId()+"");
                    } else {
                        Intent intent = new Intent(CreateGroupActivity.this, GroupChatPageActivity.class);
                        intent.putExtra("title", groupItem.getGroupTitle());
                        intent.putExtra("photo", "http://new.opaybot.ir" + groupItem.getGroupPhoto().replace("\"", ""));
                        intent.putExtra("id", groupItem.getGroupId());
                        intent.putExtra("members", groupItem.getMembersFeed());
                        intent.putExtra("contact", _ChatList_Feed);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                imageView.setEnabled(true);
                Toast.makeText(CreateGroupActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();

            }
            super.onPostExecute(result);
        }
    }

    public class AsyncInsertGpImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            try {
                return parser.changeGroupImage(params[0], params[1]);
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
                intent.putExtra("title", groupItem.getGroupTitle());
                intent.putExtra("photo", "http://new.opaybot.ir" + result.replace("\"", ""));
                intent.putExtra("id", groupItem.getGroupId());
                intent.putExtra("members", groupItem.getMembersFeed());
                intent.putExtra("contact", _ChatList_Feed);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CreateGroupActivity.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
    }

    public class fillContact extends AsyncTask<String, Void, ChatListFeed> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ChatListFeed doInBackground(String... params) {
            Parser parser = new Parser(getSharedPreferences("EZpay", 0).getString("token", ""));
            return parser.checkContactListWithGroup(params[0]);
        }

        @Override
        protected void onPostExecute(ChatListFeed result) {
            if (result != null) {
                ApplicationData.setChatListFeed(result);
            } else {
                Toast.makeText(CreateGroupActivity.this, "problem in connection!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

}
