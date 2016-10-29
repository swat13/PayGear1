package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class PayLogFeed implements Serializable {

	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private List<payLogItem> _itemlist;

	public PayLogFeed() {
		_itemlist = new Vector<payLogItem>(0);
	}

	public void addItem(payLogItem item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public void removeItem(int position) {
		_itemlist.remove(position);
		_itemcount--;
	}

	public payLogItem getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemcount;
	}

}
