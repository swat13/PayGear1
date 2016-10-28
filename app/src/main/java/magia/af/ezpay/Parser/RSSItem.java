package magia.af.ezpay.Parser;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class RSSItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean contactStatus =false;
	private String _telNo;
	private String contactImg;
	private String contactName;
	private int id;
	private String F;
	private String T;
	private int A;
	private String D;
	private boolean O;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getF() {
    return F;
  }

  public void setF(String f) {
    F = f;
  }

  public String getT() {
    return T;
  }

  public void setT(String t) {
    T = t;
  }

  public int getA() {
    return A;
  }

  public void setA(int a) {
    A = a;
  }

  public String getD() {
    return D;
  }

  public void setD(String d) {
    D = d;
  }

  public boolean getO() {
    return O;
  }

  public void setO(boolean o) {
    O = o;
  }

  public boolean isContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(boolean contactStatus) {
        this.contactStatus = contactStatus;
    }

    public String getContactImg() {
        return contactImg;
    }

    public void setContactImg(String contactImg) {
        this.contactImg = contactImg;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setTelNo(String telNo) {
		_telNo = telNo;
	}

	public String getTelNo() {
		return _telNo;
	}





}
