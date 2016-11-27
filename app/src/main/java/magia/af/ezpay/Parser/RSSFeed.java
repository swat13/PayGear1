package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RSSFeed implements Serializable {

	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private ArrayList<RSSItem> _itemlist;

	public RSSFeed() {
		_itemlist = new ArrayList<>(0);
	}

	public void addItem(RSSItem item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public void addAll(RSSFeed item) {

		_itemlist.addAll(item._itemlist);
	}

	public void addAll(int index, RSSFeed feed){
		_itemlist.addAll(index , feed._itemlist);
	}
	public void removeItem(int position) {
		_itemlist.remove(position);
//		_itemcount--;
	}

	public RSSItem getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemlist.size();
	}

}
