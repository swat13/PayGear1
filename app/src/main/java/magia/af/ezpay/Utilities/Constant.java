package magia.af.ezpay.Utilities;

import android.content.Context;

/**
 * Created by pc on 12/18/2016.
 */

public class Constant {
  public static final String REGISTER = "http://paygear.org/api/Account/RegisterByMobile";
  public static final String QR_CODE = "http://paygear.org/api/QR";
  public static final String GET_ACCOUNT = "http://paygear.org/api/account";
  public static final String VERIFY_LOGIN = "http://paygear.org/api/Account/VerifySMSCode";
  public static final String CHECK_CONTACT_LIST_WITH_GROUP = "http://paygear.org/api/Account/CheckContactListWithGroups";
  public static final String REQUEST_FROM_ANOTHER = "http://paygear.org/api/payment/RequestFromAnother";
  public static final String ACCEPT_PAYMENT_REQUEST = "http://paygear.org/api/payment/AcceptPaymentRequest";
  public static final String SEND_DEVICE_ID = "http://paygear.org/api/Device";
  public static final String SEND_PAYMENT_REQUEST = "http://paygear.org/api/payment/PayToAnotherWithTF";
  public static final String CHANGE_USER_IMAGE = "http://paygear.org/api/account/setPhotoBase64";
  public static final String GET_LOCATION = "http://paygear.org/api/location";
  public static final String POST_LOCATION = "http://paygear.org/api/location";
  public static final String ADD_MEMBER_TO_GROUP = "http://paygear.org/AddMember";
  public static final String CREATE_GROUP = "http://paygear.org/api/Group";
  public static final String SET_TITLE = "http://paygear.org/api/Account/SetTitle";
  public static final String PAY_WITH_CREDIT = "http://paygear.org/api/Payment/PayToAnotherWithCredit";
  public static final String ACCEPT_PAYMENT_REQUEST_WITH_CREDIT = "http://paygear.org/api/Payment/AcceptPaymentRequestWithCredit";
  public static final String DATE = "2050-01-01T00:00:00.000";
  public static final String WITH_DRAW_FAST = "http://paygear.org/api/Withdraw/FastWithdraw";
  public static final String WITH_DRAW_NORMAL = "http://paygear.org/api/Withdraw/NormalWithdraw";
  public static final String PAY_TO_ANOTHER_WITH_IPG = "http://paygear.org/api/payment/PayToAnotherWithIPG";
  public static final String CHANGE_PASSWORD = "http://paygear.org/api/Account/ChangePassword";
  public static final String IPG_URL = "https://paygear.org/callback/index/";


  public static String getQrCodeId(int id) {
    return "http://paygear.org/api/QR/" + id;
  }

  public static String getAccountId(int id) {
    return "http://paygear.org/api/account/" + id;
  }

  public static String getGroupChat(int id, int page, String date) {
    return "http://paygear.org/api/Group/" + id + "/Chat?pagesize=" + page + "&maxDate=" + date;
  }

  public static String getGroupWithChat(int id, int pageSize) {
    return "http://paygear.org/api/group/" + id + "?pagesize=" + pageSize;
  }

  public static String changeGroupImage(int id) {
    return "http://paygear.org/api/Group/" + id + "/SetPhotoBase64";
  }

  public static String deletePayment(int id) {
    return "http://paygear.org/api/Payment/" + id;
  }

  public static String payLogWithAnother(int page, String date) {
    return "http://paygear.org/api/payment/PayLogWithAnother?pagesize=" + page + "&maxDate=" + date;
  }

  public static String token(Context context) {
    return context.getSharedPreferences("EZpay", 0).getString("token", "");
  }

}
