package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.HashMap;

public class PayLogItem implements Serializable {

/*ss*/
    private static final long serialVersionUID = 1L;
    private String _telNo;
    private int id;
    private String From;
    private String To;
    private String photo;
    private String mobile;
    private String memberId;
    private String memberTitle;
    private int Amount;
    private String Date;
    private String comment;
    private boolean paideBool;
    private boolean group;
    private boolean deleted;
    private boolean accepted;
    private boolean status;
    private String fPhoto;
    private String fTitle;
    private String fMobile;
    private String fId;
    private String tPhoto;
    private String tTitle;
    private String tMobile;
    private String tId;
    private int cancelId;

    private HashMap<Integer,Integer> hashMap;

    public HashMap<Integer, Integer> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<Integer, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    public int getCancelId() {
        return cancelId;
    }

    public void setCancelId(int cancelId) {
        this.cancelId = cancelId;
    }

    public String get_telNo() {
        return _telNo;
    }

    public void set_telNo(String _telNo) {
        this._telNo = _telNo;
    }

    public int getNotifType() {
        return notifType;
    }

    public void setNotifType(int notifType) {
        this.notifType = notifType;
    }

    public String getNotifBody() {
        return notifBody;
    }

    public void setNotifBody(String notifBody) {
        this.notifBody = notifBody;
    }

    public String getNotifParam1() {
        return notifParam1;
    }

    public void setNotifParam1(String notifParam1) {
        this.notifParam1 = notifParam1;
    }

    private int notifType;
    private String notifBody;
    private String notifParam1;


    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getfPhoto() {
        return fPhoto;
    }

    public void setfPhoto(String fPhoto) {
        this.fPhoto = fPhoto;
    }

    public String getfTitle() {
        return fTitle;
    }

    public void setfTitle(String fTitle) {
        this.fTitle = fTitle;
    }

    public String getfMobile() {
        return fMobile;
    }

    public void setfMobile(String fMobile) {
        this.fMobile = fMobile;
    }

    public String gettPhoto() {
        return tPhoto;
    }

    public void settPhoto(String tPhoto) {
        this.tPhoto = tPhoto;
    }

    public String gettTitle() {
        return tTitle;
    }

    public void settTitle(String tTitle) {
        this.tTitle = tTitle;
    }

    public String gettMobile() {
        return tMobile;
    }

    public void settMobile(String tMobile) {
        this.tMobile = tMobile;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberTitle() {
        return memberTitle;
    }

    public void setMemberTitle(String memberTitle) {
        this.memberTitle = memberTitle;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


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
