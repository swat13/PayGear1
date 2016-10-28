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

/**
 * Created by pc on 10/26/2016.
 */

public class GetContact {

    private static final String TAG = "TAG";
    private ArrayMap<String, Boolean> stringArrayMap = new ArrayMap<>();

    public String getContact(Context context) {
        JSONArray jsonArray = null;
        ContentResolver cr = context.getContentResolver();
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

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
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
                        if (phoneNo.contains("+98")) {
                            phoneNo = phoneNo.replace("+98", "0");
                        }
                        try {
                            Log.e("00000000", "getContact: " + phoneNo);
                            if (!stringArrayMap.containsKey(phoneNo)) {
                                JSONObject jsonObject = new JSONObject();
                                Log.e("1111111111", "getContact: " + phoneNo);
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
                    pCur.close();
                }
            }
        }
        Log.i("JSON CONTACT", jsonArray.toString());
        return jsonArray != null ? jsonArray.toString() : null;
    }
}
