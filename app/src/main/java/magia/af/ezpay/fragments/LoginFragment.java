package magia.af.ezpay.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import magia.af.ezpay.R;

/**
 * Created by Saeid Yazdany on 10/26/2016.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
  private ImageButton btn_done;
  private EditText edtInputPhoneNumber;
  public static LoginFragment getInstance(){
    return new LoginFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_login , container , false);
    btn_done = (ImageButton)rootView.findViewById(R.id.btn_done);
    btn_done.setOnClickListener(this);
    edtInputPhoneNumber = (EditText)rootView.findViewById(R.id.edt_input_phone_number);
    return rootView;
  }

  @Override
  public void onClick(View v) {
    //Handle All View Click Here
    switch (v.getId()){
      case R.id.btn_done:
        Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
        ActivationCodeFragment activationCodeFragment = ActivationCodeFragment.getInstance();
        getActivity().getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.container , activationCodeFragment)
          .commit();
        break;
    }
  }
}
