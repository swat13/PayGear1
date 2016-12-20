package magia.af.ezpay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.Parser;
import magia.af.ezpay.helper.ImageMaker;

public class GroupDetails extends BaseActivity {

    private int groupId;
    private String groupTitle;
    private String groupPhoto;
    private MembersFeed groupMembers;
    private RecyclerView membersRecycler;
    private RecyclerAdapter adapter;
    ImageView groupAvatar;

    Bitmap thumbnail;
    Intent mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getInt("id");
            groupTitle = bundle.getString("title");
            groupPhoto = bundle.getString("photo");
            groupMembers = (MembersFeed) bundle.getSerializable("members");
        }


        TextView txtNameOfGroup = (TextView) findViewById(R.id.nameOfGroup);
        txtNameOfGroup.setText(groupTitle);
        groupAvatar = (ImageView) findViewById(R.id.groupAvatar);
        Glide.with(this)
                .load(groupPhoto)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.pic_profile)
                .into(new BitmapImageViewTarget(groupAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(700);
                        this.view.setImageDrawable(circularBitmapDrawable);
                    }
                });
        adapter = new RecyclerAdapter();
        membersRecycler = (RecyclerView) findViewById(R.id.memberRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setReverseLayout(true);
        membersRecycler.setLayoutManager(manager);
        membersRecycler.setAdapter(adapter);


        groupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupDetails.this);
                builder1.setMessage("Get photo from gallery or take picture ?");

                builder1.setPositiveButton(
                        "Import from gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
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


    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_contact_item, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.e("POOOOS", "onBindViewHolder: " + position);
            Log.e("Saeid TEST", "onBindViewHolder: " + groupMembers.getMember(position).getMemberTitle());
            holder.txt_contact_item_name.setText(groupMembers.getMember(position).getMemberTitle());
            holder.txt_contact_item_phone.setText(groupMembers.getMember(position).getMemberPhone());
            String imageUrl = "http://new.opaybot.ir";
            Glide.with(GroupDetails.this)
                    .load(imageUrl + groupMembers.getMember(position).getMemberPhoto())
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
            return groupMembers.memberItemCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemClickListener {
            TextView txt_contact_item_name;
            TextView txt_contact_item_phone;
            ImageView contact_item_image;
            ImageView status_circle;

            ViewHolder(View itemView) {
                super(itemView);
                txt_contact_item_name = (TextView) itemView.findViewById(R.id.txt_contact_item_name);
                txt_contact_item_phone = (TextView) itemView.findViewById(R.id.txt_contact_item_phone);
                contact_item_image = (ImageView) itemView.findViewById(R.id.contact_img);
                status_circle = (ImageView) itemView.findViewById(R.id.status_circle);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {

                    View v = parent.getChildAt(i);
                    ImageView imageView = (ImageView) v.findViewById(R.id.status_circle);
                    imageView.setVisibility(View.GONE);

                }

                ImageView imageView = (ImageView) view.findViewById(R.id.status_circle);
                imageView.setVisibility(View.VISIBLE);

            }
        }
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

        if (data != null) {
            ImageMaker imageMaker = new ImageMaker(getApplicationContext(), data);
            return imageMaker.onSelectFromGalleryResult();
        }
        return null;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.e("11111111", "onActivityResult: 0000" + resultCode);
        Log.e("22222222", "onActivityResult: 1111" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult: "+imageReturnedIntent.getData() );
//            Uri tempUri = getImageUri(getActivity(), bitmap);
            switch (requestCode) {
                case 0:
                    new AsyncInsertGpImage().execute(onCaptureImageResult(data), groupId + "");
                    break;
                case 1:
                    new AsyncInsertGpImage().execute(onSelectFromGalleryResult(data), groupId + "");
                    break;
                default:
                    break;
            }

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
                Glide.with(GroupDetails.this)
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
            } else {
                Toast.makeText(GroupDetails.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

