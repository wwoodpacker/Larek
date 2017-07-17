package org.firebirdsql.larek;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by nazar.humeniuk on 7/16/17.
 */

public class DBhelperSqllite extends SQLiteOpenHelper {
    final static String DB_NAME = "LarekDB.db";
    Context mContext;
    public DBhelperSqllite(Context context) {
        super(context, DB_NAME, null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LAREK_PAD (ID INTEGER,SERIAL TEXT,MODEL TEXT,NAME TEXT,LAREK_DEP TEXT,MAC TEXT)");
        db.execSQL("CREATE TABLE Larek_authorization_list (ID INTEGER, Name TEXT, Password TEXT, Larek_Dep TEXT,Active INTEGER)");
        db.execSQL("CREATE TABLE Larek_Employees (ID INTEGER, Surname TEXT, Name TEXT, Patronimic TEXT,Occupation TEXT,Larek_Dep TEXT, Status INTEGER)");
        db.execSQL("CREATE TABLE LAREK_PRODUCT_II (ID INTEGER, NAME TEXT, LAREK_DEP TEXT, PRICE REAL)");
        db.execSQL("CREATE TABLE LAREK_PRODUCT_SI (ID INTEGER, NAME TEXT, LAREK_PRODUCT_II INTEGER, COUNT_II INTEGER)");
        db.execSQL("CREATE TABLE LAREK_ORDER (ID INTEGER, SOLDTIME TEXT, SELLER INTEGER, EMPLOYEES INTEGER, TOTAL REAL, LAREK_DEP TEXT)");
        db.execSQL("CREATE TABLE LAREK_ORDER_ITEM (ID INTEGER, \"ORDER\" INTEGER, PRODUCT_SI_ID INTEGER, PRODUCT_SI_COUNT INTEGER, PRODUCT_SI_PRICE REAL,PRODUCT_SI_TOTAL REAL, PRODUCT_II_ID INTEGER, PRODUCT_II_COUNT INTEGER, PRODUCT_II_PRICE REAL, PRODUCT_II_TOTAL REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LAREK_PAD");
        db.execSQL("DROP TABLE IF EXISTS Larek_authorization_list");
        db.execSQL("DROP TABLE IF EXISTS Larek_Employees");
        db.execSQL("DROP TABLE IF EXISTS LAREK_PRODUCT_II");
        db.execSQL("DROP TABLE IF EXISTS LAREK_PRODUCT_SI");
        db.execSQL("DROP TABLE IF EXISTS LAREK_ORDER");
        db.execSQL("DROP TABLE IF EXISTS LAREK_ORDER_ITEM");
        onCreate(db);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db;
        try {
            db = super.getReadableDatabase();
        }
        catch (SQLiteException e) {
            File dbFile = mContext.getDatabasePath(DB_NAME);
            db = SQLiteDatabase.openOrCreateDatabase(dbFile.getAbsolutePath(), null);
        }
        return db;
    }
}
