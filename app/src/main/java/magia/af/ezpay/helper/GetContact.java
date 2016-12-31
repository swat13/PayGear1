package magia.af.ezpay.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import magia.af.ezpay.Parser.ChatListFeed;
import magia.af.ezpay.Parser.ChatListItem;
import magia.af.ezpay.Utilities.LocalPersistence;

/**
 * Created by pc on 10/26/2016.
 */

public class GetContact {

    private static final String TAG = "TAG";
    private HashMap<String, Boolean> stringArrayMap = new HashMap<>();
    Context cx;
    ChatListItem chatListItem;
    ChatListFeed chatListFeed;
    JSONArray jsonArray;

    public String getContact(Context context) {
        cx = context;
        chatListFeed = new ChatListFeed();
        ContentResolver cr = cx.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
          null, null, null, null);

        if (cur.getCount() > 0) {
            jsonArray = new JSONArray();
            int count = 0;
            while (cur.moveToNext()) {
                String id = cur.getString(
                  cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                  ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                      ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                      null,
                      ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                      new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                          ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNo.contains(" ")) {
                            phoneNo = phoneNo.replace(" ", "");
                        }
                        if (phoneNo.contains("+989")) {
                            phoneNo = phoneNo.replace("+98", "0");
                        }
                        chatListItem = new ChatListItem();
                        chatListItem.setTelNo(phoneNo);
                        chatListItem.setContactName(name);
                        chatListFeed.addItem(chatListItem);
                        if (phoneNo.startsWith("09")) {

                            try {
                                if (!stringArrayMap.containsKey(phoneNo)) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("t", name);
                                    jsonObject.put("m", phoneNo);
                                    jsonArray.put(count, jsonObject);
                                    stringArrayMap.put(phoneNo, true);
                                    count++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }


                    }
                    pCur.close();
                }
            }
        }
        ContactDatabase database = new ContactDatabase(cx);
        for (int i = 0; i < chatListFeed.getItemCount(); i++) {
            database.createData(chatListFeed.getItem(i).getTelNo(), chatListFeed.getItem(i).getContactName());
        }
        Log.i("JSON CONTACT", jsonArray.toString());
        writeToFile(jsonArray);
        return jsonArray != null ? jsonArray.toString() : null;
    }

    public boolean isNewContact(ChatListFeed chatListFeed, String phoneNumber) {

        if (chatListFeed == null) {
            return true;
        }
        for (int i = 0; i < chatListFeed.getItemCount(); i++) {
            if (chatListFeed.getItem(i).getTelNo().equals(phoneNumber)) {
                return false;
            }

        }
        return true;

    }

    public void writeToFile(JSONArray json) {
        ChatListFeed chatListFeed = new ChatListFeed();
        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                ChatListItem chatListItem = new ChatListItem();
                chatListItem.setTelNo(jsonObject.getString("m"));
                chatListFeed.addItem(chatListItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        new LocalPersistence().writeObjectToFile(cx, chatListFeed, "All_Contact_List");
    }

    public ArrayList<String> allContacts(Context context) {
        cx = context;
        ContentResolver cr = context.getContentResolver();
        ArrayList<String> phones = new ArrayList<>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNo.contains(" ")) {
                            phoneNo = phoneNo.replace(" ", "");
                        }
                        if (phoneNo.contains("+989")) {
                            phoneNo = phoneNo.replace("+98", "0");
                        }
                        phones.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        return phones;
    }

    public ChatListFeed getNewContact(Context context) {
        cx = context;
        ChatListFeed chatListFeed = new ChatListFeed();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
//        Log.e("GettingContacts","Count: "+cur.getCount());

        if (cur.getCount() > 0) {
            int count = 0;
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));


                if ( cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
//                    Log.e("GettingContacts", "CCC:" +pCur.getCount());
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNo.contains(" ")) {
                            phoneNo = phoneNo.replace(" ", "");
                        }
                        if (phoneNo.contains("+989")) {
                            phoneNo = phoneNo.replace("+98", "0");
                        }
//                        Log.e("GettingContacts",phoneNo);
                        ChatListItem chatListItem = new ChatListItem();
                        chatListItem.setTelNo(phoneNo);
                        chatListItem.setContactName(name);
                        chatListFeed.addItem(chatListItem);
                    }
                    pCur.close();
                }
            }
        }
        return chatListFeed;
    }
}
