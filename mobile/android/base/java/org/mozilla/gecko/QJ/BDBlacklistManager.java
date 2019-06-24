package org.mozilla.gecko.QJ;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import android.database.sqlite.SQLiteCursorDriver;

/**
 * Created by gayamac on 12/02/2018.
 */

class BDBlacklistManager {

    private static final String DATABASE_NAME = "databaseV1.1.qwant_junior_mobile";

    private static final String DATABASE_CREATE =
    "DROP TABLE IF EXISTS AvailableBlacklistDomains;" +
    "CREATE TABLE AvailableBlacklistDomains ( domain VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires DATE NOT NULL );" +
    "DROP TABLE IF EXISTS AvailableRedirectDomains;" +
    "CREATE TABLE AvailableRedirectDomains ( domain VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires DATE NOT NULL );" +
    "DROP TABLE IF EXISTS AvailableBlacklistUrls;" +
    "CREATE TABLE AvailableBlacklistUrls ( url VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires DATE NOT NULL );" +
    "DROP TABLE IF EXISTS AvailableRedirectUrls;" +
    "CREATE TABLE AvailableRedirectUrls ( url VARCHAR(128) NOT NULL UNIQUE, available CHAR(1) NOT NULL, sign VARCHAR(32) NOT NULL, expires DATE NOT NULL );";

    private Connection conn = null;

    private BDBlacklistManager(Connection conn) {
        this.conn = conn;
    }

    static public BDBlacklistManager open() {
        String url = DATABASE_NAME;
        Connection conn;
        try {
            conn = DriverManager.getConnection(url);
            return new BDBlacklistManager(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public BDBlacklistManager create(Context context) {
        String driver = "android.database.sqlite.SQLiteCursorDriver";

        String url = "jdbc:sqlite:" + context.getFilesDir().getAbsolutePath() + DATABASE_NAME;
        Log.d("QWANT JUNIOR MOBILE LOG", "create " + url);
        Connection conn;
        try {
            conn = DriverManager.getConnection(url);
            if (conn == null) {

            }
            PreparedStatement statement = conn.prepareStatement(DATABASE_CREATE);
            ResultSet rs = statement.executeQuery();
            while (rs.next());
            return new BDBlacklistManager(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void get(String table, String field, String path) {
        String querySql = "SELECT " + field + ", available, sign = (" + field + " || available || '2456'), expires < DATE('now') FROM " + table + " WHERE " + field + " = ? ;";

        try {
            PreparedStatement statement = conn.prepareStatement(querySql);
            statement.setString(1, path);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Log.d("QWANT JUNIOR MOBILE LOG", field + " : " + rs.getString(0) + ", available : " + rs.getBoolean(1) + ", sign : " + rs.getBoolean(2) + ", expires : " + rs.getBoolean(3) );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //let querySql = "SELECT " + field + ", available, sign = MD5(" + field + " || available || '2456'), expires < DATE('now') FROM " + table + " WHERE " + field + " = MD5(?) ;"

    }

    public void getDomainInBlacklist(String path) {
        get("AvailableBlacklistDomains", "domain", path);
    }

    public void displayAll(String table, String field) {
        String querySql = "SELECT " + field + " FROM " + table;

        try {
            PreparedStatement statement = conn.prepareStatement(querySql);
            ResultSet rs = statement.executeQuery();
            Log.d("QWANT JUNIOR MOBILE LOG", "Display all " + field + " in " + table);
            while (rs.next()) {
                Log.d("QWANT JUNIOR MOBILE LOG", field + " : " + rs.getString(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //let querySql = "SELECT * FROM " + table

    }

    public void displayAllDomainInBlacklist() {
        displayAll("AvailableBlacklistDomains", "domain");
    }

    public void insert(String table, String field, String path, boolean available) {
        String insertSql = "INSERT INTO " + table + " (" + field + ", available, sign, expires) VALUES (?, ?, (? || ? || '2456'), DATE('now', '+1 day'));";

        try {
            PreparedStatement statement = conn.prepareStatement(insertSql);
            statement.setString(1, path);
            statement.setBoolean(2, available);
            statement.setString(3, path);
            statement.setBoolean(4, available);
            ResultSet rs = statement.executeQuery();
            while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //let insertSql = "INSERT INTO " + table + " (" + field + ", available, sign, expires) VALUES (MD5(?), ?, MD5(MD5(?) || ? || '2456'), DATE('now', '+1 day'));"

    }

    public void insertDomainInBlacklist(String path, boolean available) {
        insert("AvailableBlacklistDomains", "domain", path, available);
    }

    public void update(String table, String field, String path, boolean available) {
        String updateSql = "UPDATE " + table + " SET sign = (? || ? || '2456'), available = ?, expires = DATE('now', '+1 day') WHERE " + field + " = ?;";

        try {
            PreparedStatement statement = conn.prepareStatement(updateSql);
            statement.setString(1, path);
            statement.setBoolean(2, available);
            statement.setString(3, path);
            statement.setBoolean(4, available);
            ResultSet rs = statement.executeQuery();
            while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //let insertSql = "UPDATE " + table + " SET sign = MD5(MD5(?) || ? || '2456'), available = ?, expires = DATE('now', '+1 day') WHERE " + field + " = MD5(?);"

    }

    public void updateDomainInBlacklist(String path, boolean available) {
        update("AvailableBlacklistDomains", "domain", path, available);
    }


}
