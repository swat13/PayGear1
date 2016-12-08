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

import magia.af.ezpay.Parser.Feed;
import magia.af.ezpay.Parser.Item;
import magia.af.ezpay.Utilities.LocalPersistence;

/**
 * Created by pc on 10/26/2016.
 */

public class GetContact {

    private static final String TAG = "TAG";
    private ArrayMap<String, Boolean> stringArrayMap = new ArrayMap<>();
    Context cx;
    Item item;
    Feed feed;
    JSONArray jsonArray;

    public String getContact(Context context) {
        cx = context;
        feed = new Feed();
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
                        item = new Item();
                        item.setTelNo(phoneNo);
                        item.setContactName(name);
                        feed.addItem(item);
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
        for (int i = 0; i < feed.getItemCount(); i++) {
            database.createData(feed.getItem(i).getTelNo(), feed.getItem(i).getContactName());
        }
        Log.i("JSON CONTACT", jsonArray.toString());
        writeToFile(jsonArray);
        return jsonArray != null ? jsonArray.toString() : null;
    }

    public boolean isNewContact(Feed feed, String phoneNumber) {

        if (feed == null) {
            return true;
        }
        for (int i = 0; i < feed.getItemCount(); i++) {
            if (feed.getItem(i).getTelNo().equals(phoneNumber)) {
                return false;
            }

        }
        return true;

    }

    public void writeToFile(JSONArray json) {
        Feed feed = new Feed();
        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                Item item = new Item();
                item.setTelNo(jsonObject.getString("m"));
                feed.addItem(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        new LocalPersistence().writeObjectToFile(cx, feed, "All_Contact_List");
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

    public Feed getNewContact(Context context) {
        cx = context;
        Feed feed = new Feed();
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
                        Item item = new Item();
                        item.setTelNo(phoneNo);
                        item.setContactName(name);
                        feed.addItem(item);
                    }
                    pCur.close();
                }
            }
        }
        return feed;
    }
}
