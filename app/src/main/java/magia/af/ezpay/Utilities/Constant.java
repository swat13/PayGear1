package magia.af.ezpay.Utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Parser.GroupItem;
import magia.af.ezpay.Parser.MembersFeed;
import magia.af.ezpay.Parser.MembersItem;

/**
 * Created by pc on 12/18/2016.
 */

public class Constant {
  public static final String REGISTER = "http://new.opaybot.ir/api/Account/RegisterByMobile";
  public static final String QR_CODE = "http://new.opaybot.ir/api/QR";
  public static final String GET_ACCOUNT = "http://new.opaybot.ir/api/account";
  public static final String VERIFY_LOGIN = "http://new.opaybot.ir/api/Account/VerifySMSCode";
  public static final String CHECK_CONTACT_LIST_WITH_GROUP = "http://new.opaybot.ir/api/Account/CheckContactListWithGroups";
  public static final String REQUEST_FROM_ANOTHER = "http://new.opaybot.ir/api/payment/RequestFromAnother";
  public static final String ACCEPT_PAYMENT_REQUEST = "http://new.opaybot.ir/api/payment/AcceptPaymentRequest";
  public static final String SEND_DEVICE_ID = "http://new.opaybot.ir/api/Device";
  public static final String SEND_PAYMENT_REQUEST = "http://new.opaybot.ir/api/payment/PayToAnotherWithTF";
  public static final String CHANGE_USER_IMAGE = "http://new.opaybot.ir/api/account/setPhotoBase64";
  public static final String GET_LOCATION = "http://new.opaybot.ir/api/location";
  public static final String POST_LOCATION = "http://new.opaybot.ir/api/location";
  public static final String ADD_MEMBER_TO_GROUP = "http://new.opaybot.ir/AddMember";
  public static final String CREATE_GROUP = "http://new.opaybot.ir/api/Group";


  public static String getQrCodeId(int id) {
    return "http://new.opaybot.ir/api/QR/" + id;
  }

  public static String getAccountId(int id) {
    return "http://new.opaybot.ir/api/account/" + id;
  }

  public static String getGroupChat(int id, int page, String date) {
    return "http://new.opaybot.ir/api/Group/" + id + "/Chat?pagesize=" + page + "&maxDate=" + date;
  }

  public static String getGroupWithChat(int id, int pageSize) {
    return "http://new.opaybot.ir/api/group/" + id + "?pagesize=" + pageSize;
  }

  public static String changeGroupImage(int id) {
    return "http://new.opaybot.ir/api/Group/" + id + "/SetPhotoBase64";
  }

  public static String deletePayment(int id) {
    return "http://new.opaybot.ir/api/Payment/" + id;
  }

  public static String payLogWithAnother(int page, String date) {
    return "http://new.opaybot.ir/api/payment/PayLogWithAnother?pagesize=" + page + "&maxDate=" + date;
  }

  public static String token(Context context) {
    return context.getSharedPreferences("EZpay", 0).getString("token", "");
  }

}
