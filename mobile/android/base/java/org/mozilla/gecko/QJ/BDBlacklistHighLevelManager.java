package org.mozilla.gecko.QJ;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gayamac on 13/02/2018.
 */

class BDBlacklistHighLevelManager {

    private static class BaseSQLite extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "databaseV1.1.qwant_junior_mobile";

        static final String DATABASE_DROP_BL_DOMAINS = "DROP TABLE IF EXISTS AvailableBlacklistDomains;";
        static final String DATABASE_CREATE_BL_DOMAINS = "CREATE TABLE AvailableBlacklistDomains ( domain VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires VARCHAR(10) NOT NULL );";
        static final String DATABASE_DROP_RED_DOMAINS = "DROP TABLE IF EXISTS AvailableRedirectDomains;";
        static final String DATABASE_CREATE_RED_DOMAINS = "CREATE TABLE AvailableRedirectDomains ( domain VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires VARCHAR(10) NOT NULL );";
        static final String DATABASE_DROP_BL_URLS = "DROP TABLE IF EXISTS AvailableBlacklistUrls;";
        static final String DATABASE_CREATE_BL_URLS = "CREATE TABLE AvailableBlacklistUrls ( url VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires VARCHAR(10) NOT NULL );";
        static final String DATABASE_DROP_RED_URLS = "DROP TABLE IF EXISTS AvailableRedirectUrls;";
        static final String DATABASE_CREATE_RED_URLS = "CREATE TABLE AvailableRedirectUrls ( url VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires VARCHAR(10) NOT NULL );";


        BaseSQLite(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d("QWANT JUNIOR MOBILE LOG", "onCreate" );
            sqLiteDatabase.execSQL(DATABASE_DROP_BL_DOMAINS);
            sqLiteDatabase.execSQL(DATABASE_CREATE_BL_DOMAINS);
            sqLiteDatabase.execSQL(DATABASE_DROP_RED_DOMAINS);
            sqLiteDatabase.execSQL(DATABASE_CREATE_RED_DOMAINS);
            sqLiteDatabase.execSQL(DATABASE_DROP_BL_URLS);
            sqLiteDatabase.execSQL(DATABASE_CREATE_BL_URLS);
            sqLiteDatabase.execSQL(DATABASE_DROP_RED_URLS);
            sqLiteDatabase.execSQL(DATABASE_CREATE_RED_URLS);
            Log.d("QWANT JUNIOR MOBILE LOG", "onCreate end" );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.d("QWANT JUNIOR MOBILE LOG", "onUpgrade" );
            onCreate(sqLiteDatabase);
        }
    }

    static class BlacklistBDD {

        interface GetListener {
            void onResult(boolean found, boolean available, boolean expires);
        }

        SQLiteDatabase bdd;
        private BaseSQLite baseSQLite;

        private static final String TABLE_BL_DOMAINS = "AvailableBlacklistDomains";
        private static final String TABLE_RED_DOMAINS = "AvailableRedirectDomains";
        private static final String TABLE_BL_URLS = "AvailableBlacklistUrls";
        private static final String TABLE_RED_URLS = "AvailableRedirectUrls";

        public BlacklistBDD(Context context) {
            baseSQLite = new BaseSQLite(context);
        }

        public void open() {
            bdd = baseSQLite.getWritableDatabase();
        }

        public void close() {
            bdd.close();
        }

        private static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        }

        private static String getStringDate(int incrementDay) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, incrementDay);
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            return f.format(c.getTime());
        }

        private static Date convertDate(String dateStr) throws ParseException {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            return f.parse(dateStr);
        }

        private static long subDate(Date d1, Date d2) {
            long diff = d1.getTime() - d2.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            return diffDays;
        }

        private void insert(String table, String field, String path, boolean available) {
            try {
                String hashPath = md5(path);
                String hashSign = md5(hashPath + "" + (available ? 1 : 0) + "" + 2456);

                ContentValues values = new ContentValues();
                values.put(field, hashPath);
                values.put("available", available ? 1 : 0);
                values.put("sign", hashSign);
                values.put("expires", getStringDate(1));
                bdd.insert(table, null, values);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public void insertDomainInBlacklist(String domain, boolean available) {
            insert(TABLE_BL_DOMAINS, "domain", domain, available);
        }

        public void insertDomainInRedirect(String domain, boolean available) {
            insert(TABLE_RED_DOMAINS, "domain", domain, available);
        }

        public void insertUrlInBlacklist(String url, boolean available) {
            insert(TABLE_BL_URLS, "url", url, available);
        }

        public void insertUrlInRedirect(String url, boolean available) {
            insert(TABLE_RED_URLS, "url", url, available);
        }

        private void update(String table, String field, String path, boolean available) {
            try {
                String hashPath = md5(path);
                String hashSign = md5(hashPath + "" + (available ? 1 : 0) + "" + 2456);

                ContentValues values = new ContentValues();
                values.put("available", available ? 1 : 0);
                values.put("sign", hashSign);
                values.put("expires", getStringDate(1));
                bdd.update(table, values, field + " = \"" + hashPath + "\"", null);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public void updateDomainInBlacklist(String domain, boolean available) {
            update(TABLE_BL_DOMAINS, "domain", domain, available);
        }

        public void updateDomainInRedirect(String domain, boolean available) {
            update(TABLE_RED_DOMAINS, "domain", domain, available);
        }

        public void updateUrlInBlacklist(String url, boolean available) {
            update(TABLE_BL_URLS, "url", url, available);
        }

        public void updateUrlInRedirect(String url, boolean available) {
            update(TABLE_RED_URLS, "url", url, available);
        }

        private void get(String table, String field, String path, GetListener listener) {
            Cursor c = null;
            try {
                String hashPath = md5(path);

                c = bdd.query(table, new String[] {"available", "sign", "expires"}, field + " = \"" + hashPath + "\"", null, null, null, null);

                if (c.getCount() == 0) {
                    listener.onResult(false, false, false);
                    c.close();
                    return;
                }
                c.moveToFirst();
                boolean available = c.getInt(0) == 1;
                String sign = c.getString(1);
                String expires = c.getString(2);
                Log.d("QWANT JUNIOR MOBILE LOG", field + " : " + path + ", available : " + available + ", sign : " + sign + ", expires : " + expires );

                Date expiresDate = convertDate(expires);
                Date now = new Date();

                long nbDate = subDate(expiresDate, now);

                Log.d("QWANT JUNIOR MOBILE LOG", "nbDate : " + nbDate);

                String hashSign = md5(hashPath + "" + (available ? 1 : 0) + "" + 2456);
                listener.onResult(true, available && (hashSign.equals(sign)), nbDate < 0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                 if (c != null)
                     c.close();
            }
        }

        public void getDomainInBlacklist(String domain, GetListener listener) {
            get(TABLE_BL_DOMAINS, "domain", domain, listener);
        }

        public void getDomainInRedirect(String domain, GetListener listener) {
            get(TABLE_RED_DOMAINS, "domain", domain, listener);
        }

        public void getUrlInBlacklist(String url, GetListener listener) {
            get(TABLE_BL_URLS, "url", url, listener);
        }

        public void getUrlInRedirect(String url, GetListener listener) {
            get(TABLE_RED_URLS, "url", url, listener);
        }
    }
}
