package magia.af.ezpay.Parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class PayLogFeed implements Serializable {


	HashMap<Integer, Integer> hashMap;
	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private List<PayLogItem> _itemlist;

	public PayLogFeed() {
		_itemlist = new Vector<PayLogItem>(0);
		hashMap = new HashMap<>();
	}

	public void addItem(PayLogItem item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public void addItemHash(int id, int pos) {
		hashMap.put(id, pos);
	}

	public HashMap<Integer, Integer> getHash() {
		return hashMap;
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

	public void set(int index,PayLogItem item){
		_itemlist.set(index,item);
	}

}
