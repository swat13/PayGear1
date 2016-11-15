package magia.af.ezpay.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import magia.af.ezpay.ProfileActivity;
import magia.af.ezpay.R;

/**
 * Created by pc on 11/9/2016.
 */

public class ProfileFragment extends Fragment {
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    private ImageView userAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile_backup, container, false);
    /*toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
    ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.e("Test1", "onOffsetChange: " + verticalOffset);
        toolbar.setBackgroundColor(Color.parseColor("#b07d79"));
      }
    });*/

        userAvatar = (ImageView) rootView.findViewById(R.id.user_avatar);


        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Get photo from gallery or take picture ?");
//                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Import from gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                            }
                        });

                builder1.setNegativeButton(
                        "Take picture",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    userAvatar.setImageURI(selectedImage);





                }

                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    userAvatar.setImageURI(selectedImage);
                }
                break;
        }
    }



}
