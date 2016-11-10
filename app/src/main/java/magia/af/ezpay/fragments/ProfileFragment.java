package magia.af.ezpay.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import magia.af.ezpay.R;

/**
 * Created by pc on 11/9/2016.
 */

public class ProfileFragment extends Fragment {
  Toolbar toolbar;
  AppBarLayout appBarLayout;
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_profile_backup , container , false);
    toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
    ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override
      public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.e("Test1", "onOffsetChange: " + verticalOffset);
        toolbar.setBackgroundColor(Color.parseColor("#b07d79"));
      }
    });

    return rootView;
  }
}
