package magia.af.ezpay.Parser;

import java.io.Serializable;

/**
 * Created by erfan on 12/7/2016.
 */

public class MembersItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberPhoto;
    private String memberPhone;
    private String memberTitle;
    private String memberId;

    public String getMemberPhoto() {
        return memberPhoto;
    }

    public void setMemberPhoto(String memberPhoto) {
        this.memberPhoto = memberPhoto;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getMemberTitle() {
        return memberTitle;
    }

    public void setMemberTitle(String memberTitle) {
        this.memberTitle = memberTitle;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
