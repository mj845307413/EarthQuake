package provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by ws02 on 2016/1/14.
 */
public class EarthquakeProvider extends ContentProvider {
    private MySqLite mySqLite;
    public static final Uri CONTENT_URI = Uri.parse("content://com.majun.earthquake.EarthquakeProvider/earthquakes");
    //Column Names
    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link";
    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI("com.majun.earthquake.EarthquakeProvider", "earthquakes", QUAKES);
        URI_MATCHER.addURI("com.majun.earthquake.EarthquakeProvider", "earthquakes/#", QUAKE_ID);
    }

    @Override
    public boolean onCreate() {
        mySqLite = new MySqLite(getContext(), MySqLite.DATABASE_NAME, null, MySqLite.DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase sqLiteDatabase = mySqLite.getWritableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(MySqLite.EARTHQUAKE_TABLE);
        switch (URI_MATCHER.match(uri)) {
            case QUAKE_ID:
                sqLiteQueryBuilder.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
            default:
                break;
        }
        String order;
        if (TextUtils.isEmpty(s1)) {
            order = KEY_DATE;
        } else {
            order = s1;
        }
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, strings, s, strings1, null, null, order);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case QUAKES:
                return "vnd.android.cursor.dir/vnd.majun.earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/vnd.majun.earthquake";
            default:
                throw new IllegalArgumentException("unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = mySqLite.getWritableDatabase();
        long rowID = sqLiteDatabase.insert(MySqLite.EARTHQUAKE_TABLE, "quake", contentValues);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new IllegalArgumentException("failed to insert row to" + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase sqLiteDatabase = mySqLite.getWritableDatabase();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case QUAKE_ID:
                String s1 = KEY_ID + "="
                        + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(s) ? " AND ("
                        + s + ')' : "");
                count = sqLiteDatabase.delete(MySqLite.EARTHQUAKE_TABLE, s1, strings);
                Log.i("majun", s1);
                break;
            case QUAKES:
                count = sqLiteDatabase.delete(MySqLite.EARTHQUAKE_TABLE, s, strings);
                break;
            default:
                throw new IllegalArgumentException("cannot delete");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase sqLiteDatabase = mySqLite.getWritableDatabase();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case QUAKE_ID:
                String rowID = uri.getPathSegments().get(1);
                count = sqLiteDatabase.update(MySqLite.EARTHQUAKE_TABLE, contentValues, KEY_ID + "=" + rowID + (!TextUtils.isEmpty(s) ? " AND(" + s + ")" : ""), strings);
                break;
            case QUAKES:
                count = sqLiteDatabase.update(MySqLite.EARTHQUAKE_TABLE, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("cannot update");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public class MySqLite extends SQLiteOpenHelper {
        private static final String TAG = "majun";

        private static final String DATABASE_NAME = "earthquakes.db";
        private static final int DATABASE_VERSION = 1;
        private static final String EARTHQUAKE_TABLE = "earthquakes";
        private String createDB = "create table " + EARTHQUAKE_TABLE
                + " (" + KEY_ID + " integer primary key autoincrement, "
//                + KEY_DATE + " INTEGER, "
//                + KEY_DETAILS + " TEXT, "
//                + KEY_SUMMARY + " TEXT, "
//                + KEY_LOCATION_LAT + " FLOAT, "
//                + KEY_LOCATION_LNG + " FLOAT, "
//                + KEY_MAGNITUDE + " FLOAT, "
//                + KEY_LINK + " TEXT);";
                + KEY_DATE + " integer, "
                + KEY_DETAILS + " text, "
                + KEY_SUMMARY + " text, "
                + KEY_LOCATION_LAT + " float, "
                + KEY_LOCATION_LNG + " float, "
                + KEY_MAGNITUDE + " float, "
                + KEY_LINK + " text);";

        public MySqLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.i(TAG, "createDB:" + createDB);
            sqLiteDatabase.execSQL(createDB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.i(TAG,"drop table if exist " + EARTHQUAKE_TABLE);
            sqLiteDatabase.execSQL("drop table if exist " + EARTHQUAKE_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
}
