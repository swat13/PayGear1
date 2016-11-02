package magia.af.ezpay.helper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import magia.af.ezpay.Parser.ContactModule;

/**
 * Created by Saeid Yazdany on 11/2/2016.
 */

public class ContactDatabase extends SQLiteOpenHelper {
  private Context context;
  private static final String DATABASE_NAME = "contactlist";
  private static final int DATABASE_VERSION = 1;
  private static final String TABLE_NAME = "contacts";
  private static final String CONTACT_NAME = "contact_name";
  private static final String CONTACT_NUMBER = "contact_number";
  private static final String SQL_QUERY =

    "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT," + CONTACT_NAME + " TEXT," + CONTACT_NUMBER + " TEXT);";

  public ContactDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_QUERY);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
  }

  public void createData(String phone) {

    Log.e("phone:", phone + "" );

    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(CONTACT_NUMBER , phone);
    database.insert(TABLE_NAME , null , values);
    database.close();
  }

  public ArrayList<String> getAllData() {

    ArrayList<String> array_list = new ArrayList<>();

    //hp = new HashMap();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
//    res.moveToFirst();
    while (res.moveToNext()) {
      array_list.add(res.getString(res.getColumnIndex(CONTACT_NUMBER)));
    }
    res.close();
    return array_list;
  }
}
