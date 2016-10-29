package magia.af.ezpay.Parser;

import java.io.Serializable;

public class PayLogItem implements Serializable {

  //jkkjjmkjk
  //jkjkijkjk
	private static final long serialVersionUID = 1L;
	private String _telNo;
	private int id;
	private String From;
	private String To;
	private int Amount;
	private String Date;
	private String comment;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  private boolean paideBool;


    public boolean isPaideBool() {
        return paideBool;
    }

    public void setPaideBool(boolean paideBool) {
        this.paideBool = paideBool;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public int getId() {
    return id;
  }

    public void setId(int id) {
    this.id = id;
  }

    public void setTelNo(String telNo) {
		_telNo = telNo;
	}

	public String getTelNo() {
		return _telNo;
	}

}
