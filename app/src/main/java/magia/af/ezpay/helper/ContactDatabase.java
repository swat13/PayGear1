package magia.af.ezpay.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import magia.af.ezpay.Parser.RSSFeed;
import magia.af.ezpay.Parser.RSSItem;

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
  private static final String CONTACT_IN_NETWORK = "contact_in_network";
  private static final String SQL_QUERY =

    "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT," + CONTACT_NAME + " TEXT," + CONTACT_NUMBER + " TEXT," + CONTACT_IN_NETWORK + " boolean);";

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

  public void createData(String phone, String name) {

    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(CONTACT_NUMBER, phone);
    values.put(CONTACT_NAME, name);
    database.insert(TABLE_NAME, null, values);
    database.close();
  }

  public RSSFeed getAllData() {

    RSSFeed rssFeed = new RSSFeed();

    //hp = new HashMap();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
//    res.moveToFirst();
    while (res.moveToNext()) {
      RSSItem rssItem = new RSSItem();
      rssItem.setTelNo(res.getString(res.getColumnIndex(CONTACT_NUMBER)));
      rssItem.setContactName(res.getString(res.getColumnIndex(CONTACT_NAME)));
      rssFeed.addItem(rssItem);
    }
    res.close();
    return rssFeed;
  }

  public String getNameFromNumber(String phone) {
    String name = "";
    SQLiteDatabase database = this.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where " + CONTACT_NUMBER + " ='" + phone + "'", null);
    while (cursor.moveToNext()) {
      name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
      Log.e("PHONE", "getNameFromNumber: " + name);
    }
    cursor.close();
    return name;
  }

  public void setContactInNetwork(String phone) {
    SQLiteDatabase database = this.getReadableDatabase();
    database.execSQL("UPDATE " + TABLE_NAME + " SET " + CONTACT_IN_NETWORK + " = 'true'" + " WHERE " + CONTACT_NUMBER + " = " + "'" + phone + "'");
    database.close();
  }

  public RSSFeed getInNetworkUserName() {
    RSSFeed rssFeed = new RSSFeed();
    SQLiteDatabase database = this.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where " + CONTACT_IN_NETWORK + " = 'true' ", null);
    while (cursor.moveToNext()) {
      RSSItem rssItem = new RSSItem();
      rssItem.setContactName(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
      rssItem.setTelNo(cursor.getString(cursor.getColumnIndex(CONTACT_NUMBER)));
      rssFeed.addItem(rssItem);
    }
    cursor.close();
    return rssFeed;
  }

  public RSSFeed search(String s) {
    RSSFeed rssFeed = new RSSFeed();
    SQLiteDatabase database = this.getReadableDatabase();
    Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where " + CONTACT_NAME + " LIKE " + "'"+ s +"'", null);
    while (cursor.moveToNext()) {
      RSSItem rssItem = new RSSItem();
      rssItem.setContactName(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
      rssItem.setTelNo(cursor.getString(cursor.getColumnIndex(CONTACT_NUMBER)));
      rssFeed.addItem(rssItem);
    }
    cursor.close();
    return rssFeed;
  }

}
