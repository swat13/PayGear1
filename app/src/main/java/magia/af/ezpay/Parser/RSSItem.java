package magia.af.ezpay.Parser;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class RSSItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean contactStatus = false;
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
    private int position;
    private String title;
    private boolean group;
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
    private String groupMemberPhoto;
    private String groupMemberPhone;
    private String groupMemberTitle;
    private int groupMemberLastChatId;
    private String groupMemberLastChatFrom;
    private String groupMemberLastChatTo;
    private int groupMemberLastChatAmount;
    private String groupMemberLastChatDate;
    private boolean groupMemberLastChatOrderPay;
    private boolean groupMemberLastChatStatus;
    private boolean groupMemberLastChatFromGroup;
    private String groupMemberLastChatComment;
    private String groupMemberId;
    private int contactCount;
    private int groupCount;
    private boolean groupStatus;
    private ArrayList<RSSItem> groupMembers;
    private ArrayList<RSSItem> contactMembers;
    private ArrayList<RSSItem> groupArray;
    private boolean singleSelect;
    private boolean visible;
    private int visiblePosition;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getVisiblePosition() {
        return visiblePosition;
    }

    public void setVisiblePosition(int visiblePosition) {
        this.visiblePosition = visiblePosition;
    }

    public boolean isSingleSelect() {
        return singleSelect;
    }

    public void setSingleSelect(boolean singleSelect) {
        this.singleSelect = singleSelect;
    }

    public ArrayList<RSSItem> getGroupArray() {
        return groupArray;
    }

    public void setGroupArray(ArrayList<RSSItem> groupArray) {
        this.groupArray = groupArray;
    }

    public ArrayList<RSSItem> getContactMembers() {
        return contactMembers;
    }

    public void setContactMembers(ArrayList<RSSItem> contactMembers) {
        this.contactMembers = contactMembers;
    }

    public ArrayList<RSSItem> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<RSSItem> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public boolean isGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(boolean groupStatus) {
        this.groupStatus = groupStatus;
    }
    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
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

    public boolean isGroupLastChatStatus() {
        return groupLastChatStatus;
    }

    public void setGroupLastChatStatus(boolean groupLastChatStatus) {
        this.groupLastChatStatus = groupLastChatStatus;
    }

    public boolean isGroupLastChatOrderPay() {
        return groupLastChatOrderPay;
    }

    public void setGroupLastChatOrderPay(boolean groupLastChatOrderPay) {
        this.groupLastChatOrderPay = groupLastChatOrderPay;
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

    public String getGroupMemberPhoto() {
        return groupMemberPhoto;
    }

    public void setGroupMemberPhoto(String groupMemberPhoto) {
        this.groupMemberPhoto = groupMemberPhoto;
    }

    public String getGroupMemberPhone() {
        return groupMemberPhone;
    }

    public void setGroupMemberPhone(String groupMemberPhone) {
        this.groupMemberPhone = groupMemberPhone;
    }

    public String getGroupMemberTitle() {
        return groupMemberTitle;
    }

    public void setGroupMemberTitle(String groupMemberTitle) {
        this.groupMemberTitle = groupMemberTitle;
    }

    public int getGroupMemberLastChatId() {
        return groupMemberLastChatId;
    }

    public void setGroupMemberLastChatId(int groupMemberLastChatId) {
        this.groupMemberLastChatId = groupMemberLastChatId;
    }

    public String getGroupMemberLastChatFrom() {
        return groupMemberLastChatFrom;
    }

    public void setGroupMemberLastChatFrom(String groupMemberLastChatFrom) {
        this.groupMemberLastChatFrom = groupMemberLastChatFrom;
    }

    public String getGroupMemberLastChatTo() {
        return groupMemberLastChatTo;
    }

    public void setGroupMemberLastChatTo(String groupMemberLastChatTo) {
        this.groupMemberLastChatTo = groupMemberLastChatTo;
    }

    public int getGroupMemberLastChatAmount() {
        return groupMemberLastChatAmount;
    }

    public void setGroupMemberLastChatAmount(int groupMemberLastChatAmount) {
        this.groupMemberLastChatAmount = groupMemberLastChatAmount;
    }

    public String getGroupMemberLastChatDate() {
        return groupMemberLastChatDate;
    }

    public void setGroupMemberLastChatDate(String groupMemberLastChatDate) {
        this.groupMemberLastChatDate = groupMemberLastChatDate;
    }

    public boolean isGroupMemberLastChatOrderPay() {
        return groupMemberLastChatOrderPay;
    }

    public void setGroupMemberLastChatOrderPay(boolean groupMemberLastChatOrderPay) {
        this.groupMemberLastChatOrderPay = groupMemberLastChatOrderPay;
    }

    public boolean isGroupMemberLastChatStatus() {
        return groupMemberLastChatStatus;
    }

    public void setGroupMemberLastChatStatus(boolean groupMemberLastChatStatus) {
        this.groupMemberLastChatStatus = groupMemberLastChatStatus;
    }

    public boolean isGroupMemberLastChatFromGroup() {
        return groupMemberLastChatFromGroup;
    }

    public void setGroupMemberLastChatFromGroup(boolean groupMemberLastChatFromGroup) {
        this.groupMemberLastChatFromGroup = groupMemberLastChatFromGroup;
    }

    public String getGroupMemberLastChatComment() {
        return groupMemberLastChatComment;
    }

    public void setGroupMemberLastChatComment(String groupMemberLastChatComment) {
        this.groupMemberLastChatComment = groupMemberLastChatComment;
    }

    public String getGroupMemberId() {
        return groupMemberId;
    }

    public void setGroupMemberId(String groupMemberId) {
        this.groupMemberId = groupMemberId;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    private int credit;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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
