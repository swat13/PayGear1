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

    public int getId() {
    return id;
  }

    public void setId(int id) {
    this.id = id;
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
