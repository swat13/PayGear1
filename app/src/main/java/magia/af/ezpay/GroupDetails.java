package magia.af.ezpay;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import magia.af.ezpay.Parser.MembersFeed;

public class GroupDetails extends BaseActivity {

    private int groupId;
    private String groupTitle;
    private String groupPhoto;
    private MembersFeed groupMembers;
    private RecyclerView membersRecycler;
    private RecyclerAdapter adapter;

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
        ImageView groupAvatar = (ImageView) findViewById(R.id.groupAvatar);
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
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_contact_item, parent, false);
            return new RecyclerAdapter.ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, int position) {
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

}
