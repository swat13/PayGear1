package magia.af.ezpay.Parser;

/**
 * Created by pc on 12/24/2016.
 */

public class DrawItem {
  int bankId;
  String bankName;
  int bandDrawableRes;

  public int getBandDrawableRes() {
    return bandDrawableRes;
  }

  public void setBandDrawableRes(int bandDrawableRes) {
    this.bandDrawableRes = bandDrawableRes;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public int getBankId() {
    return bankId;
  }

  public void setBankId(int bankId) {
    this.bankId = bankId;
  }
}
