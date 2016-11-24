package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PayLogFeed implements Serializable {


	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private List<PayLogItem> _itemlist;

	public PayLogFeed() {
		_itemlist = new Vector<PayLogItem>(0);
	}

	public void addItem(PayLogItem item) {
		_itemlist.add(item);
		_itemcount++;
	}
	public void addItem(PayLogItem item , int pos) {
		_itemlist.add(pos,item);
		_itemcount++;
	}
	public void addItemRange(PayLogFeed item) {

		_itemlist.addAll(0,item._itemlist);
		_itemcount+=item._itemcount;
	}
	public void removeItem(int position) {
		_itemlist.remove(position);
		_itemcount--;
	}

	public PayLogItem getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemlist.size();// _itemcount;
	}

}
