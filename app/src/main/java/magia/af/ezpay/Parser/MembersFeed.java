package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Created by erfan on 12/7/2016.
 */

public class MembersFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    private int _itemcount = 0;
    private List<MembersItem> _itemlist;

    public MembersFeed() {
        _itemlist = new Vector<MembersItem>(0);
    }

    public void addMemberItem(MembersItem membersItem) {
        _itemlist.add(membersItem);
        _itemcount++;
    }

    public void removeMemberItem(int position) {
        _itemlist.remove(position);
        _itemcount--;
    }

    public MembersItem getMember(int location) {
        return _itemlist.get(location);
    }

    public int memberItemCount() {
        return _itemcount;
    }

}
