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

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;
import magia.af.ezpay.Splash;
import magia.af.ezpay.Utilities.LocalPersistence;

/**
 * Created by pc on 10/26/2016.
 */

public class GetContact {

    private static final String TAG = "TAG";
    private ArrayMap<String, Boolean> stringArrayMap = new ArrayMap<>();
    Context cx;
    RSSItem rssItem;
    RSSFeed feed;
    JSONArray jsonArray;

    public String getContact(Context context) {
        cx = context;
        jsonArray = null;
        feed = new RSSFeed();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                                rssItem = new RSSItem();
                                rssItem.setTelNo(phoneNo);
                                rssItem.setContactName(name);
                                feed.addItem(rssItem);
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
            }
        });
        thread.start();
        return jsonArray != null ? jsonArray.toString() : null;
    }

    public boolean isNewContact(RSSFeed rssFeed, String phoneNumber) {

        if (rssFeed == null) {
            return true;
        }
        for (int i = 0; i < rssFeed.getItemCount(); i++) {
            if (rssFeed.getItem(i).getTelNo().equals(phoneNumber)) {
                return false;
            }

        }
        return true;

    }

    public void writeToFile(JSONArray json) {
        RSSFeed rssFeed = new RSSFeed();
        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                RSSItem rssItem = new RSSItem();
                rssItem.setTelNo(jsonObject.getString("m"));
                rssFeed.addItem(rssItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        new LocalPersistence().writeObjectToFile(cx, rssFeed, "All_Contact_List");
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

    public RSSFeed getNewContact(Context context) {
        cx = context;
        RSSFeed rssFeed = new RSSFeed();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
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
                        RSSItem rssItem = new RSSItem();
                        rssItem.setTelNo(phoneNo);
                        rssItem.setContactName(name);
                        rssFeed.addItem(rssItem);
                    }
                    pCur.close();
                }
            }
        }
        return rssFeed;
    }
}
