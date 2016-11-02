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
  private int lastChatId;
  private String lastChatFrom;
  private String lastChatTo;
  private int lastChatAmount;
  private String lastChatDate;
  private boolean lastChatOrderByFromOrTo;
  private String comment;

  public int getLastChatId() {
    return lastChatId;
  }

  public void setLastChatId(int lastChatId) {
    this.lastChatId = lastChatId;
  }

  public String getLastChatFrom() {
    return lastChatFrom;
  }

  public void setLastChatFrom(String lastChatFrom) {
    this.lastChatFrom = lastChatFrom;
  }

  public String getLastChatTo() {
    return lastChatTo;
  }

  public void setLastChatTo(String lastChatTo) {
    this.lastChatTo = lastChatTo;
  }

  public int getLastChatAmount() {
    return lastChatAmount;
  }

  public void setLastChatAmount(int lastChatAmount) {
    this.lastChatAmount = lastChatAmount;
  }

  public String getLastChatDate() {
    return lastChatDate;
  }

  public void setLastChatDate(String lastChatDate) {
    this.lastChatDate = lastChatDate;
  }

  public boolean isLastChatOrderByFromOrTo() {
    return lastChatOrderByFromOrTo;
  }

  public void setLastChatOrderByFromOrTo(boolean lastChatOrderByFromOrTo) {
    this.lastChatOrderByFromOrTo = lastChatOrderByFromOrTo;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

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
