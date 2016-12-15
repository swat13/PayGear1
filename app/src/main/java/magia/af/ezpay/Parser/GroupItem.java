package magia.af.ezpay.Parser;

import java.io.Serializable;

/**
 * Created by erfan on 12/7/2016.
 */

public class GroupItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private MembersFeed membersFeed;

    private String notifBody;

    public String getNotifBody() {
        return notifBody;
    }

    public void setNotifBody(String notifBody) {
        this.notifBody = notifBody;
    }

    private int groupId;
    private String groupTitle;
    private String groupPhoto;
    private int groupLastChatId;
    private String groupLastChatFrom;
    private String groupLastChatTo;
    private int groupLastChatAmount;
    private String groupLastChatDate;
    private boolean groupLastChatOrderPay;
    private boolean groupLastChatStatus;
    private String groupLastChatComment;
    private boolean groupLastChatFromGroup;

    public MembersFeed getMembersFeed() {
        return membersFeed;
    }

    public void setMembersFeed(MembersFeed membersFeed) {
        this.membersFeed = membersFeed;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(String groupPhoto) {
        this.groupPhoto = groupPhoto;
    }

    public int getGroupLastChatId() {
        return groupLastChatId;
    }

    public void setGroupLastChatId(int groupLastChatId) {
        this.groupLastChatId = groupLastChatId;
    }

    public String getGroupLastChatFrom() {
        return groupLastChatFrom;
    }

    public void setGroupLastChatFrom(String groupLastChatFrom) {
        this.groupLastChatFrom = groupLastChatFrom;
    }

    public String getGroupLastChatTo() {
        return groupLastChatTo;
    }

    public void setGroupLastChatTo(String groupLastChatTo) {
        this.groupLastChatTo = groupLastChatTo;
    }

    public int getGroupLastChatAmount() {
        return groupLastChatAmount;
    }

    public void setGroupLastChatAmount(int groupLastChatAmount) {
        this.groupLastChatAmount = groupLastChatAmount;
    }

    public String getGroupLastChatDate() {
        return groupLastChatDate;
    }

    public void setGroupLastChatDate(String groupLastChatDate) {
        this.groupLastChatDate = groupLastChatDate;
    }

    public boolean isGroupLastChatOrderPay() {
        return groupLastChatOrderPay;
    }

    public void setGroupLastChatOrderPay(boolean groupLastChatOrderPay) {
        this.groupLastChatOrderPay = groupLastChatOrderPay;
    }

    public boolean isGroupLastChatStatus() {
        return groupLastChatStatus;
    }

    public void setGroupLastChatStatus(boolean groupLastChatStatus) {
        this.groupLastChatStatus = groupLastChatStatus;
    }

    public String getGroupLastChatComment() {
        return groupLastChatComment;
    }

    public void setGroupLastChatComment(String groupLastChatComment) {
        this.groupLastChatComment = groupLastChatComment;
    }

    public boolean isGroupLastChatFromGroup() {
        return groupLastChatFromGroup;
    }

    public void setGroupLastChatFromGroup(boolean groupLastChatFromGroup) {
        this.groupLastChatFromGroup = groupLastChatFromGroup;
    }
}
