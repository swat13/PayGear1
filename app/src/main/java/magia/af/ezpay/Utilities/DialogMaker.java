package magia.af.ezpay.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import magia.af.ezpay.R;

/**
 * Created by pc on 12/18/2016.
 */

public class DialogMaker {
  private static Dialog dialog;
  public static DialogMaker makeDialog(Context context){
    DialogMaker dialogMaker = new DialogMaker();
    dialog = new Dialog(context);
    dialog.setContentView(R.layout.gif_dialog);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    ImageView gif = (ImageView)dialog.findViewById(R.id.gif);
    Glide.with(dialog.getContext()).load(R.drawable.gif_loading).into(gif);
    return dialogMaker;
  }

  public void showDialog(){
    dialog.show();
  }

  public static void disMissDialog(){
    dialog.dismiss();
  }
}
