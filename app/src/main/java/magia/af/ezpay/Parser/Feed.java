package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.ArrayList;

public class Feed implements Serializable {

	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private ArrayList<Item> _itemlist;

	public Feed() {
		_itemlist = new ArrayList<>(0);
	}

	public void addItem(Item item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public void addAll(Feed item) {

		_itemlist.addAll(item._itemlist);
	}

	public void addAll(int index, Feed feed){
		_itemlist.addAll(index , feed._itemlist);
	}
	public void removeItem(int position) {
		_itemlist.remove(position);
//		_itemcount--;
	}

	public Item getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemlist.size();
	}

}
