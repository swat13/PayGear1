package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class LogFeed implements Serializable {


	HashMap<Integer, Integer> hashMap;
	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private List<LogItem> _itemlist;

	public LogFeed() {
		_itemlist = new Vector<LogItem>(0);
		hashMap = new HashMap<>();
	}

	public void addItem(LogItem item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public void addItemHash(int id, int pos) {
		hashMap.put(id, pos);
	}

	public HashMap<Integer, Integer> getHash() {
		return hashMap;
	}

	public void addItem(LogItem item , int pos) {
		_itemlist.add(pos,item);
		_itemcount++;
	}
	public void addItemRange(LogFeed item) {

		_itemlist.addAll(0,item._itemlist);
		_itemcount+=item._itemcount;
	}
	public void removeItem(int position) {
		_itemlist.remove(position);
		_itemcount--;
	}

	public LogItem getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemlist.size();// _itemcount;
	}

}
