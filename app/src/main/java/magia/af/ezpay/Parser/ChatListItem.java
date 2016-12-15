package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatListItem implements Serializable {

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
    private int contactCount;
    private GroupItem groupItem;

    private ArrayList<ChatListItem> contactMembers;

    private boolean singleSelect;
    private boolean visible;
    private int visiblePosition;

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(GroupItem groupFeed) {
        this.groupItem = groupFeed;
    }

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

    public ArrayList<ChatListItem> getContactMembers() {
        return contactMembers;
    }

    public void setContactMembers(ArrayList<ChatListItem> contactMembers) {
        this.contactMembers = contactMembers;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
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
