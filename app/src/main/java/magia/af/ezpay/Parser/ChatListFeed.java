package magia.af.ezpay.Parser;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ChatListFeed implements Serializable {

	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private ArrayList<ChatListItem> _itemlist;

	public ChatListFeed() {
		_itemlist = new ArrayList<>(0);
	}

	public void addItem(ChatListItem chatListItem) {
		_itemlist.add(chatListItem);
		_itemcount++;
	}

	public void addAll(ChatListFeed item) {

		_itemlist.addAll(item._itemlist);
	}

	public void addAll(int index, ChatListFeed chatListFeed){
		_itemlist.addAll(index , chatListFeed._itemlist);
	}
	public void removeItem(int position) {
		_itemlist.remove(position);
	}

	public void removeItem(Object o) {
		_itemlist.remove(o);
	}


	public ChatListItem getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemlist.size();
	}

}
