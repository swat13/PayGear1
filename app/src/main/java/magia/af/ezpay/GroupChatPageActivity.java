package magia.af.ezpay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class GroupChatPageActivity extends BaseActivity {

  private int groupId;
  private String groupTitle;
  private String groupPhoto;

  private ImageView groupImage;
  private TextView txtGroupTitle;

  private static final String TAG = GroupChatPageActivity.class.getSimpleName();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat_page);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null){
      groupId = bundle.getInt("id");
      groupPhoto = bundle.getString("photo");
      groupTitle = bundle.getString("title");
    }

    groupImage = (ImageView)findViewById(R.id.groupImage);
    setGroupPhoto();
    txtGroupTitle = (TextView)findViewById(R.id.groupTitle);
    txtGroupTitle.setText(groupTitle);

    ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  public void setGroupPhoto(){
    Glide.with(this)
      .load(groupPhoto)
      .asBitmap()
      .centerCrop()
      .placeholder(R.drawable.pic_profile)
      .into(new BitmapImageViewTarget(groupImage) {
        @Override
        protected void setResource(Bitmap resource) {
          RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
          circularBitmapDrawable.setCornerRadius(700);
          groupImage.setImageDrawable(circularBitmapDrawable);
        }
      });
  }
}
